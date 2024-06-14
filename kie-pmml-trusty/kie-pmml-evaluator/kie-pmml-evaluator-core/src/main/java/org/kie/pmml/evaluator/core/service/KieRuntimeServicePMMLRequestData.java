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

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.core.utils.JSONUtils;
import org.kie.pmml.api.dto.PMML4Result;
import org.kie.pmml.api.dto.PMMLRequestData;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.canManageEfestoInput;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.executeEfestoInput;

public class KieRuntimeServicePMMLRequestData implements KieRuntimeService<PMMLRequestData, PMML4Result,
        EfestoInput<PMMLRequestData>, EfestoOutputPMML, EfestoRuntimeContext> {

    private static final ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(KieRuntimeServicePMMLRequestData.class);


    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(BaseEfestoInput.class, PMMLRequestData.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return canManageEfestoInput(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputPMML> evaluateInput(EfestoInput<PMMLRequestData> toEvaluate,
                                                    EfestoRuntimeContext context) {
        return executeEfestoInput(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return PMML_STRING;
    }

    @Override
    public Optional<EfestoInput<PMMLRequestData>> parseJsonInput(String modelLocalUriIdString, String inputDataString) {
        ModelLocalUriId modelLocalUriId;
        try {
            modelLocalUriId = objectMapper.readValue(modelLocalUriIdString, AbstractModelLocalUriIdPmml.class);
            if (!modelLocalUriId.model().equals(getModelType())) {
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.warn("Failed to parse {} as AbstractModelLocalUriIdPmml", modelLocalUriIdString);
            return Optional.empty();
        }
        PMMLRequestData inputData;
        try {
            inputData = objectMapper.readValue(inputDataString, PMMLRequestData.class);
        } catch (Exception e) {
            logger.warn("Failed to parse {} as PMMLRequestData", inputDataString);
            return Optional.empty();
        }
        return Optional.of(new BaseEfestoInput<>(modelLocalUriId, inputData));
    }
}
