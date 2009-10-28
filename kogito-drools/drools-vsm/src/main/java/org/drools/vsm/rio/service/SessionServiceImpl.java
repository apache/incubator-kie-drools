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
import java.util.ArrayList;
import java.util.List;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.command.FinishedCommand;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.GenericMessageHandler;
import org.drools.vsm.Message;
import org.drools.vsm.ServiceManagerData;
import org.drools.vsm.rio.SessionService;

/**
 *
 * @author salaboy
 */
public class SessionServiceImpl implements SessionService{
    private SystemEventListener systemEventListener;

    private ServiceManagerData  data;

    public SessionServiceImpl() {
        this.systemEventListener = SystemEventListenerFactory.getSystemEventListener();
        this.data = new ServiceManagerData();
    }
    
    
    public SessionServiceImpl(ServiceManagerData data,
                                 SystemEventListener systemEventListener) {
        this.systemEventListener = systemEventListener;
        this.data = data;
    }


    public Message write(Message msg) throws RemoteException {
        GenericMessageHandler handler = null;       
        BlockingGenericIoWriter blockingWriter = new BlockingGenericIoWriter();
        try {
            handler.messageReceived( blockingWriter, msg );
        } catch ( Exception e ) {
            throw new RemoteException( e.getMessage() );
        }
        
        return blockingWriter.getMessage();
    }
    
    private static class BlockingGenericIoWriter implements GenericIoWriter {
        
        private Message msg;

        public void write(Message message) {
            this.msg = message;
        }
        
        public Message getMessage() {
            return this.msg;
        }
        
    }

}
