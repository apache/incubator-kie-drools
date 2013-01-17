package org.jbpm.process.core.impl;

import org.kie.internal.utils.ServiceRegistryImpl;

public class XmlProcessDumperFactory {

    private static XmlProcessDumperFactoryService service;

    public static XmlProcessDumper newXmlProcessDumperFactory() {
        return getXmlProcessDumperFactoryService().newXmlProcessDumper();
    }

    public static synchronized void setXmlProcessDumperFactoryService(XmlProcessDumperFactoryService service) {
        XmlProcessDumperFactory.service = service;
    }

    public static synchronized XmlProcessDumperFactoryService getXmlProcessDumperFactoryService() {
        if (service == null) {
            loadProvider();
        }
        return service;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( XmlProcessDumperFactoryService.class, "org.jbpm.bpmn2.xml.XmlProcessDumperFactoryServiceImpl" );
        setXmlProcessDumperFactoryService(ServiceRegistryImpl.getInstance().get( XmlProcessDumperFactoryService.class ) );
    }

}
