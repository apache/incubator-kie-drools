package org.drools.vsm;

import org.apache.mina.core.session.IoSession;
import org.drools.SystemEventListener;
import org.drools.command.impl.GenericCommand;

import java.util.Arrays;
import java.util.List;

public class MessageHandler extends BaseMinaHandler {
    private Object owner;

    /**
     * Listener used for logging
     */
    private SystemEventListener systemEventListener;

    public MessageHandler(SystemEventListener systemEventListener) {
        this.systemEventListener = systemEventListener;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    @Override
    public void exceptionCaught(IoSession session,
                                Throwable cause) throws Exception {
        systemEventListener.exception("Uncaught exception on client", cause);
    }

    @Override
    public void messageReceived(IoSession session,
                                Object object) throws Exception {        
        Message msg = (Message) object;

        systemEventListener.debug("Message receieved on client : " + msg );
        
        
        MessageResponseHandler responseHandler = (MessageResponseHandler) responseHandlers.remove( msg.getResponseId() );
        //if ( responseHandler instanceof Block)
        if (responseHandler != null) {            
            Object payload = msg.getPayload();
            if (payload != null && payload instanceof RuntimeException) {
                responseHandler.setError((RuntimeException) payload);
            } else {
                responseHandler.receive( msg );
            }
        }
    }

//    public static interface GetTaskResponseHandler
//            extends
//            ResponseHandler {
//        public void execute(Task task);
//    }

}