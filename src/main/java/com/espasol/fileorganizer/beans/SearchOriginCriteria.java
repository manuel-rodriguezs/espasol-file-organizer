package com.espasol.fileorganizer.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchOriginCriteria {
    String originPath;
    String filter;
}
