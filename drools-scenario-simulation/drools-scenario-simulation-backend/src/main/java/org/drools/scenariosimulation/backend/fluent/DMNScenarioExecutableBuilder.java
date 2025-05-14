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
package org.drools.scenariosimulation.backend.fluent;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.drools.scenariosimulation.backend.util.DMNSimulationUtils;
import org.drools.util.ResourceHelper;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.efesto.runtime.model.EfestoOutputDMN;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedModelResource;
import org.kie.efesto.common.api.model.GeneratedResources;

import static org.drools.scenariosimulation.backend.util.DMNSimulationUtils.compileModels;

public class DMNScenarioExecutableBuilder {

    public static final String DMN_RESULT = "dmnResult";
    public static final String DMN_MODEL = "dmnModel";

    private final Map<String, Object> dmnContext;
    // default access for testing purpose
    DMNModel dmnModel;
    // default access for testing purpose
    ModelLocalUriId dmnModelLocalUriId;
    // default access for testing purpose
    final Map<String, GeneratedResources> generatedResourcesMap;

    private DMNScenarioExecutableBuilder(Map<String, GeneratedResources> generatedResourcesMap) {
        dmnContext = new HashMap<>();
        this.generatedResourcesMap = generatedResourcesMap;
    }

    public static DMNScenarioExecutableBuilder createBuilder() {
        try {
            Collection<File> dmnFiles =  ResourceHelper.getFileResourcesByExtension("dmn");
            Map<String, GeneratedResources> generatedResourcesMap = compileModels(dmnFiles);
            return new DMNScenarioExecutableBuilder(generatedResourcesMap);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings( "rawtypes")
    public void setActiveModel(String fileName, String modelName) {
        GeneratedResources dmnGeneratedResources = generatedResourcesMap.get("dmn");
        ModelLocalUriId targetModelLocalUriId = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(fileName, modelName);
        GeneratedModelResource generatedModelResource = dmnGeneratedResources.stream()
                .filter(GeneratedModelResource.class::isInstance)
                .map(GeneratedModelResource.class::cast)
                .filter(genRes -> genRes.getModelLocalUriId().equals(targetModelLocalUriId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find generated model resource for " + targetModelLocalUriId));
        dmnModelLocalUriId = generatedModelResource.getModelLocalUriId();
        dmnModel = (DMNModel) generatedModelResource.getCompiledModel();
    }

    public void setValue(String key, Object value) {
        dmnContext.put(key, value);
    }

    public RequestContext run() {
        EfestoOutputDMN efestoOutput = (EfestoOutputDMN) DMNSimulationUtils.getEfestoOutput(generatedResourcesMap, dmnModelLocalUriId, dmnContext);
        RequestContext requestContext = ExecutableRunner.create().createContext();
        requestContext.setResult(efestoOutput.getOutputData());
        requestContext.setOutput(DMN_MODEL, dmnModel);
        requestContext.setOutput(DMN_RESULT, efestoOutput.getOutputData());
        return requestContext;
    }
}
