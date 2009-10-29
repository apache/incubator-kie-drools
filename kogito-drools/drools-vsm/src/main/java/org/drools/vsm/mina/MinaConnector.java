package org.drools.vsm.mina;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.drools.SystemEventListener;
import org.drools.vsm.GenericConnector;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;
import org.drools.vsm.responsehandlers.BlockingMessageResponseHandler;

public class MinaConnector
    implements
    GenericConnector,
    GenericIoWriter {
    protected IoSession           session;

    protected final String        name;
    protected AtomicInteger       counter;
    protected SocketConnector     connector;
    protected SocketAddress       address;
    protected SystemEventListener eventListener;

    public MinaConnector(String name,
                         SocketConnector connector,
                         SocketAddress address,
                         SystemEventListener eventListener) {
        if ( name == null ) {
            throw new IllegalArgumentException( "Name can not be null" );
        }
        this.name = name;
        this.counter = new AtomicInteger();
        this.address = address;
        this.connector = connector;
        this.eventListener = eventListener;
    }

    /* (non-Javadoc)
     * @see org.drools.vsm.mina.Messenger#connect()
     */
    public boolean connect() {
        if ( session != null && session.isConnected() ) {
            throw new IllegalStateException( "Already connected. Disconnect first." );
        }

        try {
            this.connector.getFilterChain().addLast( "codec",
                                                     new ProtocolCodecFilter( new ObjectSerializationCodecFactory() ) );

            ConnectFuture future1 = this.connector.connect( this.address );
            future1.await( 2000 );
            if ( !future1.isConnected() ) {
                eventListener.info( "unable to connect : " + address + " : " + future1.getException() );
                return false;
            }
            eventListener.info( "connected : " + address );
            this.session = future1.getSession();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.drools.vsm.mina.Messenger#disconnect()
     */
    public void disconnect() {
        if ( session != null && session.isConnected() ) {
            session.close();
            session.getCloseFuture().join();
        }
    }

    private void addResponseHandler(int id,
                                    MessageResponseHandler responseHandler) {
        ((MinaIoHandler) this.connector.getHandler()).addResponseHandler( id,
                                                                          responseHandler );
    }

    public void write(Message msg,
                      MessageResponseHandler responseHandler) {
        if ( responseHandler != null ) {
            addResponseHandler( msg.getResponseId(),
                                responseHandler );
        }
        this.session.write( msg );
    }

    public Message write(Message msg) {
        BlockingMessageResponseHandler responseHandler = new BlockingMessageResponseHandler();

        if ( responseHandler != null ) {
            addResponseHandler( msg.getResponseId(),
                                responseHandler );
        }
        this.session.write( msg );

        Message returnMessage = responseHandler.getMessage();
        if ( responseHandler.getError() != null ) {
            throw responseHandler.getError();
        }

        return returnMessage;
    }
}
