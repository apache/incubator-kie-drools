/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.evaluator.core.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.kie.api.pmml.PMML4Result;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.model.FRI.SLASH;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class PMMLRuntimeInternalImpl implements PMMLRuntimeInternal {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeInternalImpl.class);

    private static final RuntimeManager runtimeManager = SPIUtils.getRuntimeManager(true).get();

    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;
    private final Set<PMMLListener> pmmlListeners = new HashSet<>();

    public PMMLRuntimeInternalImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this.memoryCompilerClassLoader = memoryCompilerClassLoader;
    }

    @Override
    public PMML4Result evaluate(String modelName, PMMLContext context) {
        String basePath = context.getFileName() + SLASH + getSanitizedClassName(modelName);
        FRI fri = new FRI(basePath, "pmml");
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, context);
        Optional<EfestoOutput> retrieved = runtimeManager.evaluateInput(darInputPMML, memoryCompilerClassLoader);
        if (!retrieved.isPresent()) {
            throw new KieRuntimeServiceException("Failed to retrieve EfestoOutput");
        }
        if (!(retrieved.get() instanceof EfestoOutputPMML)) {
            throw new KieRuntimeServiceException("Expected EfestoOutputPMML, retrieved " + retrieved.get().getClass());
        }
        return retrieved.map(EfestoOutputPMML.class::cast).map(EfestoOutputPMML::getOutputData).orElse(null);
    }

    @Override
    public List<PMMLModel> getPMMLModels() {
        return null;
    }

    @Override
    public Optional<PMMLModel> getPMMLModel(String modelName) {
        return Optional.empty();
    }

    @Override
    public KieMemoryCompiler.MemoryCompilerClassLoader getMemoryClassLoader() {
        return memoryCompilerClassLoader;
    }
}
