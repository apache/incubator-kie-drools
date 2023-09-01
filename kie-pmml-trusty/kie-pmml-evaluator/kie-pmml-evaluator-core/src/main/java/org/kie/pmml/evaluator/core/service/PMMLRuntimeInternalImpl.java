/**
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
package org.kie.pmml.evaluator.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.identifiers.KiePmmlComponentRoot;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.identifiers.PmmlIdFactory;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;
import org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class PMMLRuntimeInternalImpl implements PMMLRuntime {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeInternalImpl.class);

    private static final RuntimeManager runtimeManager = SPIUtils.getRuntimeManager(true).get();

    private final Map<String, GeneratedResources> generatedResourcesMap;

    public PMMLRuntimeInternalImpl() {
        this(Collections.emptyMap());
    }

    public PMMLRuntimeInternalImpl(Map<String, GeneratedResources> generatedResourcesMap) {
        this.generatedResourcesMap = generatedResourcesMap;
    }

    public Map<String, GeneratedResources> getGeneratedResourcesMap() {
        return generatedResourcesMap;
    }

    @Override
    public PMML4Result evaluate(String modelName, PMMLRuntimeContext context) {
        EfestoInputPMML darInputPMML = getEfestoInputPMML(modelName, context);
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context, darInputPMML);
        if (retrieved.isEmpty()) {
            throw new KiePMMLException("Failed to retrieve EfestoOutput");
        }
        EfestoOutput output = retrieved.iterator().next();
        if (!(output instanceof EfestoOutputPMML)) {
            throw new KiePMMLException("Expected EfestoOutputPMML, retrieved " + output.getClass());
        }
        return ((EfestoOutputPMML) output).getOutputData();
    }

    @Override
    public List<PMMLModel> getPMMLModels(PMMLRuntimeContext context) {
        logger.debug("getPMMLModels {}", context);
        return PMMLRuntimeHelper.getPMMLModels(context);
    }

    @Override
    public Optional<PMMLModel> getPMMLModel(String fileName, String modelName, PMMLRuntimeContext context) {
        return PMMLRuntimeHelper.getPMMLModel(fileName, modelName, context);
    }

    static EfestoInputPMML getEfestoInputPMML(String modelName, PMMLRuntimeContext context) {
        LocalComponentIdPmml modelLocalUriId = new EfestoAppRoot()
                .get(KiePmmlComponentRoot.class)
                .get(PmmlIdFactory.class)
                .get(context.getFileNameNoSuffix(), getSanitizedClassName(modelName));

        return new EfestoInputPMML(modelLocalUriId, context);
    }

}
