package org.drools.vsm.mina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.drools.SystemEventListener;
import org.drools.vsm.GenericMessageHandler;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;

public class MinaIoHandler extends IoHandlerAdapter {
    protected Map<Integer, MessageResponseHandler> responseHandlers;

    private GenericMessageHandler                  handler;

    /**
     * Listener used for logging
     */
    private final SystemEventListener              systemEventListener;

    public MinaIoHandler(SystemEventListener systemEventListener) {
        this( systemEventListener,
              null );

    }

    public MinaIoHandler(SystemEventListener systemEventListener,
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

    public void exceptionCaught(IoSession session,
                                Throwable cause) throws Exception {
        systemEventListener.exception( "Uncaught exception on Server",
                                       cause );
    }

    public void messageReceived(IoSession session,
                                Object object) throws Exception {
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
            this.handler.messageReceived( new MinaIoWriter( session ),
                                          (Message) object );
        } else {
            throw new RuntimeException( "Unable to process Message" );
        }
    }

    @Override
    public void sessionIdle(IoSession session,
                            IdleStatus status) throws Exception {
        this.systemEventListener.debug( "Server IDLE " + session.getIdleCount( status ) );
    }

}
