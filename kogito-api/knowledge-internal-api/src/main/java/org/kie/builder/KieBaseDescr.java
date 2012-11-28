package org.kie.builder;

import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface KieBaseDescr {

    KieSessionDescr newKieSessionDescr(String name);

    KieBaseDescr removeKieSessionDescr(String qName);

    Map<String, KieSessionDescr> getKieSessionDescrs();

    Set<String> getIncludes();

    KieBaseDescr addInclude(String kBaseQName);

    KieBaseDescr removeInclude(String kBaseQName);

    PropertyChangeListener getListener();

    KieBaseDescr setListener(PropertyChangeListener listener);

    String getName();

    KieBaseDescr setName(String name);

    AssertBehaviorOption getEqualsBehavior();

    KieBaseDescr setEqualsBehavior(AssertBehaviorOption equalsBehaviour);

    EventProcessingOption getEventProcessingMode();

    KieBaseDescr setEventProcessingMode(EventProcessingOption eventProcessingMode);

    List<String> getAnnotations();

    KieBaseDescr setAnnotations(List<String> annotations);
}
