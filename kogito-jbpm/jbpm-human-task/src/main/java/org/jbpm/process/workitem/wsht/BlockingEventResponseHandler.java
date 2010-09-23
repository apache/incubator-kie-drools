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
package org.jbpm.process.workitem.wsht;

import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.service.responsehandlers.AbstractBlockingResponseHandler;

public class BlockingEventResponseHandler extends AbstractBlockingResponseHandler implements EventResponseHandler {
    // todo why is this timeout different from the others?? - also, if this should be the same
    // they can be collasped into the base class
    private static final int DEFAULT_WAIT_TIME = 1000000;

    private volatile Payload payload;

    public synchronized void execute(Payload payload) {
        this.payload = payload;
        setDone(true);
    }

    public Payload getPayload() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means payload is available 
        boolean done = waitTillDone(DEFAULT_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve event payload");
        }

        return payload;
    }
    
    public boolean isRemove() {
    	return true;
    }

}