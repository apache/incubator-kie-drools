package org.kie.builder.impl;

import org.drools.kproject.GroupArtifactVersion;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieFactory;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatelessKieSession;

import java.util.HashMap;
import java.util.Map;

public class KieContainerImpl implements InternalKieContainer {

    private GAV gav;
    private AbstractKieJar kieJar;
    private Map<String, KieBaseModel> kSessions;

    public KieContainerImpl(GAV gav) {
        this.gav = gav;
    }

    public GAV getGAV() {
        return gav;
    }

    public void updateToVersion(String version) {
        reset();
        gav = new GroupArtifactVersion(gav.getGroupId(), gav.getArtifactId(), version);
    }

    public void updateKieJar(KieJar kieJar) {
        reset();
        this.kieJar = (AbstractKieJar) kieJar;
    }

    public KieBase getKieBase(String kBaseName) {
        return loadKieJar().getKieBase(kBaseName);
    }

    public KieSession getKieSession(String kSessionName) {
        KieBaseModel kieBaseModel = getKieBaseForSession(kSessionName);
        KieBase kieBase = getKieBase(kieBaseModel.getName());
        return kieBase.newKieSession(getKnowledgeSessionConfiguration(kieBaseModel, kSessionName), null);
    }

    public StatelessKieSession getKieStatelessSession(String kSessionName) {
        KieBaseModel kieBaseModel = getKieBaseForSession(kSessionName);
        KieBase kieBase = getKieBase(kieBaseModel.getName());
        return kieBase.newStatelessKieSession(getKnowledgeSessionConfiguration(kieBaseModel, kSessionName));
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

    public KieBase getKieBase() {
        return loadKieJar().getKieBase( KieFactory.Factory.get().getDefaultGav().toExternalForm() );
    }
}
