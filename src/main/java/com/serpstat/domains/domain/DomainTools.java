package com.serpstat.domains.domain;


import com.serpstat.core.*;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.util.List;
import java.util.Map;

/**
 * Tools for working with domains
 */
public class DomainTools extends BaseToolHandler implements ToolProvider {

    public DomainTools(SerpstatApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public String getDomainName() {
        return "Domain Analysis";
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
                createGetDomainsInfoTool(),
                createRegionsCountTool(),
                createDomainKeywordsTool(),
                createDomainUrlsTool(),
                createGetDomainsUniqKeywordsTool()
        );
    }

    /**
     * Create get domain info tool specification
     */
    private McpServerFeatures.SyncToolSpecification createGetDomainsInfoTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "get_domains_info",
                        "Get comprehensive domain information using Serpstat API. Returns visibility, keywords count, estimated traffic, dynamics and PPC data for multiple domains.",
                        DomainSchemas.DOMAINS_INFO_SCHEMA
                ),
                this::handleGetDomainsInfo
        );
    }

    /**
     * Create regions count tool specification
     */
    private McpServerFeatures.SyncToolSpecification createRegionsCountTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "domain_regions_count",
                        "Analyze domain keyword presence across all Google regional databases. Shows keyword count by country, regional performance comparison and international SEO insights. Start every complex domain analysis with this tool.",
                        DomainSchemas.REGIONS_COUNT_SCHEMA
                ),
                this::handleRegionsCount
        );
    }

    /**
     * Create get domains unique keywords tool specification
     */
    private McpServerFeatures.SyncToolSpecification createGetDomainsUniqKeywordsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "get_domains_uniq_keywords",
                        "Analyze keyword gaps between competitors - find keywords that your competitors rank for but you don't (or vice versa). Perfect for discovering missed opportunities and competitive advantages. Shows unique keywords from up to 2 domains that a third domain doesn't rank for. Essential for competitive SEO strategy and content gap analysis.",
                        DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA
                ),
                this::handleGetDomainsUniqKeywords
        );
    }

/**
 * Creates the specification for the domain keywords analysis tool.
 * <p>
 * Usage examples:
 * <ul>
 *     <li>Basic keyword analysis:<br>
 *         <pre>"Show keywords for nike.com domain in Google US"</pre>
 *     </li>
 *     <li>Analysis with filters:<br>
 *         <pre>"Find keywords for nike.com only in top-10 positions"</pre>
 *     </li>
 *     <li>Specific page analysis:<br>
 *         <pre>"Show keywords for URL "</pre>
 *     </li>
 *     <li>Analysis with exclusions:<br>
 *         <pre>"Find keywords for domain, but exclude words 'brand' and 'company'"</pre>
 *     </li>
 *     <li>Detailed analysis:<br>
 *         <pre>"Analyze domain keywords with high difficulty (difficulty &gt; 70)"</pre>
 *     </li>
 * </ul>
 *
 * @return the specification for the domain keywords analysis tool
 */
    private McpServerFeatures.SyncToolSpecification createDomainKeywordsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "domain_keywords",
                        "Get keywords that domain ranks for in Google search results. Includes position, traffic, difficulty analysis with comprehensive SEO insights and performance metrics.",
                        DomainSchemas.DOMAIN_KEYWORDS_SCHEMA
                ),
                this::handleDomainKeywords
        );
    }

    /**
     * Create domain URLs tool specification
     */
    private McpServerFeatures.SyncToolSpecification createDomainUrlsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "get_domain_urls",
                        "Get URLs within a domain and keyword count for each URL. Analyze URL structure, performance distribution, and identify top-performing pages. Each URL costs 1 API credit, minimum 1 credit per request.",
                        DomainSchemas.DOMAIN_URLS_SCHEMA
                ),
                this::handleDomainUrls
        );
    }

    private CallToolResult handleDomainKeywords(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getDomainKeywords", (args) -> {
            // Validation
            DomainValidator.validateDomainKeywordsRequest(args);

            // Log request details
            String domain = (String) args.get("domain");
            String searchEngine = (String) args.get("se");
            Integer page = (Integer) args.getOrDefault("page", 1);
            Integer size = (Integer) args.getOrDefault("size", 100);
            Boolean withSubdomains = (Boolean) args.getOrDefault("withSubdomains", false);

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("DomainTools")
                            .data(String.format("Analyzing keywords for %s in %s (page %d, size %d, subdomains: %s)",
                                    domain, searchEngine, page, size, withSubdomains))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatDomainProcedure.getDomainKeywords", args);
        });
    }
    /**
     * Handle regions count request
     */
    private CallToolResult handleRegionsCount(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getRegionsCount", (args) -> {
            // Validation
            DomainValidator.validateRegionsCountRequest(args);

            // Log request details
            String domain = (String) args.get("domain");
            String sort = (String) args.getOrDefault("sort", "keywords_count");
            String order = (String) args.getOrDefault("order", "desc");

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("DomainTools")
                            .data(String.format("Analyzing regional keywords for %s (sort: %s %s)",
                                    domain, sort, order))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatDomainProcedure.getRegionsCount", args);
        });
    }

    /*
     * Handle get domains info request
     */
    private CallToolResult handleGetDomainsInfo(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getDomainsInfo", (args) -> {
            // Validation
            DomainValidator.validateDomainsInfoRequest(args);

            // Log request details
            @SuppressWarnings("unchecked")
            List<String> domains = (List<String>) args.get("domains");
            String searchEngine = (String) args.getOrDefault("se", "g_us");

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("DomainTools")
                            .data(String.format("Processing %d domains for %s database",
                                    domains.size(), searchEngine))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatDomainProcedure.getDomainsInfo", args);
        });
    }

    /**
     * Handle domain URLs request
     */
    private CallToolResult handleDomainUrls(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getDomainUrls", (args) -> {
            // Validation
            DomainValidator.validateDomainUrlsRequest(args);

            // Log request details
            String domain = (String) args.get("domain");
            String searchEngine = (String) args.get("se");
            Integer page = (Integer) args.getOrDefault("page", 1);
            Integer size = (Integer) args.getOrDefault("size", 100);

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("DomainTools")
                            .data(String.format("Analyzing URLs for %s in %s (page %d, size %d)",
                                    domain, searchEngine, page, size))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatDomainProcedure.getDomainUrls", args);
        });
    }


    /**
     * Handle get domains unique keywords request
     */
    private CallToolResult handleGetDomainsUniqKeywords(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getDomainsUniqKeywords", (args) -> {
            // Validation
            DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(args);

            // Log request details
            @SuppressWarnings("unchecked")
            List<String> domains = (List<String>) args.get("domains");
            String minusDomain = (String) args.get("minusDomain");
            String searchEngine = (String) args.get("se");
            Integer page = (Integer) args.getOrDefault("page", 1);
            Integer size = (Integer) args.getOrDefault("size", 100);

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("DomainTools")
                            .data(String.format("Analyzing unique keywords for %s (excluding %s) in %s (page %d, size %d)",
                                    String.join(" and ", domains), minusDomain, searchEngine, page, size))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatDomainProcedure.getDomainsUniqKeywords", args);
        });
    }
    /*
     * Format response based on method
     */
    @Override
    protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
        String method = response.getMethod();

        return switch (method) {
            case "SerpstatDomainProcedure.getRegionsCount" ->
                    DomainResponseFormatter.formatRegionsCount(response, arguments, objectMapper);
            case "SerpstatDomainProcedure.getDomainKeywords" ->
                    DomainResponseFormatter.formatDomainKeywords(response, arguments, objectMapper);
            case "SerpstatDomainProcedure.getDomainUrls" ->
                    DomainUrlsResponseFormatter.format(response, arguments, objectMapper);
            case "SerpstatDomainProcedure.getDomainsUniqKeywords" ->
                    DomainUniqueKeywordsResponseFormatter.format(response, arguments, objectMapper);
            default ->
                // Default to existing format method for getDomainsInfo
                    DomainResponseFormatter.format(response, arguments, objectMapper);
        };
    }


}
