package com.espasol.fileorganizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.setTitle("Organizador de ficheros Espasol");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

}
