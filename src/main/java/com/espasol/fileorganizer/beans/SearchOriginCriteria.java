package com.espasol.fileorganizer.beans;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SearchOriginCriteria {
    String originPath;
    String filter;
}
