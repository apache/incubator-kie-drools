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
package org.drools.compiler.kie.builder.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.internal.builder.KnowledgeBuilder;

public interface KieProject {
    
    ReleaseId getGAV();
    
    InternalKieModule getKieModuleForKBase(String kBaseName);

    Collection<String> getKieBaseNames();

    KieBaseModel getKieBaseModel(String kBaseName);

    KieBaseModel getDefaultKieBaseModel();

    KieSessionModel getKieSessionModel(String kSessionName);

    KieSessionModel getDefaultKieSession();

    KieSessionModel getDefaultStatelessKieSession();

    void init();   
    
    ClassLoader getClassLoader();

    ResultsImpl verify();
    ResultsImpl verify( String... kModelNames );
    void verify(BuildContext buildContext);

    long getCreationTimestamp();

    Set<String> getTransitiveIncludes(String kBaseName);
    Set<String> getTransitiveIncludes(KieBaseModel kBaseModel);

    InputStream getPomAsStream();

    KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel, BuildContext buildContext );
    KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel, BuildContext buildContext, Predicate<String> buildFilter );

    default void writeProjectOutput(MemoryFileSystem trgMfs, BuildContext buildContext) {}
}
