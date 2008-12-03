package org.drools.io.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.event.io.ResourceChangeNotifier;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceChangeScannerConfiguration;

public class ResourceChangeScannerImpl
    implements
    ResourceChangeScanner,
    Runnable {

    private Map<Resource, Set<ResourceChangeNotifier>> resources;

    private volatile boolean                           scan;

    private volatile long                              interval;

    public ResourceChangeScannerImpl() {
        this.resources = new HashMap<Resource, Set<ResourceChangeNotifier>>();
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
            }
        }
    }

    public void scan() {
        System.out.println( "attempt scan : " + this.resources.size() );
        for ( Entry<Resource, Set<ResourceChangeNotifier>> entry : this.resources.entrySet() ) {
            Resource resource = entry.getKey();
            for ( ResourceChangeNotifier notifier : entry.getValue() ) {
                System.out.println( "scan " + resource + ": " + resource.getLastModified() + " : " + resource.getLastRead() );
                if ( resource.getLastRead() < resource.getLastModified() ) {
                    notifier.resourceModified( resource );
                }
            }
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
