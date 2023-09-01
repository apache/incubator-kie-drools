/**
 * A class to encapsulate prediction results from a {@link org.kie.internal.task.api.prediction.PredictionService}
 * implementation.
 */
package org.kie.internal.task.api.prediction;

import java.util.Map;

/**
 * Encapsulates results from a {@link PredictionService}.
 */
public class PredictionOutcome {

    private boolean present;

    private double confidenceLevel = 0.0;
    
    private double confidenceThreshold = 0.0;

    private Map<String, Object> data;

    /**
     * Creates an empty prediction.
     */
    public PredictionOutcome() {
        this.present = false;
    }

    /**
     *
     * Returns a prediction for a prediction service with the specified confidence level, confidence threshold and
     * outcome.
     *
     * @param confidenceLevel Numerical value to quantify confidence level for this prediction
     * @param confidenceThreshold The threshold above which a prediction should be automatically accepted
     * @param data A map containing the outcome names and values (respectively as map keys and values)
     */
    public PredictionOutcome(double confidenceLevel, double confidenceThreshold, Map<String, Object> data) {
        this.present = data != null;
        this.confidenceLevel = confidenceLevel;
        this.confidenceThreshold = confidenceThreshold;
        this.data = data;
    }

    public boolean isPresent() {
        return this.present;
    }

    /**
     * Returns true if a prediction has a confidence level above the specified threshold otherwise false
     */
    public boolean isCertain() {
        return this.present && confidenceLevel > confidenceThreshold;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
