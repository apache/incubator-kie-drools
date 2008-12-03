package org.drools.io.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.event.io.ResourceChangeListener;
import org.drools.event.io.ResourceChangeNotifier;
import org.drools.event.io.ResourceModifiedEvent;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeMonitor;

public class ResourceChangeNotifierImpl
    implements
    ResourceChangeNotifier 
{
    private Map<Resource, Set<ResourceChangeListener>>   subscriptions;
    private List<ResourceChangeMonitor>  monitors;
    
    
    public ResourceChangeNotifierImpl() {
        this.subscriptions = new HashMap<Resource, Set<ResourceChangeListener>>();
        this.monitors = new CopyOnWriteArrayList<ResourceChangeMonitor>();
    }
    
    public void addResourceChangeMonitor(ResourceChangeMonitor monitor) {
        if ( !this.monitors.contains( monitor )) {
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
                                                Resource resource
                                ) {
        System.out.println( "notifier : " + resource );
        Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
        if ( listeners == null ) {
            listeners = new HashSet<ResourceChangeListener>();
            this.subscriptions.put( resource, listeners );
            for ( ResourceChangeMonitor monitor : this.monitors ) {
                monitor.subscribeNotifier( this, resource );
            }             
        }
        listeners.add( listener );
    }
    
    public void unsubscribeResourceChangeListener(ResourceChangeListener listener,
                                                 Resource resource) {
        Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
        if ( listeners == null ) {
            return;
        }
        
        
        if ( listeners.isEmpty() ) {
            this.subscriptions.remove( resource );
            for ( ResourceChangeMonitor monitor : this.monitors ) {
                monitor.unsubscribeNotifier( this, resource );
            }            
        }
    }

    public void resourceAdded(Resource resource) {

    }

    public void resourceModified(Resource resource) {
        ResourceModifiedEvent event = new ResourceModifiedEventImpl( resource,
                                                                     resource.getLastModified() );
        Set<ResourceChangeListener> listeners = this.subscriptions.get( resource );
        
        if ( listeners != null ) {
            for ( ResourceChangeListener listener : listeners ) {
                listener.resourceModified( event );
            }
        }
        
    }

    public void resourceRemoved(Resource resource) {

    }


}
