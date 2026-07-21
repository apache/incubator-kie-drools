/*
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
package org.kie.kogito.predictions.smile;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.prediction.api.PredictionOutcome;
import org.kie.kogito.prediction.api.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smile.classification.RandomForest;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.NumericAttribute;
import smile.data.StringAttribute;

public class SmileRandomForest extends AbstractPredictionEngine implements PredictionService {

    public static final String IDENTIFIER = "SMILERandomForest";
    private static final String UNABLE_PARSE_TEXT = "Unable to parse text";
    private static final Logger logger = LoggerFactory.getLogger(SmileRandomForest.class);

    private final AttributeDataset dataset;
    private final Map<String, Attribute> smileAttributes;
    private final Attribute outcomeAttribute;
    private final AttributeType outcomeAttributeType;
    private final int numAttributes;
    private final int numberTrees;
    protected List<String> attributeNames = new ArrayList<>();

    private Set<String> outcomeSet = new HashSet<>();
    private static final int MINIMUM_OBSERVATIONS = 1200;
    private int observations = 0;

    public SmileRandomForest(RandomForestConfiguration configuration) {
        this(configuration.getInputFeatures(),
                configuration.getOutcomeName(),
                configuration.getOutcomeType(),
                configuration.getConfidenceThreshold(),
                configuration.getNumTrees());
    }

    public SmileRandomForest(Map<String, AttributeType> inputFeatures,
            String outputFeatureName,
            AttributeType outputFeatureType,
            double confidenceThreshold,
            int numberTrees) {
        super(inputFeatures, outputFeatureName, outputFeatureType, confidenceThreshold);
        this.numberTrees = numberTrees;
        smileAttributes = new HashMap<>();
        for (Entry<String, AttributeType> inputFeature : inputFeatures.entrySet()) {
            final String name = inputFeature.getKey();
            final AttributeType type = inputFeature.getValue();
            smileAttributes.put(name, createAttribute(name, type));
            attributeNames.add(name);
        }
        numAttributes = smileAttributes.size();
        outcomeAttribute = createAttribute(outputFeatureName, outputFeatureType);
        outcomeAttributeType = outputFeatureType;

        dataset = new AttributeDataset("dataset", smileAttributes.values().toArray(new Attribute[numAttributes]), outcomeAttribute);
    }

    protected Attribute createAttribute(String name, AttributeType type) {
        if (type == AttributeType.NOMINAL || type == AttributeType.BOOLEAN) {
            return new NominalAttribute(name);
        } else if (type == AttributeType.NUMERIC) {
            return new NumericAttribute(name);
        } else {
            return new StringAttribute(name);
        }
    }

    protected Object convertValue(String value, AttributeType type) {
        if (type == AttributeType.NOMINAL) {
            return value;
        } else if (type == AttributeType.NUMERIC) {
            return Long.valueOf(value);
        } else if (type == AttributeType.BOOLEAN) {
            return Boolean.valueOf(value);
        } else {
            return value;
        }
    }

    /**
     * Add the data provided as a map to a Smile {@link smile.data.Dataset}.
     *
     * @param data A map containing the input attribute names as keys and the attribute values as values.
     * @param outcome The value of the outcome (output data).
     */
    public void addData(Map<String, Object> data, Object outcome) {
        final double[] features = new double[numAttributes];
        int i = 0;
        for (Entry<String, Attribute> entry : smileAttributes.entrySet()) {
            try {
                features[i] = smileAttributes.get(entry.getKey()).valueOf(data.get(entry.getKey()).toString());
            } catch (ParseException e) {
                logger.error(UNABLE_PARSE_TEXT, e);
            }
            i++;
        }
        try {
            final String outcomeStr = outcome.toString();
            outcomeSet.add(outcomeStr);
            dataset.add(features, outcomeAttribute.valueOf(outcomeStr));
        } catch (ParseException e) {
            logger.error(UNABLE_PARSE_TEXT, e);
        }
    }

    /**
     * Build a set of features compatible with Smile's datasets from the map of input data
     *
     * @param data A map containing the input attribute names as keys and the attribute values as values.
     * @return A feature vector as a array of doubles.
     */
    protected double[] buildFeatures(Map<String, Object> data) {
        final double[] features = new double[numAttributes];
        for (int i = 0; i < numAttributes; i++) {
            final String attrName = attributeNames.get(i);
            try {
                features[i] = smileAttributes.get(attrName).valueOf(data.get(attrName).toString());
            } catch (ParseException e) {
                logger.error(UNABLE_PARSE_TEXT, e);
            }
        }
        return features;
    }

    /**
     * Returns the service's identifier
     *
     * @return The service identifier
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Returns a model prediction given the input data
     *
     * @param task Human task data
     * @param inputData A map containing the input attribute names as keys and the attribute values as values.
     * @return A {@link PredictionOutcome} containing the model's prediction for the input data.
     */
    @Override
    public PredictionOutcome predict(WorkItem task, Map<String, Object> inputData) {
        logger.debug("Predicting with input data: {}", inputData);
        RandomForest model = null;
        if (observations > MINIMUM_OBSERVATIONS) {
            this.confidenceThreshold = 0.75;
        }

        Map<String, Object> outcomes = new HashMap<>();
        if (outcomeSet.size() >= 2) {
            model = new RandomForest(dataset, this.numberTrees);
            final double[] features = buildFeatures(inputData);
            final double[] posteriori = new double[outcomeSet.size()];
            double prediction = model.predict(features, posteriori);

            String predictionStr = dataset.responseAttribute().toString(prediction);
            outcomes.put(outcomeAttribute.getName(), convertValue(predictionStr, outcomeAttributeType));
            final double confidence = posteriori[(int) prediction];
            outcomes.put("confidence", confidence);

            logger.debug("task id {}, total {} observations, prediction = {}, confidence = {} (threshold = {})", ((KogitoWorkItem) task).getStringId(), this.observations, predictionStr, confidence,
                    this.confidenceThreshold);

            return new PredictionOutcome(confidence, this.confidenceThreshold, outcomes);
        } else {
            outcomes.put("confidence", 0.0);
            return new PredictionOutcome(0.0, this.confidenceThreshold, outcomes);
        }
    }

    /**
     * Train the random forest model using data from the human task.
     *
     * @param task Human task data
     * @param inputData A map containing the input attribute names as keys and the attribute values as values.
     * @param outputData A map containing the output attribute names as keys and the attribute values as values.
     */
    @Override
    public void train(WorkItem task, Map<String, Object> inputData, Map<String, Object> outputData) {
        logger.debug("Training with input data: {}", inputData);
        logger.debug("Training with output data: {}", outputData);
        this.observations += 1;

        addData(inputData, outputData.get(outcomeAttribute.getName()));
    }
}
