package org.drools.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.ProtobufMarshaller;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.time.SessionClock;

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

    /**
     * @deprecated This method can't handle serialization of globals to a deficiency of the API. 
     *             Please use getSerialisedStatefulKnowledgeSession() instead.
     */
    @Deprecated
    public static StatefulSession getSerialisedStatefulSession(StatefulSession session) throws Exception {
        return getSerialisedStatefulSession( session,
                                             true );
    }

    /**
     * @deprecated This method can't handle serialization of globals to a deficiency of the API. 
     *             Please use getSerialisedStatefulKnowledgeSession() instead.
     */
    @Deprecated
    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               RuleBase ruleBase) throws Exception {
        return getSerialisedStatefulSession( session,
                                             ruleBase,
                                             true );
    }

    /**
     * @deprecated This method can't handle serialization of globals to a deficiency of the API. 
     *             Please use getSerialisedStatefulKnowledgeSession() instead.
     */
    @Deprecated
    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               boolean dispose) throws Exception {
        return getSerialisedStatefulSession( session,
                                             session.getRuleBase(),
                                             dispose );
    }

    /**
     * @deprecated This method can't handle serialization of globals to a deficiency of the API. 
     *             Please use getSerialisedStatefulKnowledgeSession() instead.
     */
    @Deprecated
    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               RuleBase ruleBase,
                                                               boolean dispose) throws Exception {
        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        ((ReteooStatefulSession)session).getTimerService();
        
        
        ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( session );
        out.close();
        bos.close();
        
        // Get the bytes of the serialized object
        final byte[] b1 = bos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );
        StatefulSession session2 = ruleBase.newStatefulSession( bais );
        bais.close();

        // Reserialize and check that byte arrays are the same
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
                                                      dispose, 
                                                      true );
        
    }
   

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(StatefulKnowledgeSession ksession,
                                                                                 boolean dispose,
                                                                                 boolean testRoundTrip ) throws Exception {

        ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller( ksession.getKnowledgeBase(),
                                                                 (ObjectMarshallingStrategy[])ksession.getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES) );
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
        {
            ByteArrayInputStream bais = new ByteArrayInputStream( b1 );
            ksession2 = marshaller.unmarshall( bais,
                    new SessionConfiguration(),
                    ksession.getEnvironment());
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
                throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
            }

//            ((StatefulKnowledgeSessionImpl) ksession2).session.getFactHandleFactory().clear( current_id, current_recency );
//            ((StatefulKnowledgeSessionImpl) ksession2).session.setGlobalResolver( ((StatefulKnowledgeSessionImpl) ksession).session.getGlobalResolver() );
            
        }

        if ( dispose ) {
            ksession.dispose();
        }

        return ksession2;
    }

    public static boolean areByteArraysEqual(byte[] b1,
                                             byte[] b2) {
        
        if ( b1.length != b2.length ) {
            System.out.println( "Different length: b1=" + b1.length + " b2=" + b2.length );
            return false;
        }
        
//        System.out.println( "b1" );
//        for ( int i = 0, length = b1.length; i < length; i++ ) {
//            if ( i == 81 ) {
//                System.out.print( "!" );    
//            }
//            System.out.print( b1[i] );
//            if ( i == 83 ) {
//                System.out.print( "!" );    
//            }            
//        }
//        
//        System.out.println( "\nb2" );
//        for ( int i = 0, length = b2.length; i < length; i++ ) {
//            if ( i == 81 ) {
//                System.out.print( "!" );    
//            }
//            System.out.print( b2[i] );
//            if ( i == 83 ) {
//                System.out.print( "!" );    
//            }   
//        }
        
        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                System.out.println( "Difference at " + i + ": [" + b1[i] + "] != [" + b2[i] + "]" );
                return false;
            }
        }

        return true;
    }
    
}
