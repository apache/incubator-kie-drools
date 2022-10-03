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
package org.kie.drl.engine.testingmodule;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.drl.api.identifiers.DrlIdFactory;
import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.drl.engine.testingmodule.utils.DrlTestUtils;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.efesto.runtimemanager.core.service.RuntimeManagerImpl;

import static org.assertj.core.api.Assertions.assertThat;

class OnTheFlyDrlTest {

    private static RuntimeManager runtimeManager;
    private static CompilationManager compilationManager;

    @BeforeAll
    static void setUp() {
        runtimeManager = new RuntimeManagerImpl();
        compilationManager = new CompilationManagerImpl();
    }

    @Test
    void evaluateWithKieSessionLocalCompilationOnTheFly() throws IOException {
        String onTheFlyPath = "OnTheFlyPath";
        Set<File> files = DrlTestUtils.collectDrlFiles("src/test/resources/org/drools/model/project/codegen");
        EfestoResource<Set<File>> toProcess = new DrlFileSetResource(files, onTheFlyPath);
        DrlCompilationContext compilationContext = DrlCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        compilationManager.processResource(compilationContext, toProcess);

        // Suppose we cannot access the previous compilationContext
        EfestoRuntimeContext runtimeContext = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader(), compilationContext.getGeneratedResourcesMap());
        ModelLocalUriId modelLocalUriId = new ReflectiveAppRoot("")
                .get(DrlIdFactory.class)
                .get(onTheFlyPath);
        EfestoInputDrlKieSessionLocal toEvaluate = new EfestoInputDrlKieSessionLocal(modelLocalUriId, "");
        Collection<EfestoOutput> output = runtimeManager.evaluateInput(runtimeContext, toEvaluate);
        assertThat(output).isNotNull().hasSize(1);
        EfestoOutput retrievedRaw = output.iterator().next();
        assertThat(retrievedRaw).isInstanceOf(EfestoOutputDrlKieSessionLocal.class);
        EfestoOutputDrlKieSessionLocal retrieved = (EfestoOutputDrlKieSessionLocal) retrievedRaw;
        assertThat(retrieved.getOutputData()).isNotNull().isInstanceOf(KieSession.class);

        KieSession session = retrieved.getOutputData();
        session.insert("test");
        assertThat(session.fireAllRules()).isEqualTo(3);
    }
}
