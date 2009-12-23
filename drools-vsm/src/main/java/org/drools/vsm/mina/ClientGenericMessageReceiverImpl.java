/**
 * 
 */
package org.drools.vsm.mina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.SystemEventListener;
import org.drools.task.service.Command;
import org.drools.vsm.ClientGenericMessageReceiver;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.GenericMessageHandler;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;

public class ClientGenericMessageReceiverImpl
    implements
    ClientGenericMessageReceiver {
    protected Map<Integer, MessageResponseHandler> responseHandlers;

    private GenericMessageHandler                  handler;

    private final SystemEventListener              systemEventListener;

    public ClientGenericMessageReceiverImpl(GenericMessageHandler handler,
                                            SystemEventListener systemEventListener) {
        this.handler = handler;
        this.responseHandlers = new ConcurrentHashMap<Integer, MessageResponseHandler>();;
        this.systemEventListener = systemEventListener;
    }

    /* (non-Javadoc)
     * @see org.drools.vsm.mina.ClientGenericMessageReceiver#addResponseHandler(int, org.drools.vsm.MessageResponseHandler)
     */
    public void addResponseHandler(int id,
                                   MessageResponseHandler responseHandler) {
        this.responseHandlers.put( id,
                                   responseHandler );
    }

    /* (non-Javadoc)
     * @see org.drools.vsm.mina.ClientGenericMessageReceiver#messageReceived(org.drools.vsm.mina.MinaIoWriter, org.drools.vsm.Message)
     */
    public void messageReceived(GenericIoWriter writer,
                                Message msg) throws Exception {

        systemEventListener.debug( "Message receieved : " + msg );

        MessageResponseHandler responseHandler = (MessageResponseHandler) responseHandlers.remove( msg.getResponseId() );

        if ( responseHandler != null ) {
            Object payload = msg.getPayload();
            if (payload instanceof Command && ((Command)msg.getPayload()).getArguments().size() > 0 &&
            	((Command)msg.getPayload()).getArguments().get(0) instanceof RuntimeException)
            	payload = ((Command)msg.getPayload()).getArguments().get(0);
            if (( payload != null && payload instanceof RuntimeException )) {
                responseHandler.setError( (RuntimeException) payload );
            } else {
                responseHandler.receive( msg );
            }
        } else if ( handler != null ) {
            this.handler.messageReceived( writer,
                                          msg );
        } else {
            throw new RuntimeException( "Unable to process Message" );
        }
    }
}