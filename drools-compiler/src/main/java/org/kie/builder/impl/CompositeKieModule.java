package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CompositeKieModule implements InternalKieModule {

    private final GAV gav;

    private final Map<String, InternalKieModule> kieModules = new HashMap<String, InternalKieModule>();

    public CompositeKieModule(GAV gav) {
        this.gav = gav;
    }

    @Override
    public GAV getGAV() {
        return gav;
    }

    public void addKieModule(KieModule kieModule) {
        kieModules.put(getKieModuleKey(kieModule), (InternalKieModule)kieModule);
    }

    @Override
    public KieModuleModel getKieModuleModel() {
        // TODO: merge all the KieModuleModel in a single one ?
        InternalKieModule firstKieModule = kieModules.values().iterator().next();
        return firstKieModule.getKieModuleModel();
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException("org.kie.builder.impl.CompositeKieModule.getBytes -> TODO");

    }

    @Override
    public Map<GAV, InternalKieModule> getDependencies() {
        return Collections.emptyMap();
    }

    @Override
    public void setDependencies(Map<GAV, InternalKieModule> dependencies) {
        throw new UnsupportedOperationException("org.kie.builder.impl.CompositeKieModule.setDependencies -> TODO");
    }

    @Override
    public boolean isAvailable(String pResourceName) {
        throw new UnsupportedOperationException("org.kie.builder.impl.CompositeKieModule.isAvailable -> TODO");
    }

    @Override
    public byte[] getBytes(String pResourceName) {
        InternalKieModule firstKieModule = kieModules.values().iterator().next();
        return firstKieModule.getBytes(pResourceName);
    }

    @Override
    public Collection<String> getFileNames() {
        InternalKieModule firstKieModule = kieModules.values().iterator().next();
        return firstKieModule.getFileNames();
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException("org.kie.builder.impl.CompositeKieModule.getFile -> TODO");
    }

    private String getKieModuleKey(KieModule kieModule) {
        GAV gav = kieModule.getGAV();
        return gav.getGroupId() + ":" + gav.getArtifactId();
    }
}
