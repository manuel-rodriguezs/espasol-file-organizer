package com.espasol.fileorganizer.beans;

import lombok.Builder;
import lombok.Value;

import java.io.File;

@Value
@Builder
public class MoveFromToInfo {
    File from;
    File to;
}
