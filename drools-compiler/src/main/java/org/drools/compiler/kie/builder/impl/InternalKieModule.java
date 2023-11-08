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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.util.ChangeSetBuilder;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.io.InternalResource;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.util.PortablePath;
import org.drools.wiring.api.ResourceProvider;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.utils.ClassLoaderResolver;
import org.kie.internal.utils.NoDepsClassLoaderResolver;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.PomModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.buildKieModule;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.drools.wiring.api.classloader.ProjectClassLoader.createProjectClassLoader;

public interface InternalKieModule extends KieModule, Serializable {

    void cacheKnowledgeBuilderForKieBase(String kieBaseName, KnowledgeBuilder kbuilder);

    KnowledgeBuilder getKnowledgeBuilderForKieBase(String kieBaseName);

    Collection<KiePackage> getKnowledgePackagesForKieBase(String kieBaseName);

    InternalKnowledgePackage getPackage(String packageName);

    void cacheResultsForKieBase( String kieBaseName, Results results);

    Map<String, Results> getKnowledgeResultsCache();    
    
    KieModuleModel getKieModuleModel();    
    
    byte[] getBytes( );  
    
    boolean hasResource( String fileName );
    InternalResource getResource( String fileName );

    ResourceConfiguration getResourceConfiguration( String fileName );
    
    Map<ReleaseId, InternalKieModule> getKieDependencies();
    
    void addKieDependency(InternalKieModule dependency);

    Collection<ReleaseId> getJarDependencies(DependencyFilter filter);

    Collection<ReleaseId> getUnresolvedDependencies();
    void setUnresolvedDependencies(Collection<ReleaseId> unresolvedDependencies);

    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    default byte[] getBytes( final PortablePath resourcePath ) {
        return getBytes(resourcePath.asString());
    }

    Collection<String> getFileNames();  
    
    File getFile();

    ResourceProvider createResourceProvider();

    Map<String, byte[]> getClassesMap();

    boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName);
    boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs);

    long getCreationTimestamp();

    InputStream getPomAsStream();

    PomModel getPomModel();

    KnowledgeBuilderConfiguration createBuilderConfiguration( KieBaseModel kBaseModel, ClassLoader classLoader );

    InternalKnowledgeBase createKieBase(KieBaseModelImpl kBaseModel, KieProject kieProject, BuildContext buildContext, KieBaseConfiguration conf);

    default void afterKieBaseCreationUpdate(String name, InternalKnowledgeBase kBase) { }

    ClassLoader getModuleClassLoader();

    default ResultsImpl build() {
        BuildContext buildContext = new BuildContext();
        buildKieModule(this, buildContext);
        return buildContext.getMessages();
    }

    default KieJarChangeSet getChanges(InternalKieModule newKieModule) {
        return ChangeSetBuilder.build( this, newKieModule );
    }

    default boolean isFileInKBase(KieBaseModel kieBase, String fileName) {
        return filterFileInKBase(this, kieBase, fileName, () -> getResource( fileName ), false);
    }

    default KieBaseUpdater createKieBaseUpdater(KieBaseUpdaterImplContext context) {
        return new KieBaseUpdaterImpl(context );
    }

    default ProjectClassLoader createModuleClassLoader( ClassLoader parent ) {
        if( parent == null ) {
            ClassLoaderResolver resolver = KieService.load(ClassLoaderResolver.class);
            if (resolver==null)  {
                resolver = new NoDepsClassLoaderResolver();
            }
            parent = resolver.getClassLoader( this );
        }
        return createProjectClassLoader( parent, createResourceProvider() );
    }

    default CompilationCache getCompilationCache( String kbaseName) { return null; }

    default InternalKieModule cloneForIncrementalCompilation(ReleaseId releaseId, KieModuleModel kModuleModel, MemoryFileSystem newFs) {
        throw new UnsupportedOperationException();
    }

    static InternalKieModule createKieModule(ReleaseId releaseId, File jar) {
        if (jar.isDirectory() || !jar.getPath().endsWith( ".jar" )) {
            return null;
        }
        try (ZipFile zipFile = new ZipFile(jar)) {
            ZipEntry zipEntry = zipFile.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH.asString());
            if (zipEntry != null) {
                return internalCreateKieModule( releaseId, jar, zipFile, zipEntry );
            }
        } catch (MalformedKieModuleException e) {
            // if the kie module exists but it's malformed raise the error
            throw e;
        } catch (IOException e) {
            // ignore: the zip file could be empty or not a jar at all
        }
        return null;
    }

    static InternalKieModule internalCreateKieModule( ReleaseId releaseId, File jar, ZipFile zipFile, ZipEntry zipEntry ) throws MalformedKieModuleException {
        try (InputStream xmlStream = zipFile.getInputStream( zipEntry )) {
            KieModuleModel kieModuleModel = KieModuleModelImpl.fromXML( xmlStream );
            setDefaultsforEmptyKieModule( kieModuleModel );
            return kieModuleModel != null ? InternalKieModuleProvider.get( releaseId, kieModuleModel, jar ) : null;
        } catch (Exception e) {
            throw new MalformedKieModuleException( e );
        }
    }

    class MalformedKieModuleException extends RuntimeException {
        MalformedKieModuleException(Exception cause) {
            super(cause);
        }
    }

    default void updateKieModule(InternalKieModule newKM) {}

    default void addGeneratedClassNames(Set<String> classNames) {}

    class CompilationCache implements Serializable {
        private static final long serialVersionUID = 3812243055974412935L;
        // this is a { DIALECT -> ( RESOURCE, List<CompilationEntry> ) } cache
        protected final Map<String, Map<String, List<CompilationCacheEntry>>> compilationCache = new HashMap<>();

        public void addEntry(String dialect, String className, byte[] bytecode) {
            Map<String, List<CompilationCacheEntry>> resourceEntries = compilationCache.get(dialect);
            if( resourceEntries == null ) {
                resourceEntries = new HashMap<>();
                compilationCache.put(dialect, resourceEntries);
            }

            String key = className.contains("$") ? className.substring(0, className.indexOf('$') ) + ".class" : className;
            List<CompilationCacheEntry> bytes = resourceEntries.get(key);
            if( bytes == null ) {
                bytes = new ArrayList<>();
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

    default boolean isVerifiable() {
        return true;
    }
}
