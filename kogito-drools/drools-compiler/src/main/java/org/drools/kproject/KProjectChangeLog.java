package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KProjectChangeLog
        implements
        PropertyChangeListener {

    private boolean               kProjectDirty;

    private Set<String>    addedKBases;
    private Set<String>    removedKBases;

    private Set<String> removedKSessions;
    private Set<String> addedKSessions;

    public KProjectChangeLog() {
        reset();
    }

    public boolean isKProjectDirty() {
        return kProjectDirty;
    }

    public void setKProjectDirty(boolean kProjectDirty) {
        this.kProjectDirty = kProjectDirty;
    }

    public boolean iskProjectDirty() {
        return kProjectDirty;
    }

    public void setkProjectDirty(boolean kProjectDirty) {
        this.kProjectDirty = kProjectDirty;
    }

    public Set<String> getAddedKBases() {
        return addedKBases;
    }

    public void setAddedKBases(Set<String> addedKBases) {
        this.addedKBases = addedKBases;
    }

    public Set<String> getRemovedKBases() {
        return removedKBases;
    }

    public void setRemovedKBases(Set<String> removedKBases) {
        this.removedKBases = removedKBases;
    }

    public Set<String> getRemovedKSessions() {
        return removedKSessions;
    }

    public void setRemovedKSessions(Set<String> removedKSessions) {
        this.removedKSessions = removedKSessions;
    }

    public Set<String> getAddedKSessions() {
        return addedKSessions;
    }

    public void setAddedKSessions(Set<String> addedKSessions) {
        this.addedKSessions = addedKSessions;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() instanceof KProjectImpl ) {
            KProject kProject = (KProject) evt.getSource();
            if ( "kBases".equals( evt.getPropertyName() ) ) {
                Map<String, KBaseImpl> oldKBases = (Map<String, KBaseImpl>) evt.getOldValue();
                Map<String, KBaseImpl> newKBases = (Map<String, KBaseImpl>) evt.getNewValue();
                if ( oldKBases.size() < newKBases.size() ) {
                    // kBase added
                    for ( Entry<String, KBaseImpl> entry : newKBases.entrySet() ) {
                        if ( !oldKBases.containsKey( entry.getKey() ) ) {
                            removedKBases.remove( entry.getKey() );               
                            addedKBases.add( entry.getKey() );
                            return;
                        }
                    }
                    throw new IllegalStateException( "Maps are different sizes, yet we can't find the new KBase" );
                } else if ( oldKBases.size() > newKBases.size() ) {
                    // kBase removed
                    for ( Entry<String, KBaseImpl> entry : oldKBases.entrySet() ) {
                        if ( !newKBases.containsKey( entry.getKey() ) ) {
                            addedKBases.remove( entry.getKey() );
                            removedKBases.add( entry.getKey() );
                            return;
                        }
                    }
                    throw new IllegalStateException( "Maps are different sizes, yet we can't find the removed KBase" );
                }
            } else {
                kProjectDirty = true;
            }

        } else if ( evt.getSource() instanceof KBaseImpl ) {
            KBaseImpl kBase = (KBaseImpl) evt.getSource();
            if ( "kSessions".equals( evt.getPropertyName() ) ) {
                Map<String, KSessionImpl> oldKSession = (Map<String, KSessionImpl>) evt.getOldValue();
                Map<String, KSessionImpl> newKSession = (Map<String, KSessionImpl>) evt.getNewValue();
                if ( oldKSession.size() < newKSession.size() ) {
                    // KSession added
                    for ( Entry<String, KSessionImpl> entry : newKSession.entrySet() ) {
                        if ( !oldKSession.containsKey( entry.getKey() ) ) {
                            removedKSessions.remove( entry.getKey() );
                            addedKSessions.add( entry.getKey() );
                            return;
                        }
                    }
                    throw new IllegalStateException( "Maps are different sizes, yet we can't find the new KBase" );
                } else if ( oldKSession.size() > newKSession.size() ) {
                    // KSession removed
                    for ( Entry<String, KSessionImpl> entry : oldKSession.entrySet() ) {
                        if ( !newKSession.containsKey( entry.getKey() ) ) {
                            addedKSessions.remove( entry.getKey() ); 
                            removedKSessions.add( entry.getKey() );
                            return;
                        }
                    }
                    throw new IllegalStateException( "Maps are different sizes, yet we can't find the removed KBase" );
                }
            } else if ( "namespace".equals( evt.getPropertyName() ) ) {
                String oldV = (String) evt.getOldValue();
                String newV = (String) evt.getNewValue();

                String oldQName = oldV + "." + kBase.getName();
                String newQName = newV + "." + kBase.getName();
                
                kBase.getKProject().moveKBase( oldQName, newQName );

                removedKBases.remove( newQName );
                removedKBases.add( oldQName );
                addedKBases.remove( oldQName );
                addedKBases.add( newQName );
            } else if ( "name".equals( evt.getPropertyName() ) ) {
                String oldV = (String) evt.getOldValue();
                String newV = (String) evt.getNewValue();

                String oldQName = kBase.getNamespace() + "." + oldV;
                String newQName = kBase.getNamespace() + "." + newV;
                
                kBase.getKProject().moveKBase( oldQName, newQName );

                removedKBases.remove( newQName );                
                removedKBases.add( oldQName );
                addedKBases.remove( oldQName );
                addedKBases.add( newQName );
            } else {
                addedKBases.add( kBase.getQName() );
            }

        } else if ( evt.getSource() instanceof KSessionImpl ) {
            KSessionImpl kSession = (KSessionImpl) evt.getSource();
            if ( "namespace".equals( evt.getPropertyName() ) ) {
                String oldV = (String) evt.getOldValue();
                String newV = (String) evt.getNewValue();

                String oldQName = oldV + "." + kSession.getName();
                String newQName = newV + "." + kSession.getName();
                
                kSession.getKBase().moveKSession( oldQName, newQName );

                removedKSessions.remove( newQName );
                removedKSessions.add( oldQName );
                addedKSessions.remove( oldQName );
                addedKSessions.add( newQName );
            } else if ( "name".equals( evt.getPropertyName() ) ) {
                String oldV = (String) evt.getOldValue();
                String newV = (String) evt.getNewValue();

                String oldQName = kSession.getNamespace() + "." + oldV;
                String newQName = kSession.getNamespace() + "." + newV;
                
                kSession.getKBase().moveKSession( oldQName, newQName );

                removedKSessions.remove( newQName );
                removedKSessions.add( oldQName );
                addedKSessions.remove( oldQName );
                addedKSessions.add( newQName );
            } else {
                addedKSessions.add( kSession.getQName() );
            }
        }
    }

    public void reset() {
        kProjectDirty = false;
        removedKBases = new HashSet<String>();
        addedKBases = new HashSet<String>();

        removedKSessions = new HashSet<String>();
        addedKSessions = new HashSet<String>();
    }

}
