package com.espasol.fileorganizer;

import javafx.concurrent.Task;

public class TaskLauncher {
    Task task;
    Runnable beforeMethod;

    public TaskLauncher onBefore(Runnable beforeMethod) {
        this.beforeMethod = beforeMethod;
        return this;
    }

    public TaskLauncher withTask(Task task) {
        this.task = task;
        return this;
    }

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
