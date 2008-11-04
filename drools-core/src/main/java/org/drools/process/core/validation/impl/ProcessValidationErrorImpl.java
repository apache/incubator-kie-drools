package org.drools.process.core.validation.impl;

import org.drools.definition.process.Process;
import org.drools.process.core.validation.ProcessValidationError;

public class ProcessValidationErrorImpl implements ProcessValidationError {

    private Process process;
    private String message;
    
    public ProcessValidationErrorImpl(Process process, String message) {
        this.process = process;
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

    public Process getProcess() {
        return process;
    }
    
    public String toString() {
        return "Process '" + process.getName() + "' [" + process.getId() + "]: " + getMessage();
    }

}
