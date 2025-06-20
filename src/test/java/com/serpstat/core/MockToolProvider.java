package com.serpstat.core;

import io.modelcontextprotocol.server.McpServerFeatures;
import java.util.List;

public class MockToolProvider implements ToolProvider {
    private final String domainName;
    private final List<McpServerFeatures.SyncToolSpecification> tools;

    public MockToolProvider(String domainName, List<McpServerFeatures.SyncToolSpecification> tools) {
        this.domainName = domainName;
        this.tools = tools;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return tools;
    }
}
