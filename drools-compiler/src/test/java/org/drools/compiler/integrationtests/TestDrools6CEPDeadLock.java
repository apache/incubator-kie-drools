package org.drools.compiler.integrationtests;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.marshalling.MarshallerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestDrools6CEPDeadLock implements RuleRuntimeEventListener {
    private KieSession ksession;
    private Marshaller marshaller;

    String drl = "package bpmn;\n" +
                 "import com.test.event.*;\n" +
                 "dialect \"mvel\"\n" +
                 "\n" +
                 "declare BusinessProcessName\n" +
                 "    name : String;\n" +
                 "end\n" +
                 "declare com.test.event.Test1\n" +
                 "    @role( event )\n" +
                 "end\n" +
                 "\n" +
                 "rule \"Insert Business Process Name\"\n" +
                 "salience 10\n" +
                 "when\n" +
                 "        not BusinessProcessName(name == \"SomeName\")\n" +
                 "then\n" +
                 "\tSystem.out.println(\"Insert Business Process Name\");\n" +
                 "        insert(new BusinessProcessName(\"SomeName\"));\n" +
                 "end\n" +
                 "\n" +
                 "rule \"Start event1\"\n" +
                 "    when\n" +
                 "           $event1 : Test1()\n" +
                 "    then\n" +
                 "    \t   System.out.println(\"Start event1 fired.\");\n" +
                 "end\n";

    @Test
    @Ignore
    public void test1() throws Exception {
        initSession();
        runFireUntilHaltThread();

        System.out.println("-= 1 =-");
        ksession.insert(new Test1());

        System.out.println("-= 2 =-");
        ksession.insert(new Test1());
    }

    private void runFireUntilHaltThread() {
        new Thread(new Runnable() {
            public void run() {
                ksession.fireUntilHalt();
            }
        }).start();
    }

    private void initSession() {
        KnowledgeBuilder kbuilder =
                KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ),
                     ResourceType.DRL);
        if (kbuilder.hasErrors())  throw new RuntimeException("Unable tocompile drl\".");
        KieBaseConfiguration config =
                KieServices.Factory.get().newKieBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( config );
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        ksession = kbase.newKieSession();

        ksession.addEventListener((RuleRuntimeEventListener) this);


        marshaller = MarshallerFactory.newMarshaller( kbase );
    }

    @Override
    public void objectInserted(ObjectInsertedEvent event) {

        System.out.println("------------------objectInserted---------------");
        marshallSession();
    }
    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {

        System.out.println("------------------objectUpdated---------------");
        marshallSession();
    }
    @Override
    public void objectDeleted(ObjectDeletedEvent event) {

        System.out.println("------------------objectDeleted---------------");
        marshallSession();
    }

    private void marshallSession() {
        try {
            marshaller.marshall(new ByteArrayOutputStream(), ksession);

            System.out.println("------------------marshalled---------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Test1 {

    }
}