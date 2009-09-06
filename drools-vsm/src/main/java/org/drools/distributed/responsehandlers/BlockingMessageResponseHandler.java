/**
 *
 */
package org.drools.distributed.responsehandlers;

import org.drools.vsm.Message;
import org.drools.vsm.BaseMinaHandler.MessageResponseHandler;

public class BlockingMessageResponseHandler extends AbstractBlockingResponseHandler implements MessageResponseHandler {
    private static final int ATTACHMENT_ID_WAIT_TIME = 10000;
    private static final int CONTENT_ID_WAIT_TIME = 3000;

    private volatile Message message;

    public synchronized void receive(Message message) {
        this.message = message;
        setDone(true);
    }

    public  Message getMessage() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means attachmentId is available
        boolean done = waitTillDone(ATTACHMENT_ID_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Object Id");
        }

        return this.message;
    }

}