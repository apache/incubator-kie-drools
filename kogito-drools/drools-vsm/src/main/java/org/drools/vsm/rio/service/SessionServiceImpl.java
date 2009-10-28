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
        systemEventListener.debug( "Message receieved : " + msg );


        // we always need to process a List, for genericity, but don't force a List on the payload
        List<GenericCommand> commands;
        if ( msg.getPayload() instanceof List ) {
            commands = (List<GenericCommand>) msg.getPayload();
        } else {
            commands = new ArrayList<GenericCommand>();
            commands.add( (GenericCommand) msg.getPayload() );
        }

        // Setup the evaluation context
        ContextImpl localSessionContext = new ContextImpl( "session_" + msg.getSessionId(),
                                                           this.data.getContextManager(),
                                                           this.data.getTemp() );
        ExecutionResultImpl localKresults = new ExecutionResultImpl();
        localSessionContext.set( "kresults_" + msg.getSessionId(),
                                 localKresults );

        for ( GenericCommand cmd : commands ) {
            // evaluate the commands
            cmd.execute( localSessionContext );
        }

        if ( !msg.isAsync() && localKresults.getIdentifiers().isEmpty() ) {
            // if it's not an async invocation and their are no results, just send a simple notification message
            return  new Message( msg.getSessionId(),
                                        msg.getResponseId(),
                                        msg.isAsync(),
                                        new FinishedCommand() );
        } else {
            // return the payload
            return  new Message( msg.getSessionId(),
                                        msg.getResponseId(),
                                        msg.isAsync(),
                                        localKresults ) ;
        }
    }

}
