/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.evaluator.core.service;

import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.canManage;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.execute;

public class KieRuntimeServicePMML implements KieRuntimeService<PMMLRuntimeContext, PMML4Result, EfestoInputPMML, EfestoOutputPMML, PMMLRuntimeContext> {

    private static final Logger logger = LoggerFactory.getLogger(KieRuntimeServicePMML.class.getName());



    @Override
    public boolean canManageInput(EfestoInput toEvaluate, PMMLRuntimeContext context) {
        return canManage(toEvaluate);
    }

    @Override
    public Optional<EfestoOutputPMML> evaluateInput(EfestoInputPMML toEvaluate, PMMLRuntimeContext context) {
        return execute(toEvaluate, context);
    }

}
