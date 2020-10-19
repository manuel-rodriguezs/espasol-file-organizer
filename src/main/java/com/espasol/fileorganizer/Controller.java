package com.espasol.fileorganizer;

import com.espasol.fileorganizer.beans.MoveOrder;
import com.espasol.fileorganizer.beans.SearchOriginCriteria;
import com.espasol.fileorganizer.tasks.FindTask;
import com.espasol.fileorganizer.tasks.MoveTask;
import com.espasol.fileorganizer.tasks.MovedDirEvent;
import com.espasol.fileorganizer.tasks.Task;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.concurrent.Callable;

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
        FindTask task = new FindTask();
        task.run(findCallableMethod());
        task.setOnBeforeStart(findOnBefore(event));
        task.setOnSucceeded(findOnSuccess(event, task));
        task.setOnFailed(taskOnFail(event, "Error buscando en carpetas", task));
        task.start();
    }

    private Callable<List<String>> findCallableMethod() {
        return () -> service.findDirectoriesToMove(SearchOriginCriteria.builder()
                .originPath(originField.getText())
                .filter(filterField.getText())
                .build());
    }

    private Runnable findOnBefore(ActionEvent event) {
        return () -> {
            disableForm(event);
            foundDirectories.getItems().clear();
            movedDirectories.getItems().clear();
        };
    }

    private EventHandler<WorkerStateEvent> findOnSuccess(ActionEvent event, FindTask task) {
        return evt -> {
            List<String> cleanedListOfDirectoriesToMove = task.getValue().stream().map(
                    dir -> dir.replace(originField.getText(), "")
            ).collect(toList());
            foundDirectories.setItems(FXCollections.observableArrayList(cleanedListOfDirectoriesToMove));
            enableForm(event);
            enableOrDisableFindButton();
            enableOrDisableMoveButton();
        };
    }

    @FXML
    protected void move(ActionEvent event) {
        MoveTask task = new MoveTask();
        task.run(moveCallableMethod());
        task.setOnBeforeStart(moveOnBefore(event, movedDirectories));
        task.setOnSucceeded(moveOnSuccess(event));
        task.setOnFailed(taskOnFail(event, "Error moviendo carpetas", task));
        task.setOnMovedDirEvent(getMovedDirEvent(), service);
        task.start();
    }

    private Callable<Object> moveCallableMethod() {
        return () -> {
            service.moveDirs(MoveOrder.builder()
                    .originPath(originField.getText())
                    .dirsToMove(foundDirectories.getItems())
                    .destinationPath(destField.getText())
                    .build()
            );
            return new Object();
        };
    }

    private Runnable moveOnBefore(ActionEvent event, ListView<String> movedDirectories) {
        return () -> {
            disableForm(event);
            movedDirectories.getItems().clear();
        };
    }

    private EventHandler<WorkerStateEvent> moveOnSuccess(ActionEvent event) {
        return evt -> {
            enableForm(event);
            enableOrDisableMoveButton();
            enableOrDisableFindButton();
            showMessage(Alert.AlertType.INFORMATION, "¡Todo OK!", "Carpetas movidas", null);
        };
    }

    private MovedDirEvent getMovedDirEvent() {
        return dir -> {
            movedDirectories.getItems().add(dir);
            foundDirectories.getItems().remove(dir);
        };
    }

    private EventHandler<WorkerStateEvent> taskOnFail(ActionEvent event, String s, Task task) {
        return evt -> {
            enableForm(event);
            showErrorMessage(s, task.getException().getMessage());
        };
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
            activatableControls = asList(filterField, btnOrigin, btnDestination, btnFind, btnMove);
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
        showMessage(Alert.AlertType.ERROR, "Ocurrió un error", header, content);
    }

    private void showMessage(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
