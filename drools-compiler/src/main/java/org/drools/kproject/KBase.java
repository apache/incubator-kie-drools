package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;

public class KBase {
    private String                namespace;

    private String                name;

    private List<String>          files;

    private List<String>          annotations;

    private AssertBehaviorOption  equalsBehavior;

    private EventProcessingOption eventProcessingMode;

    private Map<String, KSession> kSessions;
    
    private transient PropertyChangeListener listener;

    public KBase() {
        this( null, null, null );
    }
    

    public KBase(String namespace,
                 String name) {
        this( namespace, name, null );
    }

    public KBase(String namespace,
                 String name,
                 List<String> files) {
        this.namespace = namespace;
        this.name = name;
        this.files = (files == null) ? new ArrayList<String>() : files;
        this.annotations = new ArrayList<String>();
        this.kSessions = new HashMap<String, KSession>();
    }        

    public Map<String, KSession> getKSessions() {
        return kSessions;
    }

    public void setKSessions(Map<String, KSession> kSessions) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kSessions", this.kSessions, kSessions ) );
            for ( KSession ksession : kSessions.values()) {
                // make sure the listener is set for each ksession
                ksession.setListener( listener );
            }        
        }
        this.kSessions = kSessions;
    }
    
    public void addKSession(KSession kSession) {
        Map<String, KSession> newMap = new HashMap<String, KSession>();
        newMap.putAll( this.kSessions );          
        newMap.put( kSession.getQName(), kSession );        
        setKSessions( newMap );        
    }
    
    public void removeKSession(KSession kSession) {
        Map<String, KSession> newMap = new HashMap<String, KSession>();
        newMap.putAll( this.kSessions );          
        newMap.remove( kSession.getQName() );      
        setKSessions( newMap );
    }        

    public PropertyChangeListener getListener() {
        return listener;
    }

    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KSession ksession : kSessions.values()) {
            // make sure the listener is set for each ksession
            ksession.setListener( listener );
        }
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

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "files", this.files, files ) );
        }
        this.files = files;
    }

    public AssertBehaviorOption getEqualsBehavior() {
        return equalsBehavior;
    }

    public void setEqualsBehavior(AssertBehaviorOption equalsBehaviour) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "equalsBehavior", this.equalsBehavior, equalsBehavior ) );
        }
        this.equalsBehavior = equalsBehaviour;
    }

    public EventProcessingOption getEventProcessingMode() {
        return eventProcessingMode;
    }

    public void setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "eventProcessingMode", this.eventProcessingMode, eventProcessingMode ) );
        }
        this.eventProcessingMode = eventProcessingMode;
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
        return "KBase [namespace=" + namespace + ", name=" + name + ", files=" + files + ", annotations=" + annotations + ", equalsBehaviour=" + equalsBehavior + ", eventProcessingMode=" + eventProcessingMode + ", ksessions=" + kSessions + "]";
    }

}
