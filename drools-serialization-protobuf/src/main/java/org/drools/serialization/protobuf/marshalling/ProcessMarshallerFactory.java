package org.drools.serialization.protobuf.marshalling;

import org.kie.api.internal.utils.KieService;

public class ProcessMarshallerFactory {

    private static class LazyLoader {
        private static ProcessMarshallerFactoryService service = KieService.load( ProcessMarshallerFactoryService.class );
    }

    public static ProcessMarshaller newProcessMarshaller() {
        if (getProcessMarshallerFactoryService() != null) {
            return getProcessMarshallerFactoryService().newProcessMarshaller();
        } else {
            return null;
        }
    }

    public static synchronized ProcessMarshallerFactoryService getProcessMarshallerFactoryService() {
        return LazyLoader.service;
    }

}
