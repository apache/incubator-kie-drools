/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.EncodingParams;
import org.kie.kogito.explainability.model.PerturbationContext;

class LimeConfigEntityFactory {

    private static final String PROXIMITY_KERNEL_WIDTH = "proximity.kernel.width";
    private static final String PROXIMITY_THRESHOLD = "proximity.threshold";
    private static final String PROXIMITY_FILTERED_DATASET_MINIMUM = "proximity.filtered.dataset.minimum";
    private static final String PROXIMITY_FILTER_ENABLED = "proximity.filter.enabled";
    private static final String EP_NUMERIC_CLUSTER_FILTER_WIDTH = "ep.numeric.cluster.filter.width";
    private static final String EP_NUMERIC_CLUSTER_THRESHOLD = "ep.numeric.cluster.threshold";
    private static final String SAMPLING_SEPARABLE_DATASET_RATIO = "sampling.separable.dataset.ratio";
    private static final String SAMPLING_SIZE = "sampling.size";
    private static final String SAMPLING_PERTURBATIONS = "sampling.perturbations";
    private static final String SAMPLING_ADAPT_DATASET_VARIANCE = "sampling.adapt.dataset.variance";
    private static final String WEIGHTING_PENALIZE_BALANCE_SPARSE = "weighting.penalize.balance.sparse";

    private static final Map<String, BiFunction<LimeConfig, LimeConfigEntity, LimeConfig>> processors = initProcessors();

    private static Map<String, BiFunction<LimeConfig, LimeConfigEntity, LimeConfig>> initProcessors() {
        Map<String, BiFunction<LimeConfig, LimeConfigEntity, LimeConfig>> processors = new HashMap<>();
        processors.put(PROXIMITY_KERNEL_WIDTH, (limeConfig, limeConfigEntity) -> limeConfig.withProximityKernelWidth(limeConfigEntity.asDouble()));
        processors.put(PROXIMITY_THRESHOLD, (limeConfig, limeConfigEntity) -> limeConfig.withProximityThreshold(limeConfigEntity.asDouble()));
        processors.put(PROXIMITY_FILTERED_DATASET_MINIMUM, (limeConfig, limeConfigEntity) -> limeConfig.withProximityFilteredDatasetMinimum(limeConfigEntity.asDouble()));
        processors.put(EP_NUMERIC_CLUSTER_FILTER_WIDTH, (limeConfig, limeConfigEntity) -> limeConfig.withEncodingParams(new EncodingParams(limeConfigEntity.asDouble(),
                limeConfig.getEncodingParams().getNumericTypeClusterThreshold())));
        processors.put(EP_NUMERIC_CLUSTER_THRESHOLD,
                (limeConfig, limeConfigEntity) -> limeConfig.withEncodingParams(new EncodingParams(limeConfig.getEncodingParams().getNumericTypeClusterGaussianFilterWidth(),
                        limeConfigEntity.asDouble())));
        processors.put(SAMPLING_SEPARABLE_DATASET_RATIO, (limeConfig, limeConfigEntity) -> limeConfig.withSeparableDatasetRatio(limeConfigEntity.asDouble()));
        processors.put(SAMPLING_SIZE, (limeConfig, limeConfigEntity) -> limeConfig.withSamples((int) limeConfigEntity.asDouble()));
        processors.put(SAMPLING_PERTURBATIONS, (limeConfig, limeConfigEntity) -> limeConfig.withPerturbationContext(
                limeConfig.getPerturbationContext().getSeed().isPresent()
                        ? new PerturbationContext(limeConfig.getPerturbationContext().getSeed().get(), limeConfig.getPerturbationContext().getRandom(),
                                (int) limeConfigEntity.asDouble())
                        : new PerturbationContext(limeConfig.getPerturbationContext().getRandom(), (int) limeConfigEntity.asDouble())));
        processors.put(PROXIMITY_FILTER_ENABLED, (limeConfig, limeConfigEntity) -> limeConfig.withProximityFilter(limeConfigEntity.asBoolean()));
        processors.put(WEIGHTING_PENALIZE_BALANCE_SPARSE, (limeConfig, limeConfigEntity) -> limeConfig.withPenalizeBalanceSparse(limeConfigEntity.asBoolean()));
        processors.put(SAMPLING_ADAPT_DATASET_VARIANCE, (limeConfig, limeConfigEntity) -> limeConfig.withAdaptiveVariance(limeConfigEntity.asBoolean()));
        return processors;
    }

