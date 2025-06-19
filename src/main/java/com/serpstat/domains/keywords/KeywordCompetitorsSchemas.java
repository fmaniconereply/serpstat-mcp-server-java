package com.serpstat.domains.keywords;

import com.serpstat.domains.utils.SchemaUtils;

public class KeywordCompetitorsSchemas {
    public static final String KEYWORD_COMPETITORS_SCHEMA = SchemaUtils.loadSchema(
            KeywordCompetitorsSchemas.class, "/schemas/keywords/keyword_competitors.json"
    );
}