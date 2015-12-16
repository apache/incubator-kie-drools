/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import org.drools.util.ServiceRegistryImpl;

/**
 * This factory allows you to set the SystemEventListener that will be used by various components of Drools, such
 * as the KnowledgeAgent, ResourceChangeNotifier and ResourceChangeListener.
 */
public class SystemEventListenerFactory {
    private static SystemEventListenerService service;

    /**
     * Set the SystemEventListener
     * 
     * @param listener
     */
    public static void setSystemEventListener(SystemEventListener listener) {
        getSystemEventListenerService().setSystemEventListener( listener );
    }

    /**
     * Get the SystemEventListener
     * @return
     */
    public static SystemEventListener getSystemEventListener() {
        return getSystemEventListenerService().getSystemEventListener();
    }

    private static synchronized void setSystemEventListenerService(SystemEventListenerService service) {
        SystemEventListenerFactory.service = service;
    }

    private static synchronized SystemEventListenerService getSystemEventListenerService() {
        if ( service == null ) {
            loadService();
        }
        return service;
    }

    private static void loadService() {
        setSystemEventListenerService( ServiceRegistryImpl.getInstance().get( SystemEventListenerService.class ) );
    }
}
