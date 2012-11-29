package org.drools.scanner;

import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.KieScanner;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.builder.impl.InternalKieJar;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KieRepositoryScannerTest {

    private FileManager fileManager;
    private File kPom;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        kPom = createKPom();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    @Test @Ignore
    public void testKScanner() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();

        KieJar kJar1 = createKieJar(kf, "rule1", "rule2");
        KieContainer kieContainer = ks.getKieContainer(kf.newGav("org.kie", "scanner-test", "1.0-SNAPSHOT"));

        MavenRepository repository = new MavenRepository();
        repository.deployArtifact("org.kie", "scanner-test", "1.0-SNAPSHOT", ((InternalKieJar)kJar1).asFile(), kPom);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.getKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar
        KieJar kJar2 = createKieJar(kf, "rule2", "rule3");

        // deploy it on maven
        repository.deployArtifact("org.drools", "scanner-test", "1.0-SNAPSHOT", ((InternalKieJar)kJar2).asFile(), kPom);

        // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
        KieScanner scanner = kf.newKieScanner(kieContainer);

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();

        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.getKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
    }

    @Test @Ignore
    public void testKScannerWithKJarContainingClasses() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();

        KieJar kJar1 = createKieJarWithClass(kf, 2, 7);
        KieContainer kieContainer = ks.getKieContainer(kf.newGav("org.kie", "scanner-test", "1.0-SNAPSHOT"));

        MavenRepository repository = new MavenRepository();
        repository.deployArtifact("org.kie", "scanner-test", "1.0-SNAPSHOT", ((InternalKieJar)kJar1).asFile(), kPom);

        KieScanner scanner = kf.newKieScanner(kieContainer);

        KieSession ksession = kieContainer.getKieSession("KSession1");
        checkKSession(ksession, 14);

        KieJar kJar2 = createKieJarWithClass(kf, 3, 5);

        repository.deployArtifact("org.kie", "scanner-test", "1.0-SNAPSHOT", ((InternalKieJar)kJar2).asFile(), kPom);

        scanner.scanNow();

        KieSession ksession2 = kieContainer.getKieSession("KSession1");
        checkKSession(ksession2, 15);
    }

    private File createKPom() throws IOException {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>org.drools</groupId>\n" +
                "  <artifactId>scanner-test</artifactId>\n" +
                "  <version>1.0-SNAPSHOT</version>\n" +
                "\n" +
                "</project>";

        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, pom);
        return pomFile;
    }

    private KieJar createKieJar(KieFactory kf, String... rules) throws IOException {
        KieFileSystem kfs = kf.newKieFileSystem();
        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));
        }

        KieProject kproj = kf.newKieProject()
                .setGroupArtifactVersion(kf.newGav("org.kie", "scanner-test", "1.0-SNAPSHOT"));

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get("realtime") );

        kfs.write(KieContainer.KPROJECT_RELATIVE_PATH, kproj.toXML());

        KieBuilder kieBuilder = kf.newKieBuilder(kfs);
        assertTrue(kieBuilder.build().isEmpty());
        return kieBuilder.getKieJar();
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

    private void checkKSession(StatefulKnowledgeSession ksession, Object... results) {
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(results.length, list.size());
        for (Object result : results) {
            assertTrue( list.contains( result ) );
        }
    }

    private KieJar createKieJarWithClass(KieFactory kf, int value, int factor) throws IOException {
        KieFileSystem kieFileSystem = kf.newKieFileSystem();

        KieProject kproj = kf.newKieProject()
                .setGroupArtifactVersion(kf.newGav("org.kie", "scanner-test", "1.0-SNAPSHOT"));

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get("realtime") );

        kieFileSystem
                .write(KieContainer.KPROJECT_RELATIVE_PATH, kproj.toXML())
                .write("src/kbases/" + kieBaseModel1.getName() + "/rule1.drl", createDRLForJavaSource(value))
                .write("org/kie/test/Bean.java", createJavaSource(factor));

        KieBuilder kieBuilder = kf.newKieBuilder(kieFileSystem);
        assertTrue(kieBuilder.build().isEmpty());
        return kieBuilder.getKieJar();
    }

    private String createJavaSource(int factor) {
        return "package org.kie.test;\n" +
                "public class Bean {\n" +
                "   private final int value;\n" +
                "   public Bean(int value) {\n" +
                "       this.value = value;\n" +
                "   }\n" +
                "   public int getValue() {\n" +
                "       return value * " + factor + ";\n" +
                "   }\n" +
                "}";
    }

    private String createDRLForJavaSource(int value) {
        return "package org.kie.test\n" +
                //"import org.kie.test.Bean;\n" +
                "global java.util.List list\n" +
                "rule Init\n" +
                "when\n" +
                "then\n" +
                "insert( new Bean(" + value + ") );\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "   $b : Bean()\n" +
                "then\n" +
                "   list.add( $b.getValue() );\n" +
                "end\n";
    }
}
