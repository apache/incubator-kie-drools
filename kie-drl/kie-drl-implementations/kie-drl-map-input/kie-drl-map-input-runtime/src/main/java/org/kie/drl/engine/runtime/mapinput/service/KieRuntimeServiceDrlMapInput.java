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
package org.kie.drl.engine.runtime.mapinput.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.drl.engine.runtime.mapinput.model.EfestoOutputDrlMap;
import org.kie.drl.engine.runtime.mapinput.utils.DrlRuntimeHelper;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.common.core.utils.JSONUtils;
import org.kie.efesto.runtimemanager.api.model.*;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import java.util.*;

import static org.kie.efesto.common.core.utils.JSONUtils.getInputData;
import static org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils.buildWithParentClassLoader;

public class KieRuntimeServiceDrlMapInput implements KieRuntimeService<EfestoMapInputDTO, Map<String, Object>,
        BaseEfestoInput<EfestoMapInputDTO>, EfestoOutputDrlMap, EfestoRuntimeContext> {


    private static final ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(BaseEfestoInput.class, EfestoMapInputDTO.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return DrlRuntimeHelper.canManage(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputDrlMap> evaluateInput(BaseEfestoInput<EfestoMapInputDTO> toEvaluate,
                                                      EfestoRuntimeContext context) {
        if (!(context instanceof EfestoLocalRuntimeContext)) {
            context = buildWithParentClassLoader(context.getClass().getClassLoader(), context.getGeneratedResourcesMap());
        }
        return DrlRuntimeHelper.execute(toEvaluate, (EfestoLocalRuntimeContext) context);
    }

    @Override
    public String getModelType() {
        return "drl";
    }

    @Override
    public Optional<BaseEfestoInput<EfestoMapInputDTO>> parseJsonInput(String modelLocalUriIdString, String inputDataString) {
        ModelLocalUriId modelLocalUriId;
        try {
            modelLocalUriId = objectMapper.readValue(modelLocalUriIdString, ModelLocalUriId.class);
            if (!modelLocalUriId.model().equals(getModelType())) {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to parse %s as ModelLocalUriId", modelLocalUriIdString));
        }
        Map<String, Object> inputData;
        try {
            inputData = getInputData(inputDataString);
            return Optional.of(new BaseEfestoInput<>(modelLocalUriId, getDrlMapInput(inputData)));
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to parse %s as Map<String, Object>", inputDataString));
        }
    }

    private static EfestoMapInputDTO getDrlMapInput(Map<String, Object> inputData) {
        List<Object> inserts = new ArrayList<>();
        if (inputData.containsKey("inserts")) {
            inserts = (List<Object>) inputData.get("inserts");
            inputData.remove("inserts");
        }
        Map<String, Object> globals = new HashMap<>();
        if (inputData.containsKey("globals")) {
            globals = (Map<String, Object>) inputData.get("globals");
            inputData.remove("globals");
        }
        String packageName = (String) inputData.get("package");
        inputData.remove("package");
        String modelName = (String) inputData.get("modelName");
        inputData.remove("modelName");
        final Map<String, EfestoOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        inputData.forEach((s, o) -> {
            String objectType = o.getClass().getCanonicalName();
            EfestoOriginalTypeGeneratedType toPut = new EfestoOriginalTypeGeneratedType(objectType, objectType);
            fieldTypeMap.put(s, toPut);
        });
        return new EfestoMapInputDTO(inserts, globals, inputData, fieldTypeMap, modelName, packageName);
    }
}
