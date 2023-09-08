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
package org.kie.drl.engine.runtime.kiesession.local.utils;

import java.util.Optional;

import org.kie.api.runtime.KieSession;
import org.kie.drl.api.identifiers.DrlSessionIdFactory;
import org.kie.drl.api.identifiers.KieDrlComponentRoot;
import org.kie.drl.api.identifiers.LocalComponentIdDrlSession;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.drl.engine.runtime.utils.EfestoKieSessionUtil.loadKieSession;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;

public class DrlRuntimeHelper {

    private static final Logger logger = LoggerFactory.getLogger(DrlRuntimeHelper.class.getName());

    private DrlRuntimeHelper() {
    }

    public static boolean canManage(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return getGeneratedExecutableResource(toEvaluate.getModelLocalUriId(), context.getGeneratedResourcesMap()).isPresent();
    }

    public static Optional<EfestoOutputDrlKieSessionLocal> execute(EfestoInputDrlKieSessionLocal toEvaluate, EfestoRuntimeContext context) {
        KieSession kieSession;
        try {
            kieSession = loadKieSession(toEvaluate.getModelLocalUriId(), context);
        } catch (Exception e) {
            logger.warn("{} can not execute {}",
                        DrlRuntimeHelper.class.getName(),
                        toEvaluate.getModelLocalUriId(),
                        e);
            return Optional.empty();
        }
        if (kieSession == null) {
            return Optional.empty();
        }
        try {
            LocalComponentIdDrlSession modelLocalUriId = new EfestoAppRoot()
                    .get(KieDrlComponentRoot.class)
                    .get(DrlSessionIdFactory.class)
                    .get(toEvaluate.getModelLocalUriId().basePath(), kieSession.getIdentifier());
            return Optional.of(new EfestoOutputDrlKieSessionLocal(modelLocalUriId, kieSession));
        } catch (Exception e) {
            throw new KieRuntimeServiceException(String.format("%s failed to execute %s",
                    DrlRuntimeHelper.class.getName(),
                    toEvaluate.getModelLocalUriId()));
        }
    }

}
