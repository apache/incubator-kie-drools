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
import java.util.UUID;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.io.impl.DescrResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.api.runtime.PMMLRuntime;
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

    public static PMMLRuntime getPMMLRuntime(final File pmmlFile) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder();
        return getPMMLRuntime(pmmlFile, kbuilderImpl);
    }

    public static PMMLRuntime getPMMLRuntime(File pmmlFile, ReleaseId releaseId) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.setReleaseId(releaseId);
        return getPMMLRuntime(pmmlFile, kbuilderImpl);
    }

    private static PMMLRuntime getPMMLRuntime(File pmmlFile, KnowledgeBuilderImpl kbuilderImpl) {
        FileSystemResource fileSystemResource = new FileSystemResource(pmmlFile);
        new PMMLAssemblerService().addResource(kbuilderImpl, fileSystemResource, ResourceType.PMML, null);
        KieBase kieBase = createKieBase( kbuilderImpl );
        return getPMMLRuntime(kieBase);
    }

    private static PMMLRuntime getPMMLRuntime(KieBase kieBase) {
        final KieRuntimeFactory kieRuntimeFactory = KieRuntimeFactory.of(kieBase);
        return kieRuntimeFactory.get(PMMLRuntime.class);
    }

    private static KieBase createKieBase( KnowledgeBuilderImpl kbuilderImpl ) {
        KieHelper kieHelper = new KieHelper();
        kbuilderImpl.getPackageNames().stream().flatMap( name -> kbuilderImpl.getPackageDescrs( name ).stream() )
                .forEach( pDescr -> kieHelper.addResource( createDescrResource(pDescr) ) );
        KieBase kieBase = kieHelper.build( ExecutableModelProject.class );
        (( InternalKnowledgeBase ) kieBase).addPackages( kbuilderImpl.getKnowledgePackages() );
        return kieBase;
    }

    private static DescrResource createDescrResource( PackageDescr pDescr ) {
        DescrResource resource = new DescrResource( pDescr );
        resource.setSourcePath("src/main/resources/file_" + UUID.randomUUID() + ".descr");
        return resource;
    }
}
