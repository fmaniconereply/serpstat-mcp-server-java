package com.serpstat.domains.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formatter for domain unique keywords API responses
 */
public class DomainUniqueKeywordsResponseFormatter {

    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {
        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        @SuppressWarnings("unchecked")
        List<String> domains = (List<String>) arguments.get("domains");
        String minusDomain = (String) arguments.get("minusDomain");
        String searchEngine = (String) arguments.get("se");
        Integer page = (Integer) arguments.getOrDefault("page", 1);
        Integer size = (Integer) arguments.getOrDefault("size", 100);

        // Create formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatDomainProcedure.getDomainsUniqKeywords");
        formattedResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        formattedResponse.set("analyzed_domains", mapper.valueToTree(domains));
        formattedResponse.put("excluded_domain", minusDomain);
        formattedResponse.put("search_engine", searchEngine);
        formattedResponse.put("page", page);
        formattedResponse.put("page_size", size);

        // Process unique keywords data
        JsonNode dataArray = resultNode.get("data");
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.set("unique_keywords", dataArray);
            formattedResponse.put("keywords_on_page", dataArray.size());

            // Calculate analytics
            ObjectNode analytics = mapper.createObjectNode();

            // Basic statistics
            long totalVolume = 0;
            double totalCost = 0.0;
            double totalDifficulty = 0.0;
            int difficultyCount = 0;
            int highVolumeKeywords = 0; // > 10,000 searches
            int lowDifficultyKeywords = 0; // difficulty < 30
            int highCostKeywords = 0; // cost > $5
            long totalTraffic = 0;

            // Position analysis
            int topPositions = 0; // positions 1-3
            int firstPage = 0; // positions 1-10
            int secondPage = 0; // positions 11-20

            // Competition analysis
            double totalConcurrency = 0.0;
            int concurrencyCount = 0;

            // Keyword length analysis
            Map<Integer, Integer> lengthDistribution = new HashMap<>();

            // SERP features analysis
            Map<String, Integer> serpFeatures = new HashMap<>();

            // Domain position analysis
            Map<String, Map<String, Integer>> domainPositionStats = new HashMap<>();
            for (String domain : domains) {
                domainPositionStats.put(domain, new HashMap<>());
            }

            // Process each unique keyword
            for (JsonNode keywordNode : dataArray) {
                // Volume and cost
                long volume = keywordNode.path("region_queries_count").asLong(0);
                double cost = keywordNode.path("cost").asDouble(0.0);
                long traffic = keywordNode.path("traff").asLong(0);

                totalVolume += volume;
                totalCost += cost;
                totalTraffic += traffic;

                if (volume > 10000) {
                    highVolumeKeywords++;
                }
                if (cost > 5.0) {
                    highCostKeywords++;
                }

                // Position analysis
                int position = keywordNode.path("position").asInt(0);
                if (position >= 1 && position <= 3) {
                    topPositions++;
                }
                if (position >= 1 && position <= 10) {
                    firstPage++;
                } else if (position >= 11 && position <= 20) {
                    secondPage++;
                }

                // Difficulty analysis
                if (keywordNode.has("difficulty") && !keywordNode.get("difficulty").isNull()) {
                    int difficulty = keywordNode.path("difficulty").asInt(0);
                    totalDifficulty += difficulty;
                    difficultyCount++;

                    if (difficulty < 30) {
                        lowDifficultyKeywords++;
                    }
                }

                // Competition analysis
                if (keywordNode.has("concurrency") && !keywordNode.get("concurrency").isNull()) {
                    int concurrency = keywordNode.path("concurrency").asInt(0);
                    totalConcurrency += concurrency;
                    concurrencyCount++;
                }

                // Keyword length distribution
                int length = keywordNode.path("keyword_length").asInt(0);
                lengthDistribution.put(length, lengthDistribution.getOrDefault(length, 0) + 1);

                // SERP features analysis
                JsonNode typesNode = keywordNode.get("types");
                if (typesNode != null && typesNode.isArray()) {
                    for (JsonNode typeNode : typesNode) {
                        String type = typeNode.asText();
                        serpFeatures.put(type, serpFeatures.getOrDefault(type, 0) + 1);
                    }
                }

                // Domain position analysis
                for (String domain : domains) {
                    if (keywordNode.has(domain)) {
                        int domainPosition = keywordNode.path(domain).asInt(0);
                        Map<String, Integer> stats = domainPositionStats.get(domain);

                        if (domainPosition >= 1 && domainPosition <= 3) {
                            stats.put("top_3", stats.getOrDefault("top_3", 0) + 1);
                        }
                        if (domainPosition >= 1 && domainPosition <= 10) {
                            stats.put("first_page", stats.getOrDefault("first_page", 0) + 1);
                        }
                        stats.put("total", stats.getOrDefault("total", 0) + 1);
                    }
                }
            }

            // Summary statistics
            ObjectNode summary = mapper.createObjectNode();
            summary.put("total_search_volume", totalVolume);
            summary.put("total_cost_estimate", Math.round(totalCost * 100.0) / 100.0);
            summary.put("total_traffic_estimate", totalTraffic);
            summary.put("average_difficulty", difficultyCount > 0 ? Math.round((totalDifficulty / difficultyCount) * 100.0) / 100.0 : 0);
            summary.put("average_competition", concurrencyCount > 0 ? Math.round((totalConcurrency / concurrencyCount) * 100.0) / 100.0 : 0);
            summary.put("high_volume_keywords", highVolumeKeywords);
            summary.put("low_difficulty_keywords", lowDifficultyKeywords);
            summary.put("high_cost_keywords", highCostKeywords);

            if (!dataArray.isEmpty()) {
                summary.put("average_volume_per_keyword", Math.round((double) totalVolume / dataArray.size()));
                summary.put("average_cost_per_keyword", Math.round((totalCost / dataArray.size()) * 100.0) / 100.0);
                summary.put("average_traffic_per_keyword", Math.round((double) totalTraffic / dataArray.size()));
            }

            analytics.set("summary", summary);

            // Position distribution
            ObjectNode positions = mapper.createObjectNode();
            positions.put("top_3_positions", topPositions);
            positions.put("first_page_positions", firstPage);
            positions.put("second_page_positions", secondPage);

            if (!dataArray.isEmpty()) {
                positions.put("top_3_percentage", Math.round((double) topPositions / dataArray.size() * 10000.0) / 100.0);
                positions.put("first_page_percentage", Math.round((double) firstPage / dataArray.size() * 10000.0) / 100.0);
            }

            analytics.set("position_distribution", positions);

            // Keyword length distribution
            ObjectNode lengthDist = mapper.createObjectNode();
            for (Map.Entry<Integer, Integer> entry : lengthDistribution.entrySet()) {
                lengthDist.put(entry.getKey() + "_words", entry.getValue());
            }
            analytics.set("keyword_length_distribution", lengthDist);

            // Top SERP features
            ObjectNode topSerpFeatures = mapper.createObjectNode();
            serpFeatures.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> topSerpFeatures.put(entry.getKey(), entry.getValue()));
            analytics.set("top_serp_features", topSerpFeatures);