    private LimeConfigEntityFactory() {
    }

    static LimeConfig toLimeConfig(LimeConfigSolution solution) {
        List<LimeConfigEntity> entities = solution.getEntities();
        LimeConfig config = solution.getConfig().copy();
        for (LimeConfigEntity entity : entities) {
            config = processors.get(entity.getName()).apply(config, entity);
        }
        return config;

    }

    static List<LimeConfigEntity> createSamplingEntities(LimeConfig config) {
        List<LimeConfigEntity> entities = new ArrayList<>();
        boolean adaptDatasetVariance = config.isAdaptDatasetVariance();
        entities.add(new BooleanLimeConfigEntity(SAMPLING_ADAPT_DATASET_VARIANCE, adaptDatasetVariance));
        double noOfSamples = config.getNoOfSamples();
        entities.add(new NumericLimeConfigEntity(SAMPLING_SIZE, noOfSamples, 10, 1000));
        double noOfPerturbations = config.getPerturbationContext().getNoOfPerturbations();
        entities.add(new NumericLimeConfigEntity(SAMPLING_PERTURBATIONS, noOfPerturbations, 1, 10));
        double separableDatasetRatio = config.getSeparableDatasetRatio();
        entities.add(new NumericLimeConfigEntity(SAMPLING_SEPARABLE_DATASET_RATIO, separableDatasetRatio, 0.7, 0.99));
        return entities;
    }

    static List<LimeConfigEntity> createProximityEntities(LimeConfig config) {
        List<LimeConfigEntity> entities = new ArrayList<>();
        boolean proximityFilterEnabled = config.isProximityFilter();
        entities.add(new BooleanLimeConfigEntity(PROXIMITY_FILTER_ENABLED, proximityFilterEnabled));
        double proximityKernelWidth = config.getProximityKernelWidth();
        entities.add(new NumericLimeConfigEntity(PROXIMITY_KERNEL_WIDTH, proximityKernelWidth, 0.1, 0.9));
        double proximityThreshold = config.getProximityThreshold();
        entities.add(new NumericLimeConfigEntity(PROXIMITY_THRESHOLD, proximityThreshold, 0.5, 0.99));
        double proximityFilteredDatasetMinimum = config.getProximityFilteredDatasetMinimum().doubleValue();
        entities.add(new NumericLimeConfigEntity(PROXIMITY_FILTERED_DATASET_MINIMUM, proximityFilteredDatasetMinimum, 0.1, 0.9));
        return entities;
    }

    static List<LimeConfigEntity> createEncodingEntities(LimeConfig config) {
        List<LimeConfigEntity> entities = new ArrayList<>();
        double numericTypeClusterGaussianFilterWidth = config.getEncodingParams().getNumericTypeClusterGaussianFilterWidth();
        entities.add(new NumericLimeConfigEntity(EP_NUMERIC_CLUSTER_FILTER_WIDTH, numericTypeClusterGaussianFilterWidth, 0.5, 1));
        double numericTypeClusterThreshold = config.getEncodingParams().getNumericTypeClusterThreshold();
        entities.add(new NumericLimeConfigEntity(EP_NUMERIC_CLUSTER_THRESHOLD, numericTypeClusterThreshold, 1e-4, 1e-1));
        return entities;
    }

    static List<LimeConfigEntity> createWeightingEntities(LimeConfig config) {
        List<LimeConfigEntity> entities = new ArrayList<>();
        boolean penalizeBalanceSparse = config.isPenalizeBalanceSparse();
        entities.add(new BooleanLimeConfigEntity(WEIGHTING_PENALIZE_BALANCE_SPARSE, penalizeBalanceSparse));
        return entities;
    }
}
