package org.jbpm.osgi.flow.compiler;

import java.util.Hashtable;

import org.drools.compiler.ProcessBuilderFactoryService;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.kie.Service;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements
    BundleActivator {

    private ServiceRegistration processBuilderReg;

    public void start(BundleContext bc) throws Exception {
        this.processBuilderReg = bc.registerService( new String[]{ ProcessBuilderFactoryService.class.getName(), Service.class.getName()},
                                                                   new ProcessBuilderFactoryServiceImpl(),
                                                                   new Hashtable() );
    }

    public void stop(BundleContext bc) throws Exception {
        this.processBuilderReg.unregister();
    }

}