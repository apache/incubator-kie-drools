package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.lang.reflect.Constructor;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static org.drools.compiler.integrationtests.IncrementalCompilationTest.createAndDeployJar;

public class KieContainerTest {

    @Test
    public void testSharedTypeDeclarationsUsingClassLoader() throws Exception {
        String type = "package org.drools.test\n" +
                      "declare Message\n" +
                      "   message : String\n" +
                      "end\n";

        String drl1 = "package org.drools.test\n" +
                      "rule R1 when\n" +
                      "   $o : Object()\n" +
                      "then\n" +
                      "   if ($o.getClass().getName().equals(\"org.drools.test.Message\") && $o.getClass() != new Message(\"Test\").getClass()) {\n" +
                      "       throw new RuntimeException();\n" +
                      "   }\n" +
                      "end\n";

        String drl2 = "package org.drools.test\n" +
                      "rule R2_2 when\n" +
                      "   $m : Message( message == \"Hello World\" )\n" +
                      "then\n" +
                      "   if ($m.getClass() != new Message(\"Test\").getClass()) {\n" +
                      "       throw new RuntimeException();\n" +
                      "   }\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        KieModule km = createAndDeployJar( ks, releaseId1, type, drl1, drl2 );

        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieContainer kieContainer2 = ks.newKieContainer(releaseId1);

        KieSession ksession = kieContainer.newKieSession();
        KieSession ksession2 = kieContainer2.newKieSession();

        Class cls1 = kieContainer.getClassLoader().loadClass( "org.drools.test.Message");
        Constructor constructor = cls1.getConstructor(String.class);
        ksession.insert(constructor.newInstance("Hello World"));
        assertEquals( 3, ksession.fireAllRules() ); // R1 fires also for the InitialFact

        Class cls2 = kieContainer2.getClassLoader().loadClass( "org.drools.test.Message");
        Constructor constructor2 = cls2.getConstructor(String.class);
        ksession2.insert(constructor2.newInstance("Hello World"));
        assertEquals( 3, ksession2.fireAllRules() );

        assertNotSame(cls1, cls2);
    }

    @Test
    public void testSharedTypeDeclarationsUsingFactTypes() throws Exception {
        String type = "package org.drools.test\n" +
                      "declare Message\n" +
                      "   message : String\n" +
                      "end\n";

        String drl1 = "package org.drools.test\n" +
                      "rule R1 when\n" +
                      "   $m : Message()\n" +
                      "then\n" +
                      "   if ($m.getClass() != new Message(\"Test\").getClass()) {\n" +
                      "       throw new RuntimeException();\n" +
                      "   }\n" +
                      "end\n";

        String drl2 = "package org.drools.test\n" +
                      "rule R2_2 when\n" +
                      "   $m : Message( message == \"Hello World\" )\n" +
                      "then\n" +
                      "   if ($m.getClass() != new Message(\"Test\").getClass()) {\n" +
                      "       throw new RuntimeException();\n" +
                      "   }\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        KieModule km = createAndDeployJar( ks, releaseId1, type, drl1, drl2 );

        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieContainer kieContainer2 = ks.newKieContainer(releaseId1);

        KieSession ksession = kieContainer.newKieSession();
        KieSession ksession2 = kieContainer2.newKieSession();

        insertMessageFromTypeDeclaration( ksession );
        assertEquals( 2, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-delete", "1.0.1");
        km = createAndDeployJar( ks, releaseId2, type, null, drl2 );

        kieContainer.updateToVersion(releaseId2);

        // test with the old ksession ...
        ksession = kieContainer.newKieSession();
        insertMessageFromTypeDeclaration( ksession );
        assertEquals( 1, ksession.fireAllRules() );

        // ... and with a brand new one
        ksession = kieContainer.newKieSession();
        insertMessageFromTypeDeclaration (ksession );
        assertEquals( 1, ksession.fireAllRules() );

        // check that the second kieContainer hasn't been affected by the update of the first one
        insertMessageFromTypeDeclaration( ksession2 );
        assertEquals( 2, ksession2.fireAllRules() );

        ksession2 = kieContainer2.newKieSession();
        insertMessageFromTypeDeclaration( ksession2 );
        assertEquals( 2, ksession2.fireAllRules() );
    }

    private void insertMessageFromTypeDeclaration(KieSession ksession) throws InstantiationException, IllegalAccessException {
        FactType messageType = ksession.getKieBase().getFactType("org.drools.test", "Message");
        Object message = messageType.newInstance();
        messageType.set(message, "message", "Hello World");
        ksession.insert(message);
    }
}
