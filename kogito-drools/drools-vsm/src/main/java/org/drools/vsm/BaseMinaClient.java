package org.drools.vsm;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;

public class BaseMinaClient {
    protected IoSession             session;

    protected final BaseMinaHandler handler;
    protected final String          name;
    protected AtomicInteger         counter;

    protected SocketConnector       connector;
    protected SocketAddress         address;

    public BaseMinaClient(String name,
                          BaseMinaHandler handler) {
        if ( name == null ) {
            throw new IllegalArgumentException( "Name can not be null" );
        }
        this.name = name;
        this.handler = handler;
        counter = new AtomicInteger();
    }

    public boolean connect(SocketConnector connector,
                           SocketAddress address) {
        this.connector = connector;
        this.address = address;
        connector.setHandler( this.handler );
        return connect();
    }

    public boolean connect() {
        if ( session != null && session.isConnected() ) {
            throw new IllegalStateException( "Already connected. Disconnect first." );
        }

        try {
            //            SocketConnectorConfig config = new SocketConnectorConfig();
            //            if (useSsl) {
            //                SSLContext sslContext = BogusSSLContextFactory
            //                        .getInstance(false);
            //                SSLFilter sslFilter = new SSLFilter(sslContext);
            //                sslFilter.setUseClientMode(true);
            //                config.getFilterChain().addLast("sslFilter", sslFilter);
            //            }

            //connector.setHandler( arg0 );

            connector.getFilterChain().addLast( "codec",
                                                new ProtocolCodecFilter( new ObjectSerializationCodecFactory() ) );

            ConnectFuture future1 = connector.connect( address );
            future1.join();
            if ( !future1.isConnected() ) {
                return false;
            }
            session = future1.getSession();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        if ( session != null && session.isConnected() ) {
            session.close();
            session.getCloseFuture().join();
        }
    }
}
