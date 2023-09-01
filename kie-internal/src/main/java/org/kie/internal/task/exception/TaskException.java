package org.kie.internal.task.exception;

/**
 * Base class for all exceptions for the task related activities
 *
 * see org.jbpm.services.task.service.TaskServiceSession#addTask(org.jbpm.services.task.Task, ContentData)
 */
public abstract class TaskException extends RuntimeException {

    private static final long serialVersionUID = 2370182914623204842L;
    private boolean recoverable = true;

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public boolean isRecoverable() {
        return recoverable;
    }

    public void setRecoverable(boolean recoverable) {
        this.recoverable = recoverable;
    }
}
