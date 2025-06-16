package com.serpstat.core;

/**
 * Testable version of SerpstatApiClient that allows overriding the API URL for use with WireMock in tests.
 */
public class TestableSerpstatApiClient extends SerpstatApiClient {
    private final String apiUrl;

    public TestableSerpstatApiClient(String token, String apiUrl) {
        super(token);
        this.apiUrl = apiUrl;
    }

    @Override
    protected String getApiUrl() {
        return apiUrl;
    }
}
