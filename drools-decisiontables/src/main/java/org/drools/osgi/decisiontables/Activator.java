package org.drools.osgi.decisiontables;

import java.util.Hashtable;


//import org.drools.KnowledgeBaseFactoryService;
//import org.drools.builder.KnowledgeBuilderFactoryService;
//import org.drools.builder.impl.KnowledgeBuilderFactoryServiceImpl;
//import org.drools.impl.KnowledgeBaseFactoryServiceImpl;
//import org.drools.io.ResourceFactoryService;
//import org.drools.io.impl.ResourceFactoryServiceImpl;
import org.drools.Service;
import org.drools.compiler.DecisionTableProvider;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator
    implements
    BundleActivator {

    protected static transient Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceRegistration kdtableReg;

    public void start(BundleContext bc) throws Exception {
        logger.info( "registering decision tables drools services" );
        this.kdtableReg = bc.registerService(  new String[]{ DecisionTableProvider.class.getName(), Service.class.getName()},
                                               new DecisionTableProviderImpl(),
                                               new Hashtable() );
        logger.info( "drools decision tables services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.kdtableReg.unregister();
    }
}
