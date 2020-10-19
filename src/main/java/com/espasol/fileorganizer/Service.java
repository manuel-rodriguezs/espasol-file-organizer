package com.espasol.fileorganizer;

import com.espasol.fileorganizer.beans.MoveFromToOrder;
import com.espasol.fileorganizer.beans.MoveOrder;
import com.espasol.fileorganizer.beans.SearchOriginCriteria;
import com.espasol.fileorganizer.tasks.MovedDirEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Service {

    MovedDirEvent movedDirEvent;

    public List<String> findDirectoriesToMove(SearchOriginCriteria searchOriginCriteria) {
        Optional<List<File>> opDirs = searchDirectoriesWithCriteria(searchOriginCriteria);
        return opDirs
                .map(files -> files.stream().map(File::getAbsolutePath).collect(toList()))
                .orElseGet(ArrayList::new);
    }

    public void moveDirs(MoveOrder moveOrder) throws Exception {
        for (MoveFromToOrder moveFromToOrder : moveOrder.getMoveFromToOrders()) {
            FileUtils.moveDirectory(moveFromToOrder.getFrom(), moveFromToOrder.getTo());
            if (movedDirEvent != null) {
                movedDirEvent.handle(moveFromToOrder.getDir());
            }
        }
    }

    public void setMovedDirListener(MovedDirEvent event) {
        movedDirEvent = event;
    }

    private Optional<List<File>> searchDirectoriesWithCriteria(SearchOriginCriteria searchOriginCriteria) {
        File path = new File(searchOriginCriteria.getOriginPath());
        File[] files = path.listFiles();
        if (files == null) {
            return Optional.empty();
        }
        List<File> dirs = Arrays.stream(files).filter(File::isDirectory).collect(toList());
        List<File> filteredDirs = getFilteredDirs(dirs, searchOriginCriteria.getFilter());
        return Optional.of(filteredDirs);
    }

    private List<File> getFilteredDirs(List<File> dirs, String filter) {
        return dirs.stream()
                .filter(dir -> isThereFileNameFilterRecursively(filter, dir))
                .collect(toList());
    }

    private boolean isThereFileNameFilterRecursively(String filter, File dir) {
        try {
            return Files.walk(dir.getAbsoluteFile().toPath())
                    .filter(Files::isDirectory)
                    .anyMatch(d -> isThereAfileMatchingThisNameInThisDir(filter, d));
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isThereAfileMatchingThisNameInThisDir(String filter, Path d) {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(d, filter);
            return stream.iterator().hasNext();
        } catch (IOException e) {
            return false;
        }
    }
}
