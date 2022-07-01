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
package org.kie.drl.engine.runtime.mapinput.service;

import java.util.Map;
import java.util.Optional;

import org.kie.drl.engine.runtime.mapinput.model.EfestoOutputDrlMap;
import org.kie.drl.engine.runtime.mapinput.utils.DrlRuntimeHelper;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KieRuntimeServiceDrlMapInput implements KieRuntimeService<EfestoMapInputDTO, Map<String, Object>, AbstractEfestoInput<EfestoMapInputDTO>, EfestoOutputDrlMap> {

    private static final Logger logger = LoggerFactory.getLogger(KieRuntimeServiceDrlMapInput.class.getName());


    @Override
    public boolean canManageInput(EfestoInput toEvaluate, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return DrlRuntimeHelper.canManage(toEvaluate);
    }

    @Override
    public Optional<EfestoOutputDrlMap> evaluateInput(AbstractEfestoInput<EfestoMapInputDTO> toEvaluate, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return DrlRuntimeHelper.execute(toEvaluate, memoryCompilerClassLoader);
    }
}
