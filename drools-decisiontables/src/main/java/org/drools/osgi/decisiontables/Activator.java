package org.drools.osgi.decisiontables;

import java.util.Hashtable;


//import org.drools.KnowledgeBaseProvider;
//import org.drools.builder.KnowledgeBuilderProvider;
//import org.drools.builder.impl.KnowledgeBuilderProviderImpl;
//import org.drools.impl.KnowledgeBaseProviderImpl;
//import org.drools.io.ResourceProvider;
//import org.drools.io.impl.ResourceProviderImpl;
import org.drools.compiler.DecisionTableProvider;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration kdtableReg;

    public void start(BundleContext bc) throws Exception {
    	System.out.println( "registering decision tables drools services" );
        this.kdtableReg = bc.registerService( DecisionTableProvider.class.getName(),
                                               new DecisionTableProviderImpl(),
                                               new Hashtable() );
        System.out.println( "drools decision tables services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kdtableReg.unregister();
    }
}
