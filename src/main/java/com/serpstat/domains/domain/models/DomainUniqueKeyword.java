package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain unique keyword data model for getDomainsUniqKeywords API response.
 * Each keyword in the response costs 1 API credit.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class DomainUniqueKeyword {

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("subdomain")
    private String subdomain;

    @JsonProperty("keyword")
    private String keyword;

    @JsonProperty("keyword_length")
    private Integer keywordLength;

    @JsonProperty("url")
    private String url;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("date")
    private String date;

    @JsonProperty("types")
    private List<String> types;

    @JsonProperty("found_results")
    private Long foundResults;

    @JsonProperty("cost")
    private Double cost;

    @JsonProperty("concurrency")
    private Integer concurrency;

    @JsonProperty("region_queries_count")
    private Long regionQueriesCount;

    @JsonProperty("region_queries_count_wide")
    private Long regionQueriesCountWide;

    @JsonProperty("geo_names")
    private List<String> geoNames;

    @JsonProperty("traff")
    private Long traff;

    @JsonProperty("difficulty")
    private Integer difficulty;

    @JsonProperty("dynamic")
    private Integer dynamic;

    // Dynamic domain positions (e.g., "nike.com": 1, "adidas.com": 5)
    private Map<String, Integer> domainPositions = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Integer> getDomainPositions() {
        return domainPositions;
    }

    @JsonAnySetter
    public void setDomainPosition(String domainName, Integer position) {
        if (isDomainName(domainName)) {
            domainPositions.put(domainName, position);
        }
    }

    /**
     * Simple check if the property name looks like a domain name
     */
    private boolean isDomainName(String name) {
        return name != null && name.contains(".") && !name.startsWith("_") &&
                !List.of("domain", "subdomain", "keyword", "keyword_length", "url", "position",
                        "date", "types", "found_results", "cost", "concurrency",
                        "region_queries_count", "region_queries_count_wide", "geo_names",
                        "traff", "difficulty", "dynamic").contains(name);
    }
}