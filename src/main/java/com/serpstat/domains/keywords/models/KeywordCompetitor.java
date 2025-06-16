package com.serpstat.domains.keywords.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response model for keyword competitor data from SerpstatKeywordProcedure.getCompetitors
 * Each competitor in response costs 1 API credit.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class KeywordCompetitor {

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("visible")
    private Double visibility;

    @JsonProperty("keywords")
    private Integer keywordsCount;

    @JsonProperty("traff")
    private Long estimatedTraffic;

    @JsonProperty("visible_dynamic")
    private Double visibilityDynamic;

    @JsonProperty("keywords_dynamic")
    private Integer keywordsDynamic;

    @JsonProperty("traff_dynamic")
    private Long trafficDynamic;

    @JsonProperty("ads_dynamic")
    private Integer adsDynamic;

    @JsonProperty("new_keywords")
    private Integer newKeywords;

    @JsonProperty("out_keywords")
    private Integer outKeywords;

    @JsonProperty("rised_keywords")
    private Integer risedKeywords;

    @JsonProperty("down_keywords")
    private Integer downKeywords;

    @JsonProperty("ad_keywords")
    private Integer adKeywords;

    @JsonProperty("ads")
    private Integer ads;

    @JsonProperty("intersected")
    private Integer intersectedKeywords;

    @JsonProperty("relevance")
    private Double relevance;

    @JsonProperty("our_relevance")
    private Double ourRelevance;
}

