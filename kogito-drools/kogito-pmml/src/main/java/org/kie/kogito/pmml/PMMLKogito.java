/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.pmml;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.core.io.impl.FileSystemResource;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.kogito.Application;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

import static org.kie.kogito.pmml.utils.PMMLUtils.getPMMLRequestData;

/**
 * Internal Utility class.<br/>
 * Use {@link Application#predictionModels()} of Kogito API to programmatically access PMML assets and evaluate PMML
 * predictions.
 */
public class PMMLKogito {

    private PMMLKogito() {
        // intentionally private.
    }

    /**
     * Internal Utility class.<br/>
     * Use {@link Application#predictionModels()} of Kogito API to programmatically access PMML assets and evaluate
     * PMML decisions.
     *
     * @param pmmlPaths
     * @return
     */
    public static Map<KieBase, KieRuntimeFactory> createKieRuntimeFactories(String... pmmlPaths) {
        return commonCreateKieRuntimeFactory(KieRuntimeFactoryBuilder::fromResources, pmmlPaths);
    }

    /**
     * This method is equivalents to createKieRuntimeFactories but it also compiles the PMML models in memory instead of
     * just load it. It is used by Kogito Test Scenario to load and run the tests
     * @param pmmlPaths
     * @return
     */
    public static Map<KieBase, KieRuntimeFactory> createKieRuntimeFactoriesWithInMemoryCompilation(String... pmmlPaths) {
        return commonCreateKieRuntimeFactory(KieRuntimeFactoryBuilder::fromResourcesWithInMemoryCompilation, pmmlPaths);
    }

    public static KiePMMLModel modelByName(PMMLRuntime pmmlRuntime, String modelName) {
        List<KiePMMLModel> modelsWithName =
                pmmlRuntime.getModels().stream().filter(m -> modelName.equals(m.getName())).collect(Collectors.toList());
        if (modelsWithName.size() == 1) {
            return modelsWithName.get(0);
        } else {
            String errorMessage =
                    String.format ("Wrong number of model(s) with name '%s': %s",
                                   modelName,
                                   modelsWithName.size());
            throw new KiePMMLException(errorMessage);
        }
    }

    public static PMML4Result evaluate(PMMLRuntime pmmlRuntime, String modelName, Map<String, Object> pmmlContext) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, pmmlContext);
        return pmmlRuntime.evaluate(modelName, new PMMLContextImpl(pmmlRequestData));
    }

    private static Map<KieBase, KieRuntimeFactory> commonCreateKieRuntimeFactory(
            final Function<Stream<Resource>, Map<KieBase, KieRuntimeFactory>> factory,
            final String... pmmlPaths) {
        Stream<Resource> resources = Stream.of(pmmlPaths).map(FileSystemResource::new);
        return factory.apply(resources);
    }

}
