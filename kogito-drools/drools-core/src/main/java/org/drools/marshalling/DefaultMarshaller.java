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

public class DefaultMarshaller implements Marshaller {
    GlobalResolver                     globalResolver;
    private RuleBaseConfiguration      config;
    PlaceholderResolverStrategyFactory factory;

    public DefaultMarshaller() {
        this( null );
    }
    
    public DefaultMarshaller(RuleBaseConfiguration config) {
        this( config, null);
    }    

    public DefaultMarshaller(RuleBaseConfiguration config,
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

    /* (non-Javadoc)
     * @see org.drools.marshalling.Marshaller#read(java.io.InputStream, org.drools.common.InternalRuleBase, int, org.drools.concurrent.ExecutorService)
     */
    public ReteooStatefulSession read(final InputStream stream,
                                      final InternalRuleBase ruleBase,
                                      final int id,
                                      final ExecutorService executor) throws IOException,
                                                                     ClassNotFoundException {
        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                         ruleBase,
                                                                         RuleBaseNodes.getNodeMap( ruleBase ),
                                                                         factory );

        ReteooStatefulSession session = InputMarshaller.readSession( context,
                                           id,
                                           executor );
        context.close();
        return session;
        
    }

    /* (non-Javadoc)
     * @see org.drools.marshalling.Marshaller#write(java.io.OutputStream, org.drools.common.InternalRuleBase, org.drools.StatefulSession)
     */
    public void write(final OutputStream stream,
                      final InternalRuleBase ruleBase,
                      final StatefulSession session) throws IOException {
        MarshallerWriteContext context = new MarshallerWriteContext( stream,
                                                                           ruleBase,
                                                                           (InternalWorkingMemory) session,
                                                                           RuleBaseNodes.getNodeMap( ruleBase ),
                                                                           this.factory );
        OutputMarshaller.writeSession( context );
        context.close();
    }

}
