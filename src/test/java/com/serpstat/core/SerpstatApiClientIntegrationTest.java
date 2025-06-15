package com.serpstat.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for SerpstatApiClient
 * Tests end-to-end functionality with realistic scenarios
 */
@DisplayName("SerpstatApiClient Integration Tests")
class SerpstatApiClientIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8092))
        .build();

    private SerpstatApiClient client;
    private static final String TEST_TOKEN = "test-integration-token";

    @BeforeEach
    void setUp() {
        client = new SerpstatApiClient(TEST_TOKEN);
        wireMock.resetAll();
    }

    @Test
    @DisplayName("Should make successful end-to-end API call")
    void shouldMakeSuccessfulApiCall() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {
                    "data": [
                        {
                            "domain": "example.com",
                            "keywords": 1500,
                            "traffic": 25000
                        }
                    ],
                    "summary_info": {
                        "page": 1,
                        "page_total": 1,
                        "count": 1,
                        "total": 1
                    }
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(successResponse)));

        Map<String, Object> params = Map.of(
            "domain", "example.com",
            "size", 100
        );

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatDomainProcedure.getInfo", params);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMethod()).isEqualTo("SerpstatDomainProcedure.getInfo");
        assertThat(response.getRequestParams()).containsEntry("domain", "example.com");
        assertThat(response.getTimestamp()).isPositive();

        JsonNode result = response.getResult();
        assertThat(result.path("data").isArray()).isTrue();
        assertThat(result.path("data").get(0).path("domain").asText()).isEqualTo("example.com");
        assertThat(result.path("data").get(0).path("keywords").asInt()).isEqualTo(1500);
        assertThat(result.path("summary_info").path("total").asInt()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle realistic Serpstat JSON-RPC response")
    void shouldHandleRealJsonRpcResponse() throws Exception {
        // Given - Realistic Serpstat API response
        String realisticResponse = """
            {
                "id": "123",
                "result": {
                    "data": [
                        {
                            "domain": "serpstat.com",
                            "keywords": 125847,
                            "traff": 1547892,
                            "cost": 2156743.5,
                            "concurrency": 45.2,
                            "ads": 15234,
                            "ads_traff": 89456,
                            "ads_cost": 154789.25
                        }
                    ],
                    "summary_info": {
                        "left_lines": 999875,
                        "page": 1,
                        "count": 1,
                        "total": 1,
                        "sort": null,
                        "order": null
                    }
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(realisticResponse)));

        Map<String, Object> params = Map.of(
            "domain", "serpstat.com",
            "size", 1
        );

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatDomainProcedure.getInfo", params);

        // Then
        JsonNode data = response.getResult().path("data").get(0);
        assertThat(data.path("domain").asText()).isEqualTo("serpstat.com");
        assertThat(data.path("keywords").asInt()).isEqualTo(125847);
        assertThat(data.path("traff").asInt()).isEqualTo(1547892);
        assertThat(data.path("cost").asDouble()).isCloseTo(2156743.5, within(0.1));
        assertThat(data.path("concurrency").asDouble()).isCloseTo(45.2, within(0.1));

        JsonNode summaryInfo = response.getResult().path("summary_info");
        assertThat(summaryInfo.path("left_lines").asInt()).isEqualTo(999875);
        assertThat(summaryInfo.path("page").asInt()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle multiple API methods")
    void shouldHandleMultipleApiMethods() throws Exception {
        // Given
        String domainResponse = """
            {
                "id": 1,
                "result": {
                    "data": [{"domain": "example.com", "keywords": 1500}]
                }
            }
            """;

        String keywordResponse = """
            {
                "id": 2,
                "result": {
                    "data": [{"keyword": "test keyword", "volume": 5000}]
                }
            }
            """;

        String creditsResponse = """
            {
                "id": 3,
                "result": {
                    "summary_info": {"lines_left": 95000, "lines_limit": 100000}
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .withRequestBody(matchingJsonPath("$.method", equalTo("SerpstatDomainProcedure.getInfo")))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(domainResponse)));

        wireMock.stubFor(post(anyUrl())
            .withRequestBody(matchingJsonPath("$.method", equalTo("SerpstatKeywordProcedure.getInfo")))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(keywordResponse)));

        wireMock.stubFor(post(anyUrl())
            .withRequestBody(matchingJsonPath("$.method", equalTo("SerpstatStatsProcedure.getStats")))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(creditsResponse)));

        // When
        SerpstatApiResponse domainResult = client.callMethod("SerpstatDomainProcedure.getInfo", 
            Map.of("domain", "example.com"));
        
        SerpstatApiResponse keywordResult = client.callMethod("SerpstatKeywordProcedure.getInfo", 
            Map.of("keyword", "test keyword"));
        
        SerpstatApiResponse creditsResult = client.callMethod("SerpstatStatsProcedure.getStats", 
            Map.of());

        // Then
        assertThat(domainResult.getResult().path("data").get(0).path("domain").asText())
            .isEqualTo("example.com");
        assertThat(keywordResult.getResult().path("data").get(0).path("keyword").asText())
            .isEqualTo("test keyword");
        assertThat(creditsResult.getResult().path("summary_info").path("lines_left").asInt())
            .isEqualTo(95000);
    }

    @Test
    @DisplayName("Should work with complex parameter structures")
    void shouldWorkWithComplexParameters() throws Exception {
        // Given
        String complexResponse = """
            {
                "id": 1,
                "result": {
                    "data": [
                        {
                            "keyword": "seo tools",
                            "volume": 12000,
                            "difficulty": 85,
                            "serp": [
                                {"position": 1, "domain": "ahrefs.com"},
                                {"position": 2, "domain": "semrush.com"}
                            ]
                        }
                    ]
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(complexResponse)));

        Map<String, Object> complexParams = Map.of(
            "keyword", "seo tools",
            "se", "g_us",
            "filters", Map.of(
                "volume_from", 1000,
                "volume_to", 50000,
                "difficulty_from", 70
            ),
            "sort", Map.of(
                "field", "volume",
                "order", "desc"
            ),
            "regions", List.of("us", "uk", "ca"),
            "page", 1,
            "size", 100
        );

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatKeywordProcedure.getInfo", complexParams);

        // Then
        JsonNode data = response.getResult().path("data").get(0);
        assertThat(data.path("keyword").asText()).isEqualTo("seo tools");
        assertThat(data.path("volume").asInt()).isEqualTo(12000);
        assertThat(data.path("serp").isArray()).isTrue();
        assertThat(data.path("serp").get(0).path("domain").asText()).isEqualTo("ahrefs.com");

        // Verify complex parameters were sent correctly
        wireMock.verify(postRequestedFor(anyUrl())
            .withRequestBody(matchingJsonPath("$.params.keyword", equalTo("seo tools")))
            .withRequestBody(matchingJsonPath("$.params.filters.volume_from", equalTo("1000")))
            .withRequestBody(matchingJsonPath("$.params.sort.field", equalTo("volume")))
            .withRequestBody(matchingJsonPath("$.params.regions[0]", equalTo("us"))));
    }

    @Test
    @DisplayName("Should handle Unicode content")
    void shouldHandleUnicodeContent() throws Exception {
        // Given
        String unicodeResponse = """
            {
                "id": 1,
                "result": {
                    "data": [
                        {
                            "keyword": "كلمة مفتاحية",
                            "domain": "موقع.com",
                            "title": "العنوان الرئيسي",
                            "description": "وصف المحتوى باللغة العربية"
                        },
                        {
                            "keyword": "关键词",
                            "domain": "网站.com",
                            "title": "主标题",
                            "description": "中文内容描述"
                        },
                        {
                            "keyword": "ключевое слово",
                            "domain": "сайт.рф",
                            "title": "Основной заголовок",
                            "description": "Описание на русском языке"
                        }
                    ]
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody(unicodeResponse)));

        Map<String, Object> params = Map.of(
            "keyword", "العربية 中文 русский",
            "domain", "тест.com"
        );

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatKeywordProcedure.getInfo", params);

        // Then
        JsonNode data = response.getResult().path("data");
        
        // Arabic
        assertThat(data.get(0).path("keyword").asText()).isEqualTo("كلمة مفتاحية");
        assertThat(data.get(0).path("title").asText()).isEqualTo("العنوان الرئيسي");
        
        // Chinese
        assertThat(data.get(1).path("keyword").asText()).isEqualTo("关键词");
        assertThat(data.get(1).path("title").asText()).isEqualTo("主标题");
        
        // Russian
        assertThat(data.get(2).path("keyword").asText()).isEqualTo("ключевое слово");
        assertThat(data.get(2).path("domain").asText()).isEqualTo("сайт.рф");
    }

    @Test
    @DisplayName("Should handle typical domain analysis call")
    void shouldHandleTypicalDomainAnalysisCall() throws Exception {
        // Given
        String domainAnalysisResponse = """
            {
                "id": 1,
                "result": {
                    "data": [
                        {
                            "domain": "example.com",
                            "keywords": 15847,
                            "traff": 254789,
                            "cost": 125478.50,
                            "concurrency": 38.5,
                            "ads": 2547,
                            "ads_traff": 15478,
                            "ads_cost": 8547.25,
                            "region": "us"
                        }
                    ],
                    "summary_info": {
                        "left_lines": 999456,
                        "page": 1,
                        "count": 1,
                        "total": 1
                    }
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(domainAnalysisResponse)));

        Map<String, Object> params = Map.of(
            "domain", "example.com",
            "se", "g_us",
            "size", 1
        );

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatDomainProcedure.getInfo", params);

        // Then
        JsonNode domainData = response.getResult().path("data").get(0);
        assertThat(domainData.path("domain").asText()).isEqualTo("example.com");
        assertThat(domainData.path("keywords").asInt()).isEqualTo(15847);
        assertThat(domainData.path("traff").asInt()).isEqualTo(254789);
        assertThat(domainData.path("cost").asDouble()).isCloseTo(125478.50, within(0.01));
        assertThat(domainData.path("concurrency").asDouble()).isCloseTo(38.5, within(0.1));
    }

    @Test
    @DisplayName("Should handle typical keyword analysis call")
    void shouldHandleTypicalKeywordAnalysisCall() throws Exception {
        // Given
        String keywordAnalysisResponse = """
            {
                "id": 1,
                "result": {
                    "data": [
                        {
                            "keyword": "seo audit",
                            "volume": 8500,
                            "difficulty": 75,
                            "cost": 12.50,
                            "competition": 0.85,
                            "results": 145000000,
                            "serp": [
                                {
                                    "position": 1,
                                    "domain": "ahrefs.com",
                                    "url": "https://ahrefs.com/seo-audit",
                                    "title": "Free SEO Audit Tool"
                                }
                            ]
                        }
                    ],
                    "summary_info": {
                        "page": 1,
                        "page_total": 1,
                        "count": 1,
                        "total": 1
                    }
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(keywordAnalysisResponse)));

        Map<String, Object> params = Map.of(
            "keyword", "seo audit",
            "se", "g_us",
            "size", 1
        );

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatKeywordProcedure.getInfo", params);

        // Then
        JsonNode keywordData = response.getResult().path("data").get(0);
        assertThat(keywordData.path("keyword").asText()).isEqualTo("seo audit");
        assertThat(keywordData.path("volume").asInt()).isEqualTo(8500);
        assertThat(keywordData.path("difficulty").asInt()).isEqualTo(75);
        assertThat(keywordData.path("serp").get(0).path("domain").asText()).isEqualTo("ahrefs.com");
    }

    @Test
    @DisplayName("Should handle API stats call")
    void shouldHandleApiStatsCall() throws Exception {
        // Given
        String statsResponse = """
            {
                "id": 1,
                "result": {
                    "summary_info": {
                        "lines_left": 85000,
                        "lines_limit": 100000,
                        "lines_used": 15000,
                        "account_type": "Team",
                        "valid_until": "2025-12-31"
                    }
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(statsResponse)));

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatStatsProcedure.getStats", Map.of());

        // Then
        JsonNode stats = response.getResult().path("summary_info");
        assertThat(stats.path("lines_left").asInt()).isEqualTo(85000);
        assertThat(stats.path("lines_limit").asInt()).isEqualTo(100000);
        assertThat(stats.path("lines_used").asInt()).isEqualTo(15000);
        assertThat(stats.path("account_type").asText()).isEqualTo("Team");
        assertThat(stats.path("valid_until").asText()).isEqualTo("2025-12-31");
    }
}
