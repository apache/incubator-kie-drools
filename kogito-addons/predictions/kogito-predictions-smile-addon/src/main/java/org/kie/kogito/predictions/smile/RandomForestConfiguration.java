/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.predictions.smile;

import java.util.HashMap;
import java.util.Map;

public class RandomForestConfiguration {

    private String outcomeName;
    private AttributeType outcomeType;
    private double confidenceThreshold;
    private int numTrees;
    private Map<String, AttributeType> inputFeatures = new HashMap<>();

    public int getNumTrees() {
        return numTrees;
    }

    public void setNumTrees(int numTrees) {
        this.numTrees = numTrees;
    }

    /**
     * Returns the name of the output attribute
     *
     * @return The name of the output attribute
     */
    public String getOutcomeName() {
        return outcomeName;
    }

    public void setOutcomeName(String outcomeName) {
        this.outcomeName = outcomeName;
    }

    /**
     * Returns the type of the output attribute {@link AttributeType}
     *
     * @return The type of the output attribute
     */
    public AttributeType getOutcomeType() {
        return outcomeType;
    }

    public void setOutcomeType(AttributeType outcomeType) {
        this.outcomeType = outcomeType;
    }

    /**
     * Returns the confidence threshold to use for automatic task completion
     *
     * @return The confidence threshold, between 0.0 and 1.0
     */
    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public void setConfidenceThreshold(double confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }

    public Map<String, AttributeType> getInputFeatures() {
        return inputFeatures;
    }

    public void setInputFeatures(Map<String, AttributeType> inputFeatures) {
        this.inputFeatures = inputFeatures;
    }
}
