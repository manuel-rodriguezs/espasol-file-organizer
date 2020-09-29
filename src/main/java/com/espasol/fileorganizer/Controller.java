package com.espasol.fileorganizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

public class Controller {
    @FXML
    TextField filterField, originField, destField;

    @FXML
    Button btnOrigen, btnDestino, btnBuscar;

    @FXML
    ListView<String> listaEncontrados;

    List<Control> controlsForDisable;

    @FXML
    protected void selectOriginPath(ActionEvent event) {
        Optional<File> directory = getDirectoryFromdialog(event);
        setDirectoryText(directory, originField);
    }

    @FXML
    protected void selectDestPath(ActionEvent event) {
        Optional<File> directory = getDirectoryFromdialog(event);
        setDirectoryText(directory, destField);
    }

    @FXML
    protected void find(ActionEvent event) {
        disableFrom();
        listaEncontrados.getItems().clear();
        Scene scene = getSceneFromEvent(event);
        scene.setCursor(Cursor.WAIT);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), eventHandler -> {
            for (int i = 0; i < 100; i++) {
                listaEncontrados.getItems().add("elemento con texto bastante lagro lorem ipsum dolor " + i);
            }
            enableFrom();
            scene.setCursor(Cursor.DEFAULT);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private Optional<File> getDirectoryFromdialog(ActionEvent event) {
        Window window = getSceneFromEvent(event).getWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(window);
        if (file == null) {
            return Optional.empty();
        } else {
            return Optional.of(file);
        }
    }

    private Scene getSceneFromEvent(ActionEvent event) {
        Node source = (Node) event.getSource();
        return source.getScene();
    }

    private void setDirectoryText(Optional<File> directory, TextField field) {
        if (directory.isPresent()) {
            field.setText(directory.get().toString());
        } else {
            field.setText("");
        }
    }

    private void disableFrom() {
        initializeControlsForDisable();
        controlsForDisable.forEach(this::disableControl);
    }

    private void enableFrom() {
        initializeControlsForDisable();
        controlsForDisable.forEach(this::enableControl);
    }

    private void initializeControlsForDisable() {
        controlsForDisable = asList(filterField, btnOrigen, btnDestino, btnBuscar);
    }

    private void disableControl(Control control) {
        control.setDisable(true);
    }

    private void enableControl(Control control) {
        control.setDisable(false);
    }
}
