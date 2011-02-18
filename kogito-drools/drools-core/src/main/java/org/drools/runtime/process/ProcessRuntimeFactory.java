package org.drools.runtime.process;

import org.drools.common.AbstractWorkingMemory;
import org.drools.util.ServiceRegistryImpl;


public class ProcessRuntimeFactory {

    private static ProcessRuntimeFactoryService provider;

    public static InternalProcessRuntime newProcessRuntime(AbstractWorkingMemory workingMemory) {
        return getProcessRuntimeFactoryService().newProcessRuntime(workingMemory);
    }

    public static synchronized void setProcessRuntimeFactoryService(ProcessRuntimeFactoryService provider) {
        ProcessRuntimeFactory.provider = provider;
    }

    public static synchronized ProcessRuntimeFactoryService getProcessRuntimeFactoryService() {
        if (provider == null) {
        	loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( ProcessRuntimeFactoryService.class, "org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl" );
        setProcessRuntimeFactoryService(ServiceRegistryImpl.getInstance().get( ProcessRuntimeFactoryService.class ) );
    }

}
