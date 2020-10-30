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

package org.drools.mvel.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Message;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This is a sample class to launch a rule.
 */
public class KieHelloWorldTest extends CommonTestMethodBase {

    @Test
    public void testHelloWorld() throws Exception {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", createDrl( "R1" ) );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();
         
        assertEquals( 1, count );
    }

    @Test
    public void testClassLoaderStore() throws Exception {
        // DROOLS-1766
        String drl = "package org; declare Person name : String end";
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        ks.newKieBuilder( kfs ).buildAll();

        KieContainer kcontainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());

        KieProject kieProject = (( KieContainerImpl ) kcontainer).getKieProject();
        ResultsImpl messages = kieProject.verify();

        assertSame( kieProject.getClassLoader(), kcontainer.getClassLoader() );

        ProjectClassLoader pcl = (( ProjectClassLoader ) kieProject.getClassLoader());
        assertNotNull( pcl.getStore().get("org/Person.class") );
    }

    @Test
    public void testHelloWorldWithResource() throws Exception {
        // DROOLS-351
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write(
                ks.getResources()
                  .newReaderResource( new StringReader( createDrl( "R1" ) ) )
                  .setResourceType( ResourceType.DRL )
                  .setSourcePath( "src/main/resources/r1.txt" ) );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testHelloWorldWithEmptyFile() throws Exception {
        String drl = createDrl("R1");

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write("src/main/resources/r1.drl", drl)
                .write( "src/main/resources/empty.drl", ks.getResources().newInputStreamResource( new ByteArrayInputStream( new byte[0] ) ) );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testFailingHelloWorld() throws Exception {
        String drl = "package org.drools.mvel.integrationtests\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "   $m : Message( nonExistentField == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", drl);
        
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();

        assertEquals( 1, kb.getResults().getMessages().size() );
    }

    @Test
    public void testHelloWorldWithKBaseInclude() throws Exception {
        String drl = "package org.drools.mvel.integrationtests\n" +
                     "declare CancelFact\n" +
                     " cancel : boolean = true\n" +
                     "end\n" +
                     "rule R1 when\n" +
                     " $m : CancelFact( cancel == true )\n" +
                     "then\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );

        KieModuleModel module = ks.newKieModuleModel();

        // define first kbase
        final String defaultBaseName = "defaultKBase";
        KieBaseModel defaultBase = module.newKieBaseModel(defaultBaseName);
        defaultBase.setDefault(true);
        defaultBase.addPackage("*");
        defaultBase.newKieSessionModel("defaultKSession").setDefault(true);

        // define second kbase including resources of the first one
        final String includingBaseName = "includingKBase";
        KieBaseModel includingBase = module.newKieBaseModel(includingBaseName);
        includingBase.setDefault(false);
        includingBase.addInclude(defaultBaseName);
        includingBase.newKieSessionModel("includingKSession").setDefault(false);

        kfs.writeKModuleXML(module.toXML());
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, kb.getResults().getMessages().size() );

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        FactType factType = ksession.getKieBase().getFactType("org.drools.mvel.integrationtests", "CancelFact");
        assertNotNull(factType);
        ksession.insert(factType.newInstance());

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testHelloWorldWithPackages() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/KBase1/org/pkg1/r1.drl", createDrl( "org.pkg1", "R1" ) )
                .write( "src/main/resources/KBase1/org/pkg2/r2.drl", createDrl( "org.pkg2", "R2" ) )
                .writeKModuleXML( createKieProjectWithPackages( ks, "org.pkg1" ).toXML() );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testHelloWorldUsingPackages() throws Exception {
        String drlDef = "package org.pkg1\n" +
                        "import " + Message.class.getCanonicalName() + "\n" +
                        "rule R_def when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId( "org.kie", "hello-world", "1.0" );

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/r1_1.drl", drlDef)
                .write("src/main/resources/KBase1/r1_2.drl", createDrl("org.pkg1", "R1"))
                .write("src/main/resources/KBase1/r2.drl", createDrl("org.pkg2", "R2"))
                .writeKModuleXML(createKieProjectWithPackages(ks, "org.pkg1").toXML());
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert( new Message( "Hello World" ) );

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testHelloWorldUsingFolders() throws Exception {
        String drlDef = "package org.drools.mvel.integrationtests\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R_def when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId( "org.kie", "hello-world", "1.0" );

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/KBase1/org/pkg1/r1_1.drl", drlDef )
                .write( "src/main/resources/KBase1/org/pkg1/r1_2.drl", createDrl( "R1" ) )
                .write( "src/main/resources/KBase1/org/pkg2/r2.drl", createDrl( "R2" ) )
                .writeKModuleXML( createKieProjectWithPackages( ks, "org.pkg1" )
                        .setConfigurationProperty( "drools.groupDRLsInKieBasesByFolder", "true" )
                        .toXML() );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer( releaseId ).newKieSession( "KSession1" );
        ksession.insert( new Message( "Hello World" ) );

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testHelloWorldWithWildcardPackages() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/org/pkg1/test/r1.drl", createDrlWithGlobal( "org.pkg1.test", "R1" ) )
                .write( "src/main/resources/org/pkg2/test/r2.drl", createDrlWithGlobal( "org.pkg2.test", "R2" ) )
                .writeKModuleXML( createKieProjectWithPackages( ks, "org.pkg1.*" ).toXML() );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        assertEquals( 1, ksession.fireAllRules() );
        assertEquals( 1, list.size() );
        assertEquals( "R1", list.get( 0 ) );
    }

    @Test
    public void testHelloWorldWithWildcardPackagesComplex() throws Exception {
        // BZ-1174563
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/rules/rules.drl", createDrlWithGlobal("rules", "R1"))
                .write("src/main/resources/rules/tests/tests.drl", createDrlWithGlobal("rules.tests", "R2"))
                .write("src/main/resources/aaarules/aaarules.drl", createDrlWithGlobal("aaarules", "R3"))
                .write("src/main/resources/sample/brms601_1310778/rules/rules.drl", createDrlWithGlobal("sample.brms601_1310778.rules", "R4"))
                .write("src/main/resources/sample/brms601_1310778/tests/tests.drl", createDrlWithGlobal("sample.brms601_1310778.rules", "R5"))
                .write("src/main/resources/tests/tests.drl", createDrlWithGlobal("tests", "R6"))
                .write("src/main/resources/rules2/rules2.drl", createDrlWithGlobal("rules2", "R7"))
                .writeKModuleXML( createKieProjectWithPackages(ks, "rules.*").toXML());
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        assertEquals( 2, ksession.fireAllRules() );
        assertTrue( list.contains("R1") );
        assertTrue( list.contains("R2") );
    }

    public String createDrl(String ruleName) {
        return createDrl("org", ruleName);
    }

    public String createDrl(String packageName, String ruleName) {
        return "package " + packageName + "\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule " + ruleName + " when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
    }

    public String createDrlWithGlobal(String ruleName) {
        return createDrlWithGlobal("org", ruleName);
    }

    public String createDrlWithGlobal(String packageName, String ruleName) {
        return "package " + packageName + "\n" +
                "global java.util.List list\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule " + ruleName + " when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "    list.add(\"" + ruleName + "\");" +
                "end\n";
    }

    private KieModuleModel createKieProjectWithPackages(KieServices ks, String pkg) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel()
                                          .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                                          .setEventProcessingMode( EventProcessingOption.STREAM )
                                          .addPackage(pkg);

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                                                 .setType( KieSessionType.STATEFUL )
                                                 .setClockType(ClockTypeOption.get("realtime"))
                                                 .setDefault( true );

        return kproj;
    }

    @Test
    public void testHelloWorldOnVersionRange() throws Exception {
        KieServices ks = KieServices.Factory.get();

        buildVersion(ks, "Hello World", "1.0");
        buildVersion(ks, "Aloha Earth", "1.1");
        buildVersion(ks, "Hi Universe", "1.2");

        ReleaseId latestReleaseId = ks.newReleaseId("org.kie", "hello-world", "LATEST");

        KieSession ksession = ks.newKieContainer(latestReleaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));
        assertEquals( 0, ksession.fireAllRules() );

        ksession = ks.newKieContainer(latestReleaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hi Universe"));
        assertEquals( 1, ksession.fireAllRules() );

        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "hello-world", "1.0");

        ksession = ks.newKieContainer(releaseId1).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(releaseId1).newKieSession("KSession1");
        ksession.insert(new Message("Hi Universe"));
        assertEquals( 0, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "hello-world", "[1.0,1.2)");

        ksession = ks.newKieContainer(releaseId2).newKieSession("KSession1");
        ksession.insert(new Message("Aloha Earth"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(releaseId2).newKieSession("KSession1");
        ksession.insert(new Message("Hi Universe"));
        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testGetDefaultKieSessionWithNullName() throws Exception {
        // DROOLS-1276
        KieServices ks = KieServices.Factory.get();

        buildVersion(ks, "Hello World", "1.0");

        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "hello-world", "1.0");

        KieSession ksession = ks.newKieContainer(releaseId1).newKieSession((String)null);
        ksession.insert(new Message("Hello World"));
        assertEquals( 1, ksession.fireAllRules() );
    }

    private void buildVersion(KieServices ks, String message, String version) {
        String drl = "package org.drools.mvel.integrationtests\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"" + message+ "\" )\n" +
                "then\n" +
                "end\n";

        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", version);

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/r1.drl", drl)
                .writeKModuleXML(createKieProjectWithPackages(ks, "*").toXML());
        ks.newKieBuilder( kfs ).buildAll();
    }

    @Test
    public void testHelloWorldWithPackagesAnd2KieBases() throws Exception {
        String drl1 = "package org.pkg1\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R11 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R12 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.pkg2\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R21 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R22 when\n" +
                "   $m : Message( message == \"Aloha Earth\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/r1.drl", drl1)
                .write("src/main/resources/KBase1/org/pkg2/r2.drl", drl2)
                .writeKModuleXML(createKieProjectWithPackagesAnd2KieBases(ks).toXML());
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hi Universe"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Aloha Earth"));
        assertEquals( 0, ksession.fireAllRules() );

        ksession = ks.newKieContainer(releaseId).newKieSession("KSession2");
        ksession.insert(new Message("Hello World"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(releaseId).newKieSession("KSession2");
        ksession.insert(new Message("Hi Universe"));
        assertEquals( 0, ksession.fireAllRules() );

        ksession = ks.newKieContainer(releaseId).newKieSession("KSession2");
        ksession.insert(new Message("Aloha Earth"));
        assertEquals(1, ksession.fireAllRules());
    }

    private KieModuleModel createKieProjectWithPackagesAnd2KieBases(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel()
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg1")
                .newKieSessionModel("KSession1");

        kproj.newKieBaseModel()
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg2")
                .newKieSessionModel("KSession2");

        return kproj;
    }

    @Test
    public void testImport() throws Exception {
        // DROOLS-859
        String drl1 = "package rules\n" +
                      "import " + Message.class.getCanonicalName() + "\n" +
                      "global java.util.List list\n" +
                      "rule R1 when\n" +
                      "    $m : Message( message == \"Hello World\" )\n" +
                      "then\n" +
                      "    list.add(\"ok\");\n" +
                      "end\n";

        String drl2 = "package myrules\n" +
                      "import " + Message.class.getCanonicalName() + "\n" +
                      "global java.util.List list\n" +
                      "rule R2 when\n" +
                      "   $m : Message( message == \"Hi Universe\" )\n" +
                      "then\n" +
                      "   list.add(\"ko\");\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "test-import", "1.0" );

        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel( "kbase" )
             .addPackage( "rules" )
             .newKieSessionModel( "ksession" )
             .setDefault( true );

        KieFileSystem kfs = ks.newKieFileSystem()
                              .generateAndWritePomXML( releaseId )
                              .write( "src/main/resources/rules/r1.drl", drl1 )
                              .write( "src/main/resources/myrules/r2.drl", drl2 )
                              .writeKModuleXML( kproj.toXML() );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        KieSession ksession = ks.newKieContainer( releaseId ).newKieSession( );

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "list", results );

        ksession.insert( new Message( "Hello World" ) );
        ksession.insert( new Message( "Hi Universe" ) );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( "ok", results.get(0) );
    }

    @Test
    public void testErrorReportingWithWrongKmodule() throws Exception {
        // RHDM-69
        String kmodule =
                "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                "  <kbase name=\"ABC\" default=\"false\" eventProcessingMode=\"stream\" equalsBehavior=\"identity\"/>\n" +
                "  <kbase name=\"ABC\" default=\"false\" eventProcessingMode=\"stream\" equalsBehavior=\"identity\"/>\n" +
                "</kmodule>\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", createDrl( "R1" ) );
        kfs.writeKModuleXML(kmodule);

        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();

        assertEquals( 1, kb.getResults().getMessages().size() );
        assertTrue( kb.getResults().getMessages().get(0).toString().contains( "ABC" ) );
    }

    @Test
    public void testHelloWorldWithSpace() throws Exception {
        // DROOLS-2338
        final KieServices kieServices = KieServices.get();

        final Path dir = Paths.get("/tmp/t tt");
        Files.createDirectories(dir);
        final String text = "rule \"Hello world rule\"\n" +
                "when\n" +
                "then\n" +
                "    System.out.println(\"Hello world\");" +
                "end\n";
        final Path filePath = dir.resolve("one.drl");
        Files.write(filePath, text.getBytes());

        final KieFileSystem fs = kieServices.newKieFileSystem();

        fs.write( ResourceFactory.newUrlResource("file:/tmp/t%20tt/one.drl"));

        KieBuilder kieBuilder = kieServices.newKieBuilder(fs);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();

        KieSession ksession = kieServices.newKieContainer(kieModule.getReleaseId()).newKieSession();
        ksession.insert(new Object());

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testVeyifyNotExistingKieBase() throws Exception {
        // DROOLS-2757
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", createDrl( "R1" ) );
        ks.newKieBuilder( kfs ).buildAll();

        KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        try {
            kieContainer.verify( "notexistingKB" );
            fail("Verifying a not existing KieBase should throw a RuntimeException");
        } catch (RuntimeException e) {
            assertTrue( e.getMessage().contains( "notexistingKB" ) );
        }
    }

    @Test
    public void testDeclarativeCalendars() {
        String weekendCalendarSource =
                "package org.mypackage;\n" +
                "\n" +
                "public class WeekendCalendar implements org.kie.api.time.Calendar {\n" +
                "        @Override\n" +
                "        public boolean isTimeIncluded( long timestamp ) {\n" +
                "            java.util.Calendar c = java.util.Calendar.getInstance();\n" +
                "            c.setTimeInMillis(timestamp);\n" +
                "            final int day = c.get(java.util.Calendar.DAY_OF_WEEK);\n" +
                "            return day == java.util.Calendar.SATURDAY || day == java.util.Calendar.SUNDAY;\n" +
                "        }\n" +
                "    }";

        String weekdayCalendarSource =
                "package org.mypackage;\n" +
                "\n" +
                "public class WeekdayCalendar implements org.kie.api.time.Calendar {\n" +
                "        @Override\n" +
                "        public boolean isTimeIncluded( long timestamp ) {\n" +
                "            java.util.Calendar c = java.util.Calendar.getInstance();\n" +
                "            c.setTimeInMillis(timestamp);\n" +
                "            final int day = c.get(java.util.Calendar.DAY_OF_WEEK);\n" +
                "            return day != java.util.Calendar.SATURDAY && day != java.util.Calendar.SUNDAY;\n" +
                "        }\n" +
                "    }";

        String drl =
                "package org.mypackage;\n" +
                "\n" +
                "global java.util.List list\n" +
                " \n" +
                "rule \"weekend\"\n" +
                "    calendars \"weekend\"\n" +
                "    \n" +
                "    when\n" +
                "    then\n" +
                "        list.add(\"weekend\");\n" +
                "end\n" +
                " \n" +
                "rule \"weekday\"\n" +
                "    calendars \"weekday\"\n" +
                "\n" +
                "    when\n" +
                "    then\n" +
                "       list.add(\"weekday\");\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId( "org.kie", "hello-calendar", "1.0" );

        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel().newKieSessionModel("KSession1")
                .addCalendar( "weekend", "org.mypackage.WeekendCalendar" )
                .addCalendar( "weekday", "org.mypackage.WeekdayCalendar" )
                .setDefault( true );

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/org/mypackage/r1.drl", drl)
                .write("src/main/java/org/mypackage/WeekendCalendar.java", weekendCalendarSource)
                .write("src/main/java/org/mypackage/WeekdayCalendar.java", weekdayCalendarSource)
                .writeKModuleXML(kproj.toXML());
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");

        ArrayList<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
    }
}
