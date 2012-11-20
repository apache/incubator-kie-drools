package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;

public interface KBase {

    KSession newKSession(String namespace, String name);

    KBase removeKSession(String qName);

    Map<String, KSession> getKSessions();

    Set<String> getIncludes();

    KBase addInclude(String kBaseQName);

    KBase removeInclude(String kBaseQName);

    PropertyChangeListener getListener();

    KBase setListener(PropertyChangeListener listener);

    String getNamespace();

    KBase setNamespace(String namespace);

    String getName();

    KBase setName(String name);

    String getQName();

    AssertBehaviorOption getEqualsBehavior();

    KBase setEqualsBehavior(AssertBehaviorOption equalsBehaviour);

    EventProcessingOption getEventProcessingMode();

    KBase setEventProcessingMode(EventProcessingOption eventProcessingMode);

    List<String> getAnnotations();

    KBase setAnnotations(List<String> annotations);
}
