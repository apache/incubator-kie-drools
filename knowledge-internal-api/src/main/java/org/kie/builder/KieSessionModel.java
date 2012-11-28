package org.kie.builder;

import org.kie.runtime.conf.ClockTypeOption;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface KieSessionModel {

    PropertyChangeListener getListener();

    KieSessionModel setListener(PropertyChangeListener listener);

    String getName();

    KieSessionModel setName(String name);

    String getType();

    KieSessionModel setType(String type);

    ClockTypeOption getClockType();

    KieSessionModel setClockType(ClockTypeOption clockType);

    List<String> getAnnotations();

    KieSessionModel setAnnotations(List<String> annotations);

}