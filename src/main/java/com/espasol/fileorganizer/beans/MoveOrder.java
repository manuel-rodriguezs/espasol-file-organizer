package com.espasol.fileorganizer.beans;

import lombok.Builder;
import lombok.Value;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Value
@Builder
public class MoveOrder {
    List<String> dirsToMove;
    String originPath;
    String destinationPath;

    public List<MoveFromToOrder> getMoveFromToOrders() {
        return dirsToMove.stream().map(dir ->
                MoveFromToOrder.builder()
                        .from(new File(originPath + dir))
                        .to(new File(destinationPath + dir))
                        .build()
        ).collect(toList());
    }
}
