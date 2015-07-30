/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.kproject.xml.PomModel;
import org.drools.core.common.ResourceProvider;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.definition.KnowledgePackage;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface InternalKieModule extends KieModule {

    void cacheKnowledgeBuilderForKieBase(String kieBaseName, KnowledgeBuilder kbuilder);

    KnowledgeBuilder getKnowledgeBuilderForKieBase(String kieBaseName);

    Collection<KnowledgePackage> getKnowledgePackagesForKieBase(String kieBaseName);

    void cacheResultsForKieBase(String kieBaseName, Results results);

    Map<String, Results> getKnowledgeResultsCache();    
    
    KieModuleModel getKieModuleModel();    
    
    byte[] getBytes( );  
    
    boolean hasResource( String fileName );
    Resource getResource( String fileName );

    ResourceConfiguration getResourceConfiguration( String fileName );
    
    Map<ReleaseId, InternalKieModule> getKieDependencies();
    
    void addKieDependency(InternalKieModule dependency);

    Collection<ReleaseId> getJarDependencies();

    Collection<ReleaseId> getUnresolvedDependencies();
    void setUnresolvedDependencies(Collection<ReleaseId> unresolvedDependencies);

    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    
    Collection<String> getFileNames();  
    
    File getFile();

    ResourceProvider createResourceProvider();

    Map<String, byte[]> getClassesMap(boolean includeTypeDeclarations);

    boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, String fileName);

    long getCreationTimestamp();

    InputStream getPomAsStream();

    PomModel getPomModel();
}
