package org.drools.vsm.rio;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryManager;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.vsm.ClientGenericMessageReceiver;
import org.drools.vsm.GenericConnector;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;
import org.drools.vsm.mina.ClientGenericMessageReceiverImpl;

public class RioConnector
    implements
    GenericConnector,
    GenericIoWriter {

    protected final String                 name;
    protected AtomicInteger                counter;
    protected SessionService               sessionService;
    protected SocketAddress                address;
    protected SystemEventListener          eventListener;
    protected ClientGenericMessageReceiver ioHandler;

    public RioConnector(String name,
                        SystemEventListener eventListener,
                        ClientGenericMessageReceiver ioHandler) {
        if ( name == null ) {
            throw new IllegalArgumentException( "Name can not be null" );
        }
        this.name = name;
        this.counter = new AtomicInteger();
        this.eventListener = eventListener;
        this.ioHandler = ioHandler;
    }

    public RioConnector(String name,
                        SystemEventListener eventListener,
                        SessionService sessionService,
                        ClientGenericMessageReceiver ioHandler) {
        if ( name == null ) {
            throw new IllegalArgumentException( "Name can not be null" );
        }
        this.name = name;
        this.counter = new AtomicInteger();
        this.eventListener = eventListener;
        this.sessionService = sessionService;
        this.ioHandler = ioHandler; //new RioIoHandler( this.eventListener );
    }

    /* (non-Javadoc)
     * @see org.drools.vsm.mina.Messenger#connect()
     */
    public boolean connect() {
        //        Class[] classes = new Class[]{org.drools.vsm.rio.SessionService.class};
        //        ServiceTemplate tmpl = new ServiceTemplate(null, classes, null);
        //
        //        LookupDiscoveryManager lookupDiscovery = null;
        //        ServiceItem item = null;
        //        try {
        //            lookupDiscovery = new LookupDiscoveryManager(LookupDiscoveryManager.ALL_GROUPS, null, null);
        //
        //
        //            System.out.println("Discovering Manager service ...");
        //
        //            ServiceDiscoveryManager serviceDiscovery = new ServiceDiscoveryManager(lookupDiscovery, new LeaseRenewalManager());
        //
        //            /* Wait no more then 10 seconds to discover the service */
        //            item = serviceDiscovery.lookup(tmpl, null, 10000);
        //
        //            serviceDiscovery.terminate();
        //        } catch (InterruptedException ex) {
        //            Logger.getLogger(RioConnector.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (RemoteException ex) {
        //            Logger.getLogger(RioConnector.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (IOException ex) {
        //            Logger.getLogger(RioConnector.class.getName()).log(Level.SEVERE, null, ex);
        //        }
        //        if (item != null) {
        //            System.out.println("Discovered Account service");
        //            if (item.service instanceof SessionService) {
        //                sessionService = (SessionService) item.service;
        //
        //            }
        //            return true;
        //        } else {
        //            System.out.println("Session service not discovered, make sure the" +
        //                    "service is deployed");
        //            return false;
        //        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.drools.vsm.mina.Messenger#disconnect()
     */
    public void disconnect() {
        //I don't need to be disconected
    }

    public void write(Message msg) {
        if ( sessionService != null ) {
            try {
                
                Message returnMessage = this.sessionService.rioWrite( msg );
                
                ioHandler.messageReceived( sessionService ,
                                           returnMessage );
                
            } catch ( RemoteException ex ) {
                Logger.getLogger( RioConnector.class.getName() ).log( Level.SEVERE,
                                                                      null,
                                                                      ex );
            } catch ( Exception ex ) {
                Logger.getLogger( RioConnector.class.getName() ).log( Level.SEVERE,
                                                                      null,
                                                                      ex );
            }
        }
    }

    public void addResponseHandler(int id,
                                   MessageResponseHandler responseHandler) {
        ioHandler.addResponseHandler( id,
                                      responseHandler );
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
