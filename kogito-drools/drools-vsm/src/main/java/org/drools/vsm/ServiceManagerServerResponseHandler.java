package org.drools.vsm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.KnowledgeBaseProvider;
import org.drools.SystemEventListener;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.command.FinishedCommand;
import org.drools.command.KnowledgeContextResolveFromContextCommand;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.command.vsm.ServiceManagerClientConnectCommand;
import org.drools.command.vsm.ServiceManagerServerContext;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.impl.ExecutionResultImpl;

public class ServiceManagerServerResponseHandler extends IoHandlerAdapter {
    private ServiceManagerServer server;



    public static class ContextManagerImpl
        implements
        ContextManager {
        private Map<String, Context> contexts;

        public ContextManagerImpl() {
            this.contexts = new HashMap<String, Context>();
        }

        public void addContext(Context context) {
            this.contexts.put( context.getName(),
                               context );
        }

        public Context getContext(String identifier) {
            return this.contexts.get( identifier );
        }

    }

    /**
     * Listener used for logging
     */
    private final SystemEventListener systemEventListener;

    public ServiceManagerServerResponseHandler(SystemEventListener systemEventListener) {
        this.systemEventListener = systemEventListener;

    }

    public void setServiceManagerService(ServiceManagerServer server) {
        this.server = server;
    }

    @Override
    public void exceptionCaught(IoSession session,
                                Throwable cause) throws Exception {
        systemEventListener.exception( "Uncaught exception on Server",
                                       cause );
    }

    @Override
    public void messageReceived(IoSession session,
                                Object object) throws Exception {
        Message msg = (Message) object;

        systemEventListener.debug( "Message receieved on server : " + msg );

        try {
            // establish session
            if ( msg.getSessionId() == -1 ) {
                GenericCommand cmd = (GenericCommand) msg.getPayload();
                Context ctx = new ServiceManagerServerContext( null,
                                                               server );
                int sessionId = (Integer) cmd.execute( ctx );
                session.write( new Message( sessionId,
                                            msg.getResponseId(),
                                            false,
                                            null,
                                            sessionId ) );
                return;
            }
        } catch ( RuntimeException e ) {
            systemEventListener.exception( e.getMessage(),
                                           e );
            // new Message(msg.getSessionId(), msg.getResponseId(), e);
            // List<Object> list = new ArrayList<Object>(1);
            // list.add(e);
            // Command resultsCmnd = new Command(cmd.getId(), response, list);
            // session.write(resultsCmnd);
        } finally {

        }
        
        List<GenericCommand> commands;
        if ( msg.getPayload() instanceof List ) {
            commands = (List<GenericCommand>) msg.getPayload();
        } else {
            commands = new ArrayList<GenericCommand>();
            commands.add( (GenericCommand) msg.getPayload() );
        }

        //KnowledgeCommandContext ktcx = new KnowledgeCommandContext(this.temp);
        ContextImpl localSessionContext = new ContextImpl( "sesseion_" + msg.getSessionId(), this.server.getContextManager(), this.server.getTemp() );
        ExecutionResultImpl localKresults = new ExecutionResultImpl();
        localSessionContext.set( "kresults_" + msg.getSessionId(), localKresults );
        for ( GenericCommand cmd : commands ) {
            cmd.execute( localSessionContext );
        }
        
        if ( !msg.isAsync() && localKresults.getIdentifiers().isEmpty() ) {
            session.write( new Message(msg.getSessionId(), msg.getResponseId(), msg.isAsync(), null, new FinishedCommand() ) );
        } else {
            session.write( new Message(msg.getSessionId(), msg.getResponseId(), msg.isAsync(), null, localKresults ) );
        }
       

        //        SessionData data = this.sessionData.get( msg.getSessionId() );
        //        if ( data == null ) {
        //            data = new SessionData();
        //            this.sessionData.put( msg.getSessionId(),
        //                                  data );
        //        }
        //
        //        Integer sessionId = ( Integer ) data.data.get( msg.getInstanceId() );
        //        if ( sessionId == null ) {
        //            GenericCommand cmd = ( GenericCommand ) msg.getPayload();
        //            data.data.put( sessionId, cmd.execute( null ) );
        //        }

        // msg.get

        // Comman cmd = msg.getPayload();

        // TaskServiceSession taskSession = service.createSession();
        // CommandName response = null;
        // try {
        // systemEventListener.debug("Message receieved on server : " +
        // cmd.getName());
        // systemEventListener.debug("Arguments : " +
        // Arrays.toString(cmd.getArguments().toArray()));
        //
        // 
        // // response = CommandName.GetTaskResponse;
        // // long taskId = (Long) cmd.getArguments().get(0);
        // //
        // // Task task = taskSession.getTask(taskId);
        // //
        // // List args = new ArrayList(1);
        // // args.add(task);
        // // Command resultsCmnd = new Command(cmd.getId(),
        // CommandName.GetTaskResponse, args);
        // // session.write(resultsCmnd);
        //
        //
        // } catch (RuntimeException e) {
        // systemEventListener.exception(e.getMessage(),e);
        //
        // List<Object> list = new ArrayList<Object>(1);
        // list.add(e);
        // Command resultsCmnd = new Command(cmd.getId(), response, list);
        // session.write(resultsCmnd);
        // } finally {
        // taskSession.dispose();
        // }
    }

    @Override
    public void sessionIdle(IoSession session,
                            IdleStatus status) throws Exception {
        systemEventListener.debug( "Server IDLE " + session.getIdleCount( status ) );
    }

}
