/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task.service.responsehandlers;

import java.lang.reflect.Constructor;

import org.kie.task.service.ResponseHandler;

/**
 * Abstract base class for client ResponseHandlers. Provides synchonized access to <field>done</field> which represents
 * if the response is completed. Also has an <field>error</field> which will be set when there is a problem with
 * a response. Users of this class should check to see if the response completed successfully, via
 * the <method>isDone</method> and the <method>hasError</method>.
 * <p/>
 * Please note that the <field>error</field> is actually the Exception that occured on the server while
 * processing the request.
 *
 */
public abstract class AbstractBaseResponseHandler implements ResponseHandler {
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
