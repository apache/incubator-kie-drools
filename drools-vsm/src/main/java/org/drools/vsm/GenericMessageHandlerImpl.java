package org.drools.vsm;

import java.util.ArrayList;
import java.util.List;

import org.drools.SystemEventListener;
import org.drools.command.Context;
import org.drools.command.FinishedCommand;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.command.vsm.ServiceManagerServerContext;
import org.drools.runtime.impl.ExecutionResultImpl;

public class GenericMessageHandlerImpl implements GenericMessageHandler {
    private SystemEventListener systemEventListener;

    private ServiceManagerData  data;

    public GenericMessageHandlerImpl(ServiceManagerData data,
                                 SystemEventListener systemEventListener) {
        this.systemEventListener = systemEventListener;
        this.data = data;
    }

    /* (non-Javadoc)
     * @see org.drools.vsm.GenericMessageHandler#messageReceived(org.drools.vsm.GenericIoWriter, org.drools.vsm.Message)
     */
    public void messageReceived(GenericIoWriter session,
                                Message msg) throws Exception {
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
            session.write( new Message( msg.getSessionId(),
                                        msg.getResponseId(),
                                        msg.isAsync(),
                                        new FinishedCommand() ), null );
        } else {
            // return the payload
            session.write( new Message( msg.getSessionId(),
                                        msg.getResponseId(),
                                        msg.isAsync(),
                                        localKresults ), null );
        }
    }
}
