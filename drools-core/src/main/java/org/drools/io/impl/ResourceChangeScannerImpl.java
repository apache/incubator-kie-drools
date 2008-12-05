package org.drools.io.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.ChangeSet;
import org.drools.io.InternalResource;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeNotifier;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.util.StringUtils;

public class ResourceChangeScannerImpl
    implements
    ResourceChangeScanner,
    Runnable {

    private Map<Resource, Set<ResourceChangeNotifier>> resources;
    private Set<Resource>               directories;

    private volatile boolean                           scan;

    private volatile long                              interval;

    public ResourceChangeScannerImpl() {
        this.resources = new HashMap<Resource, Set<ResourceChangeNotifier>>();
        this.directories = new HashSet<Resource>();
        setInterval( 60 );
        this.scan = true;
    }

    public void configure(ResourceChangeScannerConfiguration configuration) {
        this.interval = ((ResourceChangeScannerConfigurationImpl) configuration).getInterval();
        System.out.println( this.interval );
        synchronized ( this.resources ) {
            this.resources.notify(); // notify wait, so that it will wait again
        }
    }

    public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration() {
        return new ResourceChangeScannerConfigurationImpl();
    }

    public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration(Properties properties) {
        return new ResourceChangeScannerConfigurationImpl( properties );
    }

    public void subscribeNotifier(ResourceChangeNotifier notifier,
                                  Resource resource) {
        System.out.println( "scanner : " + resource );
        synchronized ( this.resources ) {
            if ( ((InternalResource)resource).isDirectory() ) {
                this.directories.add( resource );
            }
            Set<ResourceChangeNotifier> notifiers = this.resources.get( resource );
            if ( notifiers == null ) {
                notifiers = new HashSet<ResourceChangeNotifier>();
                this.resources.put( resource,
                                    notifiers );
            }
            notifiers.add( notifier );
        }
    }

    public void unsubscribeNotifier(ResourceChangeNotifier notifier,
                                    Resource resource) {
        synchronized ( this.resources ) {
            Set<ResourceChangeNotifier> notifiers = this.resources.get( resource );
            if ( notifiers == null ) {
                return;
            }
            notifiers.remove( notifier );
            if ( notifiers.isEmpty() ) {
                this.resources.remove( resource );
                this.directories.remove( resource ); // don't bother with isDirectory check, as doing a remove is harmless if it doesn't exist
            }
        }
    }

    public void scan() {
        System.out.println( "attempt scan : " + this.resources.size() );

        if ( this.resources.size() > 0 ) {
            System.out.println( "x" );
        }
        Map<ResourceChangeNotifier, ChangeSet> notifications = new HashMap<ResourceChangeNotifier, ChangeSet>();
        
    
        List<Resource> removed = new ArrayList<Resource>();        

        // detect modified and added
        for ( Resource resource : this.directories ) {
            for ( Resource child : ((InternalResource)resource).listResources() ) {
                if ( !this.resources.containsKey( child ) ) {
                    System.out.println( "found new file : " + child );
                    // child is new
                    ((InternalResource)child).setKnowledgeType( ((InternalResource)resource).getKnowledgeType() );
                    Set<ResourceChangeNotifier> notifiers = this.resources.get( resource ); // get notifiers for this directory
                    for ( ResourceChangeNotifier notifier : notifiers ) {
                        ChangeSetImpl changeSet = (ChangeSetImpl) notifications.get( notifier );
                        if ( changeSet == null ) {
                            // lazy initialise changeSet
                            changeSet = new ChangeSetImpl();
                            notifications.put( notifier,
                                               changeSet );
                        }
                        if ( changeSet.getResourcesAdded().isEmpty() ) {
                            changeSet.setResourcesAdded( new ArrayList<Resource>() );
                        }
                        changeSet.getResourcesAdded().add( child );
                        notifier.subscribeChildResource( resource, child );
                    }                
                }
            }
        }

        for ( Entry<Resource, Set<ResourceChangeNotifier>> entry : this.resources.entrySet() ) {
            Resource resource = entry.getKey();
            Set<ResourceChangeNotifier> notifiers = entry.getValue();            
            
            if ( !((InternalResource)resource).isDirectory() ) {
                // detect if Resource has been modified
                System.out.println( "scan " + resource + ": " + ((InternalResource)resource).getLastModified() + " : " + ((InternalResource)resource).getLastRead() );
                long lastModified = ((InternalResource)resource).getLastModified();
                if ( lastModified == 0 ) {
                    removed.add( resource );
                    // resource is no longer present
                    // iterate notifiers for this resource and add to each removed
                    for ( ResourceChangeNotifier notifier : notifiers ) {
                        ChangeSetImpl changeSet = (ChangeSetImpl) notifications.get( notifier );
                        if ( changeSet == null ) {
                            // lazy initialise changeSet
                            changeSet = new ChangeSetImpl();
                            notifications.put( notifier,
                                               changeSet );
                        }
                        if ( changeSet.getResourcesRemoved().isEmpty() ) {
                            changeSet.setResourcesRemoved( new ArrayList<Resource>() );
                        }
                        changeSet.getResourcesRemoved().add( resource );
                    }
                } else if ( ((InternalResource)resource).getLastRead() <  lastModified ) {
                    // it's modified
                    // iterate notifiers for this resource and add to each modified
                    for ( ResourceChangeNotifier notifier : notifiers ) {
                        ChangeSetImpl changeSet = (ChangeSetImpl) notifications.get( notifier );
                        if ( changeSet == null ) {
                            // lazy initialise changeSet
                            changeSet = new ChangeSetImpl();
                            notifications.put( notifier,
                                               changeSet );
                        }
                        if ( changeSet.getResourcesModified().isEmpty() ) {
                            changeSet.setResourcesModified( new ArrayList<Resource>() );
                        }
                        changeSet.getResourcesModified().add( resource );
                    }
                }
            }                
        }
        
        // now iterate and removed the removed resources, we do this so as not to mutate the foreach loop while iterating
        for ( Resource resource : removed ) {
            this.resources.remove( resource );
        }

        for ( Entry<ResourceChangeNotifier, ChangeSet> entry : notifications.entrySet() ) {
            ResourceChangeNotifier notifier = entry.getKey();
            ChangeSet changeSet = entry.getValue();
            notifier.publishKnowledgeBaseChangeSet( changeSet );
        }
    }
    
    public void start() {
        this.scan = true;
    }

    public void stop() {
        this.scan = false;
    }

    public void run() {
        synchronized ( this.resources ) {
            while ( scan ) {
                scan();
                try {
                    this.resources.wait( this.interval );
                } catch ( InterruptedException e ) {
                    System.out.println( "wait interrupted, new interval is " + this.interval + "s" );
                    // swallow, this will happen when we are waiting and the interval changes
                }
            }
        }
    }

    public void setInterval(int interval) {
        this.interval = interval * 1000;
    }

    public int getInterval() {
        return (int) this.interval / 1000;
    }
}
