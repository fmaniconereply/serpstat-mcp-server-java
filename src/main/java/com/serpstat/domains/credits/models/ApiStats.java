package com.serpstat.domains.credits.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * API limits and usage statistics model
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@SuppressWarnings("unused")
public class ApiStats {

    @JsonProperty("max_lines")
    private Long maxLines;

    @JsonProperty("used_lines")
    private Long usedLines;

    @JsonProperty("left_lines")
    private Long leftLines;

    @JsonProperty("user_info")
    private UserInfo userInfo;    /**
     * Calculate usage percentage
     */
    @JsonIgnore
    public double getUsagePercentage() {
        if (maxLines == null || maxLines == 0) return 0.0;
        return (double) usedLines / maxLines * 100.0;
    }

    /**
     * Check if usage is critical (>90%)
     */
    @JsonIgnore
    public boolean isCriticalUsage() {
        return getUsagePercentage() > 90.0;
    }

    /**
     * Check if usage is high (>75%)
     */
    @JsonIgnore
    public boolean isHighUsage() {
        return getUsagePercentage() > 75.0;
    }
}
