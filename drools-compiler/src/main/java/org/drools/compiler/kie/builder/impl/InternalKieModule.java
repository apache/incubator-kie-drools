/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.kie.util.ChangeSetBuilder;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.common.ResourceProvider;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.utils.ClassLoaderResolver;
import org.kie.internal.utils.NoDepsClassLoaderResolver;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.buildKieModule;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.core.common.ProjectClassLoader.createProjectClassLoader;

public interface InternalKieModule extends KieModule, Serializable {

    void cacheKnowledgeBuilderForKieBase(String kieBaseName, KnowledgeBuilder kbuilder);

    KnowledgeBuilder getKnowledgeBuilderForKieBase(String kieBaseName);

    Collection<KiePackage> getKnowledgePackagesForKieBase(String kieBaseName);

    void cacheResultsForKieBase(String kieBaseName, Results results);

    Map<String, Results> getKnowledgeResultsCache();    
    
    KieModuleModel getKieModuleModel();    
    
    byte[] getBytes( );  
    
    boolean hasResource( String fileName );
    Resource getResource( String fileName );

    ResourceConfiguration getResourceConfiguration( String fileName );
    
    Map<ReleaseId, InternalKieModule> getKieDependencies();
    
    void addKieDependency(InternalKieModule dependency);

    Collection<ReleaseId> getJarDependencies(DependencyFilter filter);

    Collection<ReleaseId> getUnresolvedDependencies();
    void setUnresolvedDependencies(Collection<ReleaseId> unresolvedDependencies);

    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    
    Collection<String> getFileNames();  
    
    File getFile();

    ResourceProvider createResourceProvider();

    Map<String, byte[]> getClassesMap(boolean includeTypeDeclarations);

    boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName);
    boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs);

    long getCreationTimestamp();

    InputStream getPomAsStream();

    PomModel getPomModel();

    KnowledgeBuilderConfiguration getBuilderConfiguration( KieBaseModel kBaseModel );

    InternalKnowledgeBase createKieBase( KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf );

    default ResultsImpl build() {
        ResultsImpl messages = new ResultsImpl();
        buildKieModule(this, messages);
        return messages;
    }

    default KieJarChangeSet getChanges(InternalKieModule newKieModule) {
        return ChangeSetBuilder.build( this, newKieModule );
    }

    default boolean isFileInKBase(KieBaseModel kieBase, String fileName) {
        return filterFileInKBase(this, kieBase, fileName);
    }

    default Runnable createKieBaseUpdater(KieBaseUpdateContext context) {
        return new KieBaseUpdater( context );
    }

    default ProjectClassLoader createModuleClassLoader( ClassLoader parent ) {
        if( parent == null ) {
            ClassLoaderResolver resolver = ServiceRegistry.getInstance().get(ClassLoaderResolver.class);
            if (resolver==null)  {
                resolver = new NoDepsClassLoaderResolver();
            }
            parent = resolver.getClassLoader( this );
        }
        return createProjectClassLoader( parent, createResourceProvider() );
    }
}
