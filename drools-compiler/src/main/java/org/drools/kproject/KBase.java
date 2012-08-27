package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;

public interface KBase {

    public  KSession newKSession(String namespace, String name);

    public  void removeKSession(String qName);
    
    public  Map<String, KSession> getKSessions();

    public  PropertyChangeListener getListener();

    public  void setListener(PropertyChangeListener listener);

    public  String getNamespace();

    public  void setNamespace(String namespace);

    public  String getName();

    public  void setName(String name);

    public  String getQName();

    public  List<String> getFiles();

    public  void setFiles(List<String> files);

    public  AssertBehaviorOption getEqualsBehavior();

    public  void setEqualsBehavior(AssertBehaviorOption equalsBehaviour);

    public  EventProcessingOption getEventProcessingMode();

    public  void setEventProcessingMode(EventProcessingOption eventProcessingMode);

    public  List<String> getAnnotations();

    public  void setAnnotations(List<String> annotations);
}