# SerpstatApiClient Test Implementation Plan

## Overview
This document outlines the comprehensive test plan for the `SerpstatApiClient` class as part of Phase 1 of the testing strategy. The tests focus on **Happy Path** scenarios to ensure core functionality works correctly.

## Test Structure
```
src/test/java/com/serpstat/core/
├── SerpstatApiClientTest.java                    # Main test class
├── SerpstatApiClientConstructorTest.java         # Constructor tests
├── SerpstatApiClientHttpTest.java                # HTTP request/response tests
├── SerpstatApiClientCacheTest.java               # Caching mechanism tests
├── SerpstatApiClientRateLimitingTest.java        # Rate limiting tests
└── SerpstatApiClientIntegrationTest.java         # Integration tests with WireMock
```

## Detailed Test Coverage

### 1. Constructor Tests (`SerpstatApiClientConstructorTest.java`)
**Total Tests: 6**

#### Happy Path Tests (4):
- `shouldCreateClientWithValidToken()` - Creates client with valid API token
- `shouldInitializeDefaultConfiguration()` - Verifies default cache, rate limiter, timeout settings
- `shouldAcceptLongToken()` - Handles long API tokens
- `shouldAcceptTokenWithSpecialCharacters()` - Handles tokens with special characters

#### Edge Cases (2):
- `shouldCreateClientWithEmptyToken()` - Creates client with empty token (constructor doesn't validate)
- `shouldCreateClientWithNullToken()` - Creates client with null token (constructor doesn't validate)

### 2. HTTP Request/Response Tests (`SerpstatApiClientHttpTest.java`)
**Total Tests: 12**

#### Request Formation Tests (4):
- `shouldFormatJsonRpcRequestCorrectly()` - Verifies JSON-RPC 2.0 request structure
- `shouldIncludeAllRequiredHeaders()` - Checks Content-Type and Authorization headers
- `shouldBuildCorrectUrlWithToken()` - Verifies URL construction with token parameter
- `shouldSerializeParametersCorrectly()` - Tests parameter serialization to JSON

#### Response Handling Tests (6):
- `shouldParseSuccessfulResponse()` - Parses valid JSON-RPC response
- `shouldReturnSerpstatApiResponseObject()` - Returns proper response wrapper
- `shouldHandleEmptyResultResponse()` - Handles responses with empty result
- `shouldHandleComplexJsonResponse()` - Parses complex nested JSON structures
- `shouldPreserveRequestMetadata()` - Maintains method and params in response
- `shouldSetTimestampOnResponse()` - Verifies timestamp is set correctly

#### Error Handling Tests (2):
- `shouldThrowExceptionOnHttpError()` - Handles HTTP 4xx/5xx errors
- `shouldThrowExceptionOnApiError()` - Handles Serpstat API errors in response

### 3. Caching Tests (`SerpstatApiClientCacheTest.java`)
**Total Tests: 8**

#### Cache Hit Tests (3):
- `shouldReturnCachedResponse()` - Returns cached result for same request
- `shouldUseCacheKeyCorrectly()` - Uses method + params as cache key
- `shouldNotMakeHttpRequestOnCacheHit()` - Avoids HTTP calls for cached data

#### Cache Miss Tests (3):
- `shouldMakeHttpRequestOnCacheMiss()` - Makes HTTP request for new requests
- `shouldCacheResponseAfterHttpRequest()` - Stores response in cache after API call
- `shouldHandleDifferentParametersCorrectly()` - Different params create different cache entries

#### Cache Configuration Tests (2):
- `shouldRespectCacheSize()` - Verifies cache size limit (1000 entries)
- `shouldRespectCacheExpiration()` - Verifies cache expiration (60 minutes)

### 4. Rate Limiting Tests (`SerpstatApiClientRateLimitingTest.java`)
**Total Tests: 6**

#### Rate Limiter Integration Tests (3):
- `shouldCallRateLimiterBeforeRequest()` - Verifies rate limiter is called
- `shouldEnforceRateLimit()` - Tests rate limiting enforcement (10 req/sec)
- `shouldResetRateWindowCorrectly()` - Tests rate window reset behavior

#### Rate Limiting Behavior Tests (3):
- `shouldAllowRequestsWithinLimit()` - Allows requests within rate limit
- `shouldDelayRequestsExceedingLimit()` - Delays requests that exceed limit
- `shouldHandleRateLimitInterruption()` - Handles InterruptedException properly

### 5. Integration Tests (`SerpstatApiClientIntegrationTest.java`)
**Total Tests: 8**

#### WireMock Integration Tests (5):
- `shouldMakeSuccessfulApiCall()` - End-to-end successful API call
- `shouldHandleRealJsonRpcResponse()` - Tests with realistic Serpstat response
- `shouldHandleMultipleApiMethods()` - Tests different API methods
- `shouldWorkWithComplexParameters()` - Tests complex parameter structures
- `shouldHandleUnicodeContent()` - Tests Unicode characters in responses

#### Real-world Scenario Tests (3):
- `shouldHandleTypicalDomainAnalysisCall()` - Simulates domain analysis API call
- `shouldHandleTypicalKeywordAnalysisCall()` - Simulates keyword analysis API call
- `shouldHandleApiStatsCall()` - Simulates API stats call

### 6. Main Test Class (`SerpstatApiClientTest.java`)
**Total Tests: 10**

#### Core Functionality Tests (6):
- `shouldExecuteBasicApiCall()` - Basic API method execution
- `shouldReturnCorrectResponseType()` - Returns SerpstatApiResponse
- `shouldHandleNullParameters()` - Handles null parameter map
- `shouldHandleEmptyParameters()` - Handles empty parameter map
- `shouldMaintainRequestOrder()` - Tests request/response correlation
- `shouldHandleTimeoutCorrectly()` - Tests timeout configuration (30 seconds)

#### Performance Tests (2):
- `shouldHandleConcurrentRequests()` - Tests thread safety
- `shouldPerformWithinExpectedTime()` - Performance benchmark

#### Configuration Tests (2):
- `shouldUseCorrectApiUrl()` - Verifies API URL (https://api.serpstat.com/v4)
- `shouldConfigureHttpClientCorrectly()` - Verifies HttpClient configuration

## Test Data and Mocking Strategy

### Mock Response Examples:
```json
// Successful response
{
  "id": "123",
  "result": {
    "data": [{"domain": "example.com", "keywords": 1500}],
    "summary_info": {"total": 1, "page": 1}
  }
}

// Error response
{
  "id": "123",
  "error": {
    "code": -32000,
    "message": "Invalid API token"
  }
}
```

### Test Dependencies:
- **JUnit 5** - Test framework
- **Mockito** - Mocking framework
- **WireMock** - HTTP service mocking
- **AssertJ** - Fluent assertions

### Test Configuration:
- All tests use `@ExtendWith(MockitoExtension.class)`
- WireMock tests use `@RegisterExtension WireMockExtension`
- Test API tokens: `"test-api-token-123"`, `"valid-token"`
- Test timeouts: Short timeouts for fast test execution

## Implementation Priority:
1. **Constructor Tests** - Foundation tests
2. **HTTP Tests** - Core functionality
3. **Cache Tests** - Performance optimization
4. **Rate Limiting Tests** - API compliance
5. **Integration Tests** - End-to-end validation
6. **Main Test Class** - Overall functionality

## Success Criteria:
- ✅ All 50 tests pass
- ✅ 100% line coverage for SerpstatApiClient
- ✅ Tests run in under 30 seconds
- ✅ No flaky tests
- ✅ Clear test names and documentation
