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

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.FileSystemResource;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Factory</b> class to hide implementation details to end user
 */
public class PMMLRuntimeFactoryInternal {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeFactoryInternal.class);

    private PMMLRuntimeFactoryInternal() {
        // Avoid instantiation
    }

    public static PMMLRuntimeInternal getPMMLRuntime(String modelName, File pmmlFile) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder();
        return getPMMLRuntime(modelName, pmmlFile, kbuilderImpl);
    }

    public static PMMLRuntimeInternal getPMMLRuntime(String modelName, File pmmlFile, ReleaseId releaseId) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.setReleaseId(releaseId);
        return getPMMLRuntime(modelName, pmmlFile, kbuilderImpl);
    }

    private static PMMLRuntimeInternal getPMMLRuntime(String modelName, File pmmlFile, KnowledgeBuilderImpl kbuilderImpl) {
        FileSystemResource fileSystemResource = new FileSystemResource(pmmlFile);
        new PMMLAssemblerService().addResource(kbuilderImpl, fileSystemResource, ResourceType.PMML, null);
        InternalKnowledgeBase kieBase = KnowledgeBaseFactory.newKnowledgeBase(modelName, new RuleBaseConfiguration());
        kieBase.addPackages( kbuilderImpl.getKnowledgePackages() );
        return getPMMLRuntime(kieBase);
    }

    private static PMMLRuntimeInternal getPMMLRuntime(KieBase kieBase) {
        final KieRuntimeFactory kieRuntimeFactory = KieRuntimeFactory.of(kieBase);
        return kieRuntimeFactory.get(PMMLRuntimeInternal.class);
    }
}
