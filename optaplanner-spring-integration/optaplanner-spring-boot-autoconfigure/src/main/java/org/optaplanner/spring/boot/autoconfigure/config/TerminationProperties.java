package org.optaplanner.spring.boot.autoconfigure.config;

import java.time.Duration;

public class TerminationProperties {

    /**
     * How long the solver can run.
     * For example: "30s" is 30 seconds. "5m" is 5 minutes. "2h" is 2 hours. "1d" is 1 day.
     * Also supports ISO-8601 format, see java.time.Duration.
     */
    private Duration spentLimit;
    /**
     * How long the solver can run without finding a new best solution after finding a new best solution.
     * For example: "30s" is 30 seconds. "5m" is 5 minutes. "2h" is 2 hours. "1d" is 1 day.
     * Also supports ISO-8601 format, see java.time.Duration.
     */
    private Duration unimprovedSpentLimit;
    /**
     * Terminates the solver when a specific or higher score has been reached.
     * For example: "0hard/-1000soft" terminates when the best score changes from "0hard/-1200soft" to "0hard/-900soft".
     * Wildcards are supported to replace numbers.
     * For example: "0hard/*soft" to terminate when any feasible score is reached.
     */
    private String bestScoreLimit;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public Duration getSpentLimit() {
        return spentLimit;
    }

    public void setSpentLimit(Duration spentLimit) {
        this.spentLimit = spentLimit;
    }

    public Duration getUnimprovedSpentLimit() {
        return unimprovedSpentLimit;
    }

    public void setUnimprovedSpentLimit(Duration unimprovedSpentLimit) {
        this.unimprovedSpentLimit = unimprovedSpentLimit;
    }

    public String getBestScoreLimit() {
        return bestScoreLimit;
    }

    public void setBestScoreLimit(String bestScoreLimit) {
        this.bestScoreLimit = bestScoreLimit;
    }

}
