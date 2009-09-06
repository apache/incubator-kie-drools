package org.drools.vsm.responsehandlers;

import org.drools.vsm.BaseMinaHandler;

import java.lang.reflect.Constructor;

/**
 * Abstract base class for client ResponseHandlers. Provides synchonized access to <field>done</field> which represents
 * if the response is completed. Also has an <field>error</field> which will be set when there is a problem with
 * a response. Users of this class should check to see if the response completed successfully, via
 * the <method>isDone</method> and the <method>hasError</method>.
 * <p/>
 * Please note that the <field>error</field> is actually the Exception that occured on the server while
 * processing the request.
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public abstract class AbstractBaseResponseHandler implements BaseMinaHandler.ResponseHandler {
    private volatile boolean done;
    private RuntimeException error;

    public synchronized boolean hasError() {
        return error != null;
    }

    public synchronized RuntimeException getError() {
        return error;
    }

    public synchronized void setError(RuntimeException error) {
        this.error = error;
        notifyAll();
    }

    public synchronized boolean isDone() {
        return done;
    }

    protected synchronized void setDone(boolean done) {
        this.done = done;
        notifyAll();
    }

    /**
     * This method will take the specified serverSideException, and create a new one for the client based
     * on the serverSideException. This is done so a proper stack trace can be made for the client, as opposed
     * to seeing the server side stack.
     *
     * @param serverSideException exception used to create client side exception
     * @return client side exception
     */
    protected static RuntimeException createSideException(RuntimeException serverSideException) {
        RuntimeException clientSideException;

        try {
            Constructor<? extends RuntimeException> constructor = serverSideException.getClass().getConstructor(String.class);

            clientSideException = constructor.newInstance(
        		"Server-side Exception: " + serverSideException.getMessage());
        } catch (Exception e) {
            // this should never happen - if it does, it is a programming error
            throw new RuntimeException("Could not create client side exception", e);
        }

        return clientSideException;
    }
}
