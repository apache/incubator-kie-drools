package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * PMML models
 * @see <a href=http://dmg.org/pmml/v4-4/GeneralStructure.html#xsdGroup_MODEL-ELEMENT>MODEL-ELEMENT</a>
 */
public enum PMML_MODEL implements Named {

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
