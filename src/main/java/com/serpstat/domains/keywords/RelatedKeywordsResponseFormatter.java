package com.serpstat.domains.keywords;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Formatter for related keywords API responses
 */
public class RelatedKeywordsResponseFormatter {

    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {
        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        String keyword = (String) arguments.get("keyword");
        String searchEngine = (String) arguments.get("se");
        Integer page = (Integer) arguments.getOrDefault("page", 1);
        Integer size = (Integer) arguments.getOrDefault("size", 100);
        Boolean withIntents = (Boolean) arguments.getOrDefault("withIntents", false);

        // Create a formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatKeywordProcedure.getRelatedKeywords");
        formattedResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        formattedResponse.put("seed_keyword", keyword);
        formattedResponse.put("search_engine", searchEngine);
        formattedResponse.put("page", page);
        formattedResponse.put("page_size", size);
        formattedResponse.put("with_intents", withIntents);

        // Process-related keywords data
        JsonNode dataArray = resultNode.get("data");
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.set("related_keywords", dataArray);
            formattedResponse.put("keywords_on_page", dataArray.size());

            // Calculate analytics
            ObjectNode analytics = mapper.createObjectNode();

            // Basic statistics
            long totalVolume = 0;
            double totalCost = 0.0;
            double totalDifficulty = 0.0;
            double totalWeight = 0.0;
            int difficultyCount = 0;
            int weightCount = 0;
            int highVolumeKeywords = 0; // > 10,000 searches
            int lowDifficultyKeywords = 0; // difficulty < 30
            int highCostKeywords = 0; // cost > $5
            int strongConnectionKeywords = 0; // weight > 5

            // Competition analysis
            double totalConcurrency = 0.0;
            int concurrencyCount = 0;

            // Connection strength analysis
            Map<Integer, Integer> weightDistribution = new HashMap<>();

            // Intent analysis (if available)
            Map<String, Integer> intentDistribution = new HashMap<>();

            // SERP features analysis
            Map<String, Integer> serpFeatures = new HashMap<>();

            // Geographic analysis
            int keywordsWithGeo = 0;

            // Process each related keyword
            for (JsonNode keywordNode : dataArray) {
                // Volume and cost
                long volume = keywordNode.path("region_queries_count").asLong(0);
                double cost = keywordNode.path("cost").asDouble(0.0);
                totalVolume += volume;
                totalCost += cost;

                if (volume > 10000) {
                    highVolumeKeywords++;
                }
                if (cost > 5.0) {
                    highCostKeywords++;
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

                // Weight (connection strength) analysis
                if (keywordNode.has("weight") && !keywordNode.get("weight").isNull()) {
                    int weight = keywordNode.path("weight").asInt(0);
                    totalWeight += weight;
                    weightCount++;
                    weightDistribution.put(weight, weightDistribution.getOrDefault(weight, 0) + 1);

                    if (weight > 5) {
                        strongConnectionKeywords++;
                    }
                }

                // Competition analysis
                if (keywordNode.has("concurrency") && !keywordNode.get("concurrency").isNull()) {
                    int concurrency = keywordNode.path("concurrency").asInt(0);
                    totalConcurrency += concurrency;
                    concurrencyCount++;
                }

                // Intent analysis
                JsonNode intentsNode = keywordNode.get("intents");
                if (intentsNode != null && intentsNode.isArray()) {
                    for (JsonNode intentNode : intentsNode) {
                        String intent = intentNode.asText();
                        intentDistribution.put(intent, intentDistribution.getOrDefault(intent, 0) + 1);
                    }
                }

                // SERP features analysis
                JsonNode typesNode = keywordNode.get("types");
                if (typesNode != null && typesNode.isArray()) {
                    for (JsonNode typeNode : typesNode) {
                        String type = typeNode.asText();
                        serpFeatures.put(type, serpFeatures.getOrDefault(type, 0) + 1);
                    }
                }

                // Geographic analysis
                JsonNode geoNamesNode = keywordNode.get("geo_names");
                if (geoNamesNode != null && geoNamesNode.isArray() && !geoNamesNode.isEmpty()) {
                    keywordsWithGeo++;
                }
            }

            // Summary statistics
            ObjectNode summary = mapper.createObjectNode();
            summary.put("total_search_volume", totalVolume);
            summary.put("total_cost_estimate", Math.round(totalCost * 100.0) / 100.0);
            summary.put("average_difficulty", difficultyCount > 0 ? Math.round((totalDifficulty / difficultyCount) * 100.0) / 100.0 : 0);
            summary.put("average_competition", concurrencyCount > 0 ? Math.round((totalConcurrency / concurrencyCount) * 100.0) / 100.0 : 0);
            summary.put("average_connection_strength", weightCount > 0 ? Math.round((totalWeight / weightCount) * 100.0) / 100.0 : 0);
            summary.put("high_volume_keywords", highVolumeKeywords);
            summary.put("low_difficulty_keywords", lowDifficultyKeywords);
            summary.put("high_cost_keywords", highCostKeywords);
            summary.put("strong_connection_keywords", strongConnectionKeywords);
            summary.put("keywords_with_geo", keywordsWithGeo);

            if (!dataArray.isEmpty()) {
                summary.put("average_volume_per_keyword", Math.round((double) totalVolume / dataArray.size()));
                summary.put("average_cost_per_keyword", Math.round((totalCost / dataArray.size()) * 100.0) / 100.0);
            }

            analytics.set("summary", summary);

            // Connection strength distribution
            ObjectNode connectionStrength = mapper.createObjectNode();
            for (Map.Entry<Integer, Integer> entry : weightDistribution.entrySet()) {
                connectionStrength.put("weight_" + entry.getKey(), entry.getValue());
            }
            analytics.set("connection_strength_distribution", connectionStrength);

            // Intent distribution (if available)
            if (!intentDistribution.isEmpty()) {
                ObjectNode intentDist = mapper.createObjectNode();
                for (Map.Entry<String, Integer> entry : intentDistribution.entrySet()) {
                    intentDist.put(entry.getKey(), entry.getValue());
                }
                analytics.set("intent_distribution", intentDist);
            }

            // Top SERP features
            ObjectNode topSerpFeatures = mapper.createObjectNode();
            serpFeatures.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> topSerpFeatures.put(entry.getKey(), entry.getValue()));
            analytics.set("top_serp_features", topSerpFeatures);

            // Related keywords insights
            ObjectNode insights = mapper.createObjectNode();

            if (dataArray.isEmpty()) {
                insights.put("status", "NO_KEYWORDS");
                insights.put("message", "No related keywords found");
                insights.put("recommendation", "Try using a broader seed keyword or different search engine");
            } else {
                double avgVolume = !dataArray.isEmpty() ? (double) totalVolume / dataArray.size() : 0;
                double avgDifficulty = difficultyCount > 0 ? totalDifficulty / difficultyCount : 0;
                double avgWeight = weightCount > 0 ? totalWeight / weightCount : 0;

                // Volume assessment
                if (avgVolume > 50000) {
                    insights.put("volume_status", "HIGH_VOLUME");
                    insights.put("volume_message", "High search volume related keywords - excellent expansion potential");
                } else if (avgVolume > 10000) {
                    insights.put("volume_status", "MEDIUM_VOLUME");
                    insights.put("volume_message", "Medium search volume related keywords - good opportunities");
                } else {
                    insights.put("volume_status", "LOW_VOLUME");
                    insights.put("volume_message", "Low search volume related keywords - consider niche targeting");
                }

                // Difficulty assessment
                if (avgDifficulty < 30) {
                    insights.put("difficulty_status", "LOW_COMPETITION");
                    insights.put("difficulty_message", "Low competition related keywords - great for content expansion");
                } else if (avgDifficulty < 60) {
                    insights.put("difficulty_status", "MEDIUM_COMPETITION");
                    insights.put("difficulty_message", "Medium competition related keywords - balanced opportunities");
                } else {
                    insights.put("difficulty_status", "HIGH_COMPETITION");
                    insights.put("difficulty_message", "High competition related keywords - requires strong content strategy");
                }

                // Connection strength assessment
                if (avgWeight > 10) {
                    insights.put("connection_status", "STRONG_RELATION");
                    insights.put("connection_message", "Strongly related keywords - excellent semantic relevance");
                } else if (avgWeight > 5) {
                    insights.put("connection_status", "MEDIUM_RELATION");
                    insights.put("connection_message", "Moderately related keywords - good topical expansion");
                } else {
                    insights.put("connection_status", "WEAK_RELATION");
                    insights.put("connection_message", "Loosely related keywords - consider for broader content themes");
                }

                // Content strategy recommendations
                if (strongConnectionKeywords > dataArray.size() * 0.3) {
                    insights.put("strategy", "SEMANTIC_EXPANSION");
                    insights.put("strategy_message", "Many strongly connected keywords - ideal for semantic content expansion");
                } else if (lowDifficultyKeywords > dataArray.size() * 0.4) {
                    insights.put("strategy", "QUICK_WINS");
                    insights.put("strategy_message", "Many low-difficulty keywords - focus on quick content wins");
                } else if (keywordsWithGeo > dataArray.size() * 0.3) {
                    insights.put("strategy", "LOCAL_TARGETING");
                    insights.put("strategy_message", "Many geo-targeted keywords - consider local content strategy");
                } else {
                    insights.put("strategy", "LONG_TAIL_FOCUS");
                    insights.put("strategy_message", "Focus on long-tail related keywords for better targeting");
                }
            }

            analytics.set("insights", insights);
            formattedResponse.set("analytics", analytics);

        } else {
            formattedResponse.set("related_keywords", mapper.createArrayNode());
            formattedResponse.put("keywords_on_page", 0);
        }

        // Add summary info from API
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();

            if (summaryInfo.has("total")) {
                apiInfo.put("total_keywords_found", summaryInfo.get("total").asInt());
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
