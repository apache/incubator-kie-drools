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

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.kie.util.ChangeSetBuilder;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.FileLoggerModel;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieLoggers;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.definition.KnowledgePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.drools.compiler.kie.builder.impl.AbstractKieModule.buildKnowledgePackages;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.compiler.kie.util.CDIHelper.wireListnersAndWIHs;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;
import static org.drools.core.util.Drools.isJndiAvailable;

public class KieContainerImpl
    implements
    InternalKieContainer {

    private static final Logger        log    = LoggerFactory.getLogger( KieContainerImpl.class );

    private KieProject           kProject;

    private final Map<String, KieBase> kBases = new ConcurrentHashMap<String, KieBase>();

    private final Map<String, KieSession> kSessions = new ConcurrentHashMap<String, KieSession>();
    private final Map<String, StatelessKieSession> statelessKSessions = new ConcurrentHashMap<String, StatelessKieSession>();

    private final KieRepository        kr;

    private ReleaseId containerReleaseId;

    public KieModule getMainKieModule() {
        return kr.getKieModule(getReleaseId());
    }

    public KieContainerImpl(KieProject kProject, KieRepository kr) {
        this.kr = kr;
        this.kProject = kProject;
        kProject.init();
    }

    public KieContainerImpl(KieProject kProject, KieRepository kr, ReleaseId containerReleaseId) {
        this(kProject, kr);
        this.containerReleaseId = containerReleaseId;
    }

    public ReleaseId getReleaseId() {
        return kProject.getGAV();
    }

    public InputStream getPomAsStream() {
        return kProject.getPomAsStream();
    }

    public long getCreationTimestamp() {
        return kProject.getCreationTimestamp();
    }

    public ReleaseId getContainerReleaseId() {
        return containerReleaseId != null ? containerReleaseId : getReleaseId();
    }

    public Results updateToVersion(ReleaseId newReleaseId) {
        checkNotClasspathKieProject();
        Results results = update(((KieModuleKieProject) kProject).getInternalKieModule(), newReleaseId);
        containerReleaseId = newReleaseId;
        return results;
    }

    public Results updateDependencyToVersion(ReleaseId currentReleaseId, ReleaseId newReleaseId) {
        checkNotClasspathKieProject();

        ReleaseId installedReleaseId = getReleaseId();
        InternalKieModule currentKM;
        if (currentReleaseId.getGroupId().equals(installedReleaseId.getGroupId()) &&
            currentReleaseId.getArtifactId().equals(installedReleaseId.getArtifactId())) {
            // upgrading the kProject itself: taking the kmodule from there
            currentKM = ((KieModuleKieProject)kProject).getInternalKieModule();
        } else {
            // upgrading a transitive dependency: taking the kmodule from the krepo
            // if the new and the current release are equal (a snapshot) check if there is an older version with the same releaseId
            currentKM = currentReleaseId.equals(newReleaseId) ?
                        (InternalKieModule) ((KieRepositoryImpl) kr).getOldKieModule(currentReleaseId) :
                        (InternalKieModule) kr.getKieModule(currentReleaseId);
        }

        return update(currentKM, newReleaseId);
    }

    private void checkNotClasspathKieProject() {
        if( kProject instanceof ClasspathKieProject) {
            throw new UnsupportedOperationException( "It is not possible to update a classpath container to a new version." );
        }
    }

    private Results update(final InternalKieModule currentKM, final ReleaseId newReleaseId) {
        final InternalKieModule newKM = (InternalKieModule) kr.getKieModule( newReleaseId );
        ChangeSetBuilder csb = new ChangeSetBuilder();
        final KieJarChangeSet cs = csb.build( currentKM, newKM );

        final List<String> modifiedClasses = getModifiedClasses(cs);
        final boolean modifyingUsedClass = isModifyingUsedClass( modifiedClasses, getClassLoader() );
        reinitModifiedClasses( newKM, modifiedClasses, getClassLoader() );
        final List<String> unchangedResources = getUnchangedResources( newKM, cs );

        ((KieModuleKieProject) kProject).updateToModule( newKM );

        final ResultsImpl results = new ResultsImpl();

        List<String> kbasesToRemove = new ArrayList<String>();
        for ( Entry<String, KieBase> kBaseEntry : kBases.entrySet() ) {
            String kbaseName = kBaseEntry.getKey();
            final KieBaseModel kieBaseModel = kProject.getKieBaseModel( kbaseName );
            // if a kbase no longer exists, just remove it from the cache
            if ( kieBaseModel == null ) {
                // have to save for later removal to avoid iteration errors
                kbasesToRemove.add( kbaseName );
            } else {
                final InternalKnowledgeBase kBase = (InternalKnowledgeBase) kBaseEntry.getValue();
                kBase.enqueueModification( new Runnable() {
                    @Override
                    public void run() {
                        updateKBase( kBase, currentKM, newReleaseId, newKM, cs, modifiedClasses, modifyingUsedClass,
                                     unchangedResources, results, kieBaseModel);
                    }
                } );
            }
        }

        for (String kbaseToRemove : kbasesToRemove) {
            kBases.remove(kbaseToRemove);
        }

        for( Iterator<Entry<String,KieSession>> it = this.kSessions.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, KieSession> ksession = it.next();
            if( kProject.getKieSessionModel( ksession.getKey() ) == null ) {
                // remove sessions that no longer exist
                it.remove();
            }
        }

        for( Iterator<Entry<String,StatelessKieSession>> it = this.statelessKSessions.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, StatelessKieSession> ksession = it.next();
            if( kProject.getKieSessionModel( ksession.getKey() ) == null ) {
                // remove sessions that no longer exist
                it.remove();
            }
        }

        return results;
    }

    private void updateKBase( InternalKnowledgeBase kBase, InternalKieModule currentKM, ReleaseId newReleaseId,
                              InternalKieModule newKM, KieJarChangeSet cs, List<String> modifiedClasses, boolean modifyingUsedClass,
                              List<String> unchangedResources, ResultsImpl results, KieBaseModel kieBaseModel ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kBase );
        KnowledgeBuilderImpl pkgbuilder = (KnowledgeBuilderImpl)kbuilder;
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        boolean shouldRebuild = applyResourceChanges(currentKM, newKM, cs, modifiedClasses,
                                                     kBase, kieBaseModel, pkgbuilder, ckbuilder, modifyingUsedClass);
        // remove resources first
        for ( ResourceChangeSet rcs : cs.getChanges().values() ) {
            if ( rcs.getChangeType() == ChangeType.REMOVED ) {
                String resourceName = rcs.getResourceName();
                if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(newKM, kieBaseModel, resourceName) ) {
                    pkgbuilder.removeObjectsGeneratedFromResource( currentKM.getResource( resourceName ) );
                }
            }
        }

        if ( shouldRebuild ) {
            // readd unchanged dsl files to the kbuilder
            for (String dslFile : unchangedResources) {
                if (isFileInKBase(newKM, kieBaseModel, dslFile)) {
                    newKM.addResourceToCompiler(ckbuilder, kieBaseModel, dslFile);
                }
            }
            rebuildAll(newReleaseId, results, newKM, modifyingUsedClass, kieBaseModel, pkgbuilder, ckbuilder);
        }

        for ( InternalWorkingMemory wm : kBase.getWorkingMemories() ) {
            wm.notifyWaitOnRest();
        }
    }

    private List<String> getUnchangedResources( InternalKieModule newKM, KieJarChangeSet cs ) {
        List<String> dslFiles = new ArrayList<String>();
        for (String file : newKM.getFileNames()) {
            if ( includeIfUnchanged( file ) && !cs.contains( file ) ) {
                dslFiles.add(file);
            }
        }
        return dslFiles;
    }

    private static final ResourceType[] TYPES_TO_BE_INCLUDED = new ResourceType[] { ResourceType.DSL, ResourceType.GDRL };

    private boolean includeIfUnchanged( String file ) {
        for (ResourceType type : TYPES_TO_BE_INCLUDED ) {
            if (type.matchesExtension( file )) {
                return true;
            }
        }
        return false;
    }

    private boolean applyResourceChanges(InternalKieModule currentKM, InternalKieModule newKM, KieJarChangeSet cs,
                                         List<String> modifiedClasses, KieBase kBase, KieBaseModel kieBaseModel,
                                         KnowledgeBuilderImpl pkgbuilder, CompositeKnowledgeBuilder ckbuilder, boolean modifyingUsedClass) {
        boolean shouldRebuild = modifyingUsedClass;
        if (modifyingUsedClass) {
            // there are modified classes used by this kbase, so it has to be completely updated
            updateAllResources(currentKM, newKM, kieBaseModel, pkgbuilder, ckbuilder);
        } else {
            // there are no modified classes used by this kbase, so update it incrementally
            shouldRebuild = updateResourcesIncrementally(currentKM, newKM, cs, modifiedClasses, kBase,
                                                         kieBaseModel, pkgbuilder, ckbuilder) > 0;
        }
        return shouldRebuild;
    }

    private boolean isModifyingUsedClass( List<String> modifiedClasses, ClassLoader classLoader ) {
        for (String modifiedClass : modifiedClasses) {
            if ( isClassInUse( classLoader, convertResourceToClassName(modifiedClass) ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isClassInUse(ClassLoader rootClassLoader, String className) {
        return !(rootClassLoader instanceof ProjectClassLoader) || ((ProjectClassLoader) rootClassLoader).isClassInUse(className);
    }

    private boolean isFileInKBase(InternalKieModule kieModule, KieBaseModel kieBase, String fileName) {
        if (filterFileInKBase(kieModule, kieBase, fileName)) {
            return true;
        }
        for (String include : kProject.getTransitiveIncludes(kieBase)) {
            InternalKieModule includeModule = kProject.getKieModuleForKBase(include);
            if (includeModule != null && filterFileInKBase(includeModule, kProject.getKieBaseModel(include), fileName)) {
                return true;
            }
        }
        return false;
    }

    private void updateAllResources(InternalKieModule currentKM, InternalKieModule newKM, KieBaseModel kieBaseModel, KnowledgeBuilderImpl kbuilder, CompositeKnowledgeBuilder ckbuilder) {
        for (String resourceName : currentKM.getFileNames()) {
            if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(currentKM, kieBaseModel, resourceName) ) {
                Resource resource = currentKM.getResource(resourceName);
                kbuilder.removeObjectsGeneratedFromResource(resource);
            }
        }
        for (String resourceName : newKM.getFileNames()) {
            if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(newKM, kieBaseModel, resourceName) ) {
                newKM.addResourceToCompiler(ckbuilder, kieBaseModel, resourceName);
            }
        }
    }

    private int updateResourcesIncrementally(InternalKieModule currentKM,
                                             InternalKieModule newKM,
                                             KieJarChangeSet cs,
                                             List<String> modifiedClasses,
                                             KieBase kBase,
                                             KieBaseModel kieBaseModel,
                                             KnowledgeBuilderImpl kbuilder,
                                             CompositeKnowledgeBuilder ckbuilder) {
        int fileCount = modifiedClasses.size();
        for ( ResourceChangeSet rcs : cs.getChanges().values() ) {
            if ( rcs.getChangeType() != ChangeType.REMOVED ) {
                String resourceName = rcs.getResourceName();
                if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(newKM, kieBaseModel, resourceName) ) {
                    List<ResourceChange> changes = rcs.getChanges();
                    if ( ! changes.isEmpty() ) {
                        // we need to deal with individual parts of the resource
                        fileCount += AbstractKieModule.updateResource(ckbuilder,
                                                                      newKM,
                                                                      resourceName,
                                                                      rcs) ? 1 : 0;
                    } else {
                        // the whole resource has to handled
                        if( rcs.getChangeType() == ChangeType.UPDATED ) {
                            Resource resource = currentKM.getResource(resourceName);
                            kbuilder.removeObjectsGeneratedFromResource(resource);
                        }
                        fileCount += newKM.addResourceToCompiler(ckbuilder, kieBaseModel, resourceName, rcs) ? 1 : 0;
                    }
                }
            }

            for ( ResourceChangeSet.RuleLoadOrder loadOrder : rcs.getLoadOrder() ) {
            	KnowledgePackageImpl pkg = (KnowledgePackageImpl)kBase.getKiePackage( loadOrder.getPkgName() );
            	if( pkg != null ) {
	                RuleImpl rule = pkg.getRule( loadOrder.getRuleName() );
	                if ( rule != null ) {
	                    // rule can be null, if it didn't exist before
	                    rule.setLoadOrder( loadOrder.getLoadOrder() );
	                }
            	}
            }
        }
        return fileCount;
    }

    private void rebuildAll(ReleaseId newReleaseId,
                            ResultsImpl results,
                            InternalKieModule newKM,
                            boolean modifyingUsedClass,
                            KieBaseModel kieBaseModel,
                            KnowledgeBuilderImpl kbuilder,
                            CompositeKnowledgeBuilder ckbuilder) {
        ckbuilder.build();

        PackageBuilderErrors errors = kbuilder.getErrors();
        if ( !errors.isEmpty() ) {
            for ( KnowledgeBuilderError error : errors.getErrors() ) {
                results.addMessage(error);
            }
            log.error("Unable to update KieBase: " + kieBaseModel.getName() + " to release " + newReleaseId + "\n" + errors.toString());
        }

        if (modifyingUsedClass) {
            kbuilder.rewireAllClassObjectTypes();
        }
    }

    private void reinitModifiedClasses( InternalKieModule newKM, List<String> modifiedClasses, ClassLoader classLoader ) {
        if (!modifiedClasses.isEmpty()) {
            if ( classLoader instanceof ProjectClassLoader ) {
                ProjectClassLoader projectClassLoader = (ProjectClassLoader) classLoader;
                projectClassLoader.reinitTypes();
                for (String resourceName : modifiedClasses) {
                    String className = convertResourceToClassName( resourceName );
                    byte[] bytes = newKM.getBytes(resourceName);
                    Class<?> clazz = projectClassLoader.defineClass(className, resourceName, bytes);
                }
            }
        }
    }

    private List<String> getModifiedClasses(KieJarChangeSet cs) {
        List<String> modifiedClasses = new ArrayList<String>();
        for ( ResourceChangeSet rcs : cs.getChanges().values() ) {
            if ( rcs.getChangeType() != ChangeType.REMOVED ) {
                String resourceName = rcs.getResourceName();
                if ( resourceName.endsWith( ".class" ) ) {
                    modifiedClasses.add(resourceName);
                }
            }
        }
        return modifiedClasses;
    }

    public Collection<String> getKieBaseNames() {
        return kProject.getKieBaseNames();
    }

    public Collection<String> getKieSessionNamesInKieBase(String kBaseName) {
        KieBaseModel kieBaseModel = kProject.getKieBaseModel(kBaseName);
        return kieBaseModel != null ? kieBaseModel.getKieSessionModels().keySet() : Collections.<String>emptySet();
    }

    public KieBase getKieBase() {
        KieBaseModel defaultKieBaseModel = kProject.getDefaultKieBaseModel();
        if (defaultKieBaseModel == null) {
            throw new RuntimeException("Cannot find a default KieBase");
        }
        return getKieBase( defaultKieBaseModel.getName() );
    }
    
    public Results verify() {
        return this.kProject.verify();
    }

    public Results verify(String... kModelNames) {
        return this.kProject.verify( kModelNames );
    }

    public KieBase getKieBase(String kBaseName) {
        KieBase kBase = kBases.get( kBaseName );
        if ( kBase == null ) {
            KieBaseModelImpl kBaseModel = getKieBaseModelImpl(kBaseName);
            synchronized (kBaseModel) {
                kBase = kBases.get( kBaseName );
                if ( kBase == null ) {
                    ResultsImpl msgs = new ResultsImpl();
                    kBase = createKieBase(kBaseModel, kProject, msgs, null);
                    if (kBase == null) {
                        // build error, throw runtime exception
                        throw new RuntimeException("Error while creating KieBase" + msgs.filterMessages(Level.ERROR));
                    }
                    kBases.put(kBaseName, kBase);
                }
            }
        }
        return kBase;
    }

    public KieBase newKieBase(KieBaseConfiguration conf) {
        KieBaseModel defaultKieBaseModel = kProject.getDefaultKieBaseModel();
        if (defaultKieBaseModel == null) {
            throw new RuntimeException("Cannot find a default KieBase");
        }
        return newKieBase(defaultKieBaseModel.getName(), conf);
    }

    public KieBase newKieBase(String kBaseName, KieBaseConfiguration conf) {
        ResultsImpl msgs = new ResultsImpl();
        KieBase kBase = createKieBase(getKieBaseModelImpl(kBaseName), kProject, msgs, conf);
        if ( kBase == null ) {
            // build error, throw runtime exception
            throw new RuntimeException( "Error while creating KieBase" + msgs.filterMessages( Level.ERROR  ) );
        }
        return kBase;
    }

    private KieBase createKieBase(KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf) {
        ClassLoader cl = kieProject.getClassLoader();
        InternalKieModule kModule = kieProject.getKieModuleForKBase( kBaseModel.getName() );

        Collection<KnowledgePackage> pkgs = kModule.getKnowledgePackagesForKieBase(kBaseModel.getName());

        if ( pkgs == null ) {
            KnowledgeBuilder kbuilder = buildKnowledgePackages(kBaseModel, kieProject, messages);
            if ( kbuilder.hasErrors() ) {
                // Messages already populated by the buildKnowlegePackages
                return null;
            }
        }

        // if we get to here, then we know the pkgs is now cached
        pkgs = kModule.getKnowledgePackagesForKieBase(kBaseModel.getName());

        if ( kBaseModel.getEventProcessingMode() == EventProcessingOption.CLOUD &&
            (conf == null || conf.getOption(EventProcessingOption.class) == EventProcessingOption.CLOUD ) ) {
            for (KnowledgePackage kpkg : pkgs) {
                if ( ((KnowledgePackageImpl) kpkg).needsStreamMode() ) {
                    throw new RuntimeException( "The requested KieBase \"" + kBaseModel.getName() + "\" has been set to run in CLOUD mode but requires features only available in STREAM mode" );
                }
            }
        }

        if (conf == null) {
            conf = getKnowledgeBaseConfiguration(kBaseModel, cl);
        } else if (conf instanceof RuleBaseConfiguration) {
            ((RuleBaseConfiguration)conf).setClassLoader(cl);
        }
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase( conf );

        kBase.addKnowledgePackages( pkgs );
        return kBase;
    }

    private KieBaseModelImpl getKieBaseModelImpl(String kBaseName) {
        KieBaseModelImpl kBaseModel = (KieBaseModelImpl) kProject.getKieBaseModel(kBaseName);
        if (kBaseModel == null) {
            throw new RuntimeException( "The requested KieBase \"" + kBaseName + "\" does not exist" );
        }
        return kBaseModel;
    }

    private KieBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModelImpl kBaseModel, ClassLoader cl) {
        KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, cl);
        kbConf.setOption(kBaseModel.getEqualsBehavior());
        kbConf.setOption(kBaseModel.getEventProcessingMode());
        kbConf.setOption(kBaseModel.getDeclarativeAgenda());
        return kbConf;
    }

    public KieSession newKieSession() {
        return newKieSession((Environment)null, null);
    }

    public KieSession getKieSession() {
        KieSessionModel defaultKieSessionModel = findKieSessionModel(false);
        return getKieSession(defaultKieSessionModel.getName());
    }

    public KieSession newKieSession(KieSessionConfiguration conf) {
        return newKieSession((Environment)null, conf);
    }

    public KieSession newKieSession(Environment environment) {
        return newKieSession(environment, null);
    }

    public KieSession newKieSession(Environment environment, KieSessionConfiguration conf) {
        KieSessionModel defaultKieSessionModel = findKieSessionModel(false);
        return newKieSession(defaultKieSessionModel.getName(), environment, conf);
    }

    private KieSessionModel findKieSessionModel(boolean stateless) {
        KieSessionModel defaultKieSessionModel = stateless ? kProject.getDefaultStatelessKieSession() : kProject.getDefaultKieSession();
        if (defaultKieSessionModel == null) {
            throw new RuntimeException(stateless ? "Cannot find a default StatelessKieSession" : "Cannot find a default KieSession");
        }
        return defaultKieSessionModel;
    }

    public StatelessKieSession newStatelessKieSession() {
        return newStatelessKieSession((KieSessionConfiguration)null);
    }

    public StatelessKieSession newStatelessKieSession(KieSessionConfiguration conf) {
        KieSessionModel defaultKieSessionModel = findKieSessionModel(true);
        return newStatelessKieSession(defaultKieSessionModel.getName(), conf);
    }

    public StatelessKieSession getStatelessKieSession() {
        KieSessionModel defaultKieSessionModel = findKieSessionModel(true);
        return getStatelessKieSession(defaultKieSessionModel.getName());
    }

    public KieSession newKieSession(String kSessionName) {
        return newKieSession(kSessionName, null, null);
    }

    public KieSession getKieSession(String kSessionName) {
        KieSession kieSession = kSessions.get(kSessionName);
        if (kieSession instanceof StatefulKnowledgeSessionImpl && !((StatefulKnowledgeSessionImpl)kieSession).isAlive()) {
            kSessions.remove(kSessionName);
            kieSession = null;
        }
        return kieSession != null ? kieSession : newKieSession(kSessionName);
    }

    public KieSession newKieSession(String kSessionName, Environment environment) {
        return newKieSession(kSessionName, environment, null);
    }

    public KieSession newKieSession(String kSessionName, KieSessionConfiguration conf) {
        return newKieSession(kSessionName, null, conf);
    }

    public KieSession newKieSession(String kSessionName, Environment environment, KieSessionConfiguration conf) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) getKieSessionModel(kSessionName);
        if ( kSessionModel == null ) {
            log.error("Unknown KieSession name: " + kSessionName);
            return null;
        }
        if (kSessionModel.getType() == KieSessionModel.KieSessionType.STATELESS) {
            throw new RuntimeException("Trying to create a stateful KieSession from a stateless KieSessionModel: " + kSessionName);
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase == null ) {
            log.error("Unknown KieBase name: " + kSessionModel.getKieBaseModel().getName());
            return null;
        }

        KieSession kSession = kBase.newKieSession( conf != null ? conf : getKieSessionConfiguration( kSessionModel ), environment );
        if (isJndiAvailable()) {
            wireListnersAndWIHs( kSessionModel, kSession );
        }
        registerLoggers(kSessionModel, kSession);
        kSessions.put(kSessionName, kSession);
        return kSession;
    }

    private void registerLoggers(KieSessionModelImpl kSessionModel, KieRuntimeEventManager kSession) {
        KieLoggers kieLoggers = KieServices.Factory.get().getLoggers();
        if (kSessionModel.getConsoleLogger() != null) {
            kieLoggers.newConsoleLogger(kSession);
        }
        FileLoggerModel fileLogger = kSessionModel.getFileLogger();
        if (fileLogger != null) {
            if (fileLogger.isThreaded()) {
                kieLoggers.newThreadedFileLogger(kSession, fileLogger.getFile(), fileLogger.getInterval());
            } else {
                kieLoggers.newFileLogger(kSession, fileLogger.getFile());
            }
        }
    }

    public StatelessKieSession newStatelessKieSession(String kSessionName) {
        return newStatelessKieSession(kSessionName, null);
    }

    public StatelessKieSession newStatelessKieSession(String kSessionName, KieSessionConfiguration conf) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionModel == null ) {
            log.error("Unknown KieSession name: " + kSessionName);
            return null;
        }
        if (kSessionModel.getType() == KieSessionModel.KieSessionType.STATEFUL) {
            throw new RuntimeException("Trying to create a stateless KieSession from a stateful KieSessionModel: " + kSessionName);
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase == null ) {
            log.error("Unknown KieBase name: " + kSessionModel.getKieBaseModel().getName());
            return null;
        }

        StatelessKieSession statelessKieSession = kBase.newStatelessKieSession( conf != null ? conf : getKieSessionConfiguration( kSessionModel ) );
        if (isJndiAvailable()) {
            wireListnersAndWIHs( kSessionModel, statelessKieSession );
        }
        registerLoggers(kSessionModel, statelessKieSession);
        statelessKSessions.put(kSessionName, statelessKieSession);
        return statelessKieSession;
    }

    public StatelessKieSession getStatelessKieSession(String kSessionName) {
        StatelessKieSession kieSession = statelessKSessions.get(kSessionName);
        return kieSession != null ? kieSession : newStatelessKieSession(kSessionName);

    }

    public KieSessionConfiguration getKieSessionConfiguration() {
        return getKieSessionConfiguration( kProject.getDefaultKieSession() );
    }

    public KieSessionConfiguration getKieSessionConfiguration( String kSessionName ) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionModel == null ) {
            log.error("Unknown KieSession name: " + kSessionName);
            return null;
        }
        return getKieSessionConfiguration( kSessionModel );
    }

    private KieSessionConfiguration getKieSessionConfiguration( KieSessionModel kSessionModel ) {
        KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption( kSessionModel.getClockType() );
        ksConf.setOption( kSessionModel.getBeliefSystem() );
        return ksConf;
    }

    public void dispose() {
        for (KieSession kieSession : kSessions.values()) {
            kieSession.dispose();
        }
        kSessions.clear();
        statelessKSessions.clear();
    }

    public KieProject getKieProject() {
        return kProject;
    }

    public KieModule getKieModuleForKBase(String kBaseName) {
        return kProject.getKieModuleForKBase( kBaseName );
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kProject.getKieBaseModel(kBaseName);
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kProject.getKieSessionModel(kSessionName);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.kProject.getClassLoader();
    }

}
