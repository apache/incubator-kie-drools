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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;
import org.kie.pmml.evaluator.core.service.PMMLRuntimeInternalImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.pmml.utils.PMMLUtils.getPMMLRequestData;

/**
 * Internal Utility class
 */
public class PMMLKogito {
    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = org.kie.pmml.evaluator.utils.SPIUtils.getPMMLRuntimeFactory(false);

    private static final Logger logger = LoggerFactory.getLogger(PMMLKogito.class);

    private PMMLKogito() {
        // intentionally private.
    }

    /**
     * Internal Utility class.<br/>
     *
     * @param pmmlPaths
     * @return
     */
    public static Map<String, PMMLRuntime> createPMMLRuntimes(String... pmmlPaths) {
        Map<String, PMMLRuntime> toReturn = new HashMap<>();
        Stream.of(pmmlPaths).forEach(pmmlPath -> toReturn.put(pmmlPath,
                getPMMLRuntimeAlreadyCompiled()));
        return toReturn;
    }

    public static PMMLModel modelByName(PMMLRuntime pmmlRuntime, String fileName, String modelName) {
        logger.debug("modelByName {} {} {}", pmmlRuntime, fileName, modelName);
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName);
        final PMMLRuntimeContext runtimeContext = getPMMLRuntimeContext(pmmlRequestData, fileName);
        List<PMMLModel> allPmmlModels = pmmlRuntime.getPMMLModels(runtimeContext);
        logger.debug("allPmmlModels {}", allPmmlModels);
        List<PMMLModel> modelsWithName = allPmmlModels.stream().filter(m -> modelName.equals(m.getName())).collect(Collectors.toList());
        if (modelsWithName.size() == 1) {
            return modelsWithName.get(0);
        } else {
            String errorMessage =
                    String.format("Wrong number of model(s) with name '%s': %s",
                            modelName,
                            modelsWithName.size());
            throw new KiePMMLException(errorMessage);
        }
    }

    public static PMML4Result evaluate(PMMLRuntime pmmlRuntime, String fileName, String modelName, Map<String, Object> pmmlInputData) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, pmmlInputData);
        return pmmlRuntime.evaluate(modelName, getPMMLRuntimeContext(pmmlRequestData, fileName));
    }

    private static PMMLRuntimeContext getPMMLRuntimeContext(PMMLRequestData pmmlRequestData, String fileName) {
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        return new PMMLRuntimeContextImpl(pmmlRequestData, fileName, memoryCompilerClassLoader);
    }

    private static PMMLRuntime getPMMLRuntimeAlreadyCompiled() {

        return new PMMLRuntimeInternalImpl();
    }

    private static PMMLRuntime getPMMLRuntimeWithInMemoryCompilation(File pmmlFile) {
        return PMML_RUNTIME_FACTORY.getPMMLRuntimeFromFile(pmmlFile);
    }

}
