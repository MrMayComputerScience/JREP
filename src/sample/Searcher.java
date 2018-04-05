package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Searcher extends Thread {
    private static Searcher instance;
    private AtomicBoolean shouldStop;
    private Runnable onEnd;
    private ResourcePackage rp;
    public static Searcher create(ResourcePackage rp){
        Searcher ret = new Searcher(rp);
        if(instance != null){
            instance.cancel();
            try{
                instance.join();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        instance = ret;
        return ret;


    }
    public void cancel(){
        shouldStop.set(true);
    }

    public Searcher(ResourcePackage rp) {
        this.rp = rp;
        shouldStop = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        getMatchingFiles(rp.getRootDir());
        if(shouldStop.get()){
            Platform.runLater(() -> rp.getStatusLabel().setText("Cancelled"));
        }
        else {
            String statusText = String.format("Completed in %.3f seconds",
                    (double) (System.currentTimeMillis() - startTime) / 1000.0);
            Platform.runLater(() -> rp.getStatusLabel().setText(statusText));
        }

        if(onEnd != null)
            onEnd.run();
    }
    private void getMatchingFiles(File dir){
        if(shouldStop.get()){
            return;
        }
        //TODO: Add null checking for rootDir
        File[] children = dir.listFiles();

        //If no children, return empty file array
        if(children == null)
            return;


        for(int i = 0; i < children.length; i++){
            File f = children[i];
            //Increment the number of files searched
            Platform.runLater(() -> {
                int numFiles = Integer.parseInt(rp.getFilesSearched().getText());
                rp.getFilesSearched().setText(++numFiles + "");
            });


            if(matches(f)){
                Platform.runLater(() -> {
                    int numMatches = Integer.parseInt(rp.getMatchesFound().getText());
                    rp.getMatchesFound().setText(++numMatches + "");
                    rp.getResults().getItems().add(new Text(f.getAbsolutePath()));
                });
            }
            getMatchingFiles(f);
        }
    }
    public void onEnd(Runnable r){
        onEnd = r;
    }
    private boolean matches(File file){
        String compareString = file.getName();
        if(rp.isSearchContent()){
            if(file.isDirectory()){
                return false;
            }
            else{
                try(Scanner in = new Scanner(file)){
                    String content = "";
                    while (in.hasNextLine())
                        content += in.nextLine() + "\n";
                    compareString = content;
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        if(rp.isStrictMatch()){
            return compareString.equals(rp.getMatchString());
        }
        else{
            return compareString.contains(rp.getMatchString());
        }
    }
}
