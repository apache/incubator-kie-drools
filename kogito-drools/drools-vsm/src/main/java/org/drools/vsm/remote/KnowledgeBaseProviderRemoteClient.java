package org.drools.vsm.remote;

import java.util.Properties;
import java.util.UUID;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseProvider;
import org.drools.command.FinishedCommand;
import org.drools.command.NewKnowledgeBaseCommand;
import org.drools.command.SetVariableCommand;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.vsm.Message;
import org.drools.vsm.responsehandlers.BlockingMessageResponseHandler;

public class KnowledgeBaseProviderRemoteClient
    implements
    KnowledgeBaseProvider {
    private ServiceManagerRemoteClient serviceManager;

    public KnowledgeBaseProviderRemoteClient(ServiceManagerRemoteClient serviceManager) {
        this.serviceManager = serviceManager;
    }

    public Environment newEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBase newKnowledgeBase() {
        return newKnowledgeBase( ( KnowledgeBaseConfiguration ) null );
    }

    public KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        //return new NewKnowledgeBaseCommand(null);

        String localId = UUID.randomUUID().toString();

        Message msg = new Message( serviceManager.getSessionId(),
                                   serviceManager.counter.incrementAndGet(),
                                   false,
                                   new SetVariableCommand( "__TEMP__",
                                                           localId,
                                                           new NewKnowledgeBaseCommand( null ) ) );

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

        return new KnowledgeBaseRemoteClient( localId,
                                              serviceManager );

    }

    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration(Properties properties,
                                                                    ClassLoader classLoader) {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration(Properties properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId) {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId,
                                          KnowledgeBaseConfiguration conf) {
        // TODO Auto-generated method stub
        return null;
    }

}
