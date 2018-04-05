package sample;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

public class Controller {

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
    @FXML private List<Node> volatileElements;

    public void initialize(){
        boolean strictMatch = false;
        boolean searchContent = false;
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
        volatileElements = Collections.unmodifiableList(volatileElements);
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

        //DISABLE ALL VOLATILE ELEMENTS
        for(Node n : volatileElements)
            n.setDisable(true);
        ///////////////////////////////
        System.out.println("Before the removal");
        results.setItems(FXCollections.observableArrayList());
        System.out.println("rp = " + rp);
        proc = Searcher.create(rp);

        proc.onEnd(() -> Platform.runLater(() -> {
            //ENABLE VOLATILE ELEMENTS
            for(Node n : volatileElements)
                n.setDisable(false);

        }));

        proc.setDaemon(true);
        proc.start();
    }
    public void cancelSearch(){
        proc.cancel();
    }

    public void onStrictChange(){
        rp.toggleStrict();
    }
    public void onContentChange(){
        rp.toggleSearchContent();
    }
}

