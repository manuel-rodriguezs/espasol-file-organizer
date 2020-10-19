package com.espasol.fileorganizer.tasks;

import javafx.concurrent.Task;
import lombok.Builder;

@Builder
public class TaskLauncher {
    Task task;
    Runnable beforeMethod;

    public Thread start() {
        if (beforeMethod != null) {
            beforeMethod.run();
        }
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        return th;
    }
}
