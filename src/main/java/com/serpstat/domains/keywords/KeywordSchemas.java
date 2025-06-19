package com.serpstat.domains.keywords;

import com.serpstat.domains.utils.SchemaUtils;

public class KeywordSchemas {
    public static final String GET_KEYWORDS_SCHEMA = SchemaUtils.loadSchema(
            KeywordSchemas.class, "/schemas/keywords/get_keywords.json"
    );
    public static final String GET_RELATED_KEYWORDS_SCHEMA = SchemaUtils.loadSchema(
            KeywordSchemas.class, "/schemas/keywords/get_related_keywords.json"
    );
}
