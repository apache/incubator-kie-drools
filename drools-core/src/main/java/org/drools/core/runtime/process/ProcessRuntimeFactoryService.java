package org.drools.core.runtime.process;

import org.drools.core.common.InternalWorkingMemory;
import org.kie.api.internal.utils.KieService;

/**
 * ProcessRuntimeFactoryService is used by the AbstractWorkingMemory to "provide" it's concrete implementation.
 */
public interface ProcessRuntimeFactoryService extends KieService {

    InternalProcessRuntime newProcessRuntime(InternalWorkingMemory workingMemory);

}
