package org.kie.builder.impl;

import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKieProject implements KieProject {

    private static final Logger                  log                        = LoggerFactory.getLogger(KieProject.class);

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
            AbstractKieModule.buildKnowledgePackages((KieBaseModelImpl) model,
                    this,
                    messages);
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

    protected void indexParts(Map<ReleaseId, InternalKieModule> kieModules,
                              Map<String, InternalKieModule> kJarFromKBaseName) {
        for ( InternalKieModule kJar : kieModules.values() ) {
            KieModuleModel kieProject = kJar.getKieModuleModel();
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                if (kieBaseModel.isDefault()) {
                    if (defaultKieBase == null) {
                        defaultKieBase = kieBaseModel;
                    } else {
                        defaultKieBase = null;
                        log.warn("Found more than one defualt KieBase: disabling all. KieBases will be accessible only by name");
                    }
                }

                kBaseModels.put( kieBaseModel.getName(), kieBaseModel );
                ((KieBaseModelImpl) kieBaseModel).setKModule( kieProject ); // should already be set, but just in case

                kJarFromKBaseName.put( kieBaseModel.getName(), kJar );
                for ( KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values() ) {
                    if (kieSessionModel.isDefault()) {
                        if (kieSessionModel.getType() == KieSessionModel.KieSessionType.STATEFUL) {
                            if (defaultKieSession == null) {
                                defaultKieSession = kieSessionModel;
                            } else {
                                defaultKieSession = null;
                                log.warn("Found more than one defualt KieSession: disabling all. KieSessions will be accessible only by name");
                            }
                        } else {
                            if (defaultStatelessKieSession == null) {
                                defaultStatelessKieSession = kieSessionModel;
                            } else {
                                defaultStatelessKieSession = null;
                                log.warn("Found more than one defualt StatelessKieSession: disabling all. StatelessKieSessions will be accessible only by name");
                            }
                        }
                    }

                    ((KieSessionModelImpl) kieSessionModel).setKBase( kieBaseModel ); // should already be set, but just in case
                    kSessionModels.put( kieSessionModel.getName(), kieSessionModel );
                }
            }
        }
    }
}
