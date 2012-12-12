package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.Message;
import org.junit.Test;
import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KieSessionModel.KieSessionType;
import org.kie.conf.EqualityBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieSession;
import org.kie.runtime.conf.ClockTypeOption;

/**
 * This is a sample class to launch a rule.
 */
public class KieHelloWorldTest extends CommonTestMethodBase {

    @Test
    public void testHelloWorld() throws Exception {
        String drl = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
        
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();
         
        assertEquals( 1, count );
    }

    @Test
    public void testFailingHelloWorld() throws Exception {
        String drl = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( mesage == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();

        assertEquals( 1, kb.getResults().getMessages().size() );
    }

    @Test
    public void testHelloWorldWithPackages() throws Exception {
        String drl1 = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0-SNAPSHOT");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/r1.drl", drl1)
                .write("src/main/resources/KBase1/org/pkg2/r2.drl", drl2)
                .writeKModuleXML(createKieProjectWithPackages(ks, "org.pkg1").toXML());
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
    }

    @Test
    public void testHelloWorldWithWildcardPackages() throws Exception {
        String drl1 = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0-SNAPSHOT");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/test/r1.drl", drl1)
                .write("src/main/resources/KBase1/org/pkg2/test/r2.drl", drl2)
                .writeKModuleXML( createKieProjectWithPackages(ks, "org.pkg1.*").toXML());
        ks.newKieBuilder( kfs ).buildAll();

        KieSession ksession = ks.newKieContainer(releaseId).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));

        int count = ksession.fireAllRules();

        assertEquals( 1, count );
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
        String drl = "package org.drools\n" +
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
        String drl1 = "package org.drools\n" +
                "rule R11 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule R12 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
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
}