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

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterImplContext;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.io.InternalResource;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.wiring.api.ResourceProvider;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.PomModel;

public class ImpactAnalysisKieModule implements InternalKieModule {

    private final InternalKieModule internalKieModule;

    private AnalysisModel analysisModel = new AnalysisModel();

    public ImpactAnalysisKieModule(InternalKieModule internalKieModule) {
        this.internalKieModule = internalKieModule;
    }

    public AnalysisModel getAnalysisModel() {
        return analysisModel;
    }

    public void setAnalysisModel( AnalysisModel analysisModel ) {
        this.analysisModel = analysisModel;
    }

    @Override
    public void cacheKnowledgeBuilderForKieBase( String kieBaseName, KnowledgeBuilder kbuilder ) {
        internalKieModule.cacheKnowledgeBuilderForKieBase( kieBaseName, kbuilder );
    }

    @Override
    public KnowledgeBuilder getKnowledgeBuilderForKieBase( String kieBaseName ) {
        return internalKieModule.getKnowledgeBuilderForKieBase( kieBaseName );
    }

    @Override
    public Collection<KiePackage> getKnowledgePackagesForKieBase( String kieBaseName ) {
        return internalKieModule.getKnowledgePackagesForKieBase( kieBaseName );
    }

    @Override
    public InternalKnowledgePackage getPackage( String packageName ) {
        return internalKieModule.getPackage( packageName );
    }

    @Override
    public void cacheResultsForKieBase( String kieBaseName, Results results ) {
        internalKieModule.cacheResultsForKieBase( kieBaseName, results );
    }

    @Override
    public Map<String, Results> getKnowledgeResultsCache() {
        return internalKieModule.getKnowledgeResultsCache();
    }

    @Override
    public KieModuleModel getKieModuleModel() {
        return internalKieModule.getKieModuleModel();
    }

    @Override
    public byte[] getBytes() {
        return internalKieModule.getBytes();
    }

    @Override
    public boolean hasResource( String fileName ) {
        return internalKieModule.hasResource( fileName );
    }

    @Override
    public InternalResource getResource( String fileName ) {
        return internalKieModule.getResource( fileName );
    }

    @Override
    public ResourceConfiguration getResourceConfiguration( String fileName ) {
        return internalKieModule.getResourceConfiguration( fileName );
    }

    @Override
    public Map<ReleaseId, InternalKieModule> getKieDependencies() {
        return internalKieModule.getKieDependencies();
    }

    @Override
    public void addKieDependency( InternalKieModule dependency ) {
        internalKieModule.addKieDependency( dependency );
    }

    @Override
    public Collection<ReleaseId> getJarDependencies( DependencyFilter filter ) {
        return internalKieModule.getJarDependencies( filter );
    }

    @Override
    public Collection<ReleaseId> getUnresolvedDependencies() {
        return internalKieModule.getUnresolvedDependencies();
    }

    @Override
    public void setUnresolvedDependencies( Collection<ReleaseId> unresolvedDependencies ) {
        internalKieModule.setUnresolvedDependencies( unresolvedDependencies );
    }

    @Override
    public boolean isAvailable( String pResourceName ) {
        return internalKieModule.isAvailable( pResourceName );
    }

    @Override
    public byte[] getBytes( String pResourceName ) {
        return internalKieModule.getBytes( pResourceName );
    }

    @Override
    public Collection<String> getFileNames() {
        return internalKieModule.getFileNames();
    }

    @Override
    public File getFile() {
        return internalKieModule.getFile();
    }

    @Override
    public ResourceProvider createResourceProvider() {
        return internalKieModule.createResourceProvider();
    }

    @Override
    public Map<String, byte[]> getClassesMap() {
        return internalKieModule.getClassesMap();
    }

    @Override
    public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName ) {
        return internalKieModule.addResourceToCompiler( ckbuilder, kieBaseModel, fileName );
    }

    @Override
    public boolean addResourceToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs ) {
        return internalKieModule.addResourceToCompiler( ckbuilder, kieBaseModel, fileName, rcs );
    }

    @Override
    public long getCreationTimestamp() {
        return internalKieModule.getCreationTimestamp();
    }

    @Override
    public InputStream getPomAsStream() {
        return internalKieModule.getPomAsStream();
    }

    @Override
    public PomModel getPomModel() {
        return internalKieModule.getPomModel();
    }

    @Override
    public KnowledgeBuilderConfiguration createBuilderConfiguration( KieBaseModel kBaseModel, ClassLoader classLoader ) {
        return internalKieModule.createBuilderConfiguration( kBaseModel, classLoader );
    }

    @Override
    public InternalKnowledgeBase createKieBase(KieBaseModelImpl kBaseModel, KieProject kieProject, BuildContext buildContext, KieBaseConfiguration conf) {
        return internalKieModule.createKieBase( kBaseModel, kieProject, buildContext, conf );
    }

    @Override
    public void afterKieBaseCreationUpdate( String name, InternalKnowledgeBase kBase) {
        internalKieModule.afterKieBaseCreationUpdate( name, kBase );
    }

    @Override
    public ClassLoader getModuleClassLoader() {
        return internalKieModule.getModuleClassLoader();
    }

    @Override
    public ResultsImpl build() {
        return internalKieModule.build();
    }

    @Override
    public KieJarChangeSet getChanges( InternalKieModule newKieModule ) {
        return internalKieModule.getChanges( newKieModule );
    }

    @Override
    public boolean isFileInKBase( KieBaseModel kieBase, String fileName ) {
        return internalKieModule.isFileInKBase( kieBase, fileName );
    }

    @Override
    public KieBaseUpdater createKieBaseUpdater( KieBaseUpdaterImplContext context ) {
        return internalKieModule.createKieBaseUpdater( context );
    }

    @Override
    public ProjectClassLoader createModuleClassLoader( ClassLoader parent ) {
        return internalKieModule.createModuleClassLoader( parent );
    }

    @Override
    public CompilationCache getCompilationCache( String kbaseName ) {
        return internalKieModule.getCompilationCache( kbaseName );
    }

    @Override
    public InternalKieModule cloneForIncrementalCompilation( ReleaseId releaseId, KieModuleModel kModuleModel, MemoryFileSystem newFs ) {
        return internalKieModule.cloneForIncrementalCompilation( releaseId, kModuleModel, newFs );
    }

    public static InternalKieModule createKieModule( ReleaseId releaseId, File jar ) {
        return InternalKieModule.createKieModule( releaseId, jar );
    }

    public static InternalKieModule internalCreateKieModule( ReleaseId releaseId, File jar, ZipFile zipFile, ZipEntry zipEntry ) throws MalformedKieModuleException {
        return InternalKieModule.internalCreateKieModule( releaseId, jar, zipFile, zipEntry );
    }

    @Override
    public void updateKieModule( InternalKieModule newKM ) {
        internalKieModule.updateKieModule( newKM );
    }

    @Override
    public ReleaseId getReleaseId() {
        return internalKieModule.getReleaseId();
    }
}
