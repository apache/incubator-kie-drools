package org.drools.osgi.api;

import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.drools.KnowledgeBaseProvider;
import org.drools.Service;
import org.drools.io.ResourceProvider;
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

        registryTracker = new ServiceTracker( bc,
                                              Service.class.getName(),
                                              new DroolsServiceTracker( bc,
                                                                        this ) );
        registryTracker.open();
        System.out.println( "api drools services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.registryTracker.close();
        this.serviceRegistry.unregister();
    }

    public static class DroolsServiceTracker
        implements
        ServiceTrackerCustomizer {
        private BundleContext bc;
        private Activator     activator;

        public DroolsServiceTracker(BundleContext bc,
                                    Activator activator) {
            this.bc = bc;
        }

        public Object addingService(ServiceReference ref) {
            Service service = (Service) bc.getService( ref );
            
            
            ServiceRegistryImpl.getInstance().registerLocator( service.getClass().getInterfaces()[0], new ReturnInstance( service ) );
            return service;
        }

        public void modifiedService(ServiceReference arg0,
                                    Object arg1) {
            
        }

        public void removedService(ServiceReference ref,
                                   Object arg1) {
            Service service = (Service) bc.getService( ref );
            ServiceRegistryImpl.getInstance().unregisterLocator( service.getClass().getInterfaces()[0]);
        }
    }

    static class ReturnInstance<V>
        implements
        Callable<V> {
        private Service service;

        public ReturnInstance(Service service) {
            this.service = service;
        }

        public V call() throws Exception {
            return (V) this.service;
        }
    }
}
