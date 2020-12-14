/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.pmml;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.kogito.prediction.PredictionModels;
import org.kie.pmml.evaluator.core.utils.KnowledgeBaseUtils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public abstract class AbstractPredictionModels implements PredictionModels {

    // This AtomicReference is needed to have a deterministic/stable value for kieRuntimeFactoryFunction.
    // DecisionModels statically refers kieRuntimeFactoryFunction but it can be loaded before PredictionModels (it depends
    // on dependency injection container initialisation) so it is not possible to make an assumption on the order.
    // It can be probably removed (and make kieRuntimeFactoryFunction private) after resolving https://issues.redhat.com/browse/DROOLS-5724
    private static final AtomicReference<Function<String, KieRuntimeFactory>> functionReference = new AtomicReference<>();
    public static final Function<String, KieRuntimeFactory> kieRuntimeFactoryFunction = s -> functionReference.get().apply(s);

    protected static void init(String ... pmmlFiles) {
        final java.util.Map<KieBase, KieRuntimeFactory> kieRuntimeFactories = PMMLKogito.createKieRuntimeFactories(pmmlFiles);
        final Function<String, KieRuntimeFactory> function = s -> kieRuntimeFactories.keySet().stream()
                .filter(kieBase -> KnowledgeBaseUtils.getModel(kieBase, s).isPresent())
                .map(kieRuntimeFactories::get)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to find KieRuntimeFactory for model " + s));
        functionReference.set(function);
    }

    public org.kie.kogito.prediction.PredictionModel getPredictionModel(java.lang.String modelName) {
        return new org.kie.kogito.pmml.PmmlPredictionModel(getPMMLRuntime(modelName), modelName);
    }

    private org.kie.pmml.api.runtime.PMMLRuntime getPMMLRuntime(java.lang.String modelName) {
        return kieRuntimeFactoryFunction.apply(modelName).get(org.kie.pmml.api.runtime.PMMLRuntime.class);
    }
}
