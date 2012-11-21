package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.kie.runtime.conf.ClockTypeOption;

public interface KSession {

    PropertyChangeListener getListener();

    KSession setListener(PropertyChangeListener listener);

    String getName();

    KSession setName(String name);

    String getType();

    KSession setType(String type);

    ClockTypeOption getClockType();

    KSession setClockType(ClockTypeOption clockType);

    List<String> getAnnotations();

    KSession setAnnotations(List<String> annotations);

}