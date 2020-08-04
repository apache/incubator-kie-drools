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

package org.jbpm.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.drools.core.marshalling.impl.KogitoProtobufMarshaller;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.core.marshalling.impl.ReadSessionResult;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {

    public static <T> T serializeObject(final T obj) throws IOException,
            ClassNotFoundException {
        return serializeObject(obj, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T serializeObject(final T obj,
                                        final ClassLoader classLoader) throws IOException,
            ClassNotFoundException {
        return (T) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut(obj), classLoader);
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession( final KieSession ksession,
                                                                                  final boolean dispose) throws Exception {
        return getSerialisedStatefulKnowledgeSession(ksession,
                                                     dispose,
                                                     true);
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession( final KieSession ksession,
                                                                                  final boolean dispose,
                                                                                  final boolean testRoundTrip) throws Exception {
        return getSerialisedStatefulKnowledgeSession(ksession, ksession.getKieBase(), dispose);
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession( final KieSession ksession,
                                                                                  final KieBase kbase,
                                                                                  final boolean dispose) throws Exception {
        return getSerialisedStatefulKnowledgeSessionWithMessage(ksession, kbase, dispose).getSession();
    }

    public static ReadSessionResult getSerialisedStatefulKnowledgeSessionWithMessage( final KieSession ksession,
                                                                                      final KieBase kbase,
                                                                                      final boolean dispose) throws Exception {
        ObjectMarshallingStrategy[] strategies = ( ObjectMarshallingStrategy[]) ksession.getEnvironment().get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
        final ProtobufMarshaller marshaller = new KogitoProtobufMarshaller(kbase, new MarshallingConfigurationImpl( strategies, true, true ));
        final long time = ksession.getSessionClock().getCurrentTime();
        // make sure globas are in the environment of the session
        ksession.getEnvironment().set( EnvironmentName.GLOBALS, ksession.getGlobals());

        // Serialize object
        final byte[] serializedObject;
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            marshaller.marshall(bos, ksession, time);
            serializedObject = bos.toByteArray();
        }

        // Deserialize object
        final ReadSessionResult readSessionResult;
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(serializedObject)) {
            readSessionResult = marshaller.unmarshallWithMessage(bais,
                                                                 ksession.getSessionConfiguration(),
                                                                 ksession.getEnvironment());
        }

        if (dispose) {
            ksession.dispose();
        }

        return readSessionResult;
    }
}
