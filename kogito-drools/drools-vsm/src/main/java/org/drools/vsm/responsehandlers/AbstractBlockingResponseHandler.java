/**
 *
 */
package org.drools.vsm.responsehandlers;

public abstract class AbstractBlockingResponseHandler extends AbstractBaseResponseHandler {

    /**
     * This method will wait the specified amount of time in milliseconds for the response to
     * be completed. Completed is determined via the <field>done</field>. Returns true if the
     * reponse was completed in time, false otherwise. If an error occurs, this method will throw
     * a subclass of <code>RuntimeException</code> specific to the error.
     *
     * @param time max time to wait
     * @return true if response is available, false otherwise
     *
     * @see org.drools.task.service.PermissionDeniedException
     * @see org.drools.task.service.CannotAddTaskException
     * @see javax.persistence.PersistenceException
     */
    public synchronized boolean waitTillDone(long time) {

        if (!isDone()) {
            try {
                wait(time);
            } catch (InterruptedException e) {
                // swallow and return state of done
            }
        }

        if(hasError()) {            
            throw createSideException(getError());
        }

        return isDone();
    }
}