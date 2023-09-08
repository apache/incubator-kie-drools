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
package org.kie.drl.engine.testingmodule.runtime;

import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.drl.api.identifiers.DrlIdFactory;
import org.kie.drl.api.identifiers.KieDrlComponentRoot;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.drl.engine.testingmodule.utils.DrlTestUtils;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.efesto.runtimemanager.core.service.RuntimeManagerImpl;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeDrlTest {

    private static RuntimeManager runtimeManager;
    private static EfestoRuntimeContext context;

    private static final String basePath = "TestingRule";

    @BeforeAll
    static void setUp() {
        DrlTestUtils.refreshDrlIndexFile();
        runtimeManager = new RuntimeManagerImpl();
        context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void evaluateWithKieSessionLocalStaticCompilation() {
        ModelLocalUriId modelLocalUriId = new EfestoAppRoot()
                .get(KieDrlComponentRoot.class)
                .get(DrlIdFactory.class)
                .get(basePath);
        EfestoInputDrlKieSessionLocal toEvaluate = new EfestoInputDrlKieSessionLocal(modelLocalUriId, "");
        Collection<EfestoOutput> output = runtimeManager.evaluateInput(context, toEvaluate);
        assertThat(output).isNotNull().hasSize(1);
        EfestoOutput<?> retrievedRaw = output.iterator().next();
        assertThat(retrievedRaw).isInstanceOf(EfestoOutputDrlKieSessionLocal.class);
        EfestoOutputDrlKieSessionLocal retrieved = (EfestoOutputDrlKieSessionLocal) retrievedRaw;
        assertThat(retrieved.getOutputData()).isNotNull().isInstanceOf(KieSession.class);
    }
}
