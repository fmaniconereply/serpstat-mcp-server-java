package com.serpstat.domains.domain;

import com.serpstat.domains.utils.SchemaUtils;

public class DomainSchemas {
    public static final String DOMAINS_INFO_SCHEMA = SchemaUtils.loadSchema(
            DomainSchemas.class, "/schemas/domain/domains_info.json"
    );
    public static final String REGIONS_COUNT_SCHEMA = SchemaUtils.loadSchema(
            DomainSchemas.class, "/schemas/domain/regions_count.json"
    );
    public static final String DOMAIN_KEYWORDS_SCHEMA = SchemaUtils.loadSchema(
            DomainSchemas.class, "/schemas/domain/domain_keywords.json"
    );
    public static final String DOMAIN_URLS_SCHEMA = SchemaUtils.loadSchema(
            DomainSchemas.class, "/schemas/domain/domain_urls.json"
    );

    public static final String DOMAINS_UNIQ_KEYWORDS_SCHEMA = SchemaUtils.loadSchema(
            DomainSchemas.class, "/schemas/domain/domains_uniq_keywords.json"
    );
}