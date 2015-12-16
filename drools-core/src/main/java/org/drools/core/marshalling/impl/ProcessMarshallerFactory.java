/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.marshalling.impl;

import org.kie.internal.utils.ServiceRegistryImpl;

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
