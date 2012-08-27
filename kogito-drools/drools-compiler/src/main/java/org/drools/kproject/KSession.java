package org.drools.kproject;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.drools.runtime.conf.ClockTypeOption;

public interface KSession {

    public abstract PropertyChangeListener getListener();

    public abstract void setListener(PropertyChangeListener listener);

    public abstract String getNamespace();

    public abstract void setNamespace(String namespace);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getQName();

    public abstract String getType();

    public abstract void setType(String type);

    public abstract ClockTypeOption getClockType();

    public abstract void setClockType(ClockTypeOption clockType);

    public abstract List<String> getAnnotations();

    public abstract void setAnnotations(List<String> annotations);

}