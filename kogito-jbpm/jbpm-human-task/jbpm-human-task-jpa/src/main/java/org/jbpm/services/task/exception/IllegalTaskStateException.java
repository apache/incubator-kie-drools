package org.jbpm.services.task.exception;

import org.kie.internal.task.exception.TaskException;

public class IllegalTaskStateException extends TaskException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IllegalTaskStateException(String message) {
        super(message);
    }

}
