package org.drools.osgi.compiler;

import java.util.Hashtable;

import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.builder.impl.KnowledgeBuilderProviderImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration kbuilderReg;

    public static BundleContext bc;

    public void start(BundleContext bc) throws Exception {
        this.bc = bc;
        System.out.println( "registering compiler services" );
        this.kbuilderReg = bc.registerService( KnowledgeBuilderProvider.class.getName(),
                                               new KnowledgeBuilderProviderImpl(),
                                               new Hashtable() );
        System.out.println( "compiler services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kbuilderReg.unregister();
    }
}