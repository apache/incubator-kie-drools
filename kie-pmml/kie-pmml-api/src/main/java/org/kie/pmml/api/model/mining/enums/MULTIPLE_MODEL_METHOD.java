package org.kie.pmml.api.model.mining.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

public enum MULTIPLE_MODEL_METHOD {

    MAJORITY_VOTE("majorityVote"),
    WEIGHTED_MAJORITY_VOTE("weightedMajorityVote"),
    AVERAGE("average"),
    WEIGHTED_AVERAGE("weightedAverage"),
    MEDIAN("median"),
    WEIGHTED_MEDIAN("x-weightedMedian"),
    MAX("max"),
    SUM("sum"),
    WEIGHTED_SUM("x-weightedSum"),
    SELECT_FIRST("selectFirst"),
    SELECT_ALL("selectAll"),
    MODEL_CHAIN("modelChain");

    private final String name;

    MULTIPLE_MODEL_METHOD(String v) {
        name = v;
    }

    public static MULTIPLE_MODEL_METHOD byName(String name) throws KieEnumException {
        return Arrays.stream(MULTIPLE_MODEL_METHOD.values()).filter(value -> name.equals(value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MULTIPLE_MODEL_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }
}