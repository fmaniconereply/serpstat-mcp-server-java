package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domain URL data model from SerpstatDomainProcedure.getDomainUrls
 * Each URL in response costs 1 API credit.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class DomainUrl {

    @JsonProperty("url")
    private String url;

    @JsonProperty("keywords")
    private Integer keywords;
}