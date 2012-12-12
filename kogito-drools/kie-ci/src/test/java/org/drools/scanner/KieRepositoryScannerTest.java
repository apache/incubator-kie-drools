package org.drools.scanner;

import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KieServices;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieScanner;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KieSessionModel.KieSessionType;
import org.kie.builder.impl.InternalKieModule;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.drools.scanner.MavenRepository.getMavenRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KieRepositoryScannerTest {

    private FileManager fileManager;
    private File kPom;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        GAV gav = KieServices.Factory.get().newGav("org.kie", "scanner-test", "1.0-SNAPSHOT");
        kPom = createKPom(gav);
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    private void resetFileManager() {
        this.fileManager.tearDown();
        this.fileManager = new FileManager();
        this.fileManager.setUp();
    }

    @Test @Ignore
    public void testKScanner() throws Exception {
        KieServices ks = KieServices.Factory.get();
        GAV gav = ks.newGav("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJar(ks, gav, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(gav);

        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(gav, kJar1, kPom);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, gav, "rule2", "rule3");

        // deploy it on maven
        repository.deployArtifact(gav, kJar2, kPom);

        // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
        KieScanner scanner = ks.newKieScanner(kieContainer);

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();

        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
    }

    @Test @Ignore
    public void testKScannerWithKJarContainingClasses() throws Exception {
        KieServices ks = KieServices.Factory.get();
        GAV gav = ks.newGav("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJarWithClass(ks, gav, 2, 7);

        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(gav, kJar1, kPom);

        KieContainer kieContainer = ks.newKieContainer(gav);
        KieScanner scanner = ks.newKieScanner(kieContainer);

        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 14);

        InternalKieModule kJar2 = createKieJarWithClass(ks, gav, 3, 5);
        repository.deployArtifact(gav, kJar2, kPom);

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);
    }

    @Test @Ignore
    public void testLoadKieJarFromMavenRepo() throws Exception {
        // This test depends from the former one (UGLY!) and must be run immediately after it
        KieServices ks = KieServices.Factory.get();

        KieContainer kieContainer = ks.newKieContainer(ks.newGav("org.kie", "scanner-test", "1.0-SNAPSHOT"));

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);
    }

    @Test @Ignore
    public void testScannerOnPomProject() throws Exception {
        KieServices ks = KieServices.Factory.get();
        GAV gav1 = ks.newGav("org.kie", "scanner-test", "1.0");
        GAV gav2 = ks.newGav("org.kie", "scanner-test", "2.0");

        MavenRepository repository = getMavenRepository();
        repository.deployPomArtifact("org.kie", "scanner-master-test", "1.0", createMasterKPom());

        resetFileManager();

        InternalKieModule kJar1 = createKieJarWithClass(ks, gav1, 2, 7);
        repository.deployArtifact(gav1, kJar1, createKPom(gav1));

        KieContainer kieContainer = ks.newKieContainer(ks.newGav("org.kie", "scanner-master-test", "LATEST"));
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 14);

        KieScanner scanner = ks.newKieScanner(kieContainer);

        InternalKieModule kJar2 = createKieJarWithClass(ks, gav2, 3, 5);
        repository.deployArtifact(gav2, kJar2, createKPom(gav1));

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);
    }

    private File createKPom(GAV gav) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPom(gav));
        return pomFile;
    }

    private String getPom(GAV gav) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
        "  <modelVersion>4.0.0</modelVersion>\n" +
        "\n" +
        "  <groupId>" + gav.getGroupId() + "</groupId>\n" +
        "  <artifactId>" + gav.getArtifactId() + "</artifactId>\n" +
        "  <version>" + gav.getVersion() + "</version>\n" +
        "\n" +
        "</project>";
    }

    private File createMasterKPom() throws IOException {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>org.kie</groupId>\n" +
                "  <artifactId>scanner-master-test</artifactId>\n" +
                "  <version>1.0</version>\n" +
                "  <packaging>pom</packaging>\n" +
                "\n" +
                "    <dependencies>\n" +
                "      <dependency>\n" +
                "        <groupId>org.kie</groupId>\n" +
                "        <artifactId>scanner-test</artifactId>\n" +
                "        <version>LATEST</version>\n" +
                "      </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";

        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, pom);
        return pomFile;
    }

    private InternalKieModule createKieJar(KieServices ks, GAV gav, String... rules) throws IOException {
        KieFileSystem kfs = ks.newKieFileSystem();
        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));
        }

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType(KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );

        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML( getPom(gav) );

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return ( InternalKieModule ) kieBuilder.getKieModule();
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

    private void checkKSession(KieSession ksession, Object... results) {
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(results.length, list.size());
        for (Object result : results) {
            assertTrue( list.contains( result ) );
        }
    }

    private InternalKieModule createKieJarWithClass(KieServices ks, GAV gav, int value, int factor) throws IOException {
        KieFileSystem kieFileSystem = ks.newKieFileSystem();

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType(KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );

        kieFileSystem
                .writeKModuleXML(kproj.toXML())
                .writePomXML(getPom(gav))
                .write("src/main/resources/" + kieBaseModel1.getName() + "/rule1.drl", createDRLForJavaSource(value))
                .write("src/main/java/org/kie/test/Bean.java", createJavaSource(factor));

        KieBuilder kieBuilder = ks.newKieBuilder(kieFileSystem);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return ( InternalKieModule ) kieBuilder.getKieModule();
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
