package org.kie.builder;

import java.util.Map;


public interface KieProject {

    GAV getGroupArtifactVersion();

    KieProject setGroupArtifactVersion(GAV gav);

    String getKBasesPath();

    KieProject setKBasesPath(String kprojectPath);

    KieBaseModel newKieBaseModel(String name);
    
    void removeKieBaseModel(String qName);

    Map<String, KieBaseModel> getKieBaseModels();

    String toXML();
}