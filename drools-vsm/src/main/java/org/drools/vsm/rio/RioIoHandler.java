package org.drools.vsm.rio;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.drools.SystemEventListener;
import org.drools.vsm.GenericMessageHandler;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;

public class RioIoHandler  {
    protected Map<Integer, MessageResponseHandler> responseHandlers;

    private GenericMessageHandler                  handler;

    /**
     * Listener used for logging
     */
    private final SystemEventListener              systemEventListener;

    public RioIoHandler(SystemEventListener systemEventListener) {
        this( systemEventListener,
              null );

    }

    public RioIoHandler(SystemEventListener systemEventListener,
                         GenericMessageHandler handler) {
        this.systemEventListener = systemEventListener;
        this.responseHandlers = new ConcurrentHashMap<Integer, MessageResponseHandler>();
        this.handler = handler;

    }

    public void addResponseHandler(int id,
                                   MessageResponseHandler responseHandler) {
        this.responseHandlers.put( id,
                                   responseHandler );
    }

    

    public void messageReceived(SessionService sessionService, Object object) throws Exception {
        Message msg = (Message) object;

        systemEventListener.debug( "Message receieved : " + msg );

        MessageResponseHandler responseHandler = (MessageResponseHandler) responseHandlers.remove( msg.getResponseId() );

        if ( responseHandler != null ) {
            Object payload = msg.getPayload();
            if ( payload != null && payload instanceof RuntimeException ) {
                responseHandler.setError( (RuntimeException) payload );
            } else {
                responseHandler.receive( msg );
            }
        } else if ( handler != null ) {
            this.handler.messageReceived( new RioIoWriter( sessionService ),
                                          (Message) object );
        } else {
            throw new RuntimeException( "Unable to process Message" );
        }
    }

   

}
