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
package org.kie.pmml.models.mining.model.enums;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.AVERAGE_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.MAX_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.MEDIAN_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.MOST_FREQUENT_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.PROBABILITY_FUNCTION;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.SELECT_ALL_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.SELECT_FIRST_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.SELECT_LAST_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.SUM_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.WEIGHTED_AVERAGE_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.WEIGHTED_MEDIAN_RESULT;
import static org.kie.pmml.models.mining.model.enums.MultipleModelMethodFunctions.WEIGHTED_SUM_RESULT;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/MultipleModels.html#xsdType_MULTIPLE-MODEL-METHOD>MULTIPLE-MODEL-METHOD</a>
 */
public enum MULTIPLE_MODEL_METHOD {

    MAJORITY_VOTE("majorityVote",
                  MOST_FREQUENT_RESULT,
                  MOST_FREQUENT_RESULT,
                  inputData -> null),
    WEIGHTED_MAJORITY_VOTE("weightedMajorityVote",
                           inputData -> {
                               throw new KieEnumException("WEIGHTED_MAJORITY_VOTE not implemented, yet");
                           },
                           inputData -> {
                               throw new KieEnumException("WEIGHTED_MAJORITY_VOTE not implemented, yet");
                           },
                           inputData -> {
                               throw new KieEnumException("WEIGHTED_MAJORITY_VOTE not implemented, yet");
                           }),
    AVERAGE("average",
            AVERAGE_RESULT,
            MOST_FREQUENT_RESULT,
            PROBABILITY_FUNCTION),
    WEIGHTED_AVERAGE("weightedAverage",
                     WEIGHTED_AVERAGE_RESULT,
                     inputData -> null,
                     inputData -> null),
    MEDIAN("median",
           MEDIAN_RESULT,
           MEDIAN_RESULT,
           inputData -> null),
    WEIGHTED_MEDIAN("x-weightedMedian",
                    WEIGHTED_MEDIAN_RESULT,
                    inputData -> null,
                    inputData -> null),
    MAX("max",
        MAX_RESULT,
        MAX_RESULT,
        inputData -> null),
    SUM("sum",
        SUM_RESULT,
        inputData -> null,
        inputData -> null),
    WEIGHTED_SUM("x-weightedSum",
                 WEIGHTED_SUM_RESULT,
                 inputData -> null,
                 inputData -> null),
    SELECT_FIRST("selectFirst",
                 SELECT_FIRST_RESULT,
                 SELECT_FIRST_RESULT,
                 inputData -> null),
    SELECT_ALL("selectAll",
               SELECT_ALL_RESULT,
               inputData -> null,
               inputData -> null),
    MODEL_CHAIN("modelChain",
                SELECT_LAST_RESULT,
                SELECT_LAST_RESULT,
                inputData -> null);

    private final String name;
    /**
     * The function mapped to the given method used to evaluate a "prediction"
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> is the result of the model
     * evaluation.
     * It has to be a <code>LinkedHashMap</code> to keep insertion order and allow evaluation of
     * <code>SELECT_FIRST</code> method
     */
    private final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> predictionFunction;

    /**
     * The function mapped to the given method used to evaluate a "classification"
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> is the result of the model
     * evaluation.
     * It has to be a <code>LinkedHashMap</code> to keep insertion order and allow evaluation of
     * <code>SELECT_FIRST</code> method
     */
    private final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> classificationFunction;

    /**
     * The function mapped to the given method used to evaluate the probabilities of a "classification"
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> are the probabilities of the model
     * evaluation.
     * It has to be a <code>LinkedHashMap</code> to keep insertion order and allow evaluation of
     * <code>SELECT_FIRST</code> method
     */
    private final Function<LinkedHashMap<String, List<KiePMMLNameValue>>, LinkedHashMap<String, Double>> probabilityFunction;

    MULTIPLE_MODEL_METHOD(String v, Function<LinkedHashMap<String, KiePMMLNameValue>, Object> predictionFunction,
                          Function<LinkedHashMap<String, KiePMMLNameValue>, Object> classificationFunction,
                          Function<LinkedHashMap<String, List<KiePMMLNameValue>>, LinkedHashMap<String, Double>> probabilityFunction) {
        name = v;
        this.predictionFunction = predictionFunction;
        this.classificationFunction = classificationFunction;
        this.probabilityFunction = probabilityFunction;
    }

    public static MULTIPLE_MODEL_METHOD byName(String name) {
        return Arrays.stream(MULTIPLE_MODEL_METHOD.values()).filter(value -> name.equals(value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MULTIPLE_MODEL_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }

    /**
     * Return the prediction of the input data
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> is the result of the model
     * evaluation
     *
     * @param inputData
     * @return
     * @throws KieEnumException
     */
    public Object applyPrediction(LinkedHashMap<String, KiePMMLNameValue> inputData) {
        return predictionFunction.apply(inputData);
    }

    /**
     * Return the classification of the input data
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> is the result of the model
     * evaluation
     *
     * @param inputData
     * @return
     * @throws KieEnumException
     */
    public Object applyClassification(LinkedHashMap<String, KiePMMLNameValue> inputData) {
        return classificationFunction.apply(inputData);
    }

    /**
     * Return the probabilities of the input data
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> are the probabilities of the model
     * evaluation
     *
     * @param inputData
     * @return
     * @throws KieEnumException
     */
    public LinkedHashMap<String, Double> applyProbability(LinkedHashMap<String, List<KiePMMLNameValue>> inputData) {
        return probabilityFunction.apply(inputData);
    }
}