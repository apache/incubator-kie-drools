package org.kie.builder.impl;

import org.kie.KnowledgeBaseFactory;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieContainer;
import org.kie.builder.KieProject;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieStatelessSession;
import org.kie.runtime.KnowledgeSessionConfiguration;

import java.util.HashMap;
import java.util.Map;

public class KieContainerImpl implements KieContainer {

    private final GAV gav;
    private AbstractKieJar kieJar;
    private Map<String, KieBaseModel> kSessions;

    public KieContainerImpl(GAV gav) {
        this.gav = gav;
    }

    public GAV getGAV() {
        return gav;
    }

    public void updateToVersion(String version) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.updateToVersion -> TODO");
    }

    public KieBase getKieBase(String kBaseName) {
        return loadKieJar().getKieBase(kBaseName);
    }

    public KieSession getKieSession(String kSessionName) {
        KieBaseModel kieBaseModel = getKieBaseForSession(kSessionName);
        KieBase kieBase = getKieBase(kieBaseModel.getName());
        return (KieSession) kieBase.newStatefulKnowledgeSession(getKnowledgeSessionConfiguration(kieBaseModel, kSessionName), null);
    }

    public KieStatelessSession getKieStatelessSession(String kSessionName) {
        KieBaseModel kieBaseModel = getKieBaseForSession(kSessionName);
        KieBase kieBase = getKieBase(kieBaseModel.getName());
        return (KieStatelessSession) kieBase.newStatelessKnowledgeSession(getKnowledgeSessionConfiguration(kieBaseModel, kSessionName));
    }

    public void dispose() {
        reset();
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.dispose -> TODO");
    }

    private AbstractKieJar loadKieJar() {
        if (kieJar == null) {
            kieJar = (AbstractKieJar) KieServices.Factory.get().getKieRepository().getKieJar(gav);
        }
        return kieJar;
    }

    private KieBaseModel getKieBaseForSession(String kSessionName) {
        if (kSessions == null) {
            kSessions = new HashMap<String, KieBaseModel>();
            KieProject kieProject = loadKieJar().kieProject;
            for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values()) {
                for (KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values()) {
                    kSessions.put(kieSessionModel.getName(), kieBaseModel);
                }
            }
        }
        return kSessions.get(kSessionName);
    }

    private void reset() {
        kieJar = null;
        kSessions = null;
    }

    private KnowledgeSessionConfiguration getKnowledgeSessionConfiguration(KieBaseModel kieBaseModel, String ksessionName) {
        KieSessionModel kieSessionModel = kieBaseModel.getKieSessionModels().get(ksessionName);
        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption(kieSessionModel.getClockType());
        return ksConf;
    }
}
