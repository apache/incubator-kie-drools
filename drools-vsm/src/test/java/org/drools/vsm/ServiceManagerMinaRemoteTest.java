package org.drools.vsm;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.vsm.mina.MinaAcceptor;
import org.drools.vsm.mina.MinaConnector;
import org.drools.vsm.mina.MinaIoHandler;
import org.drools.vsm.remote.ServiceManagerRemoteClient;

public class ServiceManagerMinaRemoteTest extends ServiceManagerTestBase {
    AcceptorService server;

    protected void setUp() throws Exception {
        SocketAddress address = new InetSocketAddress( "127.0.0.1",
                                                       9123 );

        ServiceManagerData serverData = new ServiceManagerData();
        // setup Server
        SocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler( new MinaIoHandler( SystemEventListenerFactory.getSystemEventListener(),
                                                new GenericMessageHandlerImpl( serverData,
                                                                           SystemEventListenerFactory.getSystemEventListener() ) ) );
        this.server = new MinaAcceptor( acceptor,
                                        address );
        this.server.start();

        // setup Client
        NioSocketConnector clientConnector = new NioSocketConnector();
        clientConnector.setHandler( new MinaIoHandler( SystemEventListenerFactory.getSystemEventListener() ) );
        GenericConnector minaClient = new MinaConnector( "client 1",
                                                         clientConnector,
                                                         address,
                                                         SystemEventListenerFactory.getSystemEventListener() );
        this.client = new ServiceManagerRemoteClient( "client 1",
                                                      minaClient );

        ((ServiceManagerRemoteClient) client).connect();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ((ServiceManagerRemoteClient) client).disconnect();
        this.server.stop();
    }

}
