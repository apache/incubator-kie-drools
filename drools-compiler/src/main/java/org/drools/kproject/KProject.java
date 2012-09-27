package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.Map;


public interface KProject {
    
    public GroupArtifactVersion getGroupArtifactVersion();
    
    public void setGroupArtifactVersion(GroupArtifactVersion gav);

    public  PropertyChangeListener getListener();

    public  KProject setListener(PropertyChangeListener listener);

    public  String getKProjectPath();

    public  KProject setKProjectPath(String kprojectPath);

    public  String getKBasesPath();

    public  KProject setKBasesPath(String kprojectPath);

    public  KBase newKBase(String namespace,
                           String name);
    
    public  void removeKBase(String qName);

    public  Map<String, KBase> getKBases();

}