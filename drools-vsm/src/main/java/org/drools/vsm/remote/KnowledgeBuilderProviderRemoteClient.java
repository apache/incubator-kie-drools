/**
 * 
 */
package org.drools.vsm.remote;

import java.util.Properties;
import java.util.UUID;

import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.command.FinishedCommand;
import org.drools.command.SetVariableCommand;
import org.drools.command.builder.NewKnowledgeBuilderCommand;
import org.drools.vsm.Message;
import org.drools.vsm.responsehandlers.BlockingMessageResponseHandler;

public class KnowledgeBuilderProviderRemoteClient
    implements
    KnowledgeBuilderProvider {
    private ServiceManagerRemoteClient serviceManager;

    public KnowledgeBuilderProviderRemoteClient(ServiceManagerRemoteClient serviceManager) {
        this.serviceManager = serviceManager;
    }

    public DecisionTableConfiguration newDecisionTableConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBuilder newKnowledgeBuilder() {
        String localId = UUID.randomUUID().toString();

        Message msg = new Message( serviceManager.getSessionId(),
                                   serviceManager.counter.incrementAndGet(),
                                   false,
                                   new SetVariableCommand( "__TEMP__",
                                                           localId,
                                                           new NewKnowledgeBuilderCommand( null ) ) );

        BlockingMessageResponseHandler handler = new BlockingMessageResponseHandler();

        try {
            serviceManager.client.addResponseHandler( msg.getResponseId(),
                                                      handler );

            serviceManager.client.write( msg );

            Object object = handler.getMessage().getPayload();

            if ( !(object instanceof FinishedCommand) ) {
                throw new RuntimeException( "Response was not correctly ended" );
            }

        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to execute message",
                                        e );
        }

        return new KnowledgeBuilderRemoteClient( localId,
                                                 serviceManager );
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