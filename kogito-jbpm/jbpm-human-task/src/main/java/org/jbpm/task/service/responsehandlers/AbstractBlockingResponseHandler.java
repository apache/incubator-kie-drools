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

/**
 *
 */
package org.jbpm.task.service.responsehandlers;

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
     * @see org.jbpm.task.service.PermissionDeniedException
     * @see org.jbpm.task.service.CannotAddTaskException
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