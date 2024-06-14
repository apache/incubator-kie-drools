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
package org.kie.drl.engine.runtime.kiesession.local.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.utils.DrlRuntimeHelper;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.core.utils.JSONUtils;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import java.util.Optional;

import static org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils.buildWithParentClassLoader;


public class KieRuntimeServiceDrlKieSessionLocal implements KieRuntimeService<String, KieSession, EfestoInputDrlKieSessionLocal, EfestoOutputDrlKieSessionLocal, EfestoRuntimeContext> {

    private static final ObjectMapper objectMapper = JSONUtils.getObjectMapper();

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(EfestoInputDrlKieSessionLocal.class, String.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return DrlRuntimeHelper.canManage(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputDrlKieSessionLocal> evaluateInput(EfestoInputDrlKieSessionLocal toEvaluate, EfestoRuntimeContext context) {
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
    public Optional<EfestoInputDrlKieSessionLocal> parseJsonInput(String modelLocalUriIdString, String inputDataString) {
        ModelLocalUriId modelLocalUriId;
        try {
            modelLocalUriId = objectMapper.readValue(modelLocalUriIdString, ModelLocalUriId.class);
            return modelLocalUriId.model().equals(getModelType()) ? Optional.of(new EfestoInputDrlKieSessionLocal(modelLocalUriId, inputDataString)) : Optional.empty();
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to parse %s as ModelLocalUriId", modelLocalUriIdString));
        }
    }
}
