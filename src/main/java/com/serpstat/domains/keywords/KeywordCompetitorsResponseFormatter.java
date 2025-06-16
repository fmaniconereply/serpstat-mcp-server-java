package com.serpstat.domains.keywords;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

/**
 * Formatter for keyword competitors API responses
 */
public class KeywordCompetitorsResponseFormatter {

    /**
     * Format getCompetitors response for keyword analysis
     */
    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        String keyword = (String) arguments.get("keyword");
        String searchEngine = (String) arguments.get("se");
        Integer size = (Integer) arguments.getOrDefault("size", 20);

        // Create a formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatKeywordProcedure.getCompetitors");
        formattedResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        formattedResponse.put("analyzed_keyword", keyword);
        formattedResponse.put("search_engine", searchEngine);
        formattedResponse.put("requested_size", size);

        // Process competitors data
        JsonNode dataNode = resultNode.get("data");
        if (dataNode != null && dataNode.isObject()) {
            // Convert an object to array for better processing
            var competitorsArray = mapper.createArrayNode();
            Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                competitorsArray.add(entry.getValue());
            }

            formattedResponse.set("competitors", competitorsArray);
            formattedResponse.put("found_competitors_count", competitorsArray.size());

            // Calculate analytics
            ObjectNode analytics = mapper.createObjectNode();

            // Summary statistics
            double totalVisibility = 0;
            long totalTraffic = 0;
            double totalRelevance = 0;
            double totalOurRelevance = 0;
            int totalIntersectedKeywords = 0;
            int competitorsWithAds = 0;
            int competitorsWithPositiveDynamics = 0;

            // Performance categories
            int topPerformers = 0; // visibility > 1000
            int mediumPerformers = 0; // visibility 100-1000
            int lowPerformers = 0; // visibility < 100

            for (JsonNode competitorNode : competitorsArray) {
                double visibility = competitorNode.path("visible").asDouble(0);
                long traffic = competitorNode.path("traff").asLong(0);
                double relevance = competitorNode.path("relevance").asDouble(0);
                double ourRelevance = competitorNode.path("our_relevance").asDouble(0);
                int intersected = competitorNode.path("intersected").asInt(0);
                int ads = competitorNode.path("ads").asInt(0);
                double visibilityDynamic = competitorNode.path("visible_dynamic").asDouble(0);

                totalVisibility += visibility;
                totalTraffic += traffic;
                totalRelevance += relevance;
                totalOurRelevance += ourRelevance;
                totalIntersectedKeywords += intersected;

                if (ads > 0) {
                    competitorsWithAds++;
                }

                if (visibilityDynamic > 0) {
                    competitorsWithPositiveDynamics++;
                }

                // Categorize by performance
                if (visibility > 1000) {
                    topPerformers++;
                } else if (visibility >= 100) {
                    mediumPerformers++;
                } else {
                    lowPerformers++;
                }
            }

            // Summary statistics
            ObjectNode summary = mapper.createObjectNode();
            int competitorCount = competitorsArray.size();

            summary.put("total_competitors", competitorCount);
            summary.put("total_visibility", Math.round(totalVisibility * 100.0) / 100.0);
            summary.put("total_estimated_traffic", totalTraffic);
            summary.put("total_intersected_keywords", totalIntersectedKeywords);
            summary.put("competitors_with_ads", competitorsWithAds);
            summary.put("competitors_with_positive_dynamics", competitorsWithPositiveDynamics);

            if (competitorCount > 0) {
                summary.put("average_visibility", Math.round((totalVisibility / competitorCount) * 100.0) / 100.0);
                summary.put("average_relevance", Math.round((totalRelevance / competitorCount) * 100.0) / 100.0);
                summary.put("average_our_relevance", Math.round((totalOurRelevance / competitorCount) * 100.0) / 100.0);
                summary.put("average_traffic", Math.round((double) totalTraffic / competitorCount));
            }

            analytics.set("summary", summary);

            // Performance distribution
            ObjectNode performanceDistribution = mapper.createObjectNode();
            performanceDistribution.put("top_performers", topPerformers);
            performanceDistribution.put("medium_performers", mediumPerformers);
            performanceDistribution.put("low_performers", lowPerformers);

            if (competitorCount > 0) {
                performanceDistribution.put("top_performers_percentage",
                        Math.round((double) topPerformers / competitorCount * 10000.0) / 100.0);
                performanceDistribution.put("medium_performers_percentage",
                        Math.round((double) mediumPerformers / competitorCount * 10000.0) / 100.0);
                performanceDistribution.put("low_performers_percentage",
                        Math.round((double) lowPerformers / competitorCount * 10000.0) / 100.0);
            }

            analytics.set("performance_distribution", performanceDistribution);

            // Competition insights
            ObjectNode insights = mapper.createObjectNode();

            if (competitorCount == 0) {
                insights.put("competition_level", "NO_DATA");
                insights.put("message", "No competitors found for this keyword");
                insights.put("recommendation", "Try a different keyword or check spelling");
            } else {
                double avgVisibility = totalVisibility / competitorCount;
                double adCompetitionRate = (double) competitorsWithAds / competitorCount * 100;

                // Determine competition level
                String competitionLevel;
                String competitionMessage;
                String recommendation;

                if (avgVisibility > 2000) {
                    competitionLevel = "VERY_HIGH";
                    competitionMessage = String.format("Extremely competitive keyword with average visibility of %.1f", avgVisibility);
                    recommendation = "Consider long-tail variations or niche targeting";
                } else if (avgVisibility > 500) {
                    competitionLevel = "HIGH";
                    competitionMessage = String.format("Highly competitive keyword with average visibility of %.1f", avgVisibility);
                    recommendation = "Strong SEO strategy and quality content required";
                } else if (avgVisibility > 100) {
                    competitionLevel = "MEDIUM";
                    competitionMessage = String.format("Moderately competitive keyword with average visibility of %.1f", avgVisibility);
                    recommendation = "Good opportunity with focused optimization efforts";
                } else {
                    competitionLevel = "LOW";
                    competitionMessage = String.format("Low competition keyword with average visibility of %.1f", avgVisibility);
                    recommendation = "Excellent opportunity for quick wins";
                }

                insights.put("competition_level", competitionLevel);
                insights.put("message", competitionMessage);
                insights.put("recommendation", recommendation);

                // Additional insights
                if (adCompetitionRate > 50) {
                    insights.put("ad_competition", "HIGH");
                    insights.put("ad_insight", String.format("%.1f%% of competitors use paid ads - high commercial intent", adCompetitionRate));
                } else if (adCompetitionRate > 20) {
                    insights.put("ad_competition", "MEDIUM");
                    insights.put("ad_insight", String.format("%.1f%% of competitors use paid ads - moderate commercial potential", adCompetitionRate));
                } else {
                    insights.put("ad_competition", "LOW");
                    insights.put("ad_insight", String.format("%.1f%% of competitors use paid ads - primarily organic competition", adCompetitionRate));
                }

                // Market dynamics
                double dynamicsRate = (double) competitorsWithPositiveDynamics / competitorCount * 100;
                if (dynamicsRate > 60) {
                    insights.put("market_trend", "GROWING");
                    insights.put("trend_insight", "Most competitors are gaining visibility - growing market");
                } else if (dynamicsRate > 40) {
                    insights.put("market_trend", "STABLE");
                    insights.put("trend_insight", "Mixed competitor dynamics - stable market");
                } else {
                    insights.put("market_trend", "DECLINING");
                    insights.put("trend_insight", "Most competitors losing visibility - declining or saturated market");
                }
            }

            analytics.set("insights", insights);
            formattedResponse.set("analytics", analytics);

        } else {
            formattedResponse.set("competitors", mapper.createArrayNode());
            formattedResponse.put("found_competitors_count", 0);
        }

        // Add API metadata
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();

            if (summaryInfo.has("left_lines")) {
                apiInfo.put("credits_remaining", summaryInfo.get("left_lines").asLong());
            }
            if (summaryInfo.has("page")) {
                apiInfo.put("current_page", summaryInfo.get("page").asInt());
            }

            formattedResponse.set("api_info", apiInfo);
        }

        // Calculate estimated cost (1 credit per competitor)
        int competitorsFound = dataNode != null && dataNode.isObject() ? dataNode.size() : 0;
        // Minimum 1 credit even if no data (as per API documentation starting June 15, 2025)
        int estimatedCost = Math.max(1, competitorsFound);
        formattedResponse.put("estimated_credits_used", estimatedCost);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }
}