package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drools.ClockType;
import org.drools.runtime.conf.ClockTypeOption;

public class KSession {
    private String          namespace;
    private String          name;

    private String          type;
    private ClockTypeOption clockType;

    private List<String>    annotations;
        
    private transient PropertyChangeListener listener;

    public KSession(String namespace,
                    String name) {
        this.namespace = namespace;
        this.name = name;
        this.annotations = new ArrayList<String>();
    }

    public PropertyChangeListener getListener() {
        return listener;
    }

    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "namespace", this.namespace, namespace ) );
        }
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "name", this.name, name ) );
        }
        this.name = name;
    }

    public String getQName() {
        return this.namespace + "." + this.name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "type", this.type, type ) );
        }
        this.type = type;
    }

    public ClockTypeOption getClockType() {
        return clockType;
    }

    public void setClockType(ClockTypeOption clockType) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "clockType", this.clockType, clockType ) );
        }
        this.clockType = clockType;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "annotations", this.annotations, annotations ) );
        }
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "KSession [namespace=" + namespace + ", name=" + name + ", clockType=" + clockType + ", annotations=" + annotations + "]";
    }

}
