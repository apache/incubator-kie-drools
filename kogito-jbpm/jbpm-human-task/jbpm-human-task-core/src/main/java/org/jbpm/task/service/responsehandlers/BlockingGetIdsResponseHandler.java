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

import java.util.List;

import org.jbpm.task.service.TaskClientHandler.GetIdsResponseHandler;

public class BlockingGetIdsResponseHandler extends AbstractBlockingResponseHandler implements GetIdsResponseHandler {
    private static final int TASK_WAIT_TIME = 10000;

    private volatile List<Long> ids;

    public synchronized void execute(List<Long> ids) {
        this.ids = ids;
        setDone(true);
    }

    public List<Long> getIds() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means task is available 
        boolean done = waitTillDone(TASK_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve ids");
        }

        return ids;
    }
    
    public List<Long> getIds(int waitTime) {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means task is available 
        boolean done = waitTillDone(waitTime);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve ids");
        }

        return ids;
    }
}