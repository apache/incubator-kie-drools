package org.kie.builder;

import java.util.Map;


public interface KieProject {

    String KPROJECT_JAR_PATH = "META-INF/kproject.xml";
    String KPROJECT_RELATIVE_PATH = "src/main/resources/" + KPROJECT_JAR_PATH;

    GAV getGroupArtifactVersion();

    KieProject setGroupArtifactVersion(GAV gav);

    String getKBasesPath();

    KieProject setKBasesPath(String kprojectPath);

    KieBaseModel newKieBaseModel(String name);
    
    void removeKieBaseModel(String qName);

    Map<String, KieBaseModel> getKieBaseModels();

    String toXML();
}