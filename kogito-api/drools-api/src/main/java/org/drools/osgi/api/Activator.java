package org.drools.osgi.api;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.drools.Service;
import org.drools.util.ServiceRegistry;
import org.drools.util.ServiceRegistryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration serviceRegistry;
    private ServiceTracker      registryTracker;

    public void start(BundleContext bc) throws Exception {
        System.out.println( "registering api services" );

        this.serviceRegistry = bc.registerService( ServiceRegistry.class.getName(),
                                                   new ServiceRegistryImpl(),
                                                   new Hashtable() );

        this.registryTracker = new ServiceTracker( bc,
                                                   Service.class.getName(),
                                                   new DroolsServiceTracker( bc,
                                                                             this ) );
        registryTracker.open();
        System.out.println( "api drools services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.serviceRegistry.unregister();
        this.registryTracker.close();
    }

    public static class DroolsServiceTracker
        implements
        ServiceTrackerCustomizer {
        private BundleContext bc;
        private Activator     activator;

        public DroolsServiceTracker(BundleContext bc,
                                    Activator activator) {
            this.bc = bc;
            this.activator = activator;
        }

        public Object addingService(ServiceReference ref) {
            Service service = (Service) this.bc.getService( ref );
            System.out.println( "registering : " + service + " : " + service.getClass().getInterfaces()[0] );

            Dictionary dic = new Hashtable();
            ServiceReference regServiceRef = this.activator.serviceRegistry.getReference();
            for ( String key : regServiceRef.getPropertyKeys() ) {
                dic.put( key,
                         regServiceRef.getProperty( key ) );
            }
            dic.put( service.getClass().getInterfaces()[0].getName(),
                     "true" );
            activator.serviceRegistry.setProperties( dic );

            ServiceRegistryImpl.getInstance().registerLocator( service.getClass().getInterfaces()[0],
                                                               new BundleContextInstantiator( this.bc,
                                                                                              ref ) );
            return service;
        }

        public void modifiedService(ServiceReference arg0,
                                    Object arg1) {

        }

        public void removedService(ServiceReference ref,
                                   Object arg1) {
            Service service = (Service) bc.getService( ref );
            ServiceRegistryImpl.getInstance().unregisterLocator( service.getClass().getInterfaces()[0] );
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
