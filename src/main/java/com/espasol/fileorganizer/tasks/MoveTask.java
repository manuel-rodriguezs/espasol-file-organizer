package com.espasol.fileorganizer.tasks;

import com.espasol.fileorganizer.Service;

public class MoveTask extends Task<Object> {
    public void setOnMovedDirEvent(MovedDirEvent movedDirEvent, Service service) {
        service.setMovedDirListener(movedDirEvent);
    }
}
