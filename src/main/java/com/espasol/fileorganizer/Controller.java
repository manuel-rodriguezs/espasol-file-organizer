package com.espasol.fileorganizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
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

    List<Control> controlsForDisable;

    @FXML
    protected void selectOriginPath(ActionEvent event) {
        setDirectoryText(getDirectory(event), originField);
    }

    @FXML
    protected void selectDestPath(ActionEvent event) {
        setDirectoryText(getDirectory(event), destField);
    }

    @FXML
    protected void find() {
        disableFrom();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> enableFrom()));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private Optional<File> getDirectory(ActionEvent event) {
        Window window = getWindowFromEvent(event);
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(window);
        if (file == null) {
            return Optional.empty();
        } else {
            return Optional.of(file);
        }
    }

    private Window getWindowFromEvent(ActionEvent event) {
        Node source = (Node) event.getSource();
        return source.getScene().getWindow();
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
