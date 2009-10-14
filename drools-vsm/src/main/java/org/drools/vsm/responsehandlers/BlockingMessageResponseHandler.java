/**
 *
 */
package org.drools.vsm.responsehandlers;

import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;

public class BlockingMessageResponseHandler extends AbstractBlockingResponseHandler
    implements
    MessageResponseHandler {
    private static final int ATTACHMENT_ID_WAIT_TIME = 100000;
    private static final int CONTENT_ID_WAIT_TIME    = 500000;

    private volatile Message message;

    public synchronized void receive(Message message) {
        this.message = message;
        setDone( true );
    }

    public Message getMessage() {
        boolean done = waitTillDone( CONTENT_ID_WAIT_TIME );

        if ( !done ) {
            throw new RuntimeException( "Timeout : unable to retrieve Object Id" );
        }

        return this.message;
    }

}