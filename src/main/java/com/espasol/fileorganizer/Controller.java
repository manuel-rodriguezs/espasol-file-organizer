package com.espasol.fileorganizer;

import com.espasol.fileorganizer.beans.SearchOriginCriteria;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class Controller {
    @FXML
    TextField filterField, originField, destField;

    @FXML
    Button btnOrigin, btnDestination, btnFind;

    @FXML
    ListView<String> foundDirectories;

    List<Control> activatableControls;

    Service service = new Service();

    @FXML
    protected void selectOriginPath(ActionEvent event) {
        Optional<File> directory = getDirectoryFromDialog(event);
        if (directory.isPresent()) {
            originField.setText(directory.get().toString());
        } else {
            originField.setText("");
        }
        endableOrDisableFindButton();
    }

    @FXML
    protected void selectDestPath(ActionEvent event) {
        Optional<File> directory = getDirectoryFromDialog(event);
        if (directory.isPresent()) {
            destField.setText(directory.get().toString());
        } else {
            destField.setText("");
        }
    }

    @FXML
    protected void find(ActionEvent event) {
        new TaskLauncher()
                .onBefore(() -> {
                    disableForm(event);
                    foundDirectories.getItems().clear();
                })
                .withTask(new Task<List<String>>() {
                    @Override
                    protected List<String> call() {
                        return service.findDirectoriesToMove(SearchOriginCriteria.builder()
                                .originPath(originField.getText())
                                .filter(filterField.getText())
                                .build());
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        List<String> cleanedListOfDirectoriesToMove = getValue().stream().map(
                                dir -> dir.replace(originField.getText(), "")
                        ).collect(toList());
                        foundDirectories.setItems(FXCollections.observableArrayList(cleanedListOfDirectoriesToMove));
                        enableForm(event);
                    }
                })
                .start();
    }

    private Optional<File> getDirectoryFromDialog(ActionEvent event) {
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

    private void endableOrDisableFindButton() {
        boolean disableFindButton = originField.getText().length() == 0;
        btnFind.setDisable(disableFindButton);
    }

    private void disableForm(ActionEvent event) {
        getSceneFromEvent(event).setCursor(Cursor.WAIT);
        getActivatableControls().forEach(this::disableControl);
    }

    private void enableForm(ActionEvent event) {
        getSceneFromEvent(event).setCursor(Cursor.DEFAULT);
        getActivatableControls().forEach(this::enableControl);
    }

    private List<Control> getActivatableControls() {
        if (activatableControls == null) {
            activatableControls = asList(filterField, btnOrigin, btnDestination, btnFind);
        }
        return activatableControls;
    }

    private void disableControl(Control control) {
        control.setDisable(true);
    }

    private void enableControl(Control control) {
        control.setDisable(false);
    }

    private void showErrorMessage(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ocurrió un error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
