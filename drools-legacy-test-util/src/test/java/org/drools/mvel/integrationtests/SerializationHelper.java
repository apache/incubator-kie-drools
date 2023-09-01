package org.drools.mvel.integrationtests;

import java.io.IOException;

import org.drools.core.util.DroolsStreamUtils;
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
        return (T) DroolsStreamUtils.streamIn(DroolsStreamUtils.streamOut(obj), classLoader);
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession( final KieSession ksession,
                                                                                  final boolean dispose) throws Exception {
        return (StatefulKnowledgeSession)ksession;
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(final KieSession ksession,
                                                                                 final boolean dispose,
                                                                                 final boolean testRoundTrip) throws Exception {
        return (StatefulKnowledgeSession)ksession;
    }

}
