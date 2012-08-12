package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.util.StringUtils;

public class KProject {
    // qualifier to path
    private String              kProjectPath;
    private String              kBasesPath;

    private Map<String, KBase>  kBases;
    
    
    private  transient PropertyChangeListener listener;
    
    
    public KProject() {
        kBases = new HashMap<String, KBase>();
    }    
    
    public PropertyChangeListener getListener() {
        return listener;
    }

    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KBase kbase : kBases.values() ) {
            // make sure the listener is set for each kbase
            kbase.setListener( listener );
        }        
    }



    public String getKProjectPath() {
        return kProjectPath;
    }

    public void setKProjectPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new java.beans.PropertyChangeEvent( this, "kProjectPath", this.kProjectPath, kProjectPath ) );
        }
        this.kProjectPath = kprojectPath;
    }
    
    public String getKBasesPath() {
        return kBasesPath;
    }

    public void setKBasesPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBasesPath", this.kBasesPath, kBasesPath ) );     
        }
        this.kBasesPath = kprojectPath;
    }  
    
    public void addKBase(KBase kbase) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );        
        newMap.put( kbase.getQName(), kbase );
        setKBases( newMap );        
    }
    
    public void removeKBase(KBase kbase) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );
        newMap.remove( kbase.getQName() );
        setKBases( newMap );
    }    

    public Map<String, KBase> getKBases() {
        return kBases;
    }

    public void setKBases(Map<String, KBase> kBases) {        
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBases", this.kBases, kBases ) );
            
            for ( KBase kbase : kBases.values() ) {
                // make sure the listener is set for each kbase
                kbase.setListener( listener );
            }
        }
        
        this.kBases = kBases;
    }

    List<String> validate() {
        List<String> problems = new ArrayList<String>();
        if ( kProjectPath == null) {
            problems.add( "A path to the kproject.properties file must be specified" );
        }
//
//        // check valid kbase relative paths
//        for ( Entry<String, String> entry : kbasePaths.entrySet() ) {
//
//        }

        // validate valid kbases
        //for ( Entry<String, >)

        return problems;
    }

    @Override
    public String toString() {
        return "KProject [kprojectPath=" + kProjectPath + ", kbases=" + kBases + "]";
    }
        
}
