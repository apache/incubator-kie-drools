package org.drools.vsm.remote;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseProvider;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.command.Command;
import org.drools.command.FinishedCommand;
import org.drools.command.KnowledgeContextResolveFromContextCommand;
import org.drools.command.vsm.LookupCommand;
import org.drools.command.vsm.RegisterCommand;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.Environment;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.vsm.GenericConnector;
import org.drools.vsm.HumanTaskServiceProvider;
import org.drools.vsm.Message;
import org.drools.vsm.ServiceManager;
import org.drools.vsm.command.ServiceManagerClientConnectCommand;

public class ServiceManagerRemoteClient
    implements
    ServiceManager {
	private String name;

	public GenericConnector client;

    private List<GenericConnector> services;

    public AtomicInteger    counter;

    private int sessionId = -1;


    public ServiceManagerRemoteClient(String name, GenericConnector client) {
    	this.name = name;
    	this.client = client;
    	this.counter = new AtomicInteger();
    }
    
    public ServiceManagerRemoteClient(String name, GenericConnector client, List<GenericConnector> services) {
        this(name, client);
        this.services = services;
    }

	public boolean connect() {
        boolean connected = this.client.connect();

        if ( connected ) {
            String commandId = "serviceManager.connected" + getNextId();
            String kresultsId = "kresults_" + getSessionId();

            Message msg = new Message( -1,
                                       counter.incrementAndGet(),
                                       false,
                                       new KnowledgeContextResolveFromContextCommand( new ServiceManagerClientConnectCommand( commandId ),
                                                                                      null,
                                                                                      null,
                                                                                      null,
                                                                                      kresultsId ) );

            try {
                Object object = client.write( msg ).getPayload();

                if ( object == null ) {
                    throw new RuntimeException( "Response was not correctly received" );
                }

                sessionId = (Integer) ((ExecutionResults) object).getValue( commandId );

                connected = true;
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to execute message",
                                            e );
            }
        }
        // Connecting with services
        for (GenericConnector connector : services){
            boolean serviceConnected = connector.connect();
              if ( serviceConnected )
            	  System.out.println("Service Connected");
        }

        return connected;
    }

	public void disconnect() {
		this.client.disconnect();
		for (GenericConnector connector : this.services)
			connector.disconnect();
	}

    public KnowledgeBuilderProvider getKnowledgeBuilderFactory() {
        return new KnowledgeBuilderProviderRemoteClient( this );
    }

    public KnowledgeBaseProvider getKnowledgeBaseFactory() {
        return new KnowledgeBaseProviderRemoteClient( this );
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

    public void register(String identifier,
                         CommandExecutor executor) {
        String commandId = "client.lookup" + getNextId();
        String kresultsId = "kresults_" + getSessionId();

        int type;
        if ( executor instanceof StatefulKnowledgeSession ) {
            type = 0;
        } else {
            throw new IllegalArgumentException( "Type is not supported for registration" );
        }

        Message msg = new Message( getSessionId(),
                                   counter.incrementAndGet(),
                                   false,
                                   new KnowledgeContextResolveFromContextCommand( new RegisterCommand( identifier,
                                                                                                       ((StatefulKnowledgeSessionRemoteClient) executor).getInstanceId(),
                                                                                                       type ),
                                                                                  null,
                                                                                  null,
                                                                                  null,
                                                                                  null ) );

        try {
            Object object = client.write( msg ).getPayload();

            if ( !(object instanceof FinishedCommand) ) {
                throw new RuntimeException( "Response was not correctly ended" );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to execute message",
                                        e );
        }
    }

    public CommandExecutor lookup(String identifier) {
        String commandId = "client.lookup" + getNextId();
        String kresultsId = "kresults_" + getSessionId();

        Message msg = new Message( getSessionId(),
                                   counter.incrementAndGet(),
                                   false,
                                   new KnowledgeContextResolveFromContextCommand( new LookupCommand( identifier,
                                                                                                     commandId ),
                                                                                  null,
                                                                                  null,
                                                                                  null,
                                                                                  kresultsId ) );

        try {
            Object object = client.write( msg ).getPayload();

            if ( object == null ) {
                throw new RuntimeException( "Response was not correctly received" );
            }
            String value = (String) ((ExecutionResults) object).getValue( commandId );
            String type = String.valueOf( value.charAt( 0 ) );
            String instanceId = value.substring( 2 );

            CommandExecutor executor = null;
            switch ( Integer.parseInt( type ) ) {
                case 0 : {
                    executor = new StatefulKnowledgeSessionRemoteClient( instanceId,
                                                                         this );
                    break;
                }
                default : {

                }

            }

            return executor;
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to execute message",
                                        e );
        }
    }

    public void release(Object object) {
        // TODO Auto-generated method stub
    }

    public void release(String identifier) {
        // TODO Auto-generated method stub
    }

    public HumanTaskServiceProvider getHumanTaskService() {
    	return new HumanTaskServiceRemoteProviderImpl(this);
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

    public int getSessionId() {
        return sessionId;
    }

    public int getNextId() {
        return this.counter.incrementAndGet();
    }

    public ExecutionResults execute(Command command) {
        // TODO Auto-generated method stub
        return null;
    }

	public List<GenericConnector> getServices() {
		return services;
	}
	
	public String getName() {
		return this.name;
	}

}
