package org.drools.testcoverage.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.drools.core.util.DroolsStreamUtils;
import org.drools.serialization.protobuf.ProtobufMarshaller;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {

    public static <T> T serializeObject(final T obj) throws IOException, ClassNotFoundException {
        return serializeObject(obj, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T serializeObject(final T obj, final ClassLoader classLoader) throws IOException, ClassNotFoundException {
        return (T) DroolsStreamUtils.streamIn(DroolsStreamUtils.streamOut(obj), classLoader);
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(final KieSession ksession,
                                                                                 final boolean dispose) throws IOException, ClassNotFoundException {
        return getSerialisedStatefulKnowledgeSession(ksession, ksession.getKieBase(), dispose);
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(final KieSession ksession,
                                                                                 final KieBase kbase,
                                                                                 final boolean dispose) throws IOException, ClassNotFoundException {
        final ProtobufMarshaller marshaller =
                (ProtobufMarshaller) MarshallerFactory.newMarshaller(
                        kbase,
                        (ObjectMarshallingStrategy[]) ksession.getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES));
        final long time = ksession.getSessionClock().getCurrentTime();
        // make sure globas are in the environment of the session
        ksession.getEnvironment().set(EnvironmentName.GLOBALS, ksession.getGlobals());

        // Serialize object
        final byte[] b1;
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            marshaller.marshall(bos, ksession, time);
            b1 = bos.toByteArray();
        }

        // Deserialize object
        final StatefulKnowledgeSession ksession2;

        try (final ByteArrayInputStream bais = new ByteArrayInputStream(b1)) {
            ksession2 = marshaller.unmarshall(bais, ksession.getSessionConfiguration(), ksession.getEnvironment());
        }

        if (dispose) {
            ksession.dispose();
        }

        return ksession2;
    }
}
