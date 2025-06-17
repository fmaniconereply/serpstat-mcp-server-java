package com.serpstat.domains.keywords.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Related keyword data model for getRelatedKeywords API response
 * Each keyword in the response costs 1 API credit.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class RelatedKeyword {

    @JsonProperty("keyword")
    private String keyword;

    @JsonProperty("region_queries_count")
    private Long regionQueriesCount;

    @JsonProperty("cost")
    private Double cost;

    @JsonProperty("concurrency")
    private Integer concurrency;

    @JsonProperty("geo_names")
    private List<Object> geoNames;

    @JsonProperty("types")
    private List<String> types;

    @JsonProperty("right_spelling")
    private String rightSpelling;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("difficulty")
    private Integer difficulty;

    @JsonProperty("intents")
    private List<String> intents;
}