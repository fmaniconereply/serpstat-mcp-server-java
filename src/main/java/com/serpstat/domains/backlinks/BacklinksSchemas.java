package com.serpstat.domains.backlinks;

import com.serpstat.domains.utils.SchemaUtils;

public class BacklinksSchemas {
    public static final String BACKLINKS_SUMMARY_SCHEMA = SchemaUtils.loadSchema(
        BacklinksSchemas.class, "/schemas/backlinks/backlinks_summary.json"
    );
}

