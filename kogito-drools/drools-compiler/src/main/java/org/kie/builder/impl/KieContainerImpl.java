package org.kie.builder.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.KieBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieContainerImpl
    implements
    KieContainer {

    private static final Logger  log    = LoggerFactory.getLogger( KieContainerImpl.class );

    private KieProject           kProject;

    private Map<String, KieBase> kBases = new HashMap<String, KieBase>();

    private KieRepository        kr;

    public KieContainerImpl() {
        this( null,
              KieServices.Factory.get().getKieRepository() );
    }

    public KieContainerImpl(KieProject kProject,
                            KieRepository kr) {
        this.kr = kr;
        this.kProject = kProject;
        this.kProject.verify();
    }

    public GAV getGAV() {
        return kProject.getGAV();
    }

    public void updateToVersion(GAV version) {
        //throw new UnsupportedOperationException( "The " + getClass().getSimpleName() + " cannot be updated" );
    }

    public void updateKieJar(KieModule kieJar) {
        //throw new UnsupportedOperationException( "The " + getClass().getSimpleName() + " cannot be updated" );
    }

    public KieBase getKieBase() {
        return getKieBase( KieBaseModelImpl.DEFAULT_KIEBASE_NAME );
    }

    public KieBase getKieBase(String kBaseName) {
        KieBase kBase = kBases.get( kBaseName );
        if ( kBase == null ) {
            kBase = AbstractKieModules.createKieBase( kProject.getKieBaseModel( kBaseName ),
                                                      kProject );
            if ( kBase != null ) {
                kBases.put(  kBaseName, kBase );
            }
        }
        return kBase;
    }

    public KieSession getKieSession() {
        return null;
    }

    public StatelessKieSession getKieStatelessSession() {
        return null;
    }
    
    public KieSession getKieSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionModel == null ) {
            return null;
        }        
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        return kBase.newKieSession();
    }

    public StatelessKieSession getKieStatelessSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionName == null ) {
            return null;
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        return kBase.newStatelessKieSession();
    }

    public void dispose() {
    }

    public KieModule getKieJarForKBase(String kBaseName) {
        return kProject.getKieModuleForKBase( kBaseName );
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kProject.getKieBaseModel( kBaseName );
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kProject.getKieSessionModel( kSessionName );
    }

}