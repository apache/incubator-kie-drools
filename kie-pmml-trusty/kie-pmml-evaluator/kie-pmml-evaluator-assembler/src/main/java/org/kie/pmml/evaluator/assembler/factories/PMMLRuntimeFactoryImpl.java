/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.evaluator.assembler.factories;

import java.io.File;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.runtime.PMMLRuntime;

/**
 * Publicly-available facade to hide internal implementation details
 */
public class PMMLRuntimeFactoryImpl implements PMMLRuntimeFactory {

    @Override
    public PMMLRuntime getPMMLRuntimeFromFile(File pmmlFile) {
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile);
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromClasspath(String pmmlFileName) {
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFileName);
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromKieContainerByKieBase(String kieBase, String pmmlFileName, String gav) {
        ReleaseId releaseId = new ReleaseIdImpl(gav);
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(kieBase,  pmmlFileName, releaseId);
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromKieContainerByDefaultKieBase(String pmmlFileName, String gav) {
        ReleaseId releaseId = new ReleaseIdImpl(gav);
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFileName, releaseId);
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromFileNameModelNameAndKieBase(String pmmlFileName, String pmmlModelName,
                                                                     KieBase kieBase) {
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFileName, pmmlModelName, kieBase);
    }

}
