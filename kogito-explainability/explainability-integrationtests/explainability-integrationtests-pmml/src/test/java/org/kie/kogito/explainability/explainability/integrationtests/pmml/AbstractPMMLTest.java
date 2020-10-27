/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.explainability.integrationtests.pmml;

import java.io.File;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryImpl;

public abstract class AbstractPMMLTest {

    public static KieContainer kieContainer;
    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = new PMMLRuntimeFactoryImpl();

    static {
        final KieServices kieServices = KieServices.get();
        kieContainer = kieServices.newKieClasspathContainer();
    }

    protected PMMLRuntime pmmlRuntime;

    public AbstractPMMLTest(PMMLRuntime pmmlRuntime) {
        this.pmmlRuntime = pmmlRuntime;
    }

    public static PMMLRuntime getPMMLRuntime(final File pmmlFile) {
        return PMML_RUNTIME_FACTORY.getPMMLRuntimeFromFile(pmmlFile);
    }
}
