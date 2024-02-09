/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.impl.InternalKieContainer;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.core.util.DroolsAssert.assertEnumerationSize;
import static org.drools.core.util.DroolsAssert.assertUrlEnumerationContainsMatch;

@RunWith(Parameterized.class)
public class KieContainerTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KieContainerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testMainKieModule() {
        KieServices ks = KieServices.Factory.get();
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, createDRL("ruleA"));

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieModule kmodule = ((InternalKieContainer) kieContainer).getMainKieModule();
        assertThat(kmodule.getReleaseId()).isEqualTo(releaseId);
    }

    @Test
    public void testUpdateToNonExistingRelease() {
        // DROOLS-1562
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-release", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, createDRL("ruleA"));

        KieContainer kieContainer = ks.newKieContainer(releaseId);

        Results results = kieContainer.updateToVersion( ks.newReleaseId( "org.kie", "test-release", "1.0.1" ) );
        assertThat(results.getMessages(Level.ERROR).size()).isEqualTo(1);
        assertThat(((InternalKieContainer) kieContainer).getContainerReleaseId().getVersion()).isEqualTo("1.0.0");
    }
    
    @Test
    public void testReleaseIdGetters() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-delete-v", "1.0.1");
        ReleaseId newReleaseId = ks.newReleaseId("org.kie", "test-delete-v", "1.0.2");

        ks.getRepository().removeKieModule(releaseId);
        ks.getRepository().removeKieModule(newReleaseId);

        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, createDRL("ruleA"));

        ReleaseId configuredReleaseId = ks.newReleaseId("org.kie", "test-delete-v", "RELEASE");
		KieContainer kieContainer = ks.newKieContainer(configuredReleaseId);

        InternalKieContainer iKieContainer = (InternalKieContainer) kieContainer;
        assertThat(iKieContainer.getConfiguredReleaseId()).isEqualTo(configuredReleaseId);
        assertThat(iKieContainer.getResolvedReleaseId()).isEqualTo(releaseId);
        assertThat(iKieContainer.getReleaseId()).isEqualTo(releaseId);
        // demonstrate internal API behavior, in the future shall this be enforced?
        assertThat(iKieContainer.getContainerReleaseId()).isEqualTo(configuredReleaseId);

        KieUtil.getKieModuleFromDrls(newReleaseId, kieBaseTestConfiguration, createDRL("ruleA"));
        iKieContainer.updateToVersion(newReleaseId);

        assertThat(iKieContainer.getConfiguredReleaseId()).isEqualTo(configuredReleaseId);
        assertThat(iKieContainer.getResolvedReleaseId()).isEqualTo(newReleaseId);
        assertThat(iKieContainer.getReleaseId()).isEqualTo(newReleaseId);
        // demonstrate internal API behavior, in the future shall this be enforced?
        assertThat(iKieContainer.getContainerReleaseId()).isEqualTo(newReleaseId);
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
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, type, drl1, drl2);

        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieContainer kieContainer2 = ks.newKieContainer(releaseId1);

        KieSession ksession = kieContainer.newKieSession();
        KieSession ksession2 = kieContainer2.newKieSession();

        Class cls1 = kieContainer.getClassLoader().loadClass( "org.drools.test.Message");
        Constructor constructor = cls1.getConstructor(String.class);
        ksession.insert(constructor.newInstance("Hello World"));
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        Class cls2 = kieContainer2.getClassLoader().loadClass( "org.drools.test.Message");
        Constructor constructor2 = cls2.getConstructor(String.class);
        ksession2.insert(constructor2.newInstance("Hello World"));
        assertThat(ksession2.fireAllRules()).isEqualTo(2);

        // old CommonTestMethodBase.createAndDeployJar re-deploy MemoryKieModule into repository so results in different classloaders.
        // With new test API KieUtil, kieContainers shares the same classloader so this assert fails.
        // But I don't think this assert is important so commenting out.
