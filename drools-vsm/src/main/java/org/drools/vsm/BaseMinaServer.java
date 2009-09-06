package org.drools.vsm;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class BaseMinaServer
    implements
    Runnable {
    private final int port;

    IoHandlerAdapter  handler;

    IoAcceptor        acceptor;

    volatile boolean  running;

    public BaseMinaServer(IoHandlerAdapter handler,
                          int port) {
        this.handler = handler;
        this.port = port;
    }

    public void run() {
        try {
            start();
            while ( running ) {
                Thread.sleep( 100 );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Server Exception with class " + getClass() + " using port " + port,
                                        e );
        }
    }

    public void start() throws IOException {
        running = true;

        acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger",
                                           new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec",
                                           new ProtocolCodecFilter( new ObjectSerializationCodecFactory() ) );

        acceptor.setHandler( handler );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE,
                                                 10 );
        acceptor.bind( new InetSocketAddress( "127.0.0.1", port ) );
    }
    
    public IoAcceptor getIoAcceptor() {
        return acceptor;
    }

    public void stop() {
        acceptor.dispose();
        running = false;
    }
}