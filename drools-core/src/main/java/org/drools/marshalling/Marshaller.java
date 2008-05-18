package org.drools.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.RuleBaseConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.concurrent.ExecutorService;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.spi.GlobalResolver;

public class Marshaller {
    GlobalResolver                     globalResolver;
    private RuleBaseConfiguration      config;
    PlaceholderResolverStrategyFactory factory;

    public Marshaller() {
        this( null );
    }
    
    public Marshaller(RuleBaseConfiguration config) {
        this( config, null);
    }    

    public Marshaller(RuleBaseConfiguration config,
                      PlaceholderResolverStrategyFactory factory) {
        this.config = (config != null) ? config : new RuleBaseConfiguration();
        
        if ( factory == null ) {
            this.factory = new PlaceholderResolverStrategyFactory();
            ClassPlaceholderResolverStrategyAcceptor acceptor = new ClassPlaceholderResolverStrategyAcceptor( "*.*" );
            IdentityPlaceholderResolverStrategy strategy = new IdentityPlaceholderResolverStrategy( acceptor );
            this.factory.addStrategy( strategy );            
        } else {
            this.factory = factory;
        }
    }

    public ReteooStatefulSession read(final InputStream stream,
                                      final InternalRuleBase ruleBase,
                                      final int id,
                                      final ExecutorService executor) throws IOException,
                                                                     ClassNotFoundException {
        WMSerialisationInContext context = new WMSerialisationInContext( stream,
                                                                         ruleBase,
                                                                         RuleBaseNodes.getNodeMap( ruleBase ),
                                                                         factory );

        ReteooStatefulSession session = InputPersister.readSession( context,
                                           id,
                                           executor );
        context.close();
        return session;
        
    }

    public void write(final OutputStream stream,
                      final InternalRuleBase ruleBase,
                      final StatefulSession session) throws IOException {
        WMSerialisationOutContext context = new WMSerialisationOutContext( stream,
                                                                           ruleBase,
                                                                           (InternalWorkingMemory) session,
                                                                           RuleBaseNodes.getNodeMap( ruleBase ),
                                                                           this.factory );
        OutputPersister.writeSession( context );
        context.close();
    }

}
