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
