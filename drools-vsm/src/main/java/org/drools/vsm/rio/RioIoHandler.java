package org.drools.vsm.rio;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.drools.SystemEventListener;
import org.drools.vsm.GenericMessageHandler;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;
import org.drools.vsm.mina.ClientGenericMessageReceiverImpl;

public class RioIoHandler  {
    protected Map<Integer, MessageResponseHandler> responseHandlers;
    
    private ClientGenericMessageReceiverImpl clientMessageReceiver;
    

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
        this.clientMessageReceiver = new ClientGenericMessageReceiverImpl( handler,
                                                                           systemEventListener );

    }

    public void addResponseHandler(int id,
                                   MessageResponseHandler responseHandler) {
        this.clientMessageReceiver.addResponseHandler( id, responseHandler );
    }

    

    public void messageReceived(SessionService sessionService, Object object) throws Exception {
        Message msg = (Message) object;

        this.clientMessageReceiver.messageReceived( new RioIoWriter( sessionService ), msg );
    }

   

}
