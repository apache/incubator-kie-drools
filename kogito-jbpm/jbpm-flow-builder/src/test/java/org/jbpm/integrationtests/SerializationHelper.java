package org.jbpm.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.kie.marshalling.Marshaller;
import org.kie.marshalling.MarshallerFactory;
import org.kie.marshalling.ObjectMarshallingStrategy;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {
    public static <T> T serializeObject(T obj) throws IOException,
                                              ClassNotFoundException {
        return serializeObject( obj,
                                null );
    }

    @SuppressWarnings("unchecked")
	public static <T> T serializeObject(T obj,
                                        ClassLoader classLoader) throws IOException,
                                                                ClassNotFoundException {
        return (T) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( obj ),
                                               classLoader );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session) throws Exception {
        return getSerialisedStatefulSession( session,
                                             true );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               RuleBase ruleBase) throws Exception {
        return getSerialisedStatefulSession( session,
                                             ruleBase,
                                             true );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               boolean dispose) throws Exception {
        return getSerialisedStatefulSession( session,
                                             session.getRuleBase(),
                                             dispose );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               RuleBase ruleBase,
                                                               boolean dispose) throws Exception {
        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( session );
        out.close();
        bos.close();

        // Get the bytes of the serialized object
        final byte[] b1 = bos.toByteArray();        

        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );
        StatefulSession session2 = ruleBase.newStatefulSession( bais, true, ((ReteooStatefulSession)session).getSessionConfiguration() );
        bais.close();

        bos = new ByteArrayOutputStream();
        out = new ObjectOutputStream( bos );
        out.writeObject( session2 );
        out.close();
        bos.close();

        final byte[] b2 = bos.toByteArray();

        // bytes should be the same.
        if ( !areByteArraysEqual( b1,
                                  b2 ) ) {
            throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
        }

        session2.setGlobalResolver( session.getGlobalResolver() );

        if ( dispose ) {
            session.dispose();
        }

        return session2;
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(StatefulKnowledgeSession ksession,
                                                                                 boolean dispose) throws Exception {

        return getSerialisedStatefulKnowledgeSession( ksession,
                                                      MarshallerFactory.newSerializeMarshallingStrategy(),
                                                      dispose );
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(StatefulKnowledgeSession ksession,
                                                                                 ObjectMarshallingStrategy strategy,
                                                                                 boolean dispose) throws Exception {

        ObjectMarshallingStrategy [] strategies = new ObjectMarshallingStrategy[] { strategy }; 
        
        return getSerialisedStatefulKnowledgeSession(ksession, strategies, dispose);
    }
    
    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(StatefulKnowledgeSession ksession,
                                                                                 ObjectMarshallingStrategy [] strategies,
                                                                                 boolean dispose) throws Exception {
       
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase(), strategies );
        
        final byte [] b1 = serializeKnowledgeSession(marshaller, ksession);
        StatefulKnowledgeSession ksession2 = deserializeKnowledgeSession(marshaller, b1);
       
        final byte[] b2 = serializeKnowledgeSession(marshaller, ksession2);

        // bytes should be the same.
        if ( !areByteArraysEqual( b1,
                                  b2 ) ) {
            throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
        }

        ((StatefulKnowledgeSessionImpl) ksession2).session.setGlobalResolver( ((StatefulKnowledgeSessionImpl) ksession).session.getGlobalResolver() );

        if ( dispose ) {
            ksession.dispose();
        }

        return ksession2;
    }

    public static byte [] serializeKnowledgeSession(Marshaller marshaller, 
                                                    StatefulKnowledgeSession ksession) 
                                                    throws Exception { 
       
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshall( bos, ksession );
        byte[] ksessionBytes = bos.toByteArray();
        bos.close();
        
        return ksessionBytes;
    }
    
    public static StatefulKnowledgeSession deserializeKnowledgeSession(Marshaller marshaller, 
                                                                       byte [] serializedKsession) 
                                                                       throws Exception {
        
        ByteArrayInputStream bais = new ByteArrayInputStream( serializedKsession );
        StatefulKnowledgeSession deserializedKsession = marshaller.unmarshall( bais,
                                                                    new SessionConfiguration(),
                                                                    EnvironmentFactory.newEnvironment() );
        bais.close();
        
        return deserializedKsession;
    } 
    
    public static boolean areByteArraysEqual(byte[] b1,
                                             byte[] b2) {
        if ( b1.length != b2.length ) {
            System.out.println( "Different length: b1=" + b1.length + " b2=" + b2.length );
            return false;
        }

        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                System.out.println( "Difference at " + i + ": [" + b1[i] + "] != [" + b2[i] + "]" );
                return false;
            }
        }

        return true;
    }
    
}
