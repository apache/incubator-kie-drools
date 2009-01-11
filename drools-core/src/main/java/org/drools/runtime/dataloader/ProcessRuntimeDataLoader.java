package org.drools.runtime.dataloader;

import org.drools.runtime.process.ProcessInstance;

public interface ProcessRuntimeDataLoader {
    ProcessInstance startProcess(String processId,
                                 Object parameters);
}