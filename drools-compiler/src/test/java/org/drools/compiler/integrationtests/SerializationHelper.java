/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionClock;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(KieSession ksession,
                                                                                 boolean dispose) throws Exception {
//        if ( ((ReteooRuleBase)((KnowledgeBaseImpl) (ksession.getKieBase())).getRuleBase()).getConfiguration().isPhreakEnabled() ) {
//            return ksession;
//        }

        return getSerialisedStatefulKnowledgeSession( ksession, 
                                                      dispose, 
                                                      true );
        
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(KieSession ksession,
                                                                                 boolean dispose,
                                                                                 boolean testRoundTrip ) throws Exception {
        return getSerialisedStatefulKnowledgeSession( ksession,ksession.getKieBase(), dispose, testRoundTrip );
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(KieSession ksession,
                                                                                 KieBase kbase,
                                                                                 boolean dispose ) throws Exception {
        return getSerialisedStatefulKnowledgeSession( ksession, kbase, dispose, true );
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(KieSession ksession,
                                                                                 KieBase kbase,
                                                                                 boolean dispose,
                                                                                 boolean testRoundTrip ) throws Exception {
//        if ( ((ReteooRuleBase)((KnowledgeBaseImpl) (ksession.getKieBase())).getRuleBase()).getConfiguration().isPhreakEnabled() ) {
//            return ksession;
//        }
        
        ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller( kbase,
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
                                               ksession.getSessionConfiguration(),
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
//                throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
            }

//            ((StatefulKnowledgeSessionImpl) ksession2).session.getFactHandleFactory().clear( current_id, current_recency );
//            ((StatefulKnowledgeSessionImpl) ksession2).session.setGlobalResolver( ((StatefulKnowledgeSessionImpl) ksession).session.getGlobalResolver() );
            
        }

        if ( dispose ) {
            ksession.dispose();
        }

        return ksession2;
    }

    private static boolean areByteArraysEqual(byte[] b1, byte[] b2) {

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

        boolean result = true;
        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                System.out.println( "Difference at " + i + ": [" + b1[i] + "] != [" + b2[i] + "]" );
                result = false;
            }
        }

        return result;
    }
    
}
