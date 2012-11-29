package org.kie.builder;

import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface KieBaseModel {

    KieSessionModel newKieSessionModel(String name);

    KieBaseModel removeKieSessionModel(String qName);

    Map<String, KieSessionModel> getKieSessionModels();

    Set<String> getIncludes();

    KieBaseModel addInclude(String kBaseQName);

    KieBaseModel removeInclude(String kBaseQName);

    PropertyChangeListener getListener();

    KieBaseModel setListener(PropertyChangeListener listener);

    String getName();

    KieBaseModel setName(String name);

    AssertBehaviorOption getEqualsBehavior();

    KieBaseModel setEqualsBehavior(AssertBehaviorOption equalsBehaviour);

    EventProcessingOption getEventProcessingMode();

    KieBaseModel setEventProcessingMode(EventProcessingOption eventProcessingMode);

    List<String> getAnnotations();

    KieBaseModel setAnnotations(List<String> annotations);
}
