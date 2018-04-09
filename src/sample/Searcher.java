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
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher extends Thread {
    private static Searcher instance;
    private AtomicBoolean shouldStop;
    private Runnable onStart;
    private Runnable onEnd;
    private ResourcePackage rp;
    private static AtomicLong lastClick = new AtomicLong(0);
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
        if(onStart != null)
            onStart.run();

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
                numFiles++;
                rp.getFilesSearched().setText(numFiles + "");
            });

            if(matches(f)){
                //If it matches, update the label of files that have matched and add the match to the list
                Platform.runLater(() -> {
                    //Update the label
                    int numMatches = Integer.parseInt(rp.getMatchesFound().getText());
                    rp.getMatchesFound().setText(++numMatches + "");
                    //Create new text containing filepath
                    Text t = new Text(f.getAbsolutePath());
                    //Make it so when we double-click the text it opens the file explorer with the file highlighted
                    t.setOnMouseClicked(event -> {
                        long click = System.currentTimeMillis();
                        if(click - lastClick.get() <= 250){
                            //Eliminate triple-click bugs
                            lastClick.set(0);
                            //Make the explorer
                            try{
                                Process p = new ProcessBuilder("explorer.exe",
                                        "/select,"+f.getAbsolutePath()).start();
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            lastClick.set(click);
                        }

                    });
                    rp.getResults().getItems().add(t);
                });
            }
            getMatchingFiles(f);
        }
    }
    public void onStart(Runnable r){onStart = r;}
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
                if(rp.getFuzzyNumErrors() > 0)
                    return editDistance(rp.getMatchString(), compareString, rp.getFuzzyNumErrors()) <= rp.getFuzzyNumErrors();
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

    private static int editDistance(String text, String pattern, int maxErrors){
        int d[][];
        int m = text.length();
        int n = pattern.length();
        if(Math.abs(m - n) > maxErrors){
            return maxErrors+1;
        }
        d = new int[m+1][n+1];
        for(int i = 0; i < m+1; ++i)
            d[i][0] = i;
        for(int i = 0; i < n+1; ++i)
            d[0][i] = i;
        for(int i = 1; i <= m; ++i)
            for(int j = 1; j <= n; ++j){
                if(text.charAt(i-1) == pattern.charAt(j-1))
                    d[i][j] = d[i-1][j-1];
                else{
                    d[i][j] = min3(
                            d[i-1][j] + 1,
                            d[i][j-1] + 1,
                            d[i-1][j-1] + 1
                    );
                }
            }
        return d[m][n];
    }

    private static int min3(int a, int b, int c){
        return a < b ?
                (a < c ? a : c) :
                (b < c ? b : c);
    }
}