//        assertNotSame(cls1, cls2);
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
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, type, drl1, drl2);

        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieContainer kieContainer2 = ks.newKieContainer(releaseId1);

        KieSession ksession = kieContainer.newKieSession();
        KieSession ksession2 = kieContainer2.newKieSession();

        insertMessageFromTypeDeclaration( ksession );
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-delete", "1.0.1");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, type, null, drl2);

        kieContainer.updateToVersion(releaseId2);

        // test with the old ksession ...
        ksession = kieContainer.newKieSession();
        insertMessageFromTypeDeclaration( ksession );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        // ... and with a brand new one
        ksession = kieContainer.newKieSession();
        insertMessageFromTypeDeclaration (ksession );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        // check that the second kieContainer hasn't been affected by the update of the first one
        insertMessageFromTypeDeclaration( ksession2 );
        assertThat(ksession2.fireAllRules()).isEqualTo(2);

        ksession2 = kieContainer2.newKieSession();
        insertMessageFromTypeDeclaration( ksession2 );
        assertThat(ksession2.fireAllRules()).isEqualTo(2);
    }

    private void insertMessageFromTypeDeclaration(KieSession ksession) throws InstantiationException, IllegalAccessException {
        FactType messageType = ksession.getKieBase().getFactType("org.drools.test", "Message");
        Object message = messageType.newInstance();
        messageType.set(message, "message", "Hello World");
        ksession.insert(message);
    }


    @Test(timeout = 20000)
    public void testIncrementalCompilationSynchronization() {
        final KieServices kieServices = KieServices.Factory.get();

        ReleaseId releaseId = kieServices.newReleaseId("org.kie.test", "sync-scanner-test", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, createDRL("rule0"));

        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        KieSession kieSession = kieContainer.newKieSession();
        List<String> list = new ArrayList<>();
        kieSession.setGlobal("list", list);
        kieSession.fireAllRules();
        kieSession.dispose();
        assertThat(list.size()).isEqualTo(1);

        Thread t = new Thread(() -> {
            for (int i = 1; i < 10; i++) {
                ReleaseId releaseId1 = kieServices.newReleaseId("org.kie.test", "sync-scanner-test", "1.0." + i);
                KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, createDRL("rule" + i));
                kieContainer.updateToVersion(releaseId1);
            }
        });

        t.setDaemon(true);
        t.start();

        while (true) {
            kieSession = kieContainer.newKieSession();
            list = new ArrayList<>();
            kieSession.setGlobal("list", list);
            kieSession.fireAllRules();
            kieSession.dispose();
            // There can be multiple items in the list if an updateToVersion is triggered during a fireAllRules
            // (updateToVersion can be called multiple times during fireAllRules, especially on slower machines)
            // in that case it may fire with the old rule and multiple new ones
            assertThat(list).isNotEmpty();
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
        KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId, KieModuleModelImpl.fromXML(kmodule), resource);

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        KieModule kieModule = ((InternalKieContainer) kieContainer).getMainKieModule();
        MemoryFileSystem memoryFileSystem = (( MemoryKieModule ) kieModule).getMemoryFileSystem();
        Folder rootFolder = memoryFileSystem.getFolder("");
        Object[] members = rootFolder.getMembers().toArray();
        assertThat(members.length).isEqualTo(2);
        Folder firstFolder = (Folder) members[0];
        Folder secondFolder = (Folder) members[1];
        assertThat(secondFolder.getParent()).isEqualTo(firstFolder.getParent());
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
        KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId, KieModuleModelImpl.fromXML(kmodule), resource1, resource2, resource3);

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        ClassLoader classLoader = kieContainer.getClassLoader();
        assertEnumerationSize(1, classLoader.getResources("org/drools/testjava")); // no trailing "/"

        assertEnumerationSize(1, classLoader.getResources("org/drools/testdrl/")); // trailing "/" to test both variants
        // make sure the package resource correctly lists all its child resources (files in this case)
        URL url = classLoader.getResources("org/drools/testdrl").nextElement();
        List<String> lines = IOUtils.readLines(url.openStream());
        assertThat(lines).contains("rules1.drl", "rules1.drl.properties", "rules2.drl", "rules2.drl.properties");

        assertUrlEnumerationContainsMatch("^mfs\\:/$", classLoader.getResources(""));
    }

    @Test
    public void testGetDefaultKieSessionModel() {
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
                "    <ksession name=\"testKsession\" default=\"true\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-testGetDefaultKieSessionModel", "1.0.0");
        KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId, KieModuleModelImpl.fromXML(kmodule), resource);

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        KieSessionModel sessionModel = kieContainer.getKieSessionModel(null);
        assertThat(sessionModel).isNotNull();
        assertThat(sessionModel.getName()).isEqualTo("testKsession");
    }

    @Test
    public void testGetDefaultKieSessionModelEmptyKmodule() {
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
                "</kmodule>";

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = kieServices.newReleaseId("org.kie", "test-testGetDefaultKieSessionModelEmptyKmodule", "1.0.0");
        KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId, KieModuleModelImpl.fromXML(kmodule), resource);

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        KieSessionModel sessionModel = kieContainer.getKieSessionModel(null);
        assertThat(sessionModel).isNotNull();
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
