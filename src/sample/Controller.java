package sample;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
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
    private boolean searchContent;
    private File rootDir;
    private Searcher proc;
    private ResourcePackage rp;
    @FXML private TextField matchField;
    @FXML private ListView results;
    @FXML private Label filesSearched;
    @FXML private Label matchesFound;
    @FXML private Label rootDirLabel;
    @FXML private Label statusLabel;
    @FXML private Button searchButton;

    public void initialize(){
        strictMatch = false;
        searchContent = true;
        rootDir = new File("C:\\");
        rootDirLabel.setText(rootDir.getAbsolutePath());
        filesSearched.setText("0");
        matchesFound.setText("0");
        rp = new ResourcePackage(rootDir, matchField.getText(),
                filesSearched, matchesFound, results, statusLabel,
                strictMatch, searchContent);
        matchField.textProperty().addListener((obs, oval, nval) -> {
            rp.setMatchString(nval);
        });
    }
    public void selectRootDirectory(){
        DirectoryChooser dc = new DirectoryChooser();
        Window popup = new Stage();
        File ret = dc.showDialog(popup);
        rootDir = ret != null ? ret : rootDir;
        rootDirLabel.setText(rootDir.getAbsolutePath());
        rp.setRootDir(rootDir);
    }
    public void getMatchesButton(){
        filesSearched.setText("0");
        matchesFound.setText("0");
        statusLabel.setText("Running");
        searchButton.setDisable(true);
        results.getItems().removeAll(results.getItems());
        System.out.println("rp = " + rp);
        proc = Searcher.create(rp);
        proc.onEnd(() -> searchButton.setDisable(false));
        proc.setDaemon(true);
        proc.start();
    }
    public void cancelSearch(){
        proc.cancel();
    }
}

