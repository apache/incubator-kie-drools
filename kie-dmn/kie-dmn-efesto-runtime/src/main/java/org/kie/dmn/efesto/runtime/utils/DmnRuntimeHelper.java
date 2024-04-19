/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.efesto.runtime.utils;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.efesto.runtime.model.EfestoOutputDMN;
import org.kie.dmn.efesto.runtime.service.DMNEvaluator;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.common.api.model.GeneratedModelResource;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedModelResource;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.isPresentExecutableOrModelOrRedirect;

public class DmnRuntimeHelper {

    private static final Logger logger = LoggerFactory.getLogger(DmnRuntimeHelper.class.getName());

    private DmnRuntimeHelper() {
    }

    public static boolean canManage(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return isPresentExecutableOrModelOrRedirect(toEvaluate.getModelLocalUriId(), context);
    }

    public static Optional<EfestoOutputDMN> execute(EfestoInput<Map<String, Object>> toEvaluate, EfestoLocalRuntimeContext runtimeContext) {
        ModelLocalUriId modelLocalUriId = toEvaluate.getModelLocalUriId();
        Optional<GeneratedModelResource> generatedModelResource = getGeneratedModelResource(modelLocalUriId, runtimeContext.getGeneratedResourcesMap());
        return generatedModelResource.map(it -> execute(it.getModelSource(), modelLocalUriId, toEvaluate.getInputData()));
    }

    static EfestoOutputDMN execute(String modelSource, ModelLocalUriId modelLocalUriId, Map<String, Object> inputData) {
        try {
            DMNEvaluator dmnEvaluator = DMNEvaluator.fromXML(modelSource);
            DMNResult dmnResult = dmnEvaluator.evaluate(inputData);
            return new EfestoOutputDMN(modelLocalUriId, dmnResult);
        } catch (Exception e) {
            logger.error("Failed to evaluate {}", inputData, e);
            return null;
        }
    }

}
