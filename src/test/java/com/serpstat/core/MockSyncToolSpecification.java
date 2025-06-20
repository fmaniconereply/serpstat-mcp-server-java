package com.serpstat.core;

public class MockSyncToolSpecification implements McpServerFeatures.SyncToolSpecification {
    private final String name;

    public MockSyncToolSpecification(String name) {
        this.name = name;
    }

    @Override
    public Tool tool() {
        return new Tool() {
            @Override
            public String name() {
                return name;
            }
        };
    }
}
