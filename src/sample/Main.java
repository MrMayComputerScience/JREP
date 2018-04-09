package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        String rsrcName = Main.class.getResource("Main.class").toString();
        if(rsrcName.startsWith("jar")){
            File out = new File("out.txt");
            File err = new File("err.txt");
            try{
                PrintStream stdout = new PrintStream(new FileOutputStream(out));
                PrintStream stderr = new PrintStream(new FileOutputStream(err));
                System.setOut(stdout);
                System.setErr(stderr);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        launch(args);
    }
}
