package org.drools.compiler.kie.builder.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.definition.KnowledgePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.AbstractKieModule.*;
import static org.drools.compiler.kie.util.CDIHelper.*;

public class KieContainerImpl
    implements
    InternalKieContainer {

    private static final Logger        log    = LoggerFactory.getLogger( KieContainerImpl.class );

    private KieProject           kProject;

    private final Map<String, KieBase> kBases = new HashMap<String, KieBase>();

    private final Map<String, KieSession> kSessions = new HashMap<String, KieSession>();
    private final Map<String, StatelessKieSession> statelessKSessions = new HashMap<String, StatelessKieSession>();

    private final KieRepository        kr;

    public KieContainerImpl(KieProject kProject,
                            KieRepository kr) {
        this.kr = kr;
        this.kProject = kProject;
        kProject.init();
    }

    public ReleaseId getReleaseId() {
        return kProject.getGAV();
    }

    public void updateToVersion(ReleaseId releaseId) {
        kBases.clear();
        this.kProject = new KieModuleKieProject( (InternalKieModule)kr.getKieModule(releaseId) );
        this.kProject.init();
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
            if ( kBase != null ) {
                kBases.put( kBaseName,
                            kBase );
            }
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

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase( conf != null ? conf : getKnowledgeBaseConfiguration(kBaseModel, cl) );

        kBase.addKnowledgePackages( pkgs );
        return kBase;
    }

    private KieBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModelImpl kBaseModel, ClassLoader cl) {
        KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, cl);
        kbConf.setOption(kBaseModel.getEqualsBehavior());
        kbConf.setOption(kBaseModel.getEventProcessingMode());
        return kbConf;
    }

    public KieSession newKieSession() {
        return newKieSession((Environment)null, (KieSessionConfiguration)null);
    }

    public KieSession getKieSession() {
        KieSessionModel defaultKieSessionModel = findKieSessionModel(false);
        return getKieSession(defaultKieSessionModel.getName());
    }

    public KieSession newKieSession(KieSessionConfiguration conf) {
        return newKieSession((Environment)null, conf);
    }

    public KieSession newKieSession(Environment environment) {
        return newKieSession(environment, (KieSessionConfiguration)null);
    }

    public KieSession newKieSession(Environment environment, KieSessionConfiguration conf) {
        KieSessionModel defaultKieSessionModel = findKieSessionModel(false);
        return newKieSession(defaultKieSessionModel.getName(), environment, conf);
    }

    private KieSessionModel findKieSessionModel(boolean stateless) {
        KieSessionModel defaultKieSessionModel = stateless ? kProject.getDefaultStatelessKieSession() : kProject.getDefaultKieSession();
        if (defaultKieSessionModel == null) {
            throw new RuntimeException(stateless ? "Cannot find a defualt StatelessKieSession" : "Cannot find a defualt KieSession");
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

        KieSession oldSession = kSessions.remove(kSessionName);
        if (oldSession != null) {
            oldSession.dispose();
        }
        kSessions.put(kSessionName, kSession);

        return kSession;
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
