package org.drools.io.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.drools.ChangeSet;
import org.drools.event.io.ResourceChangeListener;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeMonitor;
import org.drools.io.ResourceChangeNotifier;

public class ResourceChangeNotifierImpl
    implements
    ResourceChangeNotifier,
    Runnable {
    private Map<Resource, Set<ResourceChangeListener>> subscriptions;
    private List<ResourceChangeMonitor>                monitors;
    private volatile boolean notify;
    
    private LinkedBlockingQueue<ChangeSet> queue;

    public ResourceChangeNotifierImpl() {
        this.subscriptions = new HashMap<Resource, Set<ResourceChangeListener>>();
        this.monitors = new CopyOnWriteArrayList<ResourceChangeMonitor>();
    }

    public void addResourceChangeMonitor(ResourceChangeMonitor monitor) {
        if ( !this.monitors.contains( monitor ) ) {
            this.monitors.add( monitor );
        }
    }

    public void removeResourceChangeMonitor(ResourceChangeMonitor monitor) {
        this.monitors.remove( monitor );
    }

    public Collection<ResourceChangeMonitor> getResourceChangeMonitor() {
        return Collections.unmodifiableCollection( this.monitors );
    }

    public void subscribeResourceChangeListener(ResourceChangeListener listener,
                                                Resource resource) {
        System.out.println( "notifier : " + resource );
        synchronized ( this.subscriptions ) {            
            Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
            if ( listeners == null ) {
                listeners = new HashSet<ResourceChangeListener>();
                this.subscriptions.put( resource,
                                        listeners );
                for ( ResourceChangeMonitor monitor : this.monitors ) {
                    monitor.subscribeNotifier( this,
                                               resource );
                }
            }
            listeners.add( listener );
        }
    }

    public void unsubscribeResourceChangeListener(ResourceChangeListener listener,
                                                  Resource resource) {
        synchronized ( this.subscriptions ) {
            Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
            if ( listeners == null ) {
                return;
            }
    
            if ( listeners.isEmpty() ) {
                this.subscriptions.remove( resource );
                for ( ResourceChangeMonitor monitor : this.monitors ) {
                    monitor.unsubscribeNotifier( this,
                                                 resource );
                }
            }
        }
    }

    public void subscribeChildResource(Resource directory,
                                       Resource child) {
        for ( ResourceChangeListener listener : this.subscriptions.get( directory ) ) {
            subscribeResourceChangeListener( listener,
                                             child );
        }
    }

    public void publishKnowledgeBaseChangeSet(ChangeSet changeSet) {
        try {
            this.queue.put( changeSet );
        } catch ( InterruptedException e ) {
            // @TODO print proper error message
            e.printStackTrace();
        }
        
    }
    
    private void processChangeSet(ChangeSet changeSet) {
        // this provides the complete published change set for this notifier.
        // however different listeners might be listening to different resources, so provide
        // listener change specified change sets.


        Map<ResourceChangeListener, ChangeSetImpl> localChangeSets = new HashMap<ResourceChangeListener, ChangeSetImpl>();

        for ( Resource resource : changeSet.getResourcesAdded() ) {
            Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
            for ( ResourceChangeListener listener : listeners ) {
                ChangeSetImpl localChangeSet = localChangeSets.get( listener );

                if ( localChangeSet == null ) {
                    // lazy initialise changeSet
                    localChangeSet = new ChangeSetImpl();
                    localChangeSets.put( listener,
                                         localChangeSet );
                }
                if ( localChangeSet.getResourcesAdded().isEmpty() ) {
                    localChangeSet.setResourcesAdded( new ArrayList<Resource>() );
                }
                localChangeSet.getResourcesAdded().add( resource );

            }
        }

        for ( Resource resource : changeSet.getResourcesRemoved() ) {
            Set<ResourceChangeListener> listeners = this.subscriptions.remove( resource );
            for ( ResourceChangeListener listener : listeners ) {
                ChangeSetImpl localChangeSet = localChangeSets.get( listener );
                if ( localChangeSet == null ) {
                    // lazy initialise changeSet
                    localChangeSet = new ChangeSetImpl();
                    localChangeSets.put( listener,
                                         localChangeSet );
                }
                if ( localChangeSet.getResourcesRemoved().isEmpty() ) {
                    localChangeSet.setResourcesRemoved( new ArrayList<Resource>() );
                }
                localChangeSet.getResourcesRemoved().add( resource );
            }
        }

        for ( Resource resource : changeSet.getResourcesModified() ) {
            Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
            for ( ResourceChangeListener listener : listeners ) {
                ChangeSetImpl localChangeSet = localChangeSets.get( listener );
                if ( localChangeSet == null ) {
                    // lazy initialise changeSet
                    localChangeSet = new ChangeSetImpl();
                    localChangeSets.put( listener,
                                         localChangeSet );
                }
                if ( localChangeSet.getResourcesModified().isEmpty() ) {
                    localChangeSet.setResourcesModified( new ArrayList<Resource>() );
                }
                localChangeSet.getResourcesModified().add( resource );
            }
        }

        for ( Entry<ResourceChangeListener, ChangeSetImpl> entry : localChangeSets.entrySet() ) {
            ResourceChangeListener listener = entry.getKey();
            ChangeSetImpl localChangeSet = entry.getValue();
            listener.resourcesChanged( localChangeSet );
        }

        //        ResourceModifiedEvent event = new ResourceModifiedEventImpl( resource,
        //                                                                     resource.getLastModified() );
        //        Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
        //        
        //        if ( listeners != null ) {
        //            for ( ResourceChangeListener listener : listeners ) {
        //                listener.resourceModified( event );
        //            }
        //        }        
    }
    
    public void start() {
        this.notify = true;
    }

    public void stop() {
        this.notify = false;
    }    

    public void run() {
        while ( this.notify ) {           
            try {
                processChangeSet( this.queue.take() );
            } catch ( InterruptedException e ) {
                // @TODO print proper error message
                e.printStackTrace();
            }
            Thread.yield();
        }
    }

}
