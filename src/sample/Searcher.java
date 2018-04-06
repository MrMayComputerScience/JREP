package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


        File[] children = dir.listFiles();

        //If no children, return empty file array
        if(children == null)
            return;


        for(int i = 0; i < children.length; i++){
            if(shouldStop.get()){
                return;
            }
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
                    Text t = new Text(f.getAbsolutePath());
                    t.setOnMouseClicked(event -> {
                        if(event.getClickCount() == 2)
                            try{
                                Desktop.getDesktop().open(f.getParentFile());
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                    });
                    rp.getResults().getItems().add(t);
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
                try(FileInputStream fis = new FileInputStream(file);
                    InputStreamReader in = new InputStreamReader(fis, "UTF8")){
                    StringBuilder builder = new StringBuilder();
                    char[] buff = new char[512];
                    System.out.println("reading file "+file.getName());
                    while(in.ready()){
                        in.read(buff);
                        builder.append(buff);
                    }
                    System.out.println("Done reading "+file.getName());
                    compareString = builder.toString();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        if(rp.isStrictMatch()){
            if(rp.useRegex())
                return compareString.matches(rp.getMatchString());
            else
                return compareString.equals(rp.getMatchString());
        }
        else{
            if(rp.useRegex()){
                Pattern p = Pattern.compile(rp.getMatchString());
                Matcher m = p.matcher(compareString);
                return m.find();
            }
            else
                return compareString.contains(rp.getMatchString());
        }
    }
}
