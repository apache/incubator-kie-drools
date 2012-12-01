package org.kie.builder.impl;

import org.drools.kproject.KieBaseModelImpl;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieJar;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatelessKieSession;

public class KieContainerImpl
    implements
    InternalKieContainer {

    private GAV                       gav;
    private InternalKieJar            kieJar;

    public KieContainerImpl(GAV gav) {
        this.gav = gav;
    }

    public GAV getGAV() {
        return gav;
    }

    public void updateToVersion(GAV version) {
        kieJar = null;
        gav = version;
    }

    public void updateKieJar(KieJar kieJar) {
        if (this.kieJar instanceof CompositeKieJar) {
            ((CompositeKieJar)this.kieJar).updateKieJar(kieJar);
        } else {
            this.kieJar = (InternalKieJar) kieJar;
        }
    }

    public KieBase getKieBase() {
        return getKieBase(KieBaseModelImpl.DEFAULT_KIEBASE_NAME);
    }

    public KieBase getKieBase(String kBaseName) {
        return loadKieJar().getKieBase( kBaseName );
    }

    public KieSession getKieSession() {
        return getKieBase().newKieSession();
    }

    public KieSession getKieSession(String kSessionName) {
        KieBaseModel kieBaseModel = getKieBaseForSession( kSessionName );
        KieBase kieBase = getKieBase( kieBaseModel.getName() );
        return kieBase.newKieSession( getKnowledgeSessionConfiguration( kieBaseModel,
                                                                        kSessionName ),
                                      null );
    }

    public StatelessKieSession getKieStatelessSession(String kSessionName) {
        KieBaseModel kieBaseModel = getKieBaseForSession( kSessionName );
        KieBase kieBase = getKieBase( kieBaseModel.getName() );
        return kieBase.newStatelessKieSession( getKnowledgeSessionConfiguration( kieBaseModel,
                                                                                 kSessionName ) );
    }

    public void dispose() {
        // TODO: should it store all the KieSession created from this container and then dispose them?
        kieJar = null;
    }

    private InternalKieJar loadKieJar() {
        if ( kieJar == null ) {
            kieJar = (InternalKieJar) KieServices.Factory.get().getKieRepository().getKieJar(gav);
            if ( kieJar == null ) {
                throw new RuntimeException("It doesn't exist any KieJar with gav: " + gav);
            }
        }
        return kieJar;
    }

    private KieBaseModel getKieBaseForSession(String kSessionName) {
        return loadKieJar().getKieBaseForSession(kSessionName);
    }

    private KnowledgeSessionConfiguration getKnowledgeSessionConfiguration(KieBaseModel kieBaseModel,
                                                                           String ksessionName) {
        KieSessionModel kieSessionModel = kieBaseModel.getKieSessionModels().get( ksessionName );
        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption( kieSessionModel.getClockType() );
        return ksConf;
    }
}
