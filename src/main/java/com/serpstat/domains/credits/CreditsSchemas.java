package com.serpstat.domains.credits;

import com.serpstat.domains.utils.SchemaUtils;

public class CreditsSchemas {
    public static final String API_STATS_SCHEMA = SchemaUtils.loadSchema(
        CreditsSchemas.class, "/schemas/credits/api_stats.json"
    );
}