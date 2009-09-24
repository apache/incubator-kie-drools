package org.drools.vsm;

import java.util.Properties;
import java.util.UUID;

import org.apache.mina.core.future.WriteFuture;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseProvider;
import org.drools.command.NewKnowledgeBaseCommand;
import org.drools.command.SetVariableCommand;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;

public class KnowledgeBaseProviderClient
    implements
    KnowledgeBaseProvider {
    private ServiceManagerClient serviceManager;

    public KnowledgeBaseProviderClient(ServiceManagerClient serviceManager) {
        this.serviceManager = serviceManager;
    }

    public Environment newEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBase newKnowledgeBase() {
        return newKnowledgeBase( (KnowledgeBaseConfiguration) null );
    }

    public KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        //return new NewKnowledgeBaseCommand(null);

        String localId = UUID.randomUUID().toString();

        Message cmd = new Message( serviceManager.getSessionId(),
                                   serviceManager.counter.incrementAndGet(),
                                   false,
                                   null,
                                   new SetVariableCommand( "__TEMP__",
                                                           localId,
                                                           new NewKnowledgeBaseCommand( null ) ) );

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

        return new KnowledgeBaseClient( localId,
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
