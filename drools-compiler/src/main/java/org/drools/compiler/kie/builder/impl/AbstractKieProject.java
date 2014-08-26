package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.drools.compiler.kie.builder.impl.AbstractKieModule.buildKnowledgePackages;

public abstract class AbstractKieProject implements KieProject {

    private static final Logger                  log                        = LoggerFactory.getLogger(KieProject.class);

    protected final Map<String, KieBaseModel>    kBaseModels                = new HashMap<String, KieBaseModel>();

    private KieBaseModel                         defaultKieBase             = null;

    private KieSessionModel                      defaultKieSession          = null;

    private KieSessionModel                      defaultStatelessKieSession = null;

    private Map<KieBaseModel, Set<String>>       includesInKieBase          = new HashMap<KieBaseModel, Set<String>>();

    protected final Map<String, KieSessionModel> kSessionModels             = new HashMap<String, KieSessionModel>();

    public ResultsImpl verify() {
        ResultsImpl messages = new ResultsImpl();
        verify(messages);
        return messages;
    }

    public ResultsImpl verify(String... kBaseNames) {
        ResultsImpl messages = new ResultsImpl();
        verify(kBaseNames, messages);
        return messages;
    }

    public void verify(ResultsImpl messages) {
        for ( KieBaseModel model : kBaseModels.values() ) {
            buildKnowledgePackages((KieBaseModelImpl) model, this, messages);
        }
    }

    public void verify(String[] kBaseNames, ResultsImpl messages) {
        for ( String modelName : kBaseNames ) {
            buildKnowledgePackages( (KieBaseModelImpl) kBaseModels.get( modelName ), this, messages);
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

    protected void indexParts(Collection<InternalKieModule> kieModules,
                              Map<String, InternalKieModule> kJarFromKBaseName) {
        for ( InternalKieModule kJar : kieModules ) {
            KieModuleModel kieProject = kJar.getKieModuleModel();
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                if (kieBaseModel.isDefault()) {
                    if (defaultKieBase == null) {
                        defaultKieBase = kieBaseModel;
                    } else {
                        defaultKieBase = null;
                        log.warn("Found more than one default KieBase: disabling all. KieBases will be accessible only by name");
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
                                log.warn("Found more than one default KieSession: disabling all. KieSessions will be accessible only by name");
                            }
                        } else {
                            if (defaultStatelessKieSession == null) {
                                defaultStatelessKieSession = kieSessionModel;
                            } else {
                                defaultStatelessKieSession = null;
                                log.warn("Found more than one default StatelessKieSession: disabling all. StatelessKieSessions will be accessible only by name");
                            }
                        }
                    }

                    ((KieSessionModelImpl) kieSessionModel).setKBase( kieBaseModel ); // should already be set, but just in case
                    kSessionModels.put( kieSessionModel.getName(), kieSessionModel );
                }
            }
        }
    }
    
    protected void cleanIndex() {
        kBaseModels.clear();
        kSessionModels.clear();
        includesInKieBase.clear();
        defaultKieBase = null;
        defaultKieSession = null;
        defaultStatelessKieSession = null;
    }

    public Set<String> getTransitiveIncludes(String kBaseName) {
        return getTransitiveIncludes(getKieBaseModel(kBaseName));
    }

    public Set<String> getTransitiveIncludes(KieBaseModel kBaseModel) {
        Set<String> includes = includesInKieBase.get(kBaseModel);
        if (includes == null) {
            includes = new HashSet<String>();
            getTransitiveIncludes(kBaseModel, includes);
            includesInKieBase.put(kBaseModel, includes);
        }
        return includes;
    }

    private void getTransitiveIncludes(KieBaseModel kBaseModel, Set<String> includes) {
        if (kBaseModel == null) {
            return;
        }
        Set<String> incs = ((KieBaseModelImpl)kBaseModel).getIncludes();
        if (incs != null && !incs.isEmpty()) {
            for (String inc : incs) {
                if (!includes.contains(inc)) {
                    includes.add(inc);
                    getTransitiveIncludes(getKieBaseModel(inc), includes);
                }
            }
        }
    }
}
