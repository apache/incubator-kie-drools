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
package org.kie.drl.engine.runtime.utils;

import org.drools.model.Model;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoKieSessionUtilTest {

    private static final String fullModelResourcesSourceClassName = "org.kie.drl.engine.compilation.model.test.Rulesefe9b92fdd254fbabc9e9002be0d51d6";

    private static final String basePath = "/TestingRule";
    private static EfestoRuntimeContext context;

    @BeforeAll
    static void setUp() {
        context = EfestoRuntimeContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void loadKieSession() {
        FRI fri = new FRI(basePath, "drl");
        KieSession retrieved = EfestoKieSessionUtil.loadKieSession(fri, context);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getIdentifier()).isZero();
    }

    @Test
    void loadModel() {
        Model retrieved = EfestoKieSessionUtil.loadModel(fullModelResourcesSourceClassName, context);
        assertThat(retrieved).isNotNull();
    }
}