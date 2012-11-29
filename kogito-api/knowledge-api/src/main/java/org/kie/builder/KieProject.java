package org.kie.builder;

import java.beans.PropertyChangeListener;
import java.util.Map;


public interface KieProject {

    GAV getGroupArtifactVersion();
    
    void setGroupArtifactVersion(GAV gav);

    PropertyChangeListener getListener();

    KieProject setListener(PropertyChangeListener listener);

    String getKProjectPath();

    KieProject setKProjectPath(String kprojectPath);

    String getKBasesPath();

    KieProject setKBasesPath(String kprojectPath);

    KieBaseModel newKieBaseModel(String name);
    
    void removeKieBaseModel(String qName);

    Map<String, KieBaseModel> getKieBaseModels();

    String toXML();
}