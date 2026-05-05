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
package org.kie.kogito.dmn;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.io.ReaderResource;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.internal.utils.DMNEvaluationUtils;
import org.kie.dmn.core.internal.utils.DMNEvaluationUtils.DMNEvaluationResult;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.kogito.Application;
import org.kie.kogito.dmn.rest.KogitoDMNResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.dmn.AbstractDecisionModels.readResource;

/**
 * Internal Utility class.<br/>
 * Use {@link Application#decisionModels()} of Kogito API to programmatically access DMN assets and evaluate DMN
 * decisions.
 */
public class DMNKogito {

    private static final Logger logger = LoggerFactory.getLogger(DMNKogito.class.getName());

    private DMNKogito() {
        // intentionally private.
    }

    /**
     * Internal Utility class.<br/>
     * Use {@link Application#decisionModels()} of Kogito API to programmatically access DMN assets and evaluate DMN
     * decisions.
     */
    public static DMNRuntime createGenericDMNRuntime(Set<DMNProfile> customDMNProfiles, boolean enableRuntimeTypeCheckOption, Reader... readers) {
        DMNKogitoCallbacks.beforeCreateGenericDMNRuntime(readers);
        List<Resource> resources = Stream.of(readers).map(ReaderResource::new).collect(Collectors.toList());
        return createGenericDMNRuntime(customDMNProfiles, enableRuntimeTypeCheckOption, resources);
    }

    /**
     * Internal Utility class.<br/>
     * Use {@link Application#decisionModels()} of Kogito API to programmatically access DMN assets and evaluate DMN
     * decisions.
     *
     * @param customDMNProfiles
     * @param enableRuntimeTypeCheckOption
     * @param modelPaths A Map of model path to model encoding
     * @return
     */
    public static DMNRuntime createGenericDMNRuntime(Set<DMNProfile> customDMNProfiles, boolean enableRuntimeTypeCheckOption,
            Map<String, String> modelPaths) {
        DMNKogitoCallbacks.beforeCreateGenericDMNRuntime(modelPaths);
        List<Resource> resources = modelPaths.entrySet()
                .stream()
                .map(modelPathEntry -> {
                    Optional<InputStream> modelStream = getDMNModelStream(modelPathEntry.getKey());
                    if (modelStream.isPresent()) {
                        return readResource(modelStream.get(), modelPathEntry.getValue());
                    } else {
                        String errorMessage = String.format("DMN model stream not found for path: %s", modelPathEntry.getKey());
                        logger.error(errorMessage);
                        throw new IllegalStateException(errorMessage);
                    }
                })
                .map(ReaderResource::new).collect(Collectors.toList());
        return createGenericDMNRuntime(customDMNProfiles, enableRuntimeTypeCheckOption, resources);
    }

    public static DMNModel modelByName(DMNRuntime dmnRuntime, String modelName) {
        List<DMNModel> modelsWithName =
                dmnRuntime.getModels().stream().filter(m -> modelName.equals(m.getName())).collect(Collectors.toList());
        if (modelsWithName.size() == 1) {
            return modelsWithName.get(0);
        } else {
            throw new RuntimeException("Multiple model with the same name: " + modelName);
        }
    }

    public static KogitoDMNResult evaluate(DMNRuntime dmnRuntime, String modelName, Map<String, Object> dmnContext) {
        return evaluate(dmnRuntime, modelByName(dmnRuntime, modelName).getNamespace(), modelName, dmnContext);
    }

    public static KogitoDMNResult evaluate(DMNRuntime dmnRuntime, String modelNamespace, String modelName, Map<String, Object> dmnContext) {
        DMNEvaluationResult evaluationResult = DMNEvaluationUtils.evaluate(dmnRuntime,
                modelNamespace,
                modelName,
                dmnContext,
                null,
                null,
                null);
        return new KogitoDMNResult(modelNamespace, modelName, evaluationResult.result);
    }

    static Optional<InputStream> getDMNModelStream(String path) {
        logger.debug("getDMNModelStream for {}", path);
        InputStream toReturn = Application.class.getResourceAsStream(path);
        if (toReturn != null) {
            return Optional.of(toReturn);
        } else {
            logger.debug("DMN model stream not found in Application.class.getResourceAsStream");
        }
        toReturn = Application.class.getClassLoader().getResourceAsStream(path);
        if (toReturn != null) {
            return Optional.of(toReturn);
        } else {
            logger.debug("DMN model stream not found in Application.class.getClassLoader().getResourceAsStream");
        }
        toReturn = org.drools.util.IoUtils.class.getClassLoader().getResourceAsStream(path);
        if (toReturn != null) {
            return Optional.of(toReturn);
        } else {
            logger.debug("DMN model stream not found in org.drools.util.IoUtils.class.getClassLoader().getResourceAsStream");
        }
        toReturn = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (toReturn != null) {
            return Optional.of(toReturn);
        } else {
            logger.debug("DMN model stream not found in Thread.currentThread().getContextClassLoader().getResourceAsStream");
        }
        logger.warn("DMN model stream not found!!!");
        return Optional.empty();
    }

    /**
     * Internal Utility class.<br/>
     * Use {@link Application#decisionModels()} of Kogito API to programmatically access DMN assets and evaluate DMN
     * decisions.
     *
     * @param customDMNProfiles
     * @param enableRuntimeTypeCheckOption
     * @param resources
     * @return
     */
    static DMNRuntime createGenericDMNRuntime(Set<DMNProfile> customDMNProfiles, boolean enableRuntimeTypeCheckOption, List<Resource> resources) {
        EvalHelper.clearGenericAccessorCache(); // KOGITO-3325 DMN hot reload manage accessor cache when stronglytyped
        DMNRuntimeBuilder dmnRuntimeBuilder = DMNRuntimeBuilder.fromDefaults();
        customDMNProfiles.forEach(dmnRuntimeBuilder::addProfile);
        DMNRuntime dmnRuntime = dmnRuntimeBuilder
                .buildConfiguration()
                .fromResources(resources)
                .getOrElseThrow(e -> new RuntimeException("Error initializing DMNRuntime", e));
        RuntimeTypeCheckOption runtimeTypeCheckOption = new RuntimeTypeCheckOption(enableRuntimeTypeCheckOption);
        ((DMNRuntimeImpl) dmnRuntime).setOption(runtimeTypeCheckOption);
        DMNKogitoCallbacks.afterCreateGenericDMNRuntime(dmnRuntime);
        return dmnRuntime;
    }

}
