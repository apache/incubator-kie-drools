/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.model.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.commons.exceptions.KieEnumException;

/**
 * PMML models
 * @see <a href=http://dmg.org/pmml/v4-4/GeneralStructure.html#xsdGroup_MODEL-ELEMENT>MODEL-ELEMENT</a>
 */
public enum PMML_MODEL {

    ANOMALY_DETECTION_MODEL("AnomalyDetectionModel"),
    ASSOCIATION_MODEL("AssociationModel"),
    BASELINE_MODEL("BaselineModel"),
    BAYESIAN_NETWORK_MODEL("BayesianNetworkModel"),
    CLUSTERING_MODEL("ClusteringModel"),
    GAUSSIAN_PROCESS_MODEL("GaussianProcessModel"),
    GENERAL_REGRESSION_MODEL("GeneralRegressionModel"),
    MINING_MODEL("MiningModel"),
    NAIVEBAYES_MODEL("NaiveBayesModel"),
    NEARESTNEIGHBOR_MODEL("NearestNeighborModel"),
    NEURALNETWORK_MODEL("NeuralNetwork"),
    REGRESSION_MODEL("RegressionModel"),
    RULESET_MODEL("RuleSetModel"),
    SCORECARD_MODEL("Scorecard"),
    SEQUENCE_MODEL("SequenceModel"),
    SUPPORT_VECTOR_MACHINE_MODEL("SupportVectorMachineModel"),
    TEXT_MODEL("TextModel"),
    TIME_SERIES_MODEL("TimeSeriesModel"),
    TREE_MODEL("TreeModel"),
    // Used only for internal tests
    TEST_MODEL("TestModel");

    private String name;

    PMML_MODEL(String name) {
        this.name = name;
    }

    public static PMML_MODEL byName(String name) {
        return Arrays.stream(PMML_MODEL.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find PMMLModelType with name: " + name));
    }

    public String getName() {
        return name;
    }
}
