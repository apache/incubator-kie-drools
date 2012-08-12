package org.drools.kproject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class KProjectChangeLog
        implements
        PropertyChangeListener {

    private boolean               kProjectDirty;

    private Map<String, String>   modifiedKBases;
    private Map<String, KBase>    removedKBases;
    private Map<String, KBase>    addedKBases;

    private Map<String, String>   modifiedKSessions;
    private Map<String, KSession> removedKSessions;
    private Map<String, KSession> addedKSessions;

    public KProjectChangeLog() {
        reset();
    }

    public boolean isKProjectDirty() {
        return kProjectDirty;
    }

    public void setKProjectDirty(boolean kProjectDirty) {
        this.kProjectDirty = kProjectDirty;
    }

    public Map<String, String> getModifiedKBases() {
        return modifiedKBases;
    }

    public void setModifiedKBases(Map<String, String> modifiedKBases) {
        this.modifiedKBases = modifiedKBases;
    }

    public Map<String, KBase> getRemovedKBases() {
        return removedKBases;
    }

    public void setRemovedKBases(Map<String, KBase> removedKBases) {
        this.removedKBases = removedKBases;
    }

    public Map<String, KBase> getAddedKBases() {
        return addedKBases;
    }

    public void setAddedKBases(Map<String, KBase> addedKBases) {
        this.addedKBases = addedKBases;
    }

    public Map<String, String> getModifiedKSessions() {
        return modifiedKSessions;
    }

    public void setModifiedKSessions(Map<String, String> modifiedKSessions) {
        this.modifiedKSessions = modifiedKSessions;
    }

    public Map<String, KSession> getRemovedKSessions() {
        return removedKSessions;
    }

    public void setRemovedKSessions(Map<String, KSession> removedKSessions) {
        this.removedKSessions = removedKSessions;
    }

    public Map<String, KSession> getAddedKSessions() {
        return addedKSessions;
    }

    public void setAddedKSessions(Map<String, KSession> addedKSessions) {
        this.addedKSessions = addedKSessions;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() instanceof KProject ) {
            KProject kProject = (KProject) evt.getSource();
            if ( "kBases".equals( evt.getPropertyName() ) ) {
                Map<String, KBase> oldKBases = (Map<String, KBase>) evt.getOldValue();
                Map<String, KBase> newKBases = (Map<String, KBase>) evt.getNewValue();
                if ( oldKBases.size() < newKBases.size() ) {
                    // kBase added
                    for ( Entry<String, KBase> entry : newKBases.entrySet() ) {
                        if ( !oldKBases.containsKey( entry.getKey() ) ) {
                            if ( modifiedKBases.containsKey( entry.getKey() ) ) {
                                if ( removedKBases.remove( entry.getKey() ) != null ) {
                                    // this already exists, as we just to remove it, so just delete the remove
                                    return;
                                }

                                // actually part of a modification, so ignore                                
                                removedKBases.remove( modifiedKBases.get( entry.getKey() ) ); // but delete remove too
                                return;
                            }

                            if ( removedKBases.remove( entry.getKey() ) != null ) {
                                // this already exists, as we just to remove it, so just delete the remove
                                return;
                            }

                            addedKBases.put( entry.getKey(), entry.getValue() );
                            return;
                        }
                    }
                    throw new IllegalStateException( "Maps are different sizes, yet we can't find the new KBase" );
                } else if ( oldKBases.size() > newKBases.size() ) {
                    // kBase removed
                    for ( Entry<String, KBase> entry : oldKBases.entrySet() ) {
                        if ( !newKBases.containsKey( entry.getKey() ) ) {
                            if ( addedKBases.remove( entry.getKey() ) != null ) {
                                // doesn't exist, as we just added it, so just delete the added
                                return;
                            }

                            removedKBases.put( entry.getKey(), entry.getValue() );
                            return;
                        }
                    }
                    throw new IllegalStateException( "Maps are different sizes, yet we can't find the removed KBase" );
                }
            } else {
                kProjectDirty = true;
            }

        } else if ( evt.getSource() instanceof KBase ) {
            KBase kBase = (KBase) evt.getSource();
            if ( "kSessions".equals( evt.getPropertyName() ) ) {
                Map<String, KSession> oldKBases = (Map<String, KSession>) evt.getOldValue();
                Map<String, KSession> newKBases = (Map<String, KSession>) evt.getNewValue();
                if ( oldKBases.size() < newKBases.size() ) {
                    // KSession added
                    for ( Entry<String, KSession> entry : newKBases.entrySet() ) {
                        if ( !oldKBases.containsKey( entry.getKey() ) ) {
                            if ( modifiedKSessions.containsKey( entry.getKey() ) ) {
                                if ( removedKSessions.remove( entry.getKey() ) != null ) {
                                    // this already exists, as we just to remove it, so just delete the remove
                                    return;
                                }

                                // actually part of a modification, so ignore                                
                                removedKSessions.remove( modifiedKSessions.get( entry.getKey() ) ); // but delete remove too
                                return;
                            }

                            if ( removedKSessions.remove( entry.getKey() ) != null ) {
                                // this already exists, as we just to remove it, so just delete the remove
                                return;
                            }

                            addedKSessions.put( entry.getKey(), entry.getValue() );
                            return;
                        }
                    }
                    throw new IllegalStateException( "Maps are different sizes, yet we can't find the new KBase" );
                } else if ( oldKBases.size() > newKBases.size() ) {
                    // KSession removed
                    for ( Entry<String, KSession> entry : oldKBases.entrySet() ) {
                        if ( !newKBases.containsKey( entry.getKey() ) ) {
                            if ( addedKSessions.remove( entry.getKey() ) != null ) {
                                // doesn't exist, as we just added it, so just delete the added
                                return;
                            }

                            removedKSessions.put( entry.getKey(), entry.getValue() );
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

                if ( !removedKBases.containsKey( oldQName ) ) {
                    // not currently added
                    return;
                }

                String origQName = modifiedKBases.remove( oldQName );
                if ( origQName != null ) {
                    // was already previously modified
                    modifiedKBases.put( newQName, origQName );
                } else {
                    modifiedKBases.put( newQName, oldV + "." + kBase.getName() );
                }
            } else if ( "name".equals( evt.getPropertyName() ) ) {
                String oldV = (String) evt.getOldValue();
                String newV = (String) evt.getNewValue();

                String oldQName = kBase.getNamespace() + "." + oldV;
                String newQName = kBase.getNamespace() + "." + newV;

                if ( !removedKBases.containsKey( oldQName ) ) {
                    // not currently added
                    return;
                }

                String origQName = modifiedKBases.remove( oldQName );
                if ( origQName != null ) {
                    // was already previously modified
                    modifiedKBases.put( newQName, origQName );
                } else {
                    modifiedKBases.put( newQName, kBase.getNamespace() + "." + oldV );
                }
            } else {
                String oldQName = modifiedKBases.remove( kBase.getQName() );
                if ( oldQName != null ) {
                    modifiedKBases.put( kBase.getQName(), oldQName );
                } else {
                    modifiedKBases.put( kBase.getQName(), kBase.getQName() );
                }
            }

        } else if ( evt.getSource() instanceof KSession ) {
            KSession kSession = (KSession) evt.getSource();
            if ( "namespace".equals( evt.getPropertyName() ) ) {
                String oldV = (String) evt.getOldValue();
                String newV = (String) evt.getNewValue();

                String oldQName = oldV + "." + kSession.getName();
                String newQName = newV + "." + kSession.getName();

                if ( !removedKSessions.containsKey( oldQName ) ) {
                    // not currently added
                    return;
                }

                String origQName = modifiedKBases.remove( oldQName );
                if ( origQName != null ) {
                    // was already previously modified
                    modifiedKSessions.put( newQName, origQName );
                } else {
                    modifiedKSessions.put( newQName, oldV + "." + kSession.getName() );
                }
            } else if ( "name".equals( evt.getPropertyName() ) ) {
                String oldV = (String) evt.getOldValue();
                String newV = (String) evt.getNewValue();

                String oldQName = kSession.getNamespace() + "." + oldV;
                String newQName = kSession.getNamespace() + "." + newV;

                if ( !removedKSessions.containsKey( oldQName ) ) {
                    // not currently added
                    return;
                }

                String origQName = modifiedKSessions.remove( oldQName );
                if ( origQName != null ) {
                    // was already previously modified
                    modifiedKSessions.put( newQName, origQName );
                } else {
                    modifiedKSessions.put( newQName, kSession.getNamespace() + "." + oldV );
                }
            } else {
                String oldQName = modifiedKSessions.remove( kSession.getQName() );
                if ( oldQName != null ) {
                    modifiedKSessions.put( kSession.getQName(), oldQName );
                } else {
                    modifiedKSessions.put( kSession.getQName(), kSession.getQName() );
                }
            }
        }
    }

    public void reset() {
        kProjectDirty = false;
        modifiedKBases = new HashMap<String, String>();
        removedKBases = new HashMap<String, KBase>();
        addedKBases = new HashMap<String, KBase>();

        modifiedKSessions = new HashMap<String, String>();
        removedKSessions = new HashMap<String, KSession>();
        addedKSessions = new HashMap<String, KSession>();
    }

}
