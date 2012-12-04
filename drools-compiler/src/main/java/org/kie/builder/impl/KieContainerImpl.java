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
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
        if ( kProject != null ) {
            kProject.init();
        }
    }

    public GAV getGAV() {
        return kProject.getGAV();
    }

    public void updateToVersion(GAV gav) {
        kBases.clear();
        this.kProject = new KieModuleKieProject( (InternalKieModule)kr.getKieModule(gav), kr );
        if ( kProject != null ) {
            this.kProject.init();
        }
    }

    public KieBase getKieBase() {
        return getKieBase( KieBaseModelImpl.DEFAULT_KIEBASE_NAME );
    }

    public KieBase getKieBase(String kBaseName) {
        KieBase kBase = kBases.get( kBaseName );
        if ( kBase == null ) {
            if ( kProject != null ) {
                kBase = AbstractKieModule.createKieBase( kProject.getKieBaseModel( kBaseName ),
                                                         kProject );
                if ( kBase != null ) {
                    kBases.put( kBaseName,
                                kBase );
                }
            } else {
                return KnowledgeBaseFactory.newKnowledgeBase();
            }
        }
        return kBase;
    }

    public KieSession getKieSession() {
        return getKieBase().newKieSession();
    }

    public StatelessKieSession getKieStatelessSession() {
        return getKieBase().newStatelessKieSession();
    }

    public KieSession getKieSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionModel == null ) {
            return null;
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase != null ) {
            return kBase.newKieSession();
        } else {
            return null;
        }
    }

    public StatelessKieSession getKieStatelessSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = (KieSessionModelImpl) kProject.getKieSessionModel( kSessionName );
        if ( kSessionName == null ) {
            return null;
        }
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        if ( kBase != null ) {
            return kBase.newStatelessKieSession();
        } else {
            return null;
        }
    }

    public void dispose() {
    }

    public KieModule getKieModuleForKBase(String kBaseName) {
        return kProject.getKieModuleForKBase( kBaseName );
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kProject.getKieBaseModel( kBaseName );
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kProject.getKieSessionModel( kSessionName );
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.kProject.getClassLoader();
    }

}