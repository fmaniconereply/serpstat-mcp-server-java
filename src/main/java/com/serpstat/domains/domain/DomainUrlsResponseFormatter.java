package com.serpstat.domains.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Formatter for domain URLs API responses
 */
public class DomainUrlsResponseFormatter {

    /**
     * Format getDomainUrls response
     */
    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        String domain = (String) arguments.get("domain");
        String searchEngine = (String) arguments.get("se");
        Integer page = (Integer) arguments.getOrDefault("page", 1);
        Integer size = (Integer) arguments.getOrDefault("size", 100);

        // Create a formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatDomainProcedure.getDomainUrls");
        formattedResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        formattedResponse.put("analyzed_domain", domain);
        formattedResponse.put("search_engine", searchEngine);
        formattedResponse.put("page", page);
        formattedResponse.put("page_size", size);

        // Process URLs data
        JsonNode dataArray = resultNode.get("data");
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.set("urls", dataArray);
            formattedResponse.put("urls_on_page", dataArray.size());

            // Calculate analytics
            ObjectNode analytics = mapper.createObjectNode();

            // Basic statistics
            long totalKeywords = 0;
            int maxKeywords = 0;
            int minKeywords = Integer.MAX_VALUE;
            String topUrl = "";
            int urlsWithKeywords = 0;

            // URL pattern analysis
            Map<String, Integer> protocolDistribution = new HashMap<>();
            Map<String, Integer> subdirectoryDistribution = new HashMap<>();
            Map<String, Integer> fileExtensionDistribution = new HashMap<>();

            // Keyword distribution analysis
            int highPerformingUrls = 0; // URLs with > 1000 keywords
            int mediumPerformingUrls = 0; // URLs with 100-1000 keywords
            int lowPerformingUrls = 0; // URLs with < 100 keywords

            // Process each URL
            for (JsonNode urlNode : dataArray) {
                int keywordsCount = urlNode.path("keywords").asInt(0);
                String url = urlNode.path("url").asText("");

                totalKeywords += keywordsCount;

                if (keywordsCount > 0) {
                    urlsWithKeywords++;

                    if (keywordsCount > maxKeywords) {
                        maxKeywords = keywordsCount;
                        topUrl = url;
                    }

                    if (keywordsCount < minKeywords) {
                        minKeywords = keywordsCount;
                    }

                    // Categorize by performance
                    if (keywordsCount > 1000) {
                        highPerformingUrls++;
                    } else if (keywordsCount >= 100) {
                        mediumPerformingUrls++;
                    } else {
                        lowPerformingUrls++;
                    }
                }

                // Analyze URL patterns
                if (!url.isEmpty()) {
                    // Protocol analysis
                    if (url.startsWith("https://")) {
                        protocolDistribution.put("https", protocolDistribution.getOrDefault("https", 0) + 1);
                    } else if (url.startsWith("http://")) {
                        protocolDistribution.put("http", protocolDistribution.getOrDefault("http", 0) + 1);
                    }

                    // Subdirectory analysis (get the first path segment)
                    try {
                        String path = url.substring(url.indexOf("://") + 3);
                        if (path.contains("/")) {
                            String firstSegment = path.substring(path.indexOf("/") + 1);
                            if (firstSegment.contains("/")) {
                                firstSegment = firstSegment.substring(0, firstSegment.indexOf("/"));
                            }
                            if (!firstSegment.isEmpty()) {
                                subdirectoryDistribution.put(firstSegment,
                                        subdirectoryDistribution.getOrDefault(firstSegment, 0) + 1);
                            }
                        }
                    } catch (Exception e) {
                        // Skip malformed URLs
                    }

                    // File extension analysis
                    if (url.contains(".")) {
                        String lastPart = url.substring(url.lastIndexOf("/") + 1);
                        if (lastPart.contains(".") && !lastPart.endsWith("/")) {
                            String extension = lastPart.substring(lastPart.lastIndexOf(".") + 1);
                            // Only track common web file extensions
                            if (extension.matches("^(html|htm|php|asp|aspx|jsp|xml|pdf|doc|docx)$")) {
                                fileExtensionDistribution.put(extension,
                                        fileExtensionDistribution.getOrDefault(extension, 0) + 1);
                            }
                        }
                    }
                }
            }

            // Summary statistics
            ObjectNode summary = mapper.createObjectNode();
            summary.put("total_keywords_across_urls", totalKeywords);
            summary.put("urls_with_keywords", urlsWithKeywords);
            summary.put("urls_without_keywords", dataArray.size() - urlsWithKeywords);
            summary.put("average_keywords_per_url", urlsWithKeywords > 0 ?
                    Math.round((double) totalKeywords / urlsWithKeywords * 100.0) / 100.0 : 0);

            if (urlsWithKeywords > 0) {
                summary.put("max_keywords_per_url", maxKeywords);
                summary.put("min_keywords_per_url", minKeywords == Integer.MAX_VALUE ? 0 : minKeywords);
                summary.put("top_performing_url", topUrl);
            }

            analytics.set("summary", summary);

            // Performance distribution
            ObjectNode performanceDistribution = mapper.createObjectNode();
            performanceDistribution.put("high_performing_urls", highPerformingUrls);
            performanceDistribution.put("medium_performing_urls", mediumPerformingUrls);
            performanceDistribution.put("low_performing_urls", lowPerformingUrls);

            if (!dataArray.isEmpty()) {
                performanceDistribution.put("high_performing_percentage",
                        Math.round((double) highPerformingUrls / dataArray.size() * 10000.0) / 100.0);
                performanceDistribution.put("medium_performing_percentage",
                        Math.round((double) mediumPerformingUrls / dataArray.size() * 10000.0) / 100.0);
                performanceDistribution.put("low_performing_percentage",
                        Math.round((double) lowPerformingUrls / dataArray.size() * 10000.0) / 100.0);
            }

            analytics.set("performance_distribution", performanceDistribution);

            // Protocol distribution
            ObjectNode protocolDist = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : protocolDistribution.entrySet()) {
                protocolDist.put(entry.getKey(), entry.getValue());
            }
            analytics.set("protocol_distribution", protocolDist);

            // Top subdirectories
            ObjectNode topSubdirectories = mapper.createObjectNode();
            subdirectoryDistribution.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> topSubdirectories.put(entry.getKey(), entry.getValue()));
            analytics.set("top_subdirectories", topSubdirectories);

            // File extensions
            ObjectNode extensions = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : fileExtensionDistribution.entrySet()) {
                extensions.put(entry.getKey(), entry.getValue());
            }
            analytics.set("file_extensions", extensions);

            // URL structure insights
            ObjectNode insights = mapper.createObjectNode();

            if (dataArray.isEmpty()) {
                insights.put("status", "NO_URLS");
                insights.put("message", "No URLs found for this domain");
                insights.put("recommendation", "Check domain spelling or try different search engine");
            } else if (urlsWithKeywords == 0) {
                insights.put("status", "NO_RANKING_URLS");
                insights.put("message", "Domain has URLs but none are ranking for keywords");
                insights.put("recommendation", "Improve content quality and SEO optimization");
            } else {
                double keywordDistribution = (double) urlsWithKeywords / dataArray.size() * 100.0;

                if (keywordDistribution > 80) {
                    insights.put("status", "EXCELLENT_DISTRIBUTION");
                    insights.put("message", String.format("Excellent keyword distribution: %.1f%% of URLs ranking", keywordDistribution));
                    insights.put("recommendation", "Continue current SEO strategy and expand content");
                } else if (keywordDistribution > 60) {
                    insights.put("status", "GOOD_DISTRIBUTION");
                    insights.put("message", String.format("Good keyword distribution: %.1f%% of URLs ranking", keywordDistribution));
                    insights.put("recommendation", "Optimize non-ranking URLs for better coverage");
                } else if (keywordDistribution > 40) {
                    insights.put("status", "MODERATE_DISTRIBUTION");
                    insights.put("message", String.format("Moderate keyword distribution: %.1f%% of URLs ranking", keywordDistribution));
                    insights.put("recommendation", "Significant opportunity to improve URL optimization");
                } else {
                    insights.put("status", "POOR_DISTRIBUTION");
                    insights.put("message", String.format("Poor keyword distribution: only %.1f%% of URLs ranking", keywordDistribution));
                    insights.put("recommendation", "Comprehensive SEO audit and content optimization needed");
                }

                // Security insights
                int httpsUrls = protocolDistribution.getOrDefault("https", 0);
                int httpUrls = protocolDistribution.getOrDefault("http", 0);
                if (httpUrls > 0) {
                    insights.put("security_note", String.format("%d URLs still using HTTP - consider HTTPS migration", httpUrls));
                } else if (httpsUrls > 0) {
                    insights.put("security_note", "All URLs using HTTPS - excellent security posture");
                }
            }

            analytics.set("insights", insights);
            formattedResponse.set("analytics", analytics);

        } else {
            formattedResponse.set("urls", mapper.createArrayNode());
            formattedResponse.put("urls_on_page", 0);
        }

        // Add summary info from API
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();

            if (summaryInfo.has("total")) {
                apiInfo.put("total_urls_found", summaryInfo.get("total").asInt());
            }
            if (summaryInfo.has("page")) {
                apiInfo.put("current_page", summaryInfo.get("page").asInt());
            }
            if (summaryInfo.has("left_lines")) {
                apiInfo.put("credits_remaining", summaryInfo.get("left_lines").asLong());
            }

            // Calculate pagination info
            if (summaryInfo.has("total")) {
                int totalUrls = summaryInfo.get("total").asInt();
                int totalPages = (int) Math.ceil((double) totalUrls / size);
                apiInfo.put("total_pages", totalPages);
                apiInfo.put("has_next_page", page < totalPages);
                apiInfo.put("credits_used_this_request", dataArray != null ? dataArray.size() : 1); // Minimum 1 credit
            }

            formattedResponse.set("api_info", apiInfo);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }
}