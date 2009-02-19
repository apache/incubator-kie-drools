package org.drools.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.concurrent.ExecutorService;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.Environment;
import org.drools.spi.GlobalResolver;

public class DefaultMarshaller
    implements
    Marshaller {
    GlobalResolver                     globalResolver;
    RuleBaseConfiguration              ruleBaseConfig;
    MarshallingConfiguration           marshallingConfig;
    PlaceholderResolverStrategyFactory factory;

    public DefaultMarshaller() {
        this( null );
    }

    public DefaultMarshaller(RuleBaseConfiguration config) {
        this( config,
              new MarshallingConfigurationImpl() );
    }

    public DefaultMarshaller(RuleBaseConfiguration ruleBaseConfig,
                             MarshallingConfiguration marshallingConfig) {
        this.ruleBaseConfig = (ruleBaseConfig != null) ? ruleBaseConfig : new RuleBaseConfiguration();
        this.marshallingConfig = marshallingConfig;
        
        if ( marshallingConfig.getPlaceholderResolverStrategyFactory() == null ) {
            this.factory = new PlaceholderResolverStrategyFactory();
            ClassPlaceholderResolverStrategyAcceptor acceptor = new ClassPlaceholderResolverStrategyAcceptor( "*.*" );
            IdentityPlaceholderResolverStrategy strategy = new IdentityPlaceholderResolverStrategy( acceptor );
            this.factory.addStrategy( strategy );
        } else {
            this.factory = marshallingConfig.getPlaceholderResolverStrategyFactory();
        }
        
    }

    /* (non-Javadoc)
     * @see org.drools.marshalling.Marshaller#read(java.io.InputStream, org.drools.common.InternalRuleBase, int, org.drools.concurrent.ExecutorService)
     */
    public ReteooStatefulSession read(final InputStream stream,
                                      final InternalRuleBase ruleBase,
                                      final int id,
                                      final ExecutorService executor,
                                      final SessionConfiguration config,
                                      final Environment environment) throws IOException,
                                                                     ClassNotFoundException {
        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                       ruleBase,
                                                                       RuleBaseNodes.getNodeMap( ruleBase ),
                                                                       factory,
                                                                       marshallingConfig.isMarshallProcessInstances(),
                                                                       marshallingConfig.isMarshallWorkItems() );

        ReteooStatefulSession session = InputMarshaller.readSession( context,
                                                                     id,
                                                                     executor,
                                                                     environment,
                                                                     config );
        context.close();
        return session;

    }

    public StatefulSession read(final InputStream stream,
                                final InternalRuleBase ruleBase,
                                StatefulSession session) throws IOException,
                                                        ClassNotFoundException {
        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                       ruleBase,
                                                                       RuleBaseNodes.getNodeMap( ruleBase ),
                                                                       factory,
                                                                       marshallingConfig.isMarshallProcessInstances(),
                                                                       marshallingConfig.isMarshallWorkItems());

        session = InputMarshaller.readSession( (ReteooStatefulSession) session,
                                               context );
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
                                                                     this.factory,
                                                                     marshallingConfig.isMarshallProcessInstances(),
                                                                     marshallingConfig.isMarshallWorkItems() );
        OutputMarshaller.writeSession( context );
        context.close();
    }

}
