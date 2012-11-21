package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.Map;


public interface KProject {
    
    GroupArtifactVersion getGroupArtifactVersion();
    
    void setGroupArtifactVersion(GroupArtifactVersion gav);

    PropertyChangeListener getListener();

    KProject setListener(PropertyChangeListener listener);

    String getKProjectPath();

    KProject setKProjectPath(String kprojectPath);

    String getKBasesPath();

    KProject setKBasesPath(String kprojectPath);

    KBase newKBase(String name);
    
    void removeKBase(String qName);

    Map<String, KBase> getKBases();

}