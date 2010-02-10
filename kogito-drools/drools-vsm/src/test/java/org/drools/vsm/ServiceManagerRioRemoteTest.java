package org.drools.vsm;

import org.drools.SystemEventListenerFactory;
import org.drools.vsm.mina.ClientGenericMessageReceiverImpl;
import org.drools.vsm.remote.ServiceManagerRemoteClient;
import org.drools.vsm.rio.RioConnector;
import org.drools.vsm.rio.RioServer;
import org.drools.vsm.rio.service.SessionServiceImpl;;

public class ServiceManagerRioRemoteTest extends ServiceManagerTestBase {
    AcceptorService server;

    protected void setUp() throws Exception {

//        ServiceManagerData serverData = new ServiceManagerData();
//
//        //setup Server
//        RioServer rioServer = new RioServer();
//        rioServer.start();
//        
//        GenericMessageHandler handler =  ((SessionServiceImpl)rioServer.getSessionService()).getGenericMessageHandler();
//                                       
//        RioConnector connector = new RioConnector( "client 1",
//                                                   SystemEventListenerFactory.getSystemEventListener(),
//                                                   rioServer.getSessionService(),
//                                                   new ClientGenericMessageReceiverImpl(handler, SystemEventListenerFactory.getSystemEventListener()));
//
//        // setup Client
//        this.client = new ServiceManagerRemoteClient( "client 1",
//                                                      connector );
//        //this.client.connect();

    }

    protected void tearDown() throws Exception {
//        super.tearDown();
//        ((ServiceManagerRemoteClient) client).disconnect();
//        //this.server.stop();
    }

}
