package com.serpstat.domains.competitors;

import com.serpstat.domains.utils.SchemaUtils;

public class CompetitorsSchemas {
    public static final String COMPETITORS_SCHEMA = SchemaUtils.loadSchema(
        CompetitorsSchemas.class, "/schemas/competitors/get_competitors.json"
    );
}

