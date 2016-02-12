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

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.drools.compiler.integrationtests.IncrementalCompilationTest.createAndDeployJar;
import static org.junit.Assert.*;

public class KieContainerTest {

    @Test
    public void testMainKieModule() {
        KieServices ks = KieServices.Factory.get();
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        createAndDeployJar( ks, releaseId, createDRL("ruleA") );

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieModule kmodule = ((InternalKieContainer) kieContainer).getMainKieModule();
        assertEquals( releaseId, kmodule.getReleaseId() );
    }

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
        assertEquals( 2, ksession.fireAllRules() );

        Class cls2 = kieContainer2.getClassLoader().loadClass( "org.drools.test.Message");
        Constructor constructor2 = cls2.getConstructor(String.class);
        ksession2.insert(constructor2.newInstance("Hello World"));
        assertEquals( 2, ksession2.fireAllRules() );

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
        createAndDeployJar( ks, releaseId1, type, drl1, drl2 );

        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieContainer kieContainer2 = ks.newKieContainer(releaseId1);

        KieSession ksession = kieContainer.newKieSession();
        KieSession ksession2 = kieContainer2.newKieSession();

        insertMessageFromTypeDeclaration( ksession );
        assertEquals( 2, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-delete", "1.0.1");
        createAndDeployJar( ks, releaseId2, type, null, drl2 );

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


    @Test(timeout = 10000)
    public void testIncrementalCompilationSynchronization() throws Exception {
        final KieServices kieServices = KieServices.Factory.get();

        ReleaseId releaseId = kieServices.newReleaseId("org.kie.test", "sync-scanner-test", "1.0.0");
        createAndDeployJar( kieServices, releaseId, createDRL("rule0") );

        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        KieSession kieSession = kieContainer.newKieSession();
        List<String> list = new ArrayList<String>();
        kieSession.setGlobal("list", list);
        kieSession.fireAllRules();
        kieSession.dispose();
        assertEquals(1, list.size());

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 10; i++) {
                    ReleaseId releaseId = kieServices.newReleaseId("org.kie.test", "sync-scanner-test", "1.0." + i);
                    createAndDeployJar( kieServices, releaseId, createDRL("rule" + i) );
                    kieContainer.updateToVersion(releaseId);
                }
            }
        });

        t.setDaemon(true);
        t.start();

        while (true) {
            kieSession = kieContainer.newKieSession();
            list = new ArrayList<String>();
            kieSession.setGlobal("list", list);
            kieSession.fireAllRules();
            kieSession.dispose();
            // There can be multiple items in the list if an updateToVersion is triggered during a fireAllRules
            // (updateToVersion can be called multiple times during fireAllRules, especially on slower machines)
            // in that case it may fire with the old rule and multiple new ones
            Assertions.assertThat(list).isNotEmpty();
            if (list.get(0).equals("rule9")) {
                break;
            }
        }
    }

    @Test
    public void testMemoryFileSystemFolderUniqueness() {
        KieServices kieServices = KieServices.Factory.get();
        String drl = "package org.drools.test\n" +
                     "rule R1 when\n" +
                     "   $m : Object()\n" +
                     "then\n" +
                     "end\n";
        Resource resource = kieServices.getResources().newReaderResource( new StringReader( drl), "UTF-8" );
        resource.setTargetPath("org/drools/test/rules.drl");
        String kmodule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                         "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                         "  <kbase name=\"testKbase\" packages=\"org.drools.test\">\n" +
                         "    <ksession name=\"testKsession\"/>\n" +
                         "  </kbase>\n" +
                         "</kmodule>";

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-delete", "1.0.0");
        createAndDeployJar(kieServices, kmodule, releaseId, resource);

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        KieModule kieModule = ((InternalKieContainer) kieContainer).getMainKieModule();
        MemoryFileSystem memoryFileSystem = ((MemoryKieModule) kieModule).getMemoryFileSystem();
        Folder rootFolder = memoryFileSystem.getFolder("");
        Object[] members = rootFolder.getMembers().toArray();
        assertEquals(2, members.length);
        Folder firstFolder = (Folder) members[0];
        Folder secondFolder = (Folder) members[1];
        assertEquals(firstFolder.getParent(), secondFolder.getParent());
    }

    @Test
    public void testClassLoaderGetResources() throws IOException {
        KieServices kieServices = KieServices.Factory.get();
        String drl1 = "package org.drools.testdrl;\n" +
                     "rule R1 when\n" +
                     "   $m : Object()\n" +
                     "then\n" +
                     "end\n";
        Resource resource1 = kieServices.getResources().newReaderResource(new StringReader(drl1), "UTF-8");
        resource1.setTargetPath("org/drools/testdrl/rules1.drl");

        String drl2 = "package org.drools.testdrl;\n" +
                     "rule R2 when\n" +
                     "   $m : Object()\n" +
                     "then\n" +
                     "end\n";
        Resource resource2 = kieServices.getResources().newReaderResource(new StringReader(drl2), "UTF-8");
        resource2.setTargetPath("org/drools/testdrl/rules2.drl");

        String java3 = "package org.drools.testjava;\n" +
                     "public class Message {}";
        Resource resource3 = kieServices.getResources().newReaderResource(new StringReader(java3), "UTF-8");
        resource3.setTargetPath("org/drools/testjava/Message.java");
        resource3.setResourceType(ResourceType.JAVA);

        String kmodule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                         "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                         "  <kbase name=\"testKbase\" packages=\"org.drools.testdrl\">\n" +
                         "    <ksession name=\"testKsession\"/>\n" +
                         "  </kbase>\n" +
                         "</kmodule>";

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-delete", "1.0.0");
        createAndDeployJar(kieServices, kmodule, releaseId, resource1, resource2, resource3);

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        ClassLoader classLoader = kieContainer.getClassLoader();
        assertEnumerationSize(1, classLoader.getResources("org/drools/testjava")); // no trailing "/"

        assertEnumerationSize(1, classLoader.getResources("org/drools/testdrl/")); // trailing "/" to test both variants
        // make sure the package resource correctly lists all its child resources (files in this case)
        URL url = classLoader.getResources("org/drools/testdrl").nextElement();
        List<String> lines = IOUtils.readLines(url.openStream());
        Assertions.assertThat(lines).contains("rules1.drl", "rules1.drl.properties", "rules2.drl", "rules2.drl.properties");
    }

    public static void assertEnumerationSize(int expectedSize, Enumeration<?> enumeration) {
        int actualSize = 0;
        while (enumeration.hasMoreElements()) {
            actualSize++;
            enumeration.nextElement();
        }
        Assertions.assertThat(actualSize).isEqualTo(expectedSize);
    }

    private String createDRL(String ruleName) {
        return "package org.kie.test\n" +
               "global java.util.List list\n" +
               "rule " + ruleName + "\n" +
               "when\n" +
               "then\n" +
               "list.add( drools.getRule().getName() );\n" +
               "end\n";
    }
}
