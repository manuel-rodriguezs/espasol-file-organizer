package com.espasol.fileorganizer;

import com.espasol.fileorganizer.beans.MoveOrder;
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
    Button btnOrigin, btnDestination, btnFind, btnMove;

    @FXML
    ListView<String> foundDirectories, movedDirectories;

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
        enableOrDisableFindButton();
        foundDirectories.getItems().clear();
        movedDirectories.getItems().clear();
        enableOrDisableMoveButton();
    }

    @FXML
    protected void selectDestPath(ActionEvent event) {
        Optional<File> directory = getDirectoryFromDialog(event);
        if (directory.isPresent()) {
            destField.setText(directory.get().toString());
        } else {
            destField.setText("");
        }
        movedDirectories.getItems().clear();
        enableOrDisableMoveButton();
    }

    @FXML
    protected void find(ActionEvent event) {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() {
                return service.findDirectoriesToMove(SearchOriginCriteria.builder()
                        .originPath(originField.getText())
                        .filter(filterField.getText())
                        .build());
            }
        };
        task.setOnFailed(evt -> {
            enableForm(event);
            showErrorMessage("Error buscando en directorios", task.getException().getMessage());
        });
        task.setOnSucceeded(evt -> {
            List<String> cleanedListOfDirectoriesToMove = task.getValue().stream().map(
                    dir -> dir.replace(originField.getText(), "")
            ).collect(toList());
            foundDirectories.setItems(FXCollections.observableArrayList(cleanedListOfDirectoriesToMove));
            enableForm(event);
            enableOrDisableMoveButton();
        });
        new TaskLauncher()
                .onBefore(() -> {
                    disableForm(event);
                    foundDirectories.getItems().clear();
                    enableOrDisableMoveButton();
                    enableOrDisableFindButton();
                })
                .withTask(task)
                .start();
    }

    @FXML
    protected void move(ActionEvent event) {
        Task<Object> task = new Task<>() {
            @Override
            protected Object call() throws Exception {
                service.moveDirs(MoveOrder.builder()
                        .originPath(originField.getText())
                        .dirsToMove(foundDirectories.getItems())
                        .destinationPath(destField.getText())
                        .build()
                );
                return new Object();
            }
        };
        task.setOnFailed(evt -> {
            enableForm(event);
            showErrorMessage("Error moviendo directorios", task.getException().getMessage());
        });
        task.setOnSucceeded(evt -> {
            enableForm(event);
            foundDirectories.getItems().clear();
            enableOrDisableMoveButton();
            enableOrDisableFindButton();
            showInfoMessage("¡Todo OK!", "Directorios movidos", null);
        });
        new TaskLauncher()
                .onBefore(() -> {
                    disableForm(event);
                    movedDirectories.getItems().clear();
                })
                .withTask(task)
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

    private void enableOrDisableFindButton() {
        boolean disableFindButton = originField.getText().length() == 0;
        btnFind.setDisable(disableFindButton);
    }

    private void enableOrDisableMoveButton() {
        boolean disableMoveButton = (destField.getText().length() == 0) || (foundDirectories.getItems().size() == 0);
        btnMove.setDisable(disableMoveButton);
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

    private void showInfoMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
