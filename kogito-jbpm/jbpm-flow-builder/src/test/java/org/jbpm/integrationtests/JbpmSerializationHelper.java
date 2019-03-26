/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.integrationtests;

import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.KieSession;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class JbpmSerializationHelper extends SerializationHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(JbpmSerializationHelper.class);
    
    public static <T> T serializeObject(T obj) throws IOException,
                                              ClassNotFoundException {
        return serializeObject( obj,
                                null );
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(KieSession ksession) throws Exception { 
        return SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T serializeObject(T obj,
                                        ClassLoader classLoader) throws IOException,
                                                                ClassNotFoundException {
        return (T) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( obj ),
                                               classLoader );
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
       
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKieBase(), strategies );
        
        final byte [] b1 = serializeKnowledgeSession(marshaller, ksession);
        StatefulKnowledgeSession ksession2 = deserializeKnowledgeSession(marshaller, b1);
       
        final byte[] b2 = serializeKnowledgeSession(marshaller, ksession2);

        // bytes should be the same.
        if ( !areByteArraysEqual( b1,
                                  b2 ) ) {
//            throw new IllegalArgumentException( "byte streams for serialisation test are not equal" );
        }

        ((StatefulKnowledgeSessionImpl) ksession2).setGlobalResolver( ((StatefulKnowledgeSessionImpl) ksession).getGlobalResolver() );

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
        StatefulKnowledgeSession deserializedKsession = (StatefulKnowledgeSession)
    		marshaller.unmarshall( bais,
                                   SessionConfiguration.newInstance(),
                                   EnvironmentFactory.newEnvironment() );
        bais.close();
        
        return deserializedKsession;
    } 
    
    public static boolean areByteArraysEqual(byte[] b1,
                                             byte[] b2) {
        if ( b1.length != b2.length ) {
            logger.info( "Different length: b1={} b2={}", b1.length, b2.length );
            return false;
        }

        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                logger.info( "Difference at {} : [{}] != [{}]", i, b1[i], b2[i]);
                return false;
            }
        }

        return true;
    }
    
}
