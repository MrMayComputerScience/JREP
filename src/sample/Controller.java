package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Controller {
    private boolean strictMatch;
    private File rootDir;
    @FXML private TextField matchField;
    public void initialize(){
        strictMatch = false;
        rootDir = selectRootDirectory();
        System.out.println(rootDir.getName());

    }
    public File selectRootDirectory(){
        File ret = null;
        DirectoryChooser dc = new DirectoryChooser();
        Window popup = new Stage();
        ret = dc.showDialog(popup);
        return ret;
    }
    public File[] getMatchingFiles(){
        File[] ret = getMatchingFiles(rootDir);
        System.out.println(Arrays.toString(ret));
        return ret;
    }
    public File[] getMatchingFiles(File dir){
        String matchString = matchField.getText();
        //TODO: Add null checking for rootDir
        File[] children = dir.listFiles();
        List<File> matchList = new LinkedList<>();

        if(children == null)
            return new File[0];

        for(File f : children){
            String s = f.getName();
            if(strictMatch){
                if(s.contentEquals(matchString)){
                    matchList.add(f);
                    System.out.println("Success!");
                }
            }
            else{
                if(s.contains(matchString)){
                    matchList.add(f);
                }
            }
            matchList.addAll(Arrays.asList(getMatchingFiles(f)));
        }
        File[] ret = new File[matchList.size()];
        matchList.toArray(ret);
        return ret;
    }

}

