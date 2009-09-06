package org.drools.vsm;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.transport.socket.SocketConnector;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.KnowledgeBaseProvider;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.command.Command;
import org.drools.command.builder.KnowledgeBuilderHasErrorsCommand;
import org.drools.command.distributed.ServiceManagerClientConnectCommand;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;
import org.drools.vsm.ServiceManager;
import org.drools.vsm.responsehandlers.BlockingMessageResponseHandler;

public class ServiceManagerClient  implements ServiceManager {
	 public BaseMinaClient client;
	 
	 public AtomicInteger counter;
    
    public ServiceManagerClient(String name,
                                BaseMinaHandler handler) {
        this.client = new BaseMinaClient( name, handler );
        this.counter = new AtomicInteger();
    }

    private int sessionId;
    
    public boolean connect(SocketConnector connector,
            				SocketAddress address) {
        boolean connected = this.client.connect(connector, address);
        
        if ( connected ) {
            Message msg = new Message( -1,
                                       counter.incrementAndGet(),
                                       false,
                                       null,
                                       new ServiceManagerClientConnectCommand() );

            BlockingMessageResponseHandler handler = new BlockingMessageResponseHandler();

            this.client.handler.addResponseHandler( msg.getResponseId(),
            										handler );

            this.client.session.write( msg );

            this.sessionId = (Integer) handler.getMessage().getPayload();
        }
        
        return connected;
    }
    
    public void disconnect() {
    	this.client.disconnect();
    }
    

    public KnowledgeBuilderProvider getKnowledgeBuilderFactory() {
        return new KnowledgeBuilderProviderClient( this );
    }
    
    public KnowledgeBaseProvider getKnowledgeBaseFactory() {
        return new KnowledgeBaseProviderClient( this );
    }    
    
    public KnowledgeAgentProvider getKnowledgeAgentFactory() {
        // TODO Auto-generated method stub
        return null;
    }    
    
    public JPAKnowledgeServiceProvider JPAKnowledgeService() {
        // TODO Auto-generated method stub
        return null;
    }

    public Environment getEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<String> list() {
        // TODO Auto-generated method stub
        return null;
    }

    public CommandExecutor lookup(String identifer) {
        // TODO Auto-generated method stub
        return null;
    }

    public void register(String identifier,
                         CommandExecutor executor) {
        // TODO Auto-generated method stub
        
    }

    public void release(Object object) {
        // TODO Auto-generated method stub
        
    }

    public void release(String identifier) {
        // TODO Auto-generated method stub
        
    }
    
    public static class RemoveKnowledgeBuilderProvider implements KnowledgeBuilderProvider {

        public DecisionTableConfiguration newDecisionTableConfiguration() {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                    KnowledgeBuilderConfiguration conf) {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
            // TODO Auto-generated method stub
            return null;
        }

        public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                              ClassLoader classLoader) {
            // TODO Auto-generated method stub
            return null;
        }        
    }
    
    public void send(Command command) {
        this.client.session.write( command );
    }

    public int getSessionId() {
        return sessionId;
    }
    
    public int getNextId() {
        return this.counter.incrementAndGet();
    }
    
    

}
