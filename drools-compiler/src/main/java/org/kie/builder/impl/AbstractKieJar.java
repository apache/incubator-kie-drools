package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProject;
import org.kie.runtime.KieBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKieJar implements InternalKieJar {

    private ClassLoader classLoader;
    
    private final Map<String, KieBase> kbases = new HashMap<String, KieBase>();

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

    public void addKieBase(String kBaseName, KieBase kBase) {
        kbases.put(kBaseName, kBase);
    }

    public void removeKieBase(String kBaseName) {
        kbases.remove(kBaseName);
    }

    public Collection<String> getKieBaseNames() {
        return kbases.keySet();
    }

    public KieBase getKieBase(String kBaseName) {
        return kbases.get(kBaseName);
    }

    protected abstract Map<String, KieBaseModel> indexKieSessions();

    public KieBaseModel getKieBaseForSession(String kSessionName) {
        if (kSessions == null) {
            kSessions = indexKieSessions();
        }
        return kSessions.get(kSessionName);
    }
}
