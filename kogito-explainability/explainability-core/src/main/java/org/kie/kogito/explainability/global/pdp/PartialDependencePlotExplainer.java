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
package org.kie.kogito.explainability.global.pdp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.global.GlobalExplainer;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionInputsDataDistribution;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates the partial dependence plot for the features of a {@link PredictionProvider}.
 * <p>
 * see also https://christophm.github.io/interpretable-ml-book/pdp.html
 */
public class PartialDependencePlotExplainer implements GlobalExplainer<List<PartialDependenceGraph>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartialDependencePlotExplainer.class);

    private final PartialDependencePlotConfig config;

    /**
     * Create a PDP explainer with given configuration.
     *
     * @param config the PDP configuration.
     */
    public PartialDependencePlotExplainer(PartialDependencePlotConfig config) {
        this.config = config;
    }

    /**
     * Create a PDP explainer with the default {@link PartialDependencePlotConfig}.
     */
    public PartialDependencePlotExplainer() {
        this(new PartialDependencePlotConfig());
    }

    @Override
    public List<PartialDependenceGraph> explainFromMetadata(PredictionProvider model, PredictionProviderMetadata metadata)
            throws InterruptedException, ExecutionException, TimeoutException {
        return explainFromDataDistribution(model, metadata.getOutputShape().getOutputs().size(), metadata.getDataDistribution());
    }

    @Override
    public List<PartialDependenceGraph> explainFromPredictions(PredictionProvider model, Collection<Prediction> predictions)
            throws InterruptedException, ExecutionException, TimeoutException {
        int outputSize = predictions.isEmpty() ? 0 : predictions.stream().findAny().map(p -> p.getOutput().getOutputs().size()).orElse(0);
        List<PredictionInput> inputs = predictions.stream().map(Prediction::getInput).collect(Collectors.toList());
        return explainFromDataDistribution(model, outputSize, new PredictionInputsDataDistribution(inputs));
    }

    private List<PartialDependenceGraph> explainFromDataDistribution(PredictionProvider model, int outputSize,
            DataDistribution dataDistribution)
            throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        List<PartialDependenceGraph> pdps = new ArrayList<>();
        List<FeatureDistribution> featureDistributions = dataDistribution.asFeatureDistributions();

        // fetch entire data distributions for all features
        List<PredictionInput> trainingData = dataDistribution.sample(config.getSeriesLength());

        // create a PDP for each feature
        for (FeatureDistribution featureDistribution : featureDistributions) {
            // generate (further) samples for the feature under analysis
            // TBD: maybe just reuse trainingData
            List<Value> xsValues = featureDistribution.sample(config.getSeriesLength()).stream()
                    .sorted(Comparator.comparing(Value::asString)) // sort alphanumerically (if Value#asNumber is NaN)
                    .sorted((v1, v2) -> Comparator.comparingDouble(Value::asNumber).compare(v1, v2)) // sort by natural order
                    .distinct() // drop duplicates
                    .collect(Collectors.toList());
            List<Feature> featureXSvalues = xsValues.stream() // transform sampled Values into Features
                    .map(v -> FeatureFactory.copyOf(featureDistribution.getFeature(), v)).collect(Collectors.toList());

            // create a PDP for each feature and each output
            for (int outputIndex = 0; outputIndex < outputSize; outputIndex++) {
                PartialDependenceGraph partialDependenceGraph = getPartialDependenceGraph(model, trainingData, xsValues,
                        featureXSvalues, outputIndex);
                pdps.add(partialDependenceGraph);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.debug("explanation time: {}ms", (end - start));
        return pdps;
    }

    private PartialDependenceGraph getPartialDependenceGraph(PredictionProvider model,
            List<PredictionInput> trainingData,
            List<Value> xsValues,
            List<Feature> featureXSvalues, int outputIndex)
            throws InterruptedException, ExecutionException, TimeoutException {
        Output outputDecision = null;
        Feature feature = null;
        // each feature value of the feature under analysis should have a corresponding output value (composed by the marginal impacts of the other features)
        List<Map<Value, Long>> valueCounts = new ArrayList<>(featureXSvalues.size());
        for (int i = 0; i < featureXSvalues.size(); i++) {
            // initialize an empty feature to use in the generated PDP
            if (feature == null) {
                feature = FeatureFactory.copyOf(featureXSvalues.get(i), new Value(null));
            }
            List<PredictionInput> predictionInputs = prepareInputs(featureXSvalues.get(i), trainingData);
            List<PredictionOutput> predictionOutputs = getOutputs(model, predictionInputs);
            // prediction requests are batched per value of feature 'Xs' under analysis
            for (PredictionOutput predictionOutput : predictionOutputs) {
                Output output = predictionOutput.getOutputs().get(outputIndex);
                if (outputDecision == null) {
                    outputDecision = new Output(output.getName(), output.getType());
                }
                // update output value counts
                updateValueCounts(valueCounts, i, output);
            }
        }

        if (outputDecision != null) {
            List<Value> yValues = collapseMarginalImpacts(valueCounts, outputDecision.getType());
            return new PartialDependenceGraph(feature, outputDecision, xsValues, yValues);
        } else {
            throw new IllegalArgumentException("cannot produce PDP for null decision");
        }
    }

    /**
     * Collapse value counts into marginal impacts.
     * For numbers ({@code Type.NUMBER.equals(type))} this is just the average of each value at each feature value.
     * For all other types the final {@link Value} is just the most frequent.
     *
     * @param valueCounts the frequency of each value at each position
     * @param type the type of the output
     * @return the marginal impacts
     */
    private List<Value> collapseMarginalImpacts(List<Map<Value, Long>> valueCounts, Type type) {
        List<Value> yValues = new ArrayList<>();
        if (Type.NUMBER.equals(type)) {
            List<Double> doubles = valueCounts.stream()
                    .map(v -> v.entrySet().stream()
                            .map(e -> e.getKey().asNumber() * e.getValue() / config.getSeriesLength()).mapToDouble(d -> d).sum())
                    .collect(Collectors.toList());
            yValues = doubles.stream().map(Value::new).collect(Collectors.toList());
        } else {
            for (Map<Value, Long> item : valueCounts) {
                long max = 0;
                String output = null;
                for (Map.Entry<Value, Long> entry : item.entrySet()) {
                    if (entry.getValue() > max) {
                        max = entry.getValue();
                        output = entry.getKey().asString();
                    }
                }
                yValues.add(new Value(output));
            }
        }
        return yValues;
    }

    private void updateValueCounts(List<Map<Value, Long>> valueCounts, int featureValueIndex, Output output) {
        Value categoricalOutput = output.getValue();
        if (valueCounts.size() <= featureValueIndex) {
            Map<Value, Long> classCount = new HashMap<>();
            classCount.put(categoricalOutput, 1L);
            valueCounts.add(classCount);
        } else {
            Map<Value, Long> classCount = valueCounts.get(featureValueIndex);
            if (classCount.containsKey(categoricalOutput)) {
                classCount.put(categoricalOutput, classCount.get(categoricalOutput) + 1);
            } else {
                classCount.put(categoricalOutput, 1L);
            }
            valueCounts.set(featureValueIndex, classCount);
        }
    }

    /**
     * Perform batch predictions on the model.
     *
     * @param model the model to be queried
     * @param predictionInputs a batch of inputs
     * @return a batch of outputs
     */
    private List<PredictionOutput> getOutputs(PredictionProvider model, List<PredictionInput> predictionInputs)
            throws InterruptedException, ExecutionException, TimeoutException {
        List<PredictionOutput> predictionOutputs;
        predictionOutputs = model.predictAsync(predictionInputs).get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        return predictionOutputs;
    }

    /**
     * Generate inputs for a particular feature, using 1) a specific discrete value from the data distribution of the
     * feature under analysis for that particular feature and 2) values from a training data distribution (which we sample)
     * for all the other feature values.
     * The resulting list of prediction inputs will have the very same value for the feature under analysis, and values
     * from the training data for all other features.
     *
     * @param featureXs specific value of the feature under analysis
     * @param trainingData training data
     * @return a list of prediction inputs
     */
    private List<PredictionInput> prepareInputs(Feature featureXs,
            List<PredictionInput> trainingData) {
        List<PredictionInput> predictionInputs = new ArrayList<>(config.getSeriesLength());

        for (PredictionInput trainingSample : trainingData) {
            List<Feature> features = trainingSample.getFeatures();
            List<Feature> newFeatures = replaceFeatures(featureXs, features);
            predictionInputs.add(new PredictionInput(newFeatures));
        }
        return predictionInputs;
    }

    private List<Feature> replaceFeatures(Feature featureXs, List<Feature> features) {
        List<Feature> newFeatures = new ArrayList<>();
        for (Feature f : features) {
            Feature newFeature;
            if (f.getName().equals(featureXs.getName())) {
                newFeature = FeatureFactory.copyOf(f, featureXs.getValue());
            } else {
                if (Type.COMPOSITE == f.getType()) {
                    @SuppressWarnings("unchecked")
                    List<Feature> elements = (List<Feature>) f.getValue().getUnderlyingObject();
                    newFeature = FeatureFactory.newCompositeFeature(f.getName(), replaceFeatures(featureXs, elements));
                } else {
                    newFeature = FeatureFactory.copyOf(f, f.getValue());
                }
            }
            newFeatures.add(newFeature);
        }
        return newFeatures;
    }
}
