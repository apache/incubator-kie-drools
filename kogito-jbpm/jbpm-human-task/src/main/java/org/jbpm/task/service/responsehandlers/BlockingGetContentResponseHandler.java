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

import org.jbpm.task.Content;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;

public class BlockingGetContentResponseHandler extends AbstractBlockingResponseHandler implements GetContentResponseHandler {
    private static final int CONTENT_WAIT_TIME = 10000;

    private volatile Content content;

    public synchronized void execute(Content content) {
        this.content = content;
        setDone(true);
    }

    public Content getContent() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means content is available 
        boolean done = waitTillDone(CONTENT_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Attachment Content");
        }

        return content;
    }
}