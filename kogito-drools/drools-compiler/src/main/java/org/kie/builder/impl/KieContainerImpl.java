package org.kie.builder.impl;

import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.KieBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.KieSessionModel;
import org.kie.builder.Results;
import org.kie.builder.Message.Level;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.kie.util.CDIHelper.wireListnersAndWIHs;

public class KieContainerImpl
    implements
    KieContainer {

    private static final Logger        log    = LoggerFactory.getLogger( KieContainerImpl.class );

    private KieProject           kProject;

    private final Map<String, KieBase> kBases = new HashMap<String, KieBase>();

    private final KieRepository        kr;

    public KieContainerImpl(KieProject kProject,
                            KieRepository kr) {
        this.kr = kr;
        this.kProject = kProject;
        kProject.init();
    }

    public GAV getGAV() {
        return kProject.getGAV();
    }

    public void updateToVersion(GAV gav) {
        kBases.clear();
        this.kProject = new KieModuleKieProject( (InternalKieModule)kr.getKieModule(gav), kr );
        this.kProject.init();
    }

    public KieBase getKieBase() {
        KieBaseModel defaultKieBaseModel = kProject.getDefaultKieBaseModel();
        if (defaultKieBaseModel == null) {
            new RuntimeException("Cannot find a defualt KieBase");
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
            kBase = AbstractKieModule.createKieBase( ( KieBaseModelImpl ) kProject.getKieBaseModel( kBaseName ),
                                                     kProject,
                                                     msgs );
            if ( kBase == null ) {
                // build error, throw runtime exception
                new RuntimeException( "Error while creating KieBase" + msgs.filterMessages( Level.ERROR  ) );
            }
            if ( kBase != null ) {
                kBases.put( kBaseName,
                            kBase );
            }
        }
        return kBase;
    }

    public KieSession newKieSession() {
        KieSessionModel defaultKieSessionModel = kProject.getDefaultKieSession();
        if (defaultKieSessionModel == null) {
            new RuntimeException("Cannot find a defualt KieSession");
        }
        return newKieSession(defaultKieSessionModel.getName());
    }

    public StatelessKieSession newKieStatelessSession() {
        KieSessionModel defaultKieSessionModel = kProject.getDefaultStatelessKieSession();
        if (defaultKieSessionModel == null) {
            new RuntimeException("Cannot find a defualt StatelessKieSession");
        }
        return newKieStatelessSession(defaultKieSessionModel.getName());
    }

    public KieSession newKieSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionModel == null ) {
            log.error("Unknown KieSession name: " + kSessionName);
            return null;
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase == null ) {
            log.error("Unknown KieBase name: " + kSessionModel.getKieBaseModel().getName());
            return null;
        }
        KieSession kSession = kBase.newKieSession(getKnowledgeSessionConfiguration(kSessionModel), null);
        wireListnersAndWIHs(kSessionModel, kSession);
        return kSession;
    }

    public StatelessKieSession newKieStatelessSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionName == null ) {
            return null;
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase == null ) {
            return null;
        }
        return kBase.newStatelessKieSession(getKnowledgeSessionConfiguration(kSessionModel));
    }

    private KnowledgeSessionConfiguration getKnowledgeSessionConfiguration(KieSessionModelImpl kSessionModel) {
        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption( kSessionModel.getClockType() );
        return ksConf;
    }

    public void dispose() {
        // TODO
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