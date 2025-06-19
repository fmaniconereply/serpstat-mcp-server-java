package com.serpstat.domains.keywords;

import com.serpstat.core.*;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.util.List;
import java.util.Map;

/**
 * Tools for keyword research and analysis
 */
public class KeywordTools extends BaseToolHandler implements ToolProvider {

    public KeywordTools(SerpstatApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public String getDomainName() {
        return "Keyword Research & Analysis";
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
                createGetKeywordsTool(),
                createKeywordCompetitorsTool(),
                createGetRelatedKeywordsTool()
        );
    }

    /**
     * Create get keywords tool specification
     */
    private McpServerFeatures.SyncToolSpecification createGetKeywordsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "get_keywords",
                        "Research related keywords and get search volume, CPC, competition data. Shows organic keywords associated with the seed keyword that domains rank for in Google's top-100 results. Includes volume, cost per click, difficulty, and SERP features analysis.",
                        KeywordSchemas.GET_KEYWORDS_SCHEMA
                ),
                this::handleGetKeywords
        );
    }

    /**
     * Create keyword competitors analysis tool specification
     */
    private McpServerFeatures.SyncToolSpecification createKeywordCompetitorsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "keyword_competitors",
                        "Analyze domains competing for a specific keyword in Google search results. Returns top-20 competitors with SEO metrics, traffic estimates, relevance scores and market dynamics. Each competitor costs 1 API credit, minimum 1 credit per request.",
                        KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA
                ),
                this::handleKeywordCompetitors
        );
    }

    /**
     * Create get related keywords tool specification
     */
    private McpServerFeatures.SyncToolSpecification createGetRelatedKeywordsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "get_related_keywords",
                        "Find semantically related keywords to expand content strategy. Returns keywords with search volume, cost per click, connection strength, difficulty scores and comprehensive semantic analysis. Each keyword costs 1 API credit, minimum 1 credit per request even for empty results. Perfect for content expansion and semantic SEO.",
                        KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA
                ),
                this::handleGetRelatedKeywords
        );
    }

    /**
     * Handle get related keywords request
     */
    private CallToolResult handleGetRelatedKeywords(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getRelatedKeywords", (args) -> {
            // Validation
            RelatedKeywordsValidator.validateRelatedKeywordsRequest(args);

            // Log request details
            String keyword = (String) args.get("keyword");
            String searchEngine = (String) args.get("se");
            Integer page = (Integer) args.getOrDefault("page", 1);
            Integer size = (Integer) args.getOrDefault("size", 100);
            Boolean withIntents = (Boolean) args.getOrDefault("withIntents", false);

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("KeywordTools")
                            .data(String.format("Researching related keywords for '%s' in %s (page %d, size %d, intents: %s)",
                                    keyword, searchEngine, page, size, withIntents))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatKeywordProcedure.getRelatedKeywords", args);
        });
    }


    /**
     * Handle keyword competitors analysis request
     */
    private CallToolResult handleKeywordCompetitors(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getKeywordCompetitors", (args) -> {
            // Validation
            KeywordCompetitorsValidator.validateKeywordCompetitorsRequest(args);

            // Log request details
            String keyword = (String) args.get("keyword");
            String searchEngine = (String) args.get("se");
            Integer size = (Integer) args.getOrDefault("size", 20);

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("KeywordTools")
                            .data(String.format("Analyzing competitors for keyword '%s' in %s (size: %d)",
                                    keyword, searchEngine, size))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatKeywordProcedure.getCompetitors", args);
        });
    }

    /**
     * Handle get keywords request
     */
    private CallToolResult handleGetKeywords(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getKeywords", (args) -> {
            // Validation
            KeywordValidator.validateGetKeywordsRequest(args);

            // Log request details
            String keyword = (String) args.get("keyword");
            String searchEngine = (String) args.get("se");
            Integer page = (Integer) args.getOrDefault("page", 1);
            Integer size = (Integer) args.getOrDefault("size", 100);
            Boolean withIntents = (Boolean) args.getOrDefault("withIntents", false);

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("KeywordTools")
                            .data(String.format("Researching keywords for '%s' in %s (page %d, size %d, intents: %s)",
                                    keyword, searchEngine, page, size, withIntents))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatKeywordProcedure.getKeywords", args);
        });
    }

    @Override
    protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
        String method = response.getMethod();

        return switch (method) {
            case "SerpstatKeywordProcedure.getCompetitors" ->
                    KeywordCompetitorsResponseFormatter.format(response, arguments, objectMapper);
            case "SerpstatKeywordProcedure.getRelatedKeywords" ->
                    RelatedKeywordsResponseFormatter.format(response, arguments, objectMapper);
            default ->
                    KeywordResponseFormatter.format(response, arguments, objectMapper);
        };
    }
}