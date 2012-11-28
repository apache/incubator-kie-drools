package org.kie.builder;

import org.kie.runtime.conf.ClockTypeOption;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface KieSessionDescr {

    PropertyChangeListener getListener();

    KieSessionDescr setListener(PropertyChangeListener listener);

    String getName();

    KieSessionDescr setName(String name);

    String getType();

    KieSessionDescr setType(String type);

    ClockTypeOption getClockType();

    KieSessionDescr setClockType(ClockTypeOption clockType);

    List<String> getAnnotations();

    KieSessionDescr setAnnotations(List<String> annotations);

}