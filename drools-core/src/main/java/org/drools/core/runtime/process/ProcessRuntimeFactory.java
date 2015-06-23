/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.runtime.process;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.internal.utils.ServiceRegistryImpl;


public class ProcessRuntimeFactory {

    private static boolean initialized;
    private static ProcessRuntimeFactoryService provider;

    private static final String PROVIDER_CLASS = "org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl";

    public static InternalProcessRuntime newProcessRuntime(StatefulKnowledgeSessionImpl workingMemory) {
        ProcessRuntimeFactoryService provider = getProcessRuntimeFactoryService();
        return provider == null ? null : provider.newProcessRuntime(workingMemory);
    }

    public static synchronized void setProcessRuntimeFactoryService(ProcessRuntimeFactoryService provider) {
        ProcessRuntimeFactory.provider = provider;
    }

    public static synchronized ProcessRuntimeFactoryService getProcessRuntimeFactoryService() {
        if (provider == null && !initialized) {
            initialized = true;
            try {
                loadProvider();
            } catch (IllegalArgumentException e) { }
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( ProcessRuntimeFactoryService.class, PROVIDER_CLASS );
        setProcessRuntimeFactoryService(ServiceRegistryImpl.getInstance().get( ProcessRuntimeFactoryService.class ) );
    }

    public static synchronized void resetInitialization() {
        initialized = false;
    }
}
