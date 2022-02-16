/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Value;

public class CompositeFeatureUtils {

    private CompositeFeatureUtils() {

    }

    public static List<Feature> flattenFeatures(List<Feature> features) {
        return DataUtils.getLinearizedFeatures(features);
    }

    public static List<Feature> unflattenFeatures(List<Feature> flattenedFeatures, List<Feature> originalFeatures) {
        final AtomicInteger tracker = new AtomicInteger();
        return originalFeatures.stream().map(feature -> unravel(flattenedFeatures, tracker, feature)).collect(Collectors.toList());
    }

    private static Feature unravel(List<Feature> flattenedFeatures, AtomicInteger tracker, Feature f) {
        Feature extractedFeature;
        final Object featureObject = f.getValue().getUnderlyingObject();
        switch (f.getType()) {
            case UNDEFINED:
                if (featureObject instanceof Feature) {
                    extractedFeature = FeatureFactory.copyOf(f,
                            new Value(unravel(flattenedFeatures, tracker, (Feature) featureObject)));
                } else {
                    extractedFeature = FeatureFactory.copyOf(f, flattenedFeatures.get(tracker.getAndIncrement()).getValue());
                }
                break;
            case COMPOSITE:
                if (featureObject instanceof List) {
                    extractedFeature = FeatureFactory.newCompositeFeature(f.getName(),
                            ((List<Feature>) featureObject).stream().map(feature -> unravel(flattenedFeatures, tracker, feature)).collect(
                                    Collectors.toList()));
                } else {
                    extractedFeature = FeatureFactory.copyOf(f, flattenedFeatures.get(tracker.getAndIncrement()).getValue());
                }
                break;
            default:
                extractedFeature = FeatureFactory.copyOf(f, flattenedFeatures.get(tracker.getAndIncrement()).getValue());
        }
        return extractedFeature;
    }

}