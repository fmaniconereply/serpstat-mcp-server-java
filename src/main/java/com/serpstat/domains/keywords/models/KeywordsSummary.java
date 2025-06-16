package com.serpstat.domains.keywords.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class KeywordsSummary {

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("left_lines")
    private Long leftLines;
}
