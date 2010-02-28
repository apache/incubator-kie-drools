package org.drools.osgi.compiler;

import java.util.Dictionary;
import java.util.Hashtable;

import org.drools.Service;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.builder.impl.KnowledgeBuilderFactoryServiceImpl;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.BPMN2ProcessProvider;
import org.drools.compiler.DecisionTableProvider;
import org.drools.osgi.api.Activator.BundleContextInstantiator;
import org.drools.util.ServiceRegistryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration kbuilderReg;

    private ServiceTracker      dtableTracker;

    public void start(BundleContext bc) throws Exception {
        System.out.println( "registering compiler services" );
        this.kbuilderReg = bc.registerService( new String[]{KnowledgeBuilderFactoryService.class.getName(), Service.class.getName()},
                                               new KnowledgeBuilderFactoryServiceImpl(),
                                               new Hashtable() );

        this.dtableTracker = new ServiceTracker( bc,
                                                 bc.createFilter( "(|(" + 
                                                                  Constants.OBJECTCLASS + "=" + 
                                                                  DecisionTableProvider.class.getName() + ")(" + 
                                                                  Constants.OBJECTCLASS + "=" + 
                                                                  BPMN2ProcessProvider.class.getName() +") )"),
                                                 new DroolsServiceTracker( bc,
                                                                           this ) );
        this.dtableTracker.open();

        System.out.println( "compiler services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kbuilderReg.unregister();
        this.dtableTracker.close();
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
            System.out.println( "registering compiler : " + service + " : " + service.getClass().getInterfaces()[0] );

            Dictionary dic = new Hashtable();
            ServiceReference regServiceRef = this.activator.kbuilderReg.getReference();
            for ( String key : regServiceRef.getPropertyKeys() ) {
                dic.put( key,
                         regServiceRef.getProperty( key ) );
            }
            dic.put( service.getClass().getInterfaces()[0].getName(),
                     "true" );
            activator.kbuilderReg.setProperties( dic );

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
            System.out.println( "unregistering compiler : " + service + " : " + service.getClass().getInterfaces()[0] );            
        }
    }

}