package org.drools.kiesession.session;

import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.runtime.process.ProcessRuntimeFactoryService;
import org.kie.api.internal.utils.KieService;


public class ProcessRuntimeFactory {

    private static ProcessRuntimeFactoryService provider = initializeProvider();

    private static ProcessRuntimeFactoryService initializeProvider() {
        return KieService.load( ProcessRuntimeFactoryService.class );
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
