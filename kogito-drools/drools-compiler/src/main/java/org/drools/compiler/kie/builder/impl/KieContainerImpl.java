package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.kie.util.ChangeSetBuilder;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.core.RuleBaseConfiguration;
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
import org.kie.api.logger.KieLoggers;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBase;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.drools.compiler.kie.builder.impl.AbstractKieModule.buildKnowledgePackages;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.compiler.kie.util.CDIHelper.wireListnersAndWIHs;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;

public class KieContainerImpl
    implements
    InternalKieContainer {

    private static final Logger        log    = LoggerFactory.getLogger( KieContainerImpl.class );

    private KieProject           kProject;

    private final Map<String, KieBase> kBases = new HashMap<String, KieBase>();

    private final Map<String, KieSession> kSessions = new HashMap<String, KieSession>();
    private final Map<String, StatelessKieSession> statelessKSessions = new HashMap<String, StatelessKieSession>();

    private final KieRepository        kr;

    private ReleaseId containerReleaseId;

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

    public long getCreationTimestamp() {
        return kProject.getCreationTimestamp();
    }

    public ReleaseId getContainerReleaseId() {
        return containerReleaseId != null ? containerReleaseId : getReleaseId();
    }

    public Results updateToVersion(ReleaseId newReleaseId) {
        checkNotClasspathKieProject();
        return update(((KieModuleKieProject) kProject).getInternalKieModule(), newReleaseId);
    }

    public Results updateDependencyToVersion(ReleaseId currentReleaseId, ReleaseId newReleaseId) {
        checkNotClasspathKieProject();
        // if the new and the current release are equal (a snapshot) check if there is an older version with the same releaseId
        InternalKieModule currentKM = currentReleaseId.equals( newReleaseId ) ?
                                      (InternalKieModule) ((KieRepositoryImpl)kr).getOldKieModule( currentReleaseId ) :
                                      (InternalKieModule) kr.getKieModule( currentReleaseId );
        return update(currentKM, newReleaseId);
    }

    private void checkNotClasspathKieProject() {
        if( kProject instanceof ClasspathKieProject) {
            throw new UnsupportedOperationException( "It is not possible to update a classpath container to a new version." );
        }
    }

    private Results update(InternalKieModule currentKM, ReleaseId newReleaseId) {
        InternalKieModule newKM = (InternalKieModule) kr.getKieModule( newReleaseId );
        ChangeSetBuilder csb = new ChangeSetBuilder();
        KieJarChangeSet cs = csb.build( currentKM, newKM );
        List<String> modifiedClasses = getModifiedClasses(cs);

        ((KieModuleKieProject) kProject).updateToModule( newKM );

        ResultsImpl results = new ResultsImpl();

        List<String> kbasesToRemove = new ArrayList<String>();
        for ( Entry<String, KieBase> kBaseEntry : kBases.entrySet() ) {
            String kbaseName = kBaseEntry.getKey();
            KieBaseModel kieBaseModel = kProject.getKieBaseModel( kbaseName );
            // if a kbase no longer exists, just remove it from the cache
            if ( kieBaseModel == null ) {
                // have to save for later removal to avoid iteration errors
                kbasesToRemove.add( kbaseName );
            } else {
                // attaching the builder to the kbase
                KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder((KnowledgeBase) kBaseEntry.getValue());
                KnowledgeBuilderImpl pkgbuilder = (KnowledgeBuilderImpl)kbuilder;
                CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

                boolean modifyingUsedClass = false;
                for (String modifiedClass : modifiedClasses) {
                    if ( pkgbuilder.isClassInUse( convertResourceToClassName(modifiedClass) ) ) {
                        modifyingUsedClass = true;
                        break;
                    }
                }

                boolean shouldRebuild = modifyingUsedClass;
                if (modifyingUsedClass) {
                    // there are modified classes used by this kbase, so it has to be completely updated
                    updateAllResources(currentKM, newKM, kieBaseModel, pkgbuilder, ckbuilder);
                } else {
                    // there are no modified classes used by this kbase, so update it incrementally
                    shouldRebuild = updateResourcesIncrementally(currentKM, newKM, cs, modifiedClasses, kBaseEntry,
                                                                 kieBaseModel, pkgbuilder, ckbuilder) > 0;
                }

                pkgbuilder.startPackageUpdate();
                try {
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
                        rebuildAll(newReleaseId, results, newKM, modifiedClasses, kieBaseModel, pkgbuilder, ckbuilder);
                    }
                } finally {
                    pkgbuilder.completePackageUpdate();
                }
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
                newKM.addResourceToCompiler(ckbuilder, resourceName);
            }
        }
    }

    private int updateResourcesIncrementally(InternalKieModule currentKM,
                                             InternalKieModule newKM,
                                             KieJarChangeSet cs,
                                             List<String> modifiedClasses,
                                             Entry<String, KieBase> kBaseEntry,
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
                        fileCount += newKM.addResourceToCompiler(ckbuilder, resourceName) ? 1 : 0;
                    }
                }
            }

            KieBase kBase = kBaseEntry.getValue();
            for ( ResourceChangeSet.RuleLoadOrder loadOrder : rcs.getLoadOrder() ) {
                RuleImpl rule = ((KnowledgePackageImpl)kBase.getKiePackage( loadOrder.getPkgName() )).getRule( loadOrder.getRuleName() );
                if ( rule != null ) {
                    // rule can be null, if it didn't exist before
                    rule.setLoadOrder( loadOrder.getLoadOrder() );
                }
            }
        }
        return fileCount;
    }

    private void rebuildAll(ReleaseId newReleaseId,
                            ResultsImpl results,
                            InternalKieModule newKM,
                            List<String> modifiedClasses,
                            KieBaseModel kieBaseModel,
                            KnowledgeBuilderImpl kbuilder,
                            CompositeKnowledgeBuilder ckbuilder) {
        Set<String> modifiedPackages = new HashSet<String>();
        if (!modifiedClasses.isEmpty()) {
            ClassLoader rootClassLoader = kbuilder.getRootClassLoader();
            if ( rootClassLoader instanceof ProjectClassLoader) {
                ProjectClassLoader projectClassLoader = (ProjectClassLoader) rootClassLoader;
                projectClassLoader.reinitTypes();
                for (String resourceName : modifiedClasses) {
                    String className = convertResourceToClassName( resourceName );
                    byte[] bytes = newKM.getBytes(resourceName);
                    Class<?> clazz = projectClassLoader.defineClass(className, resourceName, bytes);
                    modifiedPackages.add(clazz.getPackage().getName());
                }
                kbuilder.setAllRuntimesDirty(modifiedPackages);
            }
        }

        ckbuilder.build();

        PackageBuilderErrors errors = kbuilder.getErrors();
        if ( !errors.isEmpty() ) {
            for ( KnowledgeBuilderError error : errors.getErrors() ) {
                results.addMessage(error);
            }
            log.error("Unable to update KieBase: " + kieBaseModel.getName() + " to release " + newReleaseId + "\n" + errors.toString());
        }

        if (!modifiedClasses.isEmpty()) {
            kbuilder.rewireClassObjectTypes(modifiedPackages);
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

    public KieBase getKieBase(String kBaseName) {
        KieBase kBase = kBases.get( kBaseName );
        if ( kBase == null ) {
            ResultsImpl msgs = new ResultsImpl();
            kBase = createKieBase(kBaseName, kProject, msgs, null);
            if ( kBase == null ) {
                // build error, throw runtime exception
                throw new RuntimeException( "Error while creating KieBase" + msgs.filterMessages( Level.ERROR  ) );
            }
            kBases.put( kBaseName, kBase );
        }
        return kBase;
    }

    public KieBase newKieBase(KieBaseConfiguration conf) {
        KieBaseModel defaultKieBaseModel = kProject.getDefaultKieBaseModel();
        if (defaultKieBaseModel == null) {
            throw new RuntimeException("Cannot find a defualt KieBase");
        }
        return newKieBase(defaultKieBaseModel.getName(), conf);
    }

    public KieBase newKieBase(String kBaseName, KieBaseConfiguration conf) {
        ResultsImpl msgs = new ResultsImpl();
        KieBase kBase = createKieBase(kBaseName, kProject, msgs, conf);
        if ( kBase == null ) {
            // build error, throw runtime exception
            throw new RuntimeException( "Error while creating KieBase" + msgs.filterMessages( Level.ERROR  ) );
        }
        return kBase;
    }

    private KieBase createKieBase(String kBaseName, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf) {
        KieBaseModelImpl kBaseModel = (KieBaseModelImpl) kProject.getKieBaseModel(kBaseName);
        if (kBaseModel == null) {
            throw new RuntimeException( "The requested KieBase \"" + kBaseName + "\" does not exist" );
        }

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
                    throw new RuntimeException( "The requested KieBase \"" + kBaseName + "\" has been set to run in CLOUD mode but requires features only available in STREAM mode" );
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
        KieSession kSession = kBase.newKieSession( conf != null ? conf : getKnowledgeSessionConfiguration(kSessionModel), environment );
        wireListnersAndWIHs(kSessionModel, kSession);

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
        if ( kSessionName == null ) {
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
        StatelessKieSession statelessKieSession = kBase.newStatelessKieSession( conf != null ? conf : getKnowledgeSessionConfiguration(kSessionModel) );
        registerLoggers(kSessionModel, statelessKieSession);
        statelessKSessions.put(kSessionName, statelessKieSession);
        return statelessKieSession;
    }

    public StatelessKieSession getStatelessKieSession(String kSessionName) {
        StatelessKieSession kieSession = statelessKSessions.get(kSessionName);
        return kieSession != null ? kieSession : newStatelessKieSession(kSessionName);

    }

    private KieSessionConfiguration getKnowledgeSessionConfiguration(KieSessionModelImpl kSessionModel) {
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
