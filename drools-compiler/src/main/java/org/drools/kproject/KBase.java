package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;

public interface KBase {

    public  KSession newKSession(String namespace, String name);

    public  void removeKSession(String qName);
    
    public  Map<String, KSession> getKSessions();
    
    public Set<KBase> getIncludes();

    public void addInclude(KBase kbase);
    
    public void removeInclude(KBase kbase);

    public  PropertyChangeListener getListener();

    public  KBase setListener(PropertyChangeListener listener);

    public  String getNamespace();

    public  KBase setNamespace(String namespace);

    public  String getName();

    public  KBase setName(String name);

    public  String getQName();

    public  List<String> getFiles();

    public  KBase setFiles(List<String> files);

    public  AssertBehaviorOption getEqualsBehavior();

    public  KBase setEqualsBehavior(AssertBehaviorOption equalsBehaviour);

    public  EventProcessingOption getEventProcessingMode();

    public  KBase setEventProcessingMode(EventProcessingOption eventProcessingMode);

    public  List<String> getAnnotations();

    public  KBase setAnnotations(List<String> annotations);
}