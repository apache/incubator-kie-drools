package org.kie.builder.impl;

import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKieProject implements KieProject {

    protected final Map<String, KieBaseModel>    kBaseModels                = new HashMap<String, KieBaseModel>();

    private KieBaseModel                         defaultKieBase             = null;

    private KieSessionModel                      defaultKieSession          = null;

    private KieSessionModel                      defaultStatelessKieSession = null;

    protected final Map<String, KieSessionModel> kSessionModels             = new HashMap<String, KieSessionModel>();

    public ResultsImpl verify() {
        ResultsImpl messages = new ResultsImpl();
        verify(messages);
        return messages;
    }

    public void verify(ResultsImpl messages) {
        for ( KieBaseModel model : kBaseModels.values() ) {
            AbstractKieModule.buildKnowledgePackages( (KieBaseModelImpl) model,
                                                      this,
                                                      messages );
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
        return kBaseModels.get( kBaseName );
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kSessionModels.get( kSessionName );
    }

    protected void indexParts(Map<GAV, InternalKieModule> kieModules,
                              Map<String, InternalKieModule> kJarFromKBaseName) {
        for ( InternalKieModule kJar : kieModules.values() ) {
            KieModuleModel kieProject = kJar.getKieModuleModel();
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                if (kieBaseModel.isDefault()) {
                    defaultKieBase = kieBaseModel;
                }

                kBaseModels.put( kieBaseModel.getName(), kieBaseModel );
                ((KieBaseModelImpl) kieBaseModel).setKModule( kieProject ); // should already be set, but just in case

                kJarFromKBaseName.put( kieBaseModel.getName(), kJar );
                for ( KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values() ) {
                    if (kieSessionModel.isDefault()) {
                        if (kieSessionModel.getType() == KieSessionModel.KieSessionType.STATEFUL) {
                            defaultKieSession = kieSessionModel;
                        } else {
                            defaultStatelessKieSession = kieSessionModel;
                        }
                    }

                    ((KieSessionModelImpl) kieSessionModel).setKBase( kieBaseModel ); // should already be set, but just in case
                    kSessionModels.put( kieSessionModel.getName(), kieSessionModel );
                }
            }
        }
    }
}
