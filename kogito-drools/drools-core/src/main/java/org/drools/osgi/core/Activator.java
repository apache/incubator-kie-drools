package org.drools.osgi.core;

import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.drools.KnowledgeBaseFactoryService;
import org.drools.Service;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.io.ResourceFactoryService;
import org.drools.io.impl.ResourceFactoryServiceImpl;
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
    private ServiceRegistration resourceReg;
    private ServiceRegistration kbaseReg;

    public void start(BundleContext bc) throws Exception {
        System.out.println( "registering core  services" );
        this.resourceReg = bc.registerService( new String[]{ResourceFactoryService.class.getName(), Service.class.getName()},
                                               new ResourceFactoryServiceImpl(),
                                               new Hashtable() );
        this.kbaseReg = bc.registerService( new String[]{KnowledgeBaseFactoryService.class.getName(), Service.class.getName()},
                                            new KnowledgeBaseFactoryServiceImpl(),
                                            new Hashtable() );

        System.out.println( "core services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kbaseReg.unregister();
        this.resourceReg.unregister();
    }

}
