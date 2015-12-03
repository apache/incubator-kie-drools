/*
 * Copyright 2015 JBoss Inc
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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        String drl = "package org.drools.compiler.integrationtests\n" +
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
        String drl = "package org.drools.compiler.integrationtests\n" +
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
        FactType factType = ksession.getKieBase().getFactType("org.drools.compiler.integrationtests", "CancelFact");
        assertNotNull(factType);
        ksession.insert(factType.newInstance());

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testHelloWorldWithPackages() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0-SNAPSHOT");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/KBase1/org/pkg1/r1.drl", createDrl( "R1" ) )
                .write( "src/main/resources/KBase1/org/pkg2/r2.drl", createDrl( "R2" ) )
                .writeKModuleXML( createKieProjectWithPackages( ks, "org.pkg1" ).toXML() );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testHelloWorldUsingPackages() throws Exception {
        String drlDef = "package org.drools.compiler.integrationtests\n" +
                        "import " + Message.class.getCanonicalName() + "\n" +
                        "rule R_def when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId( "org.kie", "hello-world", "1.0-SNAPSHOT" );

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/r1_1.drl", drlDef)
                .write("src/main/resources/KBase1/org/pkg1/r1_2.drl", createDrl("R1"))
                .write("src/main/resources/KBase1/org/pkg2/r2.drl", createDrl("R2"))
                .writeKModuleXML(createKieProjectWithPackages(ks, "org.pkg1").toXML());
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert( new Message( "Hello World" ) );

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testHelloWorldWithWildcardPackages() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0-SNAPSHOT");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/KBase1/org/pkg1/test/r1.drl", createDrlWithGlobal( "R1" ) )
                .write( "src/main/resources/KBase1/org/pkg2/test/r2.drl", createDrlWithGlobal( "R2" ) )
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
        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0-SNAPSHOT");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/rules/rules.drl", createDrlWithGlobal("R1"))
                .write("src/main/resources/KBase1/rules/tests/tests.drl", createDrlWithGlobal("R2"))
                .write("src/main/resources/KBase1/aaarules/aaarules.drl", createDrlWithGlobal("R3"))
                .write("src/main/resources/KBase1/sample/brms601_1310778/rules/rules.drl", createDrlWithGlobal("R4"))
                .write("src/main/resources/KBase1/sample/brms601_1310778/tests/tests.drl", createDrlWithGlobal("R5"))
                .write("src/main/resources/KBase1/tests/tests.drl", createDrlWithGlobal("R6"))
                .write("src/main/resources/KBase1/rules2/rules2.drl", createDrlWithGlobal("R7"))
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
        return "package org.drools.compiler.integrationtests\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule " + ruleName + " when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
    }

    public String createDrlWithGlobal(String ruleName) {
        return "package org.drools.compiler.integrationtests\n" +
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

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage(pkg);

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( KieSessionType.STATEFUL )
                .setClockType(ClockTypeOption.get("realtime"));

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

    private void buildVersion(KieServices ks, String message, String version) {
        String drl = "package org.drools.compiler.integrationtests\n" +
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
        String drl1 = "package org.drools.compiler.integrationtests\n" +
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule R11 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R12 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools.compiler.integrationtests\n" +
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

        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0-SNAPSHOT");

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

        kproj.newKieBaseModel("KBase2")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg1")
                .newKieSessionModel("KSession1");

        kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg2")
                .newKieSessionModel("KSession2");

        return kproj;
    }

    @Test
    public void testImport() throws Exception {
        // DROOLS-859
        String drl1 = "package org.drools.compiler.integrationtests\n" +
                      "import " + Message.class.getCanonicalName() + "\n" +
                      "global java.util.List list\n" +
                      "rule R1 when\n" +
                      "    $m : Message( message == \"Hello World\" )\n" +
                      "then\n" +
                      "    list.add(\"ok\");\n" +
                      "end\n";

        String drl2 = "package org.drools.compiler.integrationtests\n" +
                      "import " + Message.class.getCanonicalName() + "\n" +
                      "global java.util.List list\n" +
                      "rule R2 when\n" +
                      "   $m : Message( message == \"Hi Universe\" )\n" +
                      "then\n" +
                      "   list.add(\"ko\");\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "test-import", "1.0-SNAPSHOT" );

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
}
