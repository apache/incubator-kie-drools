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
package org.drools.impact.analysis.parser.internal;

import java.util.function.BiFunction;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.kie.internal.builder.KnowledgeBuilder;

public class ImpactAnalysisKieProject extends KieModuleKieProject {

    public static BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> create() {
        return (internalKieModule, classLoader) -> new ImpactAnalysisKieProject(internalKieModule, classLoader);
    }

    protected ImpactModelBuilderImpl modelBuilder;

    public ImpactAnalysisKieProject( InternalKieModule kieModule, ClassLoader classLoader) {
        super(new ImpactAnalysisKieModule(kieModule), classLoader);
    }

    @Override
    protected KnowledgeBuilder createKnowledgeBuilder(KieBaseModelImpl kBaseModel, InternalKieModule kModule) {
        if (getInternalKieModule().getKieModuleModel() != kBaseModel.getKModule()) {
            // if the KieBase belongs to a different kmodule it is not necessary to build it
            return null;
        }
        KnowledgeBuilderConfigurationImpl builderConfiguration = getBuilderConfiguration(kBaseModel, kModule);
        modelBuilder = new ImpactModelBuilderImpl(builderConfiguration, kModule.getReleaseId());
        return modelBuilder;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs, BuildContext buildContext) {
        ImpactAnalysisKieModule kmodule = (ImpactAnalysisKieModule) getInternalKieModule();
        kmodule.setAnalysisModel( modelBuilder.getAnalysisModel() );
    }

    @Override
    protected boolean compileIncludedKieBases() {
        return false;
    }


}