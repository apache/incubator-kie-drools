/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.osgi.api;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.drools.Service;
import org.drools.marshalling.MarshallerProvider;
import org.drools.util.ServiceRegistry;
import org.drools.util.ServiceRegistryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator
    implements
    BundleActivator {

    protected static final transient Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceRegistration serviceRegistry;
    private ServiceTracker      registryTracker;
    private ServiceTracker marshallerProviderTracker;

    public void start(BundleContext bc) throws Exception {
        logger.info( "registering api services" );

        this.serviceRegistry = bc.registerService( ServiceRegistry.class.getName(),
                                                   ServiceRegistryImpl.getInstance(),
                                                   new Hashtable() );

        this.registryTracker = new ServiceTracker( bc,
                                                   Service.class.getName(),
                                                   new DroolsServiceTracker( bc,
                                                                             this ) );
        
        registryTracker.open();
        
        this.marshallerProviderTracker = new ServiceTracker( bc,
                MarshallerProvider.class.getName(),
                new DroolsServiceTracker( bc,
                                          this) );
        
        this.marshallerProviderTracker.open();

        logger.info( "api drools services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.serviceRegistry.unregister();
        this.registryTracker.close();
        this.marshallerProviderTracker.close();
    }

    public static class DroolsServiceTracker
        implements
        ServiceTrackerCustomizer {

        protected static final transient Logger logger = LoggerFactory.getLogger(DroolsServiceTracker.class);

        private BundleContext bc;
        private Activator     activator;

        public DroolsServiceTracker(BundleContext bc,
                                    Activator activator) {
            this.bc = bc;
            this.activator = activator;
        }

        public Object addingService(ServiceReference ref) {
            Service service = (Service) this.bc.getService( ref );
            logger.info( "registering api : " + service + " : " + service.getClass().getInterfaces()[0] );

            Dictionary dic = new Hashtable();
            ServiceReference regServiceRef = this.activator.serviceRegistry.getReference();
            for ( String key : regServiceRef.getPropertyKeys() ) {
                dic.put( key,
                         regServiceRef.getProperty( key ) );
            }
            dic.put( service.getClass().getInterfaces()[0].getName(),
                     "true" );
            activator.serviceRegistry.setProperties( dic );

            ((ServiceRegistryImpl) bc.getService( regServiceRef )).registerLocator( service.getClass().getInterfaces()[0],
                                                                                    new BundleContextInstantiator( this.bc,
                                                                                                                   ref ) );

            return service;
        }

        public void modifiedService(ServiceReference arg0,
                                    Object arg1) {

        }

        public void removedService(ServiceReference ref,
                                   Object arg1) {
            Service service = (Service) this.bc.getService( ref );
            logger.info( "unregistering : " + service + " : " + service.getClass().getInterfaces()[0] );

            Dictionary dic = new Hashtable();
            ServiceReference regServiceRef = this.activator.serviceRegistry.getReference();

            ((ServiceRegistryImpl) bc.getService( regServiceRef )).unregisterLocator( service.getClass().getInterfaces()[0] );

            for ( String key : regServiceRef.getPropertyKeys() ) {
                if ( !key.equals( service.getClass().getInterfaces()[0].getName() ) ) {
                    dic.put( key,
                             regServiceRef.getProperty( key ) );
                }
            }
            activator.serviceRegistry.setProperties( dic );
        }
    }

    public static class BundleContextInstantiator<V>
        implements
        Callable<V> {
        private BundleContext    bc;
        private ServiceReference ref;

        public BundleContextInstantiator(BundleContext bc,
                                         ServiceReference ref) {
            this.bc = bc;
            this.ref = ref;
        }

        public V call() throws Exception {
            return (V) this.bc.getService( this.ref );
        }
    }
}
