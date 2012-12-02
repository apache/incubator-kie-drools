package org.kie.builder.impl;

import org.drools.rule.Collect;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProjectModel;
import org.kie.definition.KnowledgePackage;
import org.kie.runtime.KieBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKieJar implements InternalKieJar {

    private ClassLoader classLoader;
    
    private final Map<String, Collection<KnowledgePackage>> packageCache = new HashMap<String, Collection<KnowledgePackage>>();
    
//    private final Map<String, KieBase> kbases = new HashMap<String, KieBase>();

    protected Map<String, KieBaseModel> kSessions;

    protected final GAV gav;

    public AbstractKieJar(GAV gav) {
        this.gav = gav;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public GAV getGAV() {
        return gav;
    }
    public Map<String, Collection<KnowledgePackage>> getKnowledgePackageCache() {
        return packageCache;
    }
// 
//    public void addKieBase(String kBaseName, KieBase kBase) {
//        kbases.put(kBaseName, kBase);
//    }
//
//    public void removeKieBase(String kBaseName) {
//        kbases.remove(kBaseName);
//    }
//
//    public Collection<String> getKieBaseNames() {
//        return kbases.keySet();
//    }
//
//    public KieBase getKieBase(String kBaseName) {
//        return kbases.get(kBaseName);
//    }

    protected abstract Map<String, KieBaseModel> indexKieSessions();

    public KieBaseModel getKieBaseForSession(String kSessionName) {
        if (kSessions == null) {
            kSessions = indexKieSessions();
        }
        return kSessions.get(kSessionName);
    }
}
