package sample;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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
    private boolean useRegex;
    public ResourcePackage(File rootDir, String matchString,
                           Label filesSearched, Label matchesFound, ListView results, Label statusLabel,
                           boolean strictMatch, boolean searchContent, boolean useRegex) {
        this.rootDir = rootDir;
        this.matchString = matchString;
        this.filesSearched = filesSearched;
        this.matchesFound = matchesFound;
        this.results = results;
        this.statusLabel = statusLabel;
        this.strictMatch = strictMatch;
        this.searchContent = searchContent;
        this.useRegex = useRegex;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public void setMatchString(String matchString) {
        this.matchString = matchString;
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

    public boolean useRegex() {
        return useRegex;
    }

    public void toggleRegex(){
        useRegex = !useRegex;
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
                ", searchContent=" + searchContent +
                ", useRegex=" + useRegex +
                '}';
    }
}
