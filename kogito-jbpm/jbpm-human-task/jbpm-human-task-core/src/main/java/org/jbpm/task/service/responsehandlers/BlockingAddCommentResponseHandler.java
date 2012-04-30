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

import org.jbpm.task.service.TaskClientHandler.AddCommentResponseHandler;

public class BlockingAddCommentResponseHandler extends AbstractBlockingResponseHandler
        implements AddCommentResponseHandler {

    private static final int COMMENT_ID_WAIT_TIME = 10000;

    private volatile long commentId;

    public synchronized void execute(long commentId) {
        this.commentId = commentId;
        setDone(true);
    }

    public long getCommentId() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means commentId is available 
        boolean done = waitTillDone(COMMENT_ID_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Comment Id");
        }

        return commentId;
    }
}