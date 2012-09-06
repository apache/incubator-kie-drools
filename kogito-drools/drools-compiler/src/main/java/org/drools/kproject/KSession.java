package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.drools.runtime.conf.ClockTypeOption;

public interface KSession {

    public PropertyChangeListener getListener();

    public KSession setListener(PropertyChangeListener listener);

    public String getNamespace();

    public KSession setNamespace(String namespace);

    public String getName();

    public KSession setName(String name);

    public String getQName();

    public String getType();

    public KSession setType(String type);

    public ClockTypeOption getClockType();

    public KSession setClockType(ClockTypeOption clockType);

    public List<String> getAnnotations();

    public KSession setAnnotations(List<String> annotations);

}