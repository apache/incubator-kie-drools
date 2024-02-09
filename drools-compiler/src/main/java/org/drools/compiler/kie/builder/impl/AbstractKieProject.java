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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.util.StringUtils;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.GroupDRLsInKieBasesByFolderOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;

public abstract class AbstractKieProject implements KieProject {

    private static final Logger                  log                        = LoggerFactory.getLogger(KieProject.class);

    protected final Map<String, KieBaseModel>    kBaseModels                = new HashMap<>();

    private KieBaseModel                         defaultKieBase             = null;

    private KieSessionModel                      defaultKieSession          = null;

    private KieSessionModel                      defaultStatelessKieSession = null;

    private Map<KieBaseModel, Set<String>>       includesInKieBase          = new HashMap<>();

    private final Map<String, KieSessionModel>   kSessionModels             = new HashMap<>();

    private static final Predicate<String> BUILD_ALL = s -> true;

    public ResultsImpl verify() {
        BuildContext buildContext = new BuildContext();
        verify(buildContext);
        return buildContext.getMessages();
    }

    public ResultsImpl verify(String... kBaseNames) {
        BuildContext buildContext = new BuildContext();
        verify(kBaseNames, buildContext);
        return buildContext.getMessages();
    }

    public void verify(BuildContext buildContext) {
        for ( KieBaseModel model : kBaseModels.values() ) {
            buildKnowledgePackages((KieBaseModelImpl) model, buildContext);
        }
    }

    private void verify(String[] kBaseNames, BuildContext buildContext) {
        for ( String modelName : kBaseNames ) {
            KieBaseModelImpl kieBaseModel = (KieBaseModelImpl) kBaseModels.get( modelName );
            if ( kieBaseModel == null ) {
                throw new RuntimeException( "Unknown KieBase. Cannot find a KieBase named: " + modelName );
            }
            buildKnowledgePackages( kieBaseModel, buildContext );
        }
    }

    public KieBaseModel getDefaultKieBaseModel() {
        return defaultKieBase;
    }

    public KieSessionModel getDefaultKieSession() {
        return defaultKieSession;
    }

    public KieSessionModel getDefaultStatelessKieSession() {
        return defaultStatelessKieSession;
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kBaseName == null ? getDefaultKieBaseModel() : kBaseModels.get( kBaseName );
    }

    public Collection<String> getKieBaseNames() {
        return kBaseModels.keySet();
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kSessionName == null ? getDefaultKieSession() : kSessionModels.get( kSessionName );
    }

    void indexParts( InternalKieModule mainKieModule,
                     Collection<InternalKieModule> depKieModules,
                     Map<String, InternalKieModule> kJarFromKBaseName ) {
        for ( InternalKieModule kJar : depKieModules ) {
            indexKieModule( kJarFromKBaseName, kJar, false );
        }
        if (mainKieModule != null) {
            indexKieModule( kJarFromKBaseName, mainKieModule, true );
        }
    }

    private void indexKieModule( Map<String, InternalKieModule> kJarFromKBaseName, InternalKieModule kJar, boolean isMainModule ) {
        boolean defaultKieBaseFromMain = false;
        boolean defaultKieSessionFromMain = false;
        boolean defaultStatelessKieSessionFromMain = false;
        KieModuleModel kieProject = kJar.getKieModuleModel();

        for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
            if (kieBaseModel.isDefault()) {
                if (defaultKieBase == null || (isMainModule && !defaultKieBaseFromMain)) {
                    defaultKieBase = kieBaseModel;
                    defaultKieBaseFromMain = isMainModule;
                } else {
                    defaultKieBase = null;
                    log.warn("Found more than one default KieBase: disabling all. KieBases will be accessible only by name");
                }
            }

            kBaseModels.put( kieBaseModel.getName(), kieBaseModel );
            ((KieBaseModelImpl) kieBaseModel).setKModule( kieProject ); // should already be set, but just in case

            kJarFromKBaseName.put( kieBaseModel.getName(), kJar );
            for ( KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values() ) {
                if (kieSessionModel.isDefault()) {
                    if (kieSessionModel.getType() == KieSessionModel.KieSessionType.STATEFUL) {
                        if (defaultKieSession == null || (isMainModule && !defaultKieSessionFromMain)) {
                            defaultKieSession = kieSessionModel;
                            defaultKieSessionFromMain = isMainModule;
                        } else {
                            defaultKieSession = null;
                            log.warn("Found more than one default KieSession: disabling all. KieSessions will be accessible only by name");
                        }
                    } else {
                        if (defaultStatelessKieSession == null || (isMainModule && !defaultStatelessKieSessionFromMain)) {
                            defaultStatelessKieSession = kieSessionModel;
                            defaultStatelessKieSessionFromMain = isMainModule;
                        } else {
                            defaultStatelessKieSession = null;
                            log.warn("Found more than one default StatelessKieSession: disabling all. StatelessKieSessions will be accessible only by name");
                        }
                    }
                }

                ((KieSessionModelImpl) kieSessionModel).setKBase( kieBaseModel ); // should already be set, but just in case
                kSessionModels.put( kieSessionModel.getName(), kieSessionModel );
            }
        }
    }

    void cleanIndex() {
        kBaseModels.clear();
        kSessionModels.clear();
        includesInKieBase.clear();
        defaultKieBase = null;
        defaultKieSession = null;
        defaultStatelessKieSession = null;
    }

    public Set<String> getTransitiveIncludes(String kBaseName) {
        return getTransitiveIncludes(getKieBaseModel(kBaseName));
    }

    public Set<String> getTransitiveIncludes(KieBaseModel kBaseModel) {
        Set<String> includes = includesInKieBase.get(kBaseModel);
        if (includes == null) {
            includes = new HashSet<>();
            getTransitiveIncludes(kBaseModel, includes);
            includesInKieBase.put(kBaseModel, includes);
        }
        return includes;
    }

    private void getTransitiveIncludes(KieBaseModel kBaseModel, Set<String> includes) {
        if (kBaseModel == null) {
            return;
        }
        Set<String> incs = kBaseModel.getIncludes();
        if (incs != null && !incs.isEmpty()) {
            for (String inc : incs) {
                if (!includes.contains(inc)) {
                    includes.add(inc);
                    getTransitiveIncludes(getKieBaseModel(inc), includes);
                }
            }
        }
    }

    public KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel, BuildContext buildContext ) {
        return buildKnowledgePackages( kBaseModel, buildContext, BUILD_ALL );
    }

    public KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel, BuildContext buildContext, Predicate<String> buildFilter ) {
        boolean useFolders = useFolders( kBaseModel );

        Set<Asset> assets = new LinkedHashSet<>();

        boolean allIncludesAreValid = true;
        for (String include : getTransitiveIncludes(kBaseModel)) {
            if ( StringUtils.isEmpty( include )) {
                continue;
            }
            InternalKieModule includeModule = getKieModuleForKBase(include);
            if (includeModule == null) {
                String text = "Unable to build KieBase, could not find include: " + include;
                log.error(text);
                buildContext.getMessages().addMessage( Message.Level.ERROR, KieModuleModelImpl.KMODULE_SRC_PATH.asString(), text ).setKieBaseName( kBaseModel.getName() );
                allIncludesAreValid = false;
                continue;
            }
            if (compileIncludedKieBases()) {
                addFiles( buildFilter, assets, getKieBaseModel( include ), includeModule, useFolders );
            }
        }

        if (!allIncludesAreValid) {
            return null;
        }

        InternalKieModule kModule = getKieModuleForKBase(kBaseModel.getName());
        addFiles( buildFilter, assets, kBaseModel, kModule, useFolders );

        KnowledgeBuilder kbuilder;
        if (assets.isEmpty()) {
            if (buildFilter == BUILD_ALL) {
                log.warn( "No files found for KieBase " + kBaseModel.getName() +
                                  (kModule instanceof FileKieModule ? ", searching folder " + kModule.getFile() : ""));
            }
            kbuilder = new InternalKnowledgeBuilder.Empty( getClassLoader(), () -> provideKnowledgeBuilder( kBaseModel, kModule, buildContext ) );

        } else {
            kbuilder = provideKnowledgeBuilder( kBaseModel, kModule, buildContext );
            if ( kbuilder == null ) {
                return null;
            }

            CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

            for (Asset asset : assets) {
                if (buildContext.registerResourceToBuild(kBaseModel.getName(), asset.name)) {
                    asset.kmodule.addResourceToCompiler(ckbuilder, kBaseModel, asset.name);
                }
            }
            ckbuilder.build();

            if ( kbuilder.hasErrors() ) {
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    buildContext.getMessages().addMessage( error ).setKieBaseName( kBaseModel.getName() );
                }
                log.error( "Unable to build KieBaseModel:" + kBaseModel.getName() + "\n" + kbuilder.getErrors().toString() );
            }
            if ( kbuilder.hasResults( ResultSeverity.WARNING ) ) {
                for (KnowledgeBuilderResult warn : kbuilder.getResults( ResultSeverity.WARNING )) {
                    buildContext.getMessages().addMessage( warn ).setKieBaseName( kBaseModel.getName() );
                }
                log.warn( "Warning : " + kBaseModel.getName() + "\n" + kbuilder.getResults( ResultSeverity.WARNING ).toString() );
            }
        }

        // cache KnowledgeBuilder and results
        if (buildFilter == BUILD_ALL) {
            kModule.cacheKnowledgeBuilderForKieBase( kBaseModel.getName(), kbuilder );
            kModule.cacheResultsForKieBase( kBaseModel.getName(), buildContext.getMessages() );
        }

        return kbuilder;
    }

    private KnowledgeBuilderImpl provideKnowledgeBuilder( KieBaseModelImpl kBaseModel, InternalKieModule kModule, BuildContext buildContext ) {
        KnowledgeBuilderImpl kbuilder = (KnowledgeBuilderImpl) createKnowledgeBuilder( kBaseModel, kModule );
        if ( kbuilder != null ) {
            kbuilder.setReleaseId( getGAV() );
            kbuilder.setBuildContext( buildContext );
        }
        return kbuilder;
    }

    private boolean useFolders( KieBaseModelImpl kBaseModel ) {
        String modelProp = kBaseModel.getKModule().getConfigurationProperty( GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME );
        if (modelProp == null) {
            modelProp = System.getProperty( GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME );
        }
        return modelProp != null && modelProp.equalsIgnoreCase("true");
    }

    protected boolean compileIncludedKieBases() {
        return true;
    }

    protected KnowledgeBuilder createKnowledgeBuilder( KieBaseModelImpl kBaseModel, InternalKieModule kModule ) {
        return KnowledgeBuilderFactory.newKnowledgeBuilder( getBuilderConfiguration( kBaseModel, kModule ) );
    }

    private void addFiles( Predicate<String> buildFilter, Set<Asset> assets, KieBaseModel kieBaseModel,
                           InternalKieModule kieModule, boolean useFolders) {
        for (String fileName : kieModule.getFileNames()) {
            if (buildFilter.test( fileName ) && !fileName.startsWith(".") && !fileName.endsWith(".properties") &&
                    filterFileInKBase(kieModule, kieBaseModel, fileName, () -> kieModule.getResource( fileName ), useFolders)) {
                assets.add(new Asset( kieModule, fileName ));
            }
        }
    }

    protected KnowledgeBuilderConfigurationImpl getBuilderConfiguration( KieBaseModel kBaseModel, InternalKieModule kModule ) {
        return (KnowledgeBuilderConfigurationImpl) kModule.createBuilderConfiguration(kBaseModel, getClassLoader());
    }

    private static class Asset {
        private final InternalKieModule kmodule;
        private final String name;

        private Asset( InternalKieModule kmodule, String name ) {
            this.kmodule = kmodule;
            this.name = name;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            Asset asset = (Asset) o;
            return kmodule.equals( asset.kmodule ) && name.equals( asset.name );
        }

        @Override
        public int hashCode() {
            int result = kmodule.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Asset: " + name;
        }
    }
}
