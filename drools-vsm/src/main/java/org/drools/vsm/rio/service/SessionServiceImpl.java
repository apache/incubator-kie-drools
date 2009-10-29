/*
 *  Copyright 2009 salaboy.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.vsm.rio.service;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.vsm.BlockingGenericIoWriter;
import org.drools.vsm.GenericMessageHandler;
import org.drools.vsm.GenericMessageHandlerImpl;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;
import org.drools.vsm.ServiceManagerData;
import org.drools.vsm.rio.SessionService;

/**
 *
 * @author salaboy
 */
public class SessionServiceImpl implements SessionService{   
    private GenericMessageHandler handler;

    public SessionServiceImpl() {        
        handler = new GenericMessageHandlerImpl(new ServiceManagerData(), SystemEventListenerFactory.getSystemEventListener());
    }
    
    
    public SessionServiceImpl(ServiceManagerData data,
                              SystemEventListener systemEventListener) {
        handler = new GenericMessageHandlerImpl( data, systemEventListener );
    }


    public Message write(Message msg) throws RemoteException {   
        BlockingGenericIoWriter blockingWriter = new BlockingGenericIoWriter();
        try {
            handler.messageReceived( blockingWriter, msg );
        } catch ( Exception e ) {
            throw new RemoteException( e.getMessage() );
        }
        
        return blockingWriter.getMessage();
    }
    
    public GenericMessageHandler getGenericMessageHandler() {
        return this.handler;
    }


    public void write(Message msg,
                      MessageResponseHandler responseHandler) {
        throw new UnsupportedOperationException();
    }
    

}
