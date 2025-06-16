package com.serpstat.domains.keywords.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Keyword data model for getKeywords API response
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class Keyword {

    @JsonProperty("keyword")
    private String keyword;

    @JsonProperty("cost")
    private Double cost;

    @JsonProperty("concurrency")
    private Integer concurrency;

    @JsonProperty("found_results")
    private Long foundResults;

    @JsonProperty("region_queries_count")
    private Long regionQueriesCount;

    @JsonProperty("region_queries_count_wide")
    private Long regionQueriesCountWide;

    @JsonProperty("types")
    private List<String> types;

    @JsonProperty("geo_names")
    private List<Object> geoNames;

    @JsonProperty("social_domains")
    private List<String> socialDomains;

    @JsonProperty("right_spelling")
    private String rightSpelling;

    @JsonProperty("lang")
    private String language;

    @JsonProperty("keyword_length")
    private Integer keywordLength;

    @JsonProperty("difficulty")
    private Integer difficulty;

    @JsonProperty("intents")
    private List<String> intents;
}
