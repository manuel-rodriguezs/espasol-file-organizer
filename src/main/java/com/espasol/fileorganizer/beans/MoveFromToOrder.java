package com.espasol.fileorganizer.beans;

import lombok.Builder;
import lombok.Value;

import java.io.File;

@Value
@Builder
public class MoveFromToOrder {
    File from;
    File to;
}
