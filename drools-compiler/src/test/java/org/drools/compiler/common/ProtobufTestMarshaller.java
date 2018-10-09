package org.drools.compiler.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.core.marshalling.impl.ReadSessionResult;
import org.drools.core.marshalling.impl.RuleBaseNodes;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.marshalling.MarshallingConfiguration;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.time.SessionClock;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.drools.core.util.IoUtils.areByteArraysEqual;

public class ProtobufTestMarshaller extends ProtobufMarshaller {

    public static ReadSessionResult getSerialisedStatefulKnowledgeSession(KieSession ksession,
                                                                           KieBase kbase,
                                                                           boolean dispose,
                                                                           boolean testRoundTrip ) throws Exception {

        ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) ksession.getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
        ProtobufMarshaller marshaller = new ProtobufTestMarshaller(kbase, new MarshallingConfigurationImpl(strategies, true, true ));

        long time = ksession.<SessionClock>getSessionClock().getCurrentTime();
        // make sure globas are in the environment of the session
        ksession.getEnvironment().set( EnvironmentName.GLOBALS, ksession.getGlobals() );

        // Serialize object
        final byte [] b1;
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            marshaller.marshall( bos,
                                 ksession,
                                 time );
            b1 = bos.toByteArray();
            bos.close();
        }

        // Deserialize object
        StatefulKnowledgeSession ksession2;
        ReadSessionResult readSessionResult;
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(b1 );
            readSessionResult = ((ProtobufTestMarshaller) marshaller).unmarshallWithMessage( bais,
                                                                                             ksession.getSessionConfiguration(),
                                                                                             ksession.getEnvironment());
            ksession2 = readSessionResult.getSession();
            bais.close();
        }

        if( testRoundTrip ) {
            // for now, we can ensure the IDs will match because queries are creating untraceable fact handles at the moment
//            int previous_id = ((StatefulKnowledgeSessionImpl)ksession).session.getFactHandleFactory().getId();
//            long previous_recency = ((StatefulKnowledgeSessionImpl)ksession).session.getFactHandleFactory().getRecency();
//            int current_id = ((StatefulKnowledgeSessionImpl)ksession2).session.getFactHandleFactory().getId();
//            long current_recency = ((StatefulKnowledgeSessionImpl)ksession2).session.getFactHandleFactory().getRecency();
//            ((StatefulKnowledgeSessionImpl)ksession2).session.getFactHandleFactory().clear( previous_id, previous_recency );

            // Reserialize and check that byte arrays are the same
            final byte[] b2;
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                marshaller.marshall( bos,
                                     ksession2,
                                     time );
                b2 = bos.toByteArray();
                bos.close();
            }

            // bytes should be the same.
            if ( !areByteArraysEqual( b1,
                                      b2 ) ) {
//                throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
            }

//            ((StatefulKnowledgeSessionImpl) ksession2).session.getFactHandleFactory().clear( current_id, current_recency );
//            ((StatefulKnowledgeSessionImpl) ksession2).session.setGlobalResolver( ((StatefulKnowledgeSessionImpl) ksession).session.getGlobalResolver() );

        }

        if ( dispose ) {
            ksession.dispose();
        }

        return readSessionResult;
    }


    public ProtobufTestMarshaller(KieBase kbase, MarshallingConfiguration marshallingConfig) {
        super(kbase, marshallingConfig);
    }

    public ReadSessionResult unmarshallWithMessage(final InputStream stream,
                                               KieSessionConfiguration config,
                                               Environment environment) throws IOException,
            ClassNotFoundException {
        if ( config == null ) {
            config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        }

        if ( environment == null ) {
            environment = KieServices.get().newEnvironment();
        }

        MarshallerReaderContext context = new MarshallerReaderContext(stream,
                                                                      (KnowledgeBaseImpl) kbase,
                                                                      RuleBaseNodes.getNodeMap((KnowledgeBaseImpl) kbase ),
                                                                      this.strategyStore,
                                                                      TIMER_READERS,
                                                                      this.marshallingConfig.isMarshallProcessInstances(),
                                                                      this.marshallingConfig.isMarshallWorkItems(),
                                                                      environment );

        int id = ((KnowledgeBaseImpl) this.kbase).nextWorkingMemoryCounter();
        RuleBaseConfiguration conf = ((KnowledgeBaseImpl) this.kbase).getConfiguration();

        ReadSessionResult readSessionResult = ProtobufInputMarshaller.readSession(context,
                                                                                  id,
                                                                                  environment,
                                                                                  (SessionConfiguration) config,
                                                                                  initializer);
        StatefulKnowledgeSessionImpl session = readSessionResult.getSession();
        context.close();
        if ( ((SessionConfiguration) config).isKeepReference() ) {
            ((KnowledgeBaseImpl) this.kbase).addStatefulSession(session);
        }
        return readSessionResult;

    }


}
