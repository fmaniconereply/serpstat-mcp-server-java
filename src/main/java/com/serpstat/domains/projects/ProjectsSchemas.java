package com.serpstat.domains.projects;

import com.serpstat.domains.utils.SchemaUtils;

public class ProjectsSchemas {
    public static final String PROJECTS_LIST_SCHEMA = SchemaUtils.loadSchema(
        ProjectsSchemas.class, "/schemas/projects/projects_list.json"
    );
}