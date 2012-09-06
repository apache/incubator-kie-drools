package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.util.StringUtils;

public class KProjectImpl implements KProject {
    // qualifier to path
    private String              kProjectPath;
    private String              kBasesPath;

    private Map<String, KBase>  kBases;
    
    
    private  transient PropertyChangeListener listener;
    
    
    public KProjectImpl() {
        kBases = Collections.emptyMap();
    }    
    
    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#setListener(java.beans.PropertyChangeListener)
     */
    public KProject setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KBase kbase : kBases.values() ) {
            // make sure the listener is set for each kbase
            kbase.setListener( listener );
        }
        return this;
    }



    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#getKProjectPath()
     */
    public String getKProjectPath() {
        return kProjectPath;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#setKProjectPath(java.lang.String)
     */
    public KProject setKProjectPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new java.beans.PropertyChangeEvent( this, "kProjectPath", this.kProjectPath, kProjectPath ) );
        }
        this.kProjectPath = kprojectPath;
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#getKBasesPath()
     */
    public String getKBasesPath() {
        return kBasesPath;
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#setKBasesPath(java.lang.String)
     */
    public KProject setKBasesPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBasesPath", this.kBasesPath, kBasesPath ) );     
        }
        this.kBasesPath = kprojectPath;
        return this;
    }  
    
    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#addKBase(org.drools.kproject.KBaseImpl)
     */
    public KBase newKBase(String namespace,
                         String name) {
        KBase kbase = new KBaseImpl(this, namespace, name);
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );        
        newMap.put( kbase.getQName(), kbase );
        setKBases( newMap );   
        
        return kbase;
    }
    
    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#removeKBase(org.drools.kproject.KBase)
     */
    public void removeKBase(String qName) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }    
    
    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#removeKBase(org.drools.kproject.KBase)
     */
    public void moveKBase(String oldQName, String newQName) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );
        KBase kBase = newMap.remove( oldQName );
        newMap.put( newQName, kBase );
        setKBases( newMap );
    }        

    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#getKBases()
     */
    public Map<String, KBase> getKBases() {
        return Collections.unmodifiableMap( kBases );
    }

    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KBase> kBases) {        
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

    /* (non-Javadoc)
     * @see org.drools.kproject.KProject#toString()
     */
    @Override
    public String toString() {
        return "KProject [kprojectPath=" + kProjectPath + ", kbases=" + kBases + "]";
    }
        
}
