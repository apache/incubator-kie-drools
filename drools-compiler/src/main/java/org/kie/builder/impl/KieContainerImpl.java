package org.kie.builder.impl;

import org.drools.impl.InternalKnowledgeBase;
import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.KieBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.Message.Level;
import org.kie.builder.ReleaseId;
import org.kie.builder.Results;
import org.kie.builder.model.KieBaseModel;
import org.kie.builder.model.KieSessionModel;
import org.kie.definition.KnowledgePackage;
import org.kie.internal.utils.CompositeClassLoader;
import org.kie.runtime.Environment;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.kie.builder.impl.AbstractKieModule.buildKnowledgePackages;
import static org.kie.util.CDIHelper.wireListnersAndWIHs;

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
        this.kProject = new KieModuleKieProject( (InternalKieModule)kr.getKieModule(releaseId), kr );
        this.kProject.init();
    }

    public KieBase getKieBase() {
        KieBaseModel defaultKieBaseModel = kProject.getDefaultKieBaseModel();
        if (defaultKieBaseModel == null) {
            throw new RuntimeException("Cannot find a defualt KieBase");
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
            kBase = createKieBase(kBaseName, kProject, msgs);
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

    private KieBase createKieBase(String kBaseName, KieProject kieProject, ResultsImpl messages) {
        KieBaseModelImpl kBaseModel = (KieBaseModelImpl) kProject.getKieBaseModel(kBaseName);
        CompositeClassLoader cl = kieProject.getClassLoader(); // the most clone the CL, as each builder and rbase populates it

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

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase( getKnowledgeBaseConfiguration(kBaseModel, cl) );

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
        return newKieSession((Environment)null);
    }

    public KieSession getKieSession() {
        KieSessionModel defaultKieSessionModel = kProject.getDefaultKieSession();
        if (defaultKieSessionModel == null) {
            throw new RuntimeException("Cannot find a defualt KieSession");
        }
        return getKieSession(defaultKieSessionModel.getName());
    }

    public KieSession newKieSession(Environment environment) {
        KieSessionModel defaultKieSessionModel = kProject.getDefaultKieSession();
        if (defaultKieSessionModel == null) {
            throw new RuntimeException("Cannot find a defualt KieSession");
        }
        return newKieSession(defaultKieSessionModel.getName(), environment);
    }

    public StatelessKieSession newStatelessKieSession() {
        KieSessionModel defaultKieSessionModel = kProject.getDefaultStatelessKieSession();
        if (defaultKieSessionModel == null) {
            throw new RuntimeException("Cannot find a defualt StatelessKieSession");
        }
        return newStatelessKieSession(defaultKieSessionModel.getName());
    }

    public StatelessKieSession getStatelessKieSession() {
        KieSessionModel defaultKieSessionModel = kProject.getDefaultStatelessKieSession();
        if (defaultKieSessionModel == null) {
            throw new RuntimeException("Cannot find a defualt StatelessKieSession");
        }
        return getStatelessKieSession(defaultKieSessionModel.getName());
    }

    public KieSession newKieSession(String kSessionName) {
        return newKieSession(kSessionName, null);
    }

    public KieSession getKieSession(String kSessionName) {
        KieSession kieSession = kSessions.get(kSessionName);
        return kieSession != null ? kieSession : newKieSession(kSessionName);
    }

    public KieSession newKieSession(String kSessionName, Environment environment) {
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
        KieSession kSession = kBase.newKieSession(getKnowledgeSessionConfiguration(kSessionModel), environment);
        wireListnersAndWIHs(kSessionModel, kSession);

        KieSession oldSession = kSessions.remove(kSessionName);
        if (oldSession != null) {
            oldSession.dispose();
        }
        kSessions.put(kSessionName, kSession);

        return kSession;
    }

    public StatelessKieSession newStatelessKieSession(String kSessionName) {
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
        StatelessKieSession statelessKieSession = kBase.newStatelessKieSession(getKnowledgeSessionConfiguration(kSessionModel));
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