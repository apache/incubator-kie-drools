package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.Map;

public interface KProject {

    public  PropertyChangeListener getListener();

    public  void setListener(PropertyChangeListener listener);

    public  String getKProjectPath();

    public  void setKProjectPath(String kprojectPath);

    public  String getKBasesPath();

    public  void setKBasesPath(String kprojectPath);

    public  KBase newKBase(String namespace,
                           String name);
    
    public  void removeKBase(String qName);

    public  Map<String, KBase> getKBases();

}