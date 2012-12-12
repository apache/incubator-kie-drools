/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.io.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

import org.drools.io.internal.InternalResource;
import org.kie.ChangeSet;
import org.kie.SystemEventListener;
import org.kie.concurrent.ExecutorProviderFactory;
import org.kie.io.Resource;
import org.kie.io.ResourceChangeNotifier;
import org.kie.io.ResourceChangeScanner;
import org.kie.io.ResourceChangeScannerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceChangeScannerImpl
    implements
    ResourceChangeScanner {
    
    private final Logger logger = LoggerFactory.getLogger( ResourceChangeScanner.class );

    private final Map<Resource, Set<ResourceChangeNotifier>> resources;
    private final Set<Resource>                              directories;
    private int                                              interval;

    private Future<Boolean>                                  scannerSchedulerExecutor;
    private ProcessChangeSet                                 scannerScheduler;

    public ResourceChangeScannerImpl() {
        this.resources = new HashMap<Resource, Set<ResourceChangeNotifier>>();
        this.directories = new HashSet<Resource>();
        this.setInterval( 60 );
        logger.info( "ResourceChangeScanner created with default interval=60" );
    }

    public void configure(ResourceChangeScannerConfiguration configuration) {
        this.setInterval( ((ResourceChangeScannerConfigurationImpl) configuration).getInterval() );
        logger.info( "ResourceChangeScanner reconfigured with interval=" + getInterval() );

        // restart it if it's already running.
        if ( this.scannerScheduler != null && this.scannerScheduler.isRunning() ) {
            stop();
            start();
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
        synchronized ( this.resources ) {
            if ( ((InternalResource) resource).isDirectory() ) {
                this.directories.add( resource );
            }
            Set<ResourceChangeNotifier> notifiers = this.resources.get( resource );
            if ( notifiers == null ) {
                notifiers = new HashSet<ResourceChangeNotifier>();
                this.resources.put( resource,
                                    notifiers );
            }
            logger.debug( "ResourceChangeScanner subcribing notifier=" + notifier + " to resource=" + resource );
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
            logger.debug( "ResourceChangeScanner unsubcribing notifier=" + notifier + " to resource=" + resource );
            notifiers.remove( notifier );
            if ( notifiers.isEmpty() ) {
                logger.debug( "ResourceChangeScanner resource=" + resource + " now has no subscribers" );
                this.resources.remove( resource );
                this.directories.remove( resource ); // don't bother with
                // isDirectory check, as
                // doing a remove is
                // harmless if it doesn't
                // exist
            }
        }
    }

    public Map<Resource, Set<ResourceChangeNotifier>> getResources() {
        return resources;
    }

    public void scan() {
        logger.debug( "ResourceChangeScanner attempt to scan " + this.resources.size() + " resources" );

        synchronized ( this.resources ) {
            Map<ResourceChangeNotifier, ChangeSet> notifications = new HashMap<ResourceChangeNotifier, ChangeSet>();

            List<Resource> removed = new ArrayList<Resource>();

            // detect modified and added
            for ( Resource resource : this.directories ) {
                logger.debug( "ResourceChangeScanner scanning directory=" + resource );
                for ( Resource child : ((InternalResource) resource).listResources() ) {
                    if ( ((InternalResource) child).isDirectory() ) {
                        continue; // ignore sub directories
                    }
                    if ( !this.resources.containsKey( child ) ) {

                        logger.debug( "ResourceChangeScanner new resource=" + child );
                        // child is new
                        ((InternalResource) child).setResourceType( ((InternalResource) resource).getResourceType() );
                        Set<ResourceChangeNotifier> notifiers = this.resources.get( resource ); // get notifiers for this
                        // directory
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
                            notifier.subscribeChildResource( resource,
                                                             child );
                        }
                    }
                }
            }

            for ( Entry<Resource, Set<ResourceChangeNotifier>> entry : this.resources.entrySet() ) {
                Resource resource = entry.getKey();
                Set<ResourceChangeNotifier> notifiers = entry.getValue();

                if ( !((InternalResource) resource).isDirectory() ) {
                    // detect if Resource has been removed
                    long lastModified = ((InternalResource) resource).getLastModified();
                    long lastRead = ((InternalResource) resource).getLastRead();
                    
                    if ( lastModified == 0 ) {
                        logger.debug( "ResourceChangeScanner removed resource=" + resource );
                        removed.add( resource );
                        // resource is no longer present
                        // iterate notifiers for this resource and add to each
                        // removed
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
                    } else if ( lastRead < lastModified && lastRead >= 0 ) {
                        logger.debug( "ResourceChangeScanner modified resource=" + resource + " : " + lastRead + " : " + lastModified );
                        // it's modified
                        // iterate notifiers for this resource and add to each
                        // modified
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

            // now iterate and removed the removed resources, we do this so as
            // not to mutate the foreach loop while iterating
            for ( Resource resource : removed ) {
                this.resources.remove( resource );
            }

            for ( Entry<ResourceChangeNotifier, ChangeSet> entry : notifications.entrySet() ) {
                ResourceChangeNotifier notifier = entry.getKey();
                ChangeSet changeSet = entry.getValue();
                notifier.publishChangeSet( changeSet );
            }
        }
    }

    public void setInterval(int interval) {
        if ( interval <= 0 ) {
            throw new IllegalArgumentException( "Invalid interval time: " + interval + ". It should be a positive number bigger than 0" );
        }

        this.interval = interval;
        logger.info( "ResourceChangeScanner reconfigured with interval=" + getInterval() );

        if ( this.scannerScheduler != null && this.scannerScheduler.isRunning() ) {
            stop();
            start();
        }
    }

    public int getInterval() {
        return this.interval;
    }

    public void start() {
        this.scannerScheduler = new ProcessChangeSet( this.resources,
                                                      this,
                                                      this.interval );
        this.scannerSchedulerExecutor =
                ExecutorProviderFactory.getExecutorProvider().<Boolean>getCompletionService()
                        .submit(this.scannerScheduler, true);
    }

    public void stop() {
        if ( this.scannerScheduler != null && this.scannerScheduler.isRunning() ) {
            this.scannerScheduler.stop();
            this.scannerSchedulerExecutor.cancel(true);
            this.scannerScheduler = null;
        }
    }

    public void reset() {
        this.resources.clear();
        this.directories.clear();
    }

    public static class ProcessChangeSet
        implements
        Runnable {
        
        private final Logger logger = LoggerFactory.getLogger( ProcessChangeSet.class );
        
        private volatile boolean                                 scan;
        private final ResourceChangeScannerImpl                  scanner;
        private final long                                       interval;
        private final Map<Resource, Set<ResourceChangeNotifier>> resources;

        ProcessChangeSet(Map<Resource, Set<ResourceChangeNotifier>> resources,
                         ResourceChangeScannerImpl scanner,
                         int interval) {
            this.resources = resources;
            this.scanner = scanner;
            this.interval = interval;
            this.scan = true;
        }

        public int getInterval() {
            return (int) this.interval;
        }

        public void stop() {
            this.scan = false;
        }

        public boolean isRunning() {
            return this.scan;
        }

        public void run() {
            synchronized ( this ) {
                if ( this.scan ) {
                    this.logger.info( "ResourceChangeNotification scanner has started" );
                }
                while ( this.scan ) {
                    try {
                        // logger.trace( "BEFORE : sync this.resources" );
                        synchronized ( this.resources ) {
                            // logger.trace( "DURING : sync this.resources" );
                            // lock the resources, as we don't want this modified
                            // while processing
                            this.scanner.scan();
                        }
                        // logger.trace( "AFTER : SCAN" );
                    } catch (RuntimeException e) {
                        this.logger.error( e.getMessage(), e );
                    } catch (Error e) {
                        this.logger.error( e.getMessage(), e );
                    }
                    try {
                        this.logger.debug( "ResourceChangeScanner thread is waiting for " + this.interval + " seconds." );
                        wait( this.interval * 1000 );
                    } catch ( InterruptedException e ) {
                        if ( this.scan ) {
                            this.logger.error( e.getMessage(), new RuntimeException( "ResourceChangeNotification ChangeSet scanning thread was interrupted, but shutdown was not requested",
                                                                           e ) );
                        }
                    }

                }
                this.logger.info( "ResourceChangeNotification scanner has stopped" );
            }
        }
    }

    public void setSystemEventListener(SystemEventListener listener) {
        // TODO Auto-generated method stub
        
    }
}
