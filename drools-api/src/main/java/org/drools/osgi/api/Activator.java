package org.drools.osgi.api;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration serviceRegistry;
//    private ServiceRegistration kbaseReg;

    public void start(BundleContext bc) throws Exception {
//        System.out.println( "registering core drools services" );
//        this.resourceReg = bc.registerService( ResourceProvider.class.getName(),
//                                               new ResourceProviderImpl(),
//                                               new Hashtable() );
//        this.kbaseReg = bc.registerService( KnowledgeBaseProvider.class.getName(),
//                                            new KnowledgeBaseProviderImpl(),
//                                            new Hashtable() );
//        System.out.println( "drools core services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
//        this.resourceReg.unregister();
    }
}
