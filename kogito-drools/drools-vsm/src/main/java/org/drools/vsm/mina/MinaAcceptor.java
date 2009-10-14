package org.drools.vsm.mina;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.drools.vsm.AcceptorService;

public class MinaAcceptor
    implements
    AcceptorService {
    protected SocketAcceptor acceptor;
    protected SocketAddress  address;

    public MinaAcceptor(SocketAcceptor acceptor,
                        SocketAddress address) {
        this.acceptor = acceptor;
        this.address = address;
    }

    public synchronized void start() throws IOException {
        acceptor.getFilterChain().addLast( "logger",
                                           new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec",
                                           new ProtocolCodecFilter( new ObjectSerializationCodecFactory() ) );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE,
                                                 10 );
        acceptor.bind( address );
    }

    public synchronized void stop() {
        acceptor.dispose();
    }

    public synchronized IoAcceptor getIoAcceptor() {
        return acceptor;
    }

}