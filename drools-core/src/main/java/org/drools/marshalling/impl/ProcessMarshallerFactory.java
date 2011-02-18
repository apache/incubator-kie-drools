package org.drools.marshalling.impl;

import org.drools.util.ServiceRegistryImpl;

public class ProcessMarshallerFactory {

    private static ProcessMarshallerFactoryService service;

    public static ProcessMarshaller newProcessMarshaller() {
        return getProcessMarshallerFactoryService().newProcessMarshaller();
    }

    public static synchronized void setProcessMarshallerFactoryService(ProcessMarshallerFactoryService service) {
        ProcessMarshallerFactory.service = service;
    }

    public static synchronized ProcessMarshallerFactoryService getProcessMarshallerFactoryService() {
        if (service == null) {
        	loadProvider();
        }
        return service;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( ProcessMarshallerFactoryService.class, "org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl" );
        setProcessMarshallerFactoryService(ServiceRegistryImpl.getInstance().get( ProcessMarshallerFactoryService.class ) );
    }

}
