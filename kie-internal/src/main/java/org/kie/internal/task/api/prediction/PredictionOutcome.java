/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