            // Domain performance comparison
            ObjectNode domainComparison = mapper.createObjectNode();
            for (Map.Entry<String, Map<String, Integer>> entry : domainPositionStats.entrySet()) {
                String domain = entry.getKey();
                Map<String, Integer> stats = entry.getValue();

                ObjectNode domainStats = mapper.createObjectNode();
                domainStats.put("total_keywords", stats.getOrDefault("total", 0));
                domainStats.put("top_3_positions", stats.getOrDefault("top_3", 0));
                domainStats.put("first_page_positions", stats.getOrDefault("first_page", 0));

                if (stats.getOrDefault("total", 0) > 0) {
                    double topPercentage = (double) stats.getOrDefault("top_3", 0) / stats.get("total") * 100;
                    double firstPagePercentage = (double) stats.getOrDefault("first_page", 0) / stats.get("total") * 100;
                    domainStats.put("top_3_percentage", Math.round(topPercentage * 100.0) / 100.0);
                    domainStats.put("first_page_percentage", Math.round(firstPagePercentage * 100.0) / 100.0);
                }

                domainComparison.set(domain, domainStats);
            }
            analytics.set("domain_comparison", domainComparison);

            // Competitive insights
            ObjectNode insights = mapper.createObjectNode();

            if (dataArray.isEmpty()) {
                insights.put("status", "NO_UNIQUE_KEYWORDS");
                insights.put("message", String.format("No unique keywords found for %s that %s doesn't rank for",
                        String.join(" and ", domains), minusDomain));
                insights.put("recommendation", "Try different domain combinations or adjust filters");
            } else {
                double avgDifficulty = difficultyCount > 0 ? totalDifficulty / difficultyCount : 0;
                double firstPagePercentage = !dataArray.isEmpty() ? (double) firstPage / dataArray.size() * 100.0 : 0;

                if (avgDifficulty < 40 && firstPagePercentage > 70) {
                    insights.put("opportunity_level", "HIGH");
                    insights.put("message", "Excellent opportunity - many low-difficulty keywords with good positions");
                    insights.put("recommendation", "Focus on content optimization for these unique keyword opportunities");
                } else if (avgDifficulty < 60 && firstPagePercentage > 50) {
                    insights.put("opportunity_level", "MEDIUM");
                    insights.put("message", "Good opportunity - moderate difficulty with decent positions");
                    insights.put("recommendation", "Consider targeting these keywords with focused SEO efforts");
                } else if (highVolumeKeywords > dataArray.size() * 0.3) {
                    insights.put("opportunity_level", "HIGH_VOLUME");
                    insights.put("message", "High search volume keywords available - significant traffic potential");
                    insights.put("recommendation", "Prioritize high-volume keywords for maximum impact");
                } else {
                    insights.put("opportunity_level", "COMPETITIVE");
                    insights.put("message", "Competitive keywords - requires strong SEO strategy");
                    insights.put("recommendation", "Focus on long-tail variations and content depth");
                }

                // Competitive gap analysis
                String strongestDomain = null;
                int maxFirstPageCount = 0;
                for (Map.Entry<String, Map<String, Integer>> entry : domainPositionStats.entrySet()) {
                    int firstPageCount = entry.getValue().getOrDefault("first_page", 0);
                    if (firstPageCount > maxFirstPageCount) {
                        maxFirstPageCount = firstPageCount;
                        strongestDomain = entry.getKey();
                    }
                }

                if (strongestDomain != null) {
                    insights.put("strongest_performer", strongestDomain);
                    insights.put("performance_insight", String.format("%s dominates with %d first-page rankings",
                            strongestDomain, maxFirstPageCount));
                }
            }

            analytics.set("insights", insights);
            formattedResponse.set("analytics", analytics);

        } else {
            formattedResponse.set("unique_keywords", mapper.createArrayNode());
            formattedResponse.put("keywords_on_page", 0);
        }

        // Add summary info from API
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();

            if (summaryInfo.has("total")) {
                apiInfo.put("total_unique_keywords", summaryInfo.get("total").asInt());
            }
            if (summaryInfo.has("page")) {
                apiInfo.put("current_page", summaryInfo.get("page").asInt());
            }
            if (summaryInfo.has("left_lines")) {
                apiInfo.put("credits_remaining", summaryInfo.get("left_lines").asLong());
            }

            // Calculate pagination info
            if (summaryInfo.has("total")) {
                int totalKeywords = summaryInfo.get("total").asInt();
                int totalPages = (int) Math.ceil((double) totalKeywords / size);
                apiInfo.put("total_pages", totalPages);
                apiInfo.put("has_next_page", page < totalPages);
                apiInfo.put("credits_used_this_request", dataArray != null ? dataArray.size() : 1); // Minimum 1 credit
            }

            formattedResponse.set("api_info", apiInfo);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }
}
