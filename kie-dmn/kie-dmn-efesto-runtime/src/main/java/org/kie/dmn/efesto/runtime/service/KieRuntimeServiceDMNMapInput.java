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
package org.kie.dmn.efesto.runtime.service;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.efesto.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.efesto.runtime.model.EfestoOutputDMN;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.kie.dmn.efesto.runtime.utils.DmnRuntimeHelper.canManage;
import static org.kie.dmn.efesto.runtime.utils.DmnRuntimeHelper.execute;
import static org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils.buildWithParentClassLoader;

public class KieRuntimeServiceDMNMapInput implements KieRuntimeService<Map<String, Object>, DMNResult, EfestoInput<Map<String, Object>>, EfestoOutputDMN, EfestoRuntimeContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieRuntimeServiceDMNMapInput.class);

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(BaseEfestoInput.class, HashMap.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return canManage(toEvaluate, context)
                && toEvaluate.getModelLocalUriId() instanceof LocalComponentIdDmn;
    }

    @Override
    public Optional<EfestoOutputDMN> evaluateInput(EfestoInput<Map<String, Object>> toEvaluate,
                                                   EfestoRuntimeContext context) {
        if (!canManageInput(toEvaluate, context)) {
            throw new KieRuntimeServiceException("Wrong parameters " + toEvaluate + " " + context);
        }
        if (!(context instanceof EfestoLocalRuntimeContext)) {
            context = buildWithParentClassLoader(context.getClass().getClassLoader(), context.getGeneratedResourcesMap());
        }
        return execute(toEvaluate, (EfestoLocalRuntimeContext) context);
    }

    @Override
    public String getModelType() {
        return "dmn";
    }

    @Override
    public Optional<EfestoInput<Map<String, Object>>> parseJsonInput(String modelLocalUriIdString, String inputDataString) {
        return Optional.empty();
    }

}
