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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.ObjectName;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.compiler.management.KieContainerMonitor;
import org.drools.compiler.reteoo.compiled.ObjectTypeNodeCompiler;
import org.drools.core.InitialFact;
import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.impl.InternalKieContainer;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatefulSessionPool;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.management.DroolsManagementAgent.CBSKey;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.FileLoggerModel;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.MBeansOption;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieLoggers;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.time.Calendar;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.ResourceChangeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.util.InjectionHelper.wireSessionComponents;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;
import static org.drools.core.util.Drools.isJndiAvailable;

public class KieContainerImpl
    implements
    InternalKieContainer {

    private static final Logger log = LoggerFactory.getLogger( KieContainerImpl.class );

    public static final String ALPHA_NETWORK_COMPILER_OPTION = "drools.alphaNetworkCompiler";

    private KieProject kProject;

    private final Map<String, KieBase> kBases = new ConcurrentHashMap<String, KieBase>();

    private final Map<String, KieSession> kSessions = new ConcurrentHashMap<String, KieSession>();
    private final Map<String, StatelessKieSession> statelessKSessions = new ConcurrentHashMap<String, StatelessKieSession>();

    private final KieRepository        kr;

    private ReleaseId configuredReleaseId;
    private ReleaseId containerReleaseId;

	private final String containerId;

    private final Map<String, KieSessionConfiguration> sessionConfsCache = new ConcurrentHashMap<>();

    public KieModule getMainKieModule() {
        return kr.getKieModule(getReleaseId());
    }

    /**
     * Please note: the recommended way of getting a KieContainer is relying on {@link org.kie.api.KieServices KieServices} API,
     * for example: {@link org.kie.api.KieServices#newKieContainer(ReleaseId) KieServices.newKieContainer(...)}.
     * The direct manual call to KieContainerImpl constructor instead would not guarantee the consistency of the supplied containerId.
     */
    public KieContainerImpl(KieProject kProject, KieRepository kr) {
        this("impl"+UUID.randomUUID(), kProject, kr);
    }

    /**
     * Please note: the recommended way of getting a KieContainer is relying on {@link org.kie.api.KieServices KieServices} API,
     * for example: {@link org.kie.api.KieServices#newKieContainer(ReleaseId) KieServices.newKieContainer(...)}.
     * The direct manual call to KieContainerImpl constructor instead would not guarantee the consistency of the supplied containerId.
     */
    public KieContainerImpl(KieProject kProject, KieRepository kr, ReleaseId containerReleaseId) {
        this("impl"+UUID.randomUUID(), kProject, kr, containerReleaseId);
    }

    /**
     * Please note: the recommended way of getting a KieContainer is relying on {@link org.kie.api.KieServices KieServices} API,
     * for example: {@link org.kie.api.KieServices#newKieContainer(ReleaseId) KieServices.newKieContainer(...)}.
     * The direct manual call to KieContainerImpl constructor instead would not guarantee the consistency of the supplied containerId.
     */
    public KieContainerImpl(String containerId, KieProject kProject, KieRepository kr) {
        this.kr = kr;
        this.kProject = kProject;
        this.containerId = containerId;
        kProject.init();
        initMBeans(containerId);
    }

    /**
     * Please note: the recommended way of getting a KieContainer is relying on {@link org.kie.api.KieServices KieServices} API,
     * for example: {@link org.kie.api.KieServices#newKieContainer(ReleaseId) KieServices.newKieContainer(...)}.
     * The direct manual call to KieContainerImpl constructor instead would not guarantee the consistency of the supplied containerId.
     */
    public KieContainerImpl(String containerId, KieProject kProject, KieRepository kr, ReleaseId containerReleaseId) {
        this(containerId, kProject, kr);
        this.configuredReleaseId = containerReleaseId;
        this.containerReleaseId = containerReleaseId;
    }

	private void initMBeans(String containerId) {
		if ( isMBeanOptionEnabled() ) {
	        KieContainerMonitor monitor = new KieContainerMonitor(this);
	        ObjectName on = DroolsManagementAgent.createObjectNameBy(containerId);
	        DroolsManagementAgent.getInstance().registerMBean( this, monitor, on );
        }
	}

    @Override
    public String getContainerId() {
    	return this.containerId;
    }

    @Override
    public ReleaseId getConfiguredReleaseId() {
		return configuredReleaseId;
	}

	@Override
	public ReleaseId getResolvedReleaseId() {
		return getReleaseId();
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

    @Override
    public ReleaseId getContainerReleaseId() {
        return containerReleaseId != null ? containerReleaseId : getReleaseId();
    }

    public Results updateToVersion(ReleaseId newReleaseId) {
        checkNotClasspathKieProject();
        Results results = update(((KieModuleKieProject) kProject).getInternalKieModule(), newReleaseId);
        if (results != null) {
            containerReleaseId = newReleaseId;
        } else {
            results = new ResultsImpl();
            ( (ResultsImpl) results ).addMessage( Message.Level.ERROR, null, "Cannot find KieModule with ReleaseId: " + newReleaseId );
        }
        return results;
    }

    public Results updateToKieModule(InternalKieModule newKM) {
        checkNotClasspathKieProject();
        Results results = update(((KieModuleKieProject) kProject).getInternalKieModule(), newKM);
        containerReleaseId = newKM.getReleaseId();
        return results;
    }

    public Results updateDependencyToVersion(ReleaseId currentReleaseId, ReleaseId newReleaseId) {
        ReleaseId installedReleaseId = getReleaseId();
        if (currentReleaseId.getGroupId().equals(installedReleaseId.getGroupId()) &&
            currentReleaseId.getArtifactId().equals(installedReleaseId.getArtifactId())) {
            // upgrading the kProject itself: taking the kmodule from there
            return updateToVersion(newReleaseId);
        }

        checkNotClasspathKieProject();
        // upgrading a transitive dependency: taking the kmodule from the krepo
        // if the new and the current release are equal (a snapshot) check if there is an older version with the same releaseId
        InternalKieModule currentKM = currentReleaseId.equals(newReleaseId) ?
                                      (InternalKieModule) ((KieRepositoryImpl) kr).getOldKieModule(currentReleaseId) :
                                      (InternalKieModule) kr.getKieModule(currentReleaseId);
        return update(currentKM, newReleaseId);
    }

    private void checkNotClasspathKieProject() {
        if( kProject instanceof ClasspathKieProject) {
            throw new UnsupportedOperationException( "It is not possible to update a classpath container to a new version." );
        }
    }

    private Results update(final InternalKieModule currentKM, final ReleaseId newReleaseId) {
        final InternalKieModule newKM = (InternalKieModule) kr.getKieModule( newReleaseId );
        return newKM == null ? null : update( currentKM, newKM );
    }

    private Results update( InternalKieModule currentKM, InternalKieModule newKM ) {
        final KieJarChangeSet cs = currentKM.getChanges( newKM );
        List<String> modifiedClassNames = getModifiedClasses(cs);
        final boolean modifyingUsedClass = isModifyingUsedClass( modifiedClassNames, getClassLoader() );
        final Collection<Class<?>> modifiedClasses = reinitModifiedClasses( newKM, modifiedClassNames, getClassLoader(), modifyingUsedClass );
        final Collection<String> unchangedResources = getUnchangedResources( newKM, cs );

        Map<String, KieBaseModel> currentKieBaseModels = ((KieModuleKieProject ) kProject).updateToModule( newKM );

        final ResultsImpl results = new ResultsImpl();

        currentKM.updateKieModule(newKM);

        List<String> kbasesToRemove = new ArrayList<String>();
        for ( Entry<String, KieBase> kBaseEntry : kBases.entrySet() ) {
            String kbaseName = kBaseEntry.getKey();
            KieBaseModelImpl newKieBaseModel = (KieBaseModelImpl) kProject.getKieBaseModel( kbaseName );
            KieBaseModelImpl currentKieBaseModel = (KieBaseModelImpl) currentKieBaseModels.get( kbaseName );
            // if a kbase no longer exists, just remove it from the cache
            if ( newKieBaseModel == null ) {
                // have to save for later removal to avoid iteration errors
                kbasesToRemove.add( kbaseName );
            } else {
                final InternalKnowledgeBase kBase = (InternalKnowledgeBase) kBaseEntry.getValue();
                KieBaseUpdateContext context = new KieBaseUpdateContext( kProject, kBase, currentKM, newKM,
                                                                         cs, modifiedClasses, modifyingUsedClass, unchangedResources,
                                                                         results, newKieBaseModel, currentKieBaseModel );
                kBase.enqueueModification( currentKM.createKieBaseUpdater( context ) );
            }
        }

        for (String kbaseToRemove : kbasesToRemove) {
            kBases.remove(kbaseToRemove);
        }

        // remove sessions that no longer exist
        this.kSessions.entrySet().removeIf( ksession -> kProject.getKieSessionModel( ksession.getKey() ) == null );
        this.statelessKSessions.entrySet().removeIf( ksession -> kProject.getKieSessionModel( ksession.getKey() ) == null );

        return results;
    }

    private Collection<String> getUnchangedResources( InternalKieModule newKM, KieJarChangeSet cs ) {
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

    private boolean isModifyingUsedClass( List<String> modifiedClasses, ClassLoader classLoader ) {
        return modifiedClasses.stream().anyMatch( c -> isClassInUse( classLoader, convertResourceToClassName(c) ) );
    }

    private boolean isClassInUse(ClassLoader rootClassLoader, String className) {
        return !(rootClassLoader instanceof ProjectClassLoader) || ((ProjectClassLoader) rootClassLoader).isClassInUse(className);
    }

    private Collection<Class<?>> reinitModifiedClasses( InternalKieModule newKM, List<String> modifiedClasses, ClassLoader classLoader, boolean modifyingUsedClass ) {
        if (modifiedClasses.isEmpty() || !(classLoader instanceof ProjectClassLoader)) {
            return Collections.emptyList();
        }

        List<Class<?>> classes = new ArrayList<Class<?>>();
        ProjectClassLoader projectClassLoader = (ProjectClassLoader) classLoader;
        if (modifyingUsedClass) {
            projectClassLoader.reinitTypes();
        }
        for (String resourceName : modifiedClasses) {
            String className = convertResourceToClassName( resourceName );
            byte[] bytes = newKM.getBytes(resourceName);
            Class<?> clazz = projectClassLoader.defineClass(className, resourceName, bytes);
            classes.add(clazz);
        }
        return classes;
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
        InternalKieModule kModule = kieProject.getKieModuleForKBase( kBaseModel.getName() );
        InternalKnowledgeBase kBase = kModule.createKieBase(kBaseModel, kieProject, messages, conf);
        if ( kBase == null ) {
            return null;
        }
        kBase.setResolvedReleaseId(containerReleaseId);
        kBase.setContainerId(containerId);
        kBase.setKieContainer(this);
        kBase.initMBeans();

        generateCompiledAlphaNetwork(kBaseModel, kModule, kBase);

        return kBase;
    }

    public void generateCompiledAlphaNetwork(KieBaseModelImpl kBaseModel, InternalKieModule kModule, InternalKnowledgeBase kBase) {
        final String configurationProperty = kBaseModel.getKModule().getConfigurationProperty(ALPHA_NETWORK_COMPILER_OPTION);
        final Boolean isAlphaNetworkEnabled = Boolean.valueOf(configurationProperty);
        if (isAlphaNetworkEnabled) {
            KnowledgeBuilder kbuilder = kModule.getKnowledgeBuilderForKieBase(kBaseModel.getName());
            kBase.getRete().getEntryPointNodes().values().stream()
                    .flatMap(ep -> ep.getObjectTypeNodes().values().stream())
                    .filter(f -> !InitialFact.class.isAssignableFrom(f.getObjectType().getClassType()))
                    .forEach(otn -> otn.setCompiledNetwork(ObjectTypeNodeCompiler.compile(((KnowledgeBuilderImpl) kbuilder), otn)));
        }
    }

    private KieBaseModelImpl getKieBaseModelImpl(String kBaseName) {
        KieBaseModelImpl kBaseModel = (KieBaseModelImpl) kProject.getKieBaseModel(kBaseName);
        if (kBaseModel == null) {
            throw new RuntimeException( "The requested KieBase \"" + kBaseName + "\" does not exist" );
        }
        return kBaseModel;
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
        return newKieSession(null, environment, conf);
    }

    public KieContainerSessionsPool newKieSessionsPool( int initialSize) {
        return new KieContainerSessionsPoolImpl(this, initialSize);
    }

    StatefulSessionPool createKieSessionsPool(String kSessionName, KieSessionConfiguration conf, Environment env, int initialSize, boolean stateless) {
        KieSessionModel kSessionModel = kSessionName != null ? getKieSessionModel(kSessionName) : findKieSessionModel(false);
        if ( kSessionModel == null ) {
            log.error("Unknown KieSession name: " + kSessionName);
            return null;
        }
        KnowledgeBaseImpl kBase = (KnowledgeBaseImpl) getKieBaseFromKieSessionModel( kSessionModel );
        return kBase == null ? null : new StatefulSessionPool(kBase, initialSize, () -> {
            SessionConfiguration sessConf = conf != null ? (SessionConfiguration) conf : kBase.getSessionConfiguration();
            StatefulKnowledgeSessionImpl kSession = stateless ?
                    kBase.internalCreateStatefulKnowledgeSession( env, sessConf, false ).setStateless( true ) :
                    (StatefulKnowledgeSessionImpl) kBase.newKieSession( sessConf, env );
            registerNewKieSession( kSessionModel, ( InternalKnowledgeBase ) kBase, kSession );
            return kSession;
        });
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
        KieSessionModelImpl kSessionModel = kSessionName != null ?
                                            (KieSessionModelImpl) getKieSessionModel(kSessionName) :
                                            (KieSessionModelImpl) findKieSessionModel(false);

        if ( kSessionModel == null ) {
            log.error("Unknown KieSession name: " + kSessionName);
            return null;
        }

        KieBase kBase = getKieBaseFromKieSessionModel( kSessionModel );
        if ( kBase == null ) return null;

        KieSession kSession = kBase.newKieSession( conf != null ? conf : getKieSessionConfiguration( kSessionModel ), environment );
        registerNewKieSession( kSessionModel, ( InternalKnowledgeBase ) kBase, kSession );
        return kSession;
    }

    private void registerNewKieSession( KieSessionModel kSessionModel, InternalKnowledgeBase kBase, KieSession kSession ) {
        if (isJndiAvailable()) {
            wireSessionComponents( kSessionModel, kSession );
        }
        registerLoggers(kSessionModel, kSession);
        registerCalendars(kSessionModel, kSession);

        ((StatefulKnowledgeSessionImpl ) kSession).initMBeans(containerId, kBase.getId(), kSessionModel.getName());

        kSessions.put(kSessionModel.getName(), kSession);
    }

    private KieBase getKieBaseFromKieSessionModel( KieSessionModel kSessionModel ) {
        if (kSessionModel.getType() == KieSessionModel.KieSessionType.STATELESS) {
            throw new RuntimeException("Trying to create a stateful KieSession from a stateless KieSessionModel: " + kSessionModel.getName());
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase == null ) {
            log.error("Unknown KieBase name: " + kSessionModel.getKieBaseModel().getName());
            return null;
        }
        return kBase;
    }

    private void registerLoggers(KieSessionModel kSessionModel, KieRuntimeEventManager kSession) {
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

    private void registerCalendars(KieSessionModel kSessionModel, KieSession kSession) {
        for (Map.Entry<String, String> entry : kSessionModel.getCalendars().entrySet()) {
            try {
                Calendar calendar = (Calendar) getClassLoader().loadClass( entry.getValue() ).newInstance();
                kSession.getCalendars().set( entry.getKey(), calendar );
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                log.error( "Cannot instance calendar " + entry.getKey(), e );
            }
        }
    }

    public StatelessKieSession newStatelessKieSession(String kSessionName) {
        return newStatelessKieSession(kSessionName, null);
    }

    public StatelessKieSession newStatelessKieSession(String kSessionName, KieSessionConfiguration conf) {
        KieSessionModelImpl kSessionModel = kSessionName != null ?
                                            (KieSessionModelImpl) getKieSessionModel(kSessionName) :
                                            (KieSessionModelImpl) findKieSessionModel(true);

        if ( kSessionModel == null ) {
            log.error("Unknown KieSession name: " + kSessionName);
            return null;
        }
        if (kSessionModel.getType() == KieSessionModel.KieSessionType.STATEFUL) {
            throw new RuntimeException("Trying to create a stateless KieSession from a stateful KieSessionModel: " + kSessionModel.getName());
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase == null ) {
            log.error("Unknown KieBase name: " + kSessionModel.getKieBaseModel().getName());
            return null;
        }

        StatelessKieSession statelessKieSession = kBase.newStatelessKieSession( conf != null ? conf : getKieSessionConfiguration( kSessionModel ) );
        if (isJndiAvailable()) {
            wireSessionComponents( kSessionModel, statelessKieSession );
        }
        registerLoggers(kSessionModel, statelessKieSession);

        ((StatelessKnowledgeSessionImpl) statelessKieSession).initMBeans(containerId, ((InternalKnowledgeBase) kBase).getId(), kSessionModel.getName());

        statelessKSessions.put(kSessionModel.getName(), statelessKieSession);
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
        KieSessionConfiguration ksConf = sessionConfsCache.computeIfAbsent(kSessionModel.getName(),
                k -> new SessionConfigurationImpl( null, kProject.getClassLoader() ) );
        ksConf.setOption( kSessionModel.getClockType() );
        ksConf.setOption( kSessionModel.getBeliefSystem() );
        return ksConf;
    }

    public void dispose() {
        sessionConfsCache.clear();
        kBases.values().forEach( kb -> ( (InternalKnowledgeBase) kb ).setKieContainer( null ) );

        Set<DroolsManagementAgent.CBSKey> cbskeys = new HashSet<DroolsManagementAgent.CBSKey>();
        if ( isMBeanOptionEnabled() ) {
            for (Entry<String, KieSession> kv : kSessions.entrySet()) {
                cbskeys.add(new DroolsManagementAgent.CBSKey(containerId, ((InternalKnowledgeBase) kv.getValue().getKieBase()).getId(), kv.getKey()));
            }
            for (Entry<String, StatelessKieSession> kv : statelessKSessions.entrySet()) {
                cbskeys.add(new DroolsManagementAgent.CBSKey(containerId, ((InternalKnowledgeBase) kv.getValue().getKieBase()).getId(), kv.getKey()));
            }
        }

        for (KieSession kieSession : kSessions.values()) {
            kieSession.dispose();
        }
        kSessions.clear();
        statelessKSessions.clear();

        if ( isMBeanOptionEnabled() ) {
            for (CBSKey c : cbskeys) {
                DroolsManagementAgent.getInstance().unregisterKnowledgeSessionBean(c);
            }
            for (KieBase kb : kBases.values()) {
                DroolsManagementAgent.getInstance().unregisterKnowledgeBase((InternalKnowledgeBase) kb);
            }
            DroolsManagementAgent.getInstance().unregisterMBeansFromOwner(this);
        }

        ((InternalKieServices) KieServices.Factory.get()).clearRefToContainerId(this.containerId, this);
    }

    @Override
    public void disposeSession(KieSession kieSession) {
        if (!isMBeanOptionEnabled()) {
            kSessions.values().remove( kieSession );
        }
    }

    private boolean isMBeanOptionEnabled() {
        return MBeansOption.isEnabled( System.getProperty( MBeansOption.PROPERTY_NAME, MBeansOption.DISABLED.toString() ) );
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
