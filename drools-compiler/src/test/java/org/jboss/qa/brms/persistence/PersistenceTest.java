package org.jboss.qa.brms.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.MarshallerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jboss.qa.brms.persistence.MessageEvent.Type;

public class PersistenceTest {
    private static File storage;
    private static KnowledgeBase kbase;

    public static void main(String[] args) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(PersistenceTest.class.getResourceAsStream("lifecycle.drl")), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newInputStreamResource(PersistenceTest.class.getResourceAsStream("cep-lifecycle.drl")), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }


        kbase = kbuilder.newKnowledgeBase();
        storage = File.createTempFile("session", "bin");

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        marshallSession(ksession);

        ksession = unmarshallSession();
        ksession.fireAllRules();
        marshallSession(ksession);

        ksession = unmarshallSession();
        MessageEvent event = new MessageEvent(Type.received, new Message("update"));
        ksession.getWorkingMemoryEntryPoint("EventStream").insert(event);
        marshallSession(ksession);

        ksession = unmarshallSession();
        ksession.getWorkingMemoryEntryPoint("EventStream").insert(event);
        ksession.fireAllRules();
        marshallSession(ksession);
    }

    private static final void marshallSession(StatefulKnowledgeSession ksession) throws Exception {
        OutputStream os = new FileOutputStream(storage);
        MarshallerFactory.newMarshaller(kbase).marshall(os, ksession);
        os.close();
    }

    private static final StatefulKnowledgeSession unmarshallSession() throws Exception {
        InputStream is = new FileInputStream(storage);
        try {
            return MarshallerFactory.newMarshaller(kbase).unmarshall(is);
        } finally {
            is.close();
        }
    }
}
