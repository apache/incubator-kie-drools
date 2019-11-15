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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.kie.util.ChangeSetBuilder;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.reflective.ResourceProvider;
import org.drools.reflective.classloader.ProjectClassLoader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.buildKieModule;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.drools.compiler.kproject.ReleaseIdImpl.adapt;
import static org.drools.reflective.classloader.ProjectClassLoader.createProjectClassLoader;

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

    Map<String, byte[]> getClassesMap();

    boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName);
    boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs);

    long getCreationTimestamp();

    InputStream getPomAsStream();

    PomModel getPomModel();

    KnowledgeBuilderConfiguration getBuilderConfiguration( KieBaseModel kBaseModel, ClassLoader classLoader );

    InternalKnowledgeBase createKieBase( KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf );

    ClassLoader getModuleClassLoader();

    default ResultsImpl build() {
        ResultsImpl messages = new ResultsImpl();
        buildKieModule(this, messages);
        return messages;
    }

    default KieJarChangeSet getChanges(InternalKieModule newKieModule) {
        return ChangeSetBuilder.build( this, newKieModule );
    }

    default boolean isFileInKBase(KieBaseModel kieBase, String fileName) {
        return filterFileInKBase(this, kieBase, fileName, () -> getBytes( fileName ), false);
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

    default CompilationCache getCompilationCache( String kbaseName) { return null; }

    static InternalKieModule createKieModule(ReleaseId releaseId, File jar) {
        try (ZipFile zipFile = new ZipFile(jar)) {
            ZipEntry zipEntry = zipFile.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH);
            if (zipEntry != null) {
                KieModuleModel kieModuleModel = KieModuleModelImpl.fromXML( zipFile.getInputStream( zipEntry ) );
                setDefaultsforEmptyKieModule( kieModuleModel );
                return kieModuleModel != null ? InternalKieModuleProvider.get( adapt( releaseId ), kieModuleModel, jar ) : null;
            }
        } catch (Exception e) {
            LocalLogger.logger.error(e.getMessage(), e);
        }
        return null;
    }

    default void updateKieModule(InternalKieModule newKM) {

    }

    class CompilationCache implements Serializable {
        private static final long serialVersionUID = 3812243055974412935L;
        // this is a { DIALECT -> ( RESOURCE, List<CompilationEntry> ) } cache
        protected final Map<String, Map<String, List<CompilationCacheEntry>>> compilationCache = new HashMap<String, Map<String, List<CompilationCacheEntry>>>();

        public void addEntry(String dialect, String className, byte[] bytecode) {
            Map<String, List<CompilationCacheEntry>> resourceEntries = compilationCache.get(dialect);
            if( resourceEntries == null ) {
                resourceEntries = new HashMap<String, List<CompilationCacheEntry>>();
                compilationCache.put(dialect, resourceEntries);
            }

            String key = className.contains("$") ? className.substring(0, className.indexOf('$') ) + ".class" : className;
            List<CompilationCacheEntry> bytes = resourceEntries.get(key);
            if( bytes == null ) {
                bytes = new ArrayList<CompilationCacheEntry>();
                resourceEntries.put(key, bytes);
            }
            //System.out.println(String.format("Adding to in-memory cache: %s %s", key, className ));
            bytes.add(new CompilationCacheEntry(className, bytecode));
        }

        public Map<String, List<CompilationCacheEntry>> getCacheForDialect(String dialect) {
            return compilationCache.get(dialect);
        }
    }

    class CompilationCacheEntry implements Serializable {
        private static final long serialVersionUID = 1423987159014688588L;
        public final String className;
        public final byte[] bytecode;

        public CompilationCacheEntry( String className, byte[] bytecode) {
            this.className = className;
            this.bytecode = bytecode;
        }
    }

    final class LocalLogger {
        private static final Logger logger = LoggerFactory.getLogger(InternalKieModule.class);
    }
}
