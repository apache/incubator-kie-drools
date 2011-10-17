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
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.DefaultMarshaller;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.GlobalResolver;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {
    public static <T> T serializeObject(T obj) throws IOException,
                                              ClassNotFoundException {
        return serializeObject( obj,
                                null );
    }

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
        GlobalResolver globalResolver = session.getGlobalResolver();
        byte [] serializedSession = serializeStatefulSession(session);
        if( dispose ) { 
            session.dispose();
        }
        return deserializeStatefulSession(serializedSession, globalResolver, ruleBase);
    }
    
    public static byte [] serializeStatefulSession(StatefulSession session) 
        throws Exception { 
        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        ((ReteooStatefulSession)session).getTimerService();
        
        
        ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( session );
        out.close();
        bos.close();
        
        // Get the bytes of the serialized object
        final byte[] serializedObjectByteArray = bos.toByteArray();
        return serializedObjectByteArray;
    }
    
    public static StatefulSession deserializeStatefulSession(byte [] b1, GlobalResolver globalResolver, RuleBase ruleBase) 
        throws Exception { 

        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );
        StatefulSession session = ruleBase.newStatefulSession( bais );
        bais.close();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( bos );
        out.writeObject( session );
        out.close();
        bos.close();

        final byte[] b2 = bos.toByteArray();

        // bytes should be the same.
        if ( !areByteArraysEqual( b1,
                                  b2 ) ) {
            throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
        }

        session.setGlobalResolver( globalResolver );

        return session;
    }

   

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(StatefulKnowledgeSession ksession,
                                                                                 boolean dispose) throws Exception {

        // OCRAM: test this!
        DefaultMarshaller marshaller = ( DefaultMarshaller ) MarshallerFactory.newMarshaller( ksession.getKnowledgeBase(),
                                                                                              (ObjectMarshallingStrategy[])ksession.getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES) );

        long time = ksession.getSessionClock().getCurrentTime();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshall( bos,
                             ksession,
                             time );
        final byte[] b1 = bos.toByteArray();
        bos.close();
       
        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );
        StatefulKnowledgeSession ksession2 = marshaller.unmarshall( bais,
                                                                    new SessionConfiguration(),
                                                                     ksession.getEnvironment());
        bais.close();

        bos = new ByteArrayOutputStream();
        marshaller.marshall( bos,
                             ksession2,
                             time );
        final byte[] b2 = bos.toByteArray();
        bos.close();

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
        System.out.println( "\n" );
        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                System.out.println( "Difference at " + i + ": [" + b1[i] + "] != [" + b2[i] + "]" );
                return false;
            }
        }

        return true;
    }
    
}
