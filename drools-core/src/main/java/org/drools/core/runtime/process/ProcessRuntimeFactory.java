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

package org.drools.core.runtime.process;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.internal.utils.ServiceRegistry;


public class ProcessRuntimeFactory {

    private static ProcessRuntimeFactoryService provider = initializeProvider();

    private static ProcessRuntimeFactoryService initializeProvider() {
        return ServiceRegistry.getService( ProcessRuntimeFactoryService.class );
    }

    /**
     * This method is used in jBPM OSGi Activators as we need a way to force re-initialization when starting
     * the bundles.
     */
    public static synchronized void reInitializeProvider() {
        provider = initializeProvider();
    }

    public static InternalProcessRuntime newProcessRuntime(StatefulKnowledgeSessionImpl workingMemory) {
        return provider == null ? null : provider.newProcessRuntime(workingMemory);
    }

    public static void setProcessRuntimeFactoryService(ProcessRuntimeFactoryService provider) {
        ProcessRuntimeFactory.provider = provider;
    }

    public static ProcessRuntimeFactoryService getProcessRuntimeFactoryService() {
        return provider;
    }

}
