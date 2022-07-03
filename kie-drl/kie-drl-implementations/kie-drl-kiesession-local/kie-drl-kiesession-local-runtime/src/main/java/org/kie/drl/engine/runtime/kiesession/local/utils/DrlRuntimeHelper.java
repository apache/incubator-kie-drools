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
package org.kie.drl.engine.runtime.kiesession.local.utils;

import java.util.Optional;

import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.drl.engine.runtime.utils.EfestoKieSessionUtil.loadKieSession;
import static org.kie.efesto.common.api.model.FRI.SLASH;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;

public class DrlRuntimeHelper {

    private static final Logger logger = LoggerFactory.getLogger(DrlRuntimeHelper.class.getName());


    private DrlRuntimeHelper() {
    }


    public static boolean canManage(EfestoInput toEvaluate) {
        return (toEvaluate instanceof EfestoInputDrlKieSessionLocal) && getGeneratedExecutableResource(toEvaluate.getFRI(), "drl").isPresent();
    }

    public static Optional<EfestoOutputDrlKieSessionLocal> execute(EfestoInputDrlKieSessionLocal toEvaluate, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        KieSession kieSession;
        try {
            kieSession = loadKieSession(toEvaluate.getFRI(), memoryCompilerClassLoader);
        } catch (Exception e) {
            logger.warn("{} can not execute {}",
                    DrlRuntimeHelper.class.getName(),
                    toEvaluate.getFRI());
            return Optional.empty();
        }
        if (kieSession == null) {
            return Optional.empty();
        }
        try {
            String sessionPath = toEvaluate.getFRI().getBasePath() + SLASH + kieSession.getIdentifier();
            FRI sessionFRI = new FRI(sessionPath, "drl");
            return Optional.of(new EfestoOutputDrlKieSessionLocal(sessionFRI, kieSession));
        } catch (Exception e) {
            throw new KieRuntimeServiceException(String.format("%s failed to execute %s",
                    DrlRuntimeHelper.class.getName(),
                    toEvaluate.getFRI()));
        }
    }

}
