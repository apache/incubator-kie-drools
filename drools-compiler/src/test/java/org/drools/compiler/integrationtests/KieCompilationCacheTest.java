package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class KieCompilationCacheTest extends CommonTestMethodBase {

    @Test
    public void testCompilationCache() throws Exception {
        String drl = "package org.drools.compiler\n" +
                "declare type X\n" +
                "    foo : String\n" +
                "end\n"+
                "rule R1 when\n" +
                "   $m : X( foo == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
        
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        ks.newKieBuilder( kfs ).buildAll();
        
        ReleaseId releaseId = ks.getRepository().getDefaultReleaseId();
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule( releaseId );
        byte[] jar = kieModule.getBytes();
        
        MemoryFileSystem mfs = MemoryFileSystem.readFromJar( jar );
        File file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase1") );
        assertNotNull( file );

        Resource jarRes = ks.getResources().newByteArrayResource( jar );
        KieModule km = ks.getRepository().addKieModule( jarRes );
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        
        KieBase kbase = kc.getKieBase();
        FactType type = kbase.getFactType( "org.drools.compiler", "X" );
        FactField foo = type.getField( "foo" );
        Object x = type.newInstance();
        foo.set( x, "Hello World" );
        
        KieSession ksession = kc.newKieSession();
        ksession.insert(x);

        int count = ksession.fireAllRules();
        assertEquals( 1, count );
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
        
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule( releaseId );
        byte[] jar = kieModule.getBytes();
        
        MemoryFileSystem mfs = MemoryFileSystem.readFromJar( jar );
        File file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase1") );
        assertNotNull( file );
        file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase2") );
        assertNotNull( file );

        Resource jarRes = ks.getResources().newByteArrayResource( jar );
        KieModule km = ks.getRepository().addKieModule( jarRes );
        
        KieSession ksession = ks.newKieContainer( km.getReleaseId() ).newKieSession("KSession1");
        ksession.insert(new Message("Hello World"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession1");
        ksession.insert(new Message("Hi Universe"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession1");
        ksession.insert(new Message("Aloha Earth"));
        assertEquals( 0, ksession.fireAllRules() );

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession2");
        ksession.insert(new Message("Hello World"));
        assertEquals( 1, ksession.fireAllRules() );

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession2");
        ksession.insert(new Message("Hi Universe"));
        assertEquals( 0, ksession.fireAllRules() );

        ksession = ks.newKieContainer(km.getReleaseId()).newKieSession("KSession2");
        ksession.insert(new Message("Aloha Earth"));
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testCacheWigAccumulate() throws Exception {
        String drl1 = "package org.drools.compiler.integrationtests\n" +
                "rule R11 when\n" +
                "   Number() from accumulate(String(), \n" +
                "              init(int x = 0;)," +
                "              action(x++;)," +
                "              reverse(x--;)," +
                "              result(x))\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "cache-accumulate", "1.0-SNAPSHOT");

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseId)
                .write("src/main/resources/KBase1/org/pkg1/r1.drl", drl1)
                .writeKModuleXML(createKieProjectWithPackagesAnd2KieBases(ks).toXML());
        ks.newKieBuilder( kfs ).buildAll();
        
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule( releaseId );
        byte[] jar = kieModule.getBytes();
        
        MemoryFileSystem mfs = MemoryFileSystem.readFromJar( jar );
        File file = mfs.getFile( KieBuilderImpl.getCompilationCachePath( releaseId, "KBase1") );
        assertNotNull( file );

        Resource jarRes = ks.getResources().newByteArrayResource( jar );
        KieModule km = ks.getRepository().addKieModule( jarRes );
        
        KieSession ksession = ks.newKieContainer( km.getReleaseId() ).newKieSession("KSession1");
        ksession.insert(new String("Hello World"));
        assertEquals( 1, ksession.fireAllRules() );
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