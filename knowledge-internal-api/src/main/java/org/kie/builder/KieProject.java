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

    KieBaseDescr newKieBaseDescr(String name);
    
    void removeKieBaseDescr(String qName);

    Map<String, KieBaseDescr> getKieBaseDescrs();

    String toXML();
}