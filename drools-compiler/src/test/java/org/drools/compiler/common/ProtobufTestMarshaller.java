package org.drools.compiler.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.drools.core.SessionConfiguration;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.core.marshalling.impl.ReadSessionResult;
import org.drools.core.marshalling.impl.RuleBaseNodes;
import org.kie.api.KieBase;
import org.kie.api.marshalling.MarshallingConfiguration;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;

public class ProtobufTestMarshaller extends ProtobufMarshaller {

    public static ReadSessionResult getSerialisedStatefulKnowledgeSession(KieSession ksession,
                                                                          KieBase kbase) throws Exception {

        ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) ksession.getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
        ProtobufMarshaller marshaller = new ProtobufTestMarshaller(kbase, new MarshallingConfigurationImpl(strategies, true, true));

        long time = ksession.getSessionClock().getCurrentTime();
        ksession.getEnvironment().set(EnvironmentName.GLOBALS, ksession.getGlobals());

        final byte[] serializedObject;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            marshaller.marshall(bos,
                                ksession,
                                time);
            serializedObject = bos.toByteArray();
        }

        ReadSessionResult readSessionResult;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedObject)) {

            readSessionResult = ((ProtobufTestMarshaller) marshaller).unmarshallWithMessage(bais,
                                                                                            ksession.getSessionConfiguration(),
                                                                                            ksession.getEnvironment());
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

        MarshallerReaderContext context = new MarshallerReaderContext(stream,
                                                                      (KnowledgeBaseImpl) kbase,
                                                                      RuleBaseNodes.getNodeMap((KnowledgeBaseImpl) kbase),
                                                                      this.strategyStore,
                                                                      TIMER_READERS,
                                                                      this.marshallingConfig.isMarshallProcessInstances(),
                                                                      this.marshallingConfig.isMarshallWorkItems(),
                                                                      environment);

        int id = ((KnowledgeBaseImpl) this.kbase).nextWorkingMemoryCounter();

        ReadSessionResult readSessionResult = ProtobufInputMarshaller.readSession(context,
                                                                                  id,
                                                                                  environment,
                                                                                  (SessionConfiguration) config,
                                                                                  initializer);
        context.close();
        return readSessionResult;
    }
}
