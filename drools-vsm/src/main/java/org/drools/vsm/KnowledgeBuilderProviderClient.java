/**
 * 
 */
package org.drools.vsm;

import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import org.apache.mina.core.future.WriteFuture;
import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.command.SetVariableCommand;
import org.drools.command.builder.KnowledgeBuilderAddCommand;
import org.drools.command.builder.NewKnowledgeBuilderCommand;
import org.drools.definition.KnowledgePackage;
import org.drools.vsm.responsehandlers.BlockingMessageResponseHandler;

import sun.misc.UUEncoder;

public class KnowledgeBuilderProviderClient
    implements
    KnowledgeBuilderProvider {
    private ServiceManagerClient serviceManager;

    public KnowledgeBuilderProviderClient(ServiceManagerClient serviceManager) {
        this.serviceManager = serviceManager;
    }

    public DecisionTableConfiguration newDecisionTableConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBuilder newKnowledgeBuilder() {
        String localId = UUID.randomUUID().toString();

        Message cmd = new Message( serviceManager.getSessionId(),
                                   serviceManager.counter.incrementAndGet(),
                                   false,
                                   null,
                                   new SetVariableCommand( "__TEMP__",
                                                           localId,
                                                           new NewKnowledgeBuilderCommand( null ) ) );

        WriteFuture future = serviceManager.client.session.write( cmd );
        try {
            int tries = 0;
            while ( !future.isDone() && tries++ < 6 ) {
                Thread.sleep( 500 );                
            }
            if ( !future.isDone() ) {
                throw new RuntimeException( "unable to create new KnowledgeBuilder" );
            }
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "unable to create new KnowledgeBuilder" );
        }

        return new KnowledgeBuilderClient( localId,
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