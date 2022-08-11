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
package org.kie.kogito.pmml;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.kie.kogito.prediction.PredictionModelNotFoundException;
import org.kie.kogito.prediction.PredictionModels;
import org.kie.pmml.api.runtime.PMMLRuntime;

/**
 * Abstract class with common code extended with codegen. Please check codegen usages before rename/move this class.
 * For example it is referenced by DecisionContainerGenerator
 */
public abstract class AbstractPredictionModels implements PredictionModels {

    // This AtomicReference is needed to have a deterministic/stable value for pmmlRuntimeRetrieverFunction.
    // DecisionModels statically refers pmmlRuntimeRetrieverFunction but it can be loaded before PredictionModels (it depends
    // on dependency injection container initialisation) so it is not possible to make an assumption on the order.
    // It can be probably removed (and make pmmlRuntimeRetrieverFunction private) after resolving https://issues.redhat.com/browse/DROOLS-5724
    private static final AtomicReference<Function<String, PMMLRuntime>> functionReference = new AtomicReference<>();
    public static final Function<String, PMMLRuntime> pmmlRuntimeRetrieverFunction = s -> functionReference.get().apply(s);

    protected static void init(String... pmmlFiles) {
        final java.util.Map<String, PMMLRuntime> pmmlRuntimes = PMMLKogito.createPMMLRuntimes(pmmlFiles);
        final Function<String, PMMLRuntime> function = s -> pmmlRuntimes.keySet().stream()
                .filter(pmmlRuntimes::containsKey)
                .map(pmmlRuntimes::get)
                .findFirst()
                .orElseThrow(() -> new PredictionModelNotFoundException("Failed to find PMMLRuntime for model " + s));
        functionReference.set(function);
    }

    public org.kie.kogito.prediction.PredictionModel getPredictionModel(String fileName, java.lang.String modelName) {
        return new org.kie.kogito.pmml.PmmlPredictionModel(getPMMLRuntime(fileName), fileName, modelName);
    }

    private org.kie.pmml.api.runtime.PMMLRuntime getPMMLRuntime(String fileName) {
        return pmmlRuntimeRetrieverFunction.apply(fileName);
    }
}
