package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;

public class ResourcePackage {
    private File rootDir;
    private String matchString;
    private Label filesSearched;
    private Label matchesFound;
    private ListView results;
    private Label statusLabel;
    private boolean strictMatch;
    private boolean searchContent;
    public ResourcePackage(File rootDir, String matchString,
                           Label filesSearched, Label matchesFound, ListView results, Label statusLabel,
                           boolean strictMatch, boolean searchContent) {
        this.rootDir = rootDir;
        this.matchString = matchString;
        this.filesSearched = filesSearched;
        this.matchesFound = matchesFound;
        this.results = results;
        this.statusLabel = statusLabel;
        this.strictMatch = strictMatch;
        this.searchContent = searchContent;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public void setMatchString(String matchString) {
        this.matchString = matchString;
    }

    public void setStrictMatch(boolean strictMatch) {
        this.strictMatch = strictMatch;
    }

    public void setSearchContent(boolean searchContent) {
        this.searchContent = searchContent;
    }

    public File getRootDir() {
        return rootDir;
    }

    public String getMatchString() {
        return matchString;
    }

    public Label getFilesSearched() {
        return filesSearched;
    }

    public Label getMatchesFound() {
        return matchesFound;
    }

    public ListView getResults() {
        return results;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public boolean isStrictMatch() {
        return strictMatch;
    }

    public boolean isSearchContent() {
        return searchContent;
    }

    public void toggleStrict(){
        strictMatch = !strictMatch;
    }

    public void toggleSearchContent(){
        searchContent = !searchContent;
    }

    @Override
    public String toString() {
        return "ResourcePackage{" +
                "rootDir=" + rootDir +
                ", matchString='" + matchString + '\'' +
                ", filesSearched=" + filesSearched +
                ", matchesFound=" + matchesFound +
                ", results=" + results +
                ", statusLabel=" + statusLabel +
                ", strictMatch=" + strictMatch +
                '}';
    }
}
