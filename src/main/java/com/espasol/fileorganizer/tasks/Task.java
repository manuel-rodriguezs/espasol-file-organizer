package com.espasol.fileorganizer.tasks;

import java.util.concurrent.Callable;

public abstract class Task<T> extends javafx.concurrent.Task<T> {

    Callable<T> runMethod;
    Runnable onBeforeMethod;

    public void run(Callable<T> run) {
        this.runMethod = run;
    }

    @Override
    protected T call() throws Exception {
        return runMethod.call();
    }

    public void setOnBeforeStart(Runnable run) {
        onBeforeMethod = run;
    }

    public Thread start() {
        return TaskLauncher.builder()
                .beforeMethod(onBeforeMethod)
                .task(this)
                .build()
                .start();
    }
}
