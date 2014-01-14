package org.kie.scanner;

import org.drools.compiler.kie.builder.impl.InternalKieScanner;
import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.scanner.MavenRepository.getMavenRepository;

public class KieRepositoryScannerTest extends AbstractKieCiTest {

    private FileManager fileManager;
    private File kPom;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");
        kPom = createKPom(fileManager, releaseId);
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
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, kPom);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");

        // deploy it on maven
        repository.deployArtifact(releaseId, kJar2, kPom);

        // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
        KieScanner scanner = ks.newKieScanner(kieContainer);

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();

        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");

        ks.getRepository().removeKieModule(releaseId);
    }

    @Test @Ignore
    public void testKScannerWithRange() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-test", "1.0.1");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-test", "1.0.2");
        ReleaseId releaseRange = ks.newReleaseId("org.kie", "scanner-test", "[1.0.0,)");

        InternalKieModule kJar1 = createKieJar(ks, releaseId1, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseRange);

        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId1, kJar1, kPom);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId2, "rule2", "rule3");

        // deploy it on maven
        repository.deployArtifact(releaseId2, kJar2, kPom);

        // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
        InternalKieScanner scanner = (InternalKieScanner) ks.newKieScanner(kieContainer);
        assertEquals(releaseId1, scanner.getCurrentReleaseId());
        assertEquals(InternalKieScanner.Status.STOPPED, scanner.getStatus());

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();
        assertEquals(releaseId2, scanner.getCurrentReleaseId());
        assertEquals(InternalKieScanner.Status.STOPPED, scanner.getStatus());

        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
    }

    @Test @Ignore
    public void testKScannerWithKJarContainingClasses() throws Exception {
        testKScannerWithType(false);
    }

    @Test @Ignore
    public void testKScannerWithKJarContainingTypeDeclaration() throws Exception {
        testKScannerWithType(true);
    }

    private void testKScannerWithType(boolean useTypeDeclaration) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7);

        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, kPom);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieScanner scanner = ks.newKieScanner(kieContainer);

        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 14);

        InternalKieModule kJar2 = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 3, 5);
        repository.deployArtifact(releaseId, kJar2, kPom);

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);

        ks.getRepository().removeKieModule(releaseId);
    }

    @Test @Ignore
    public void testLoadKieJarFromMavenRepo() throws Exception {
        // This test depends from the former one (UGLY!) and must be run immediately after it
        KieServices ks = KieServices.Factory.get();

        KieContainer kieContainer = ks.newKieContainer(ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT"));

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);
    }

    @Test @Ignore
    public void testScannerOnPomProject() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-test", "1.0");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-test", "2.0");

        MavenRepository repository = getMavenRepository();
        repository.deployPomArtifact("org.kie", "scanner-master-test", "1.0", createMasterKPom());

        resetFileManager();

        InternalKieModule kJar1 = createKieJarWithClass(ks, releaseId1, false, 2, 7);
        repository.deployArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        KieContainer kieContainer = ks.newKieContainer(ks.newReleaseId("org.kie", "scanner-master-test", "LATEST"));
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 14);

        KieScanner scanner = ks.newKieScanner(kieContainer);

        InternalKieModule kJar2 = createKieJarWithClass(ks, releaseId2, false, 3, 5);
        repository.deployArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId1));

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);
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

    @Test
    public void testKieScannerOnClasspathContainerMustFail() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = ks.getKieClasspathContainer();
        try {
            KieScanner scanner = ks.newKieScanner(kieContainer);
            fail("Creating a KieScanner from a KieClasspathContainer must fail");
        } catch (RuntimeException e) { }
    }

    @Test @Ignore
    public void testTypeAndRuleInDifferentKieModules() throws Exception {
        KieServices ks = KieServices.Factory.get();

        ReleaseId depId = ks.newReleaseId("org.kie", "test-types", "1.0");
        InternalKieModule kJar1 = createKieJarWithType(ks, depId);
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(depId, kJar1, kPom);

        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-rules", "1.0");
        InternalKieModule kieModule = createKieJarWithRules(ks, releaseId, depId);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 15);
    }

    private InternalKieModule createKieJarWithType(KieServices ks, ReleaseId releaseId) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.write("src/main/resources/KBase1/r1.drl", "package org.kie.test\n" + getDRLWithType());

        kfs.writePomXML( getPom(releaseId) );

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return ( InternalKieModule ) kieBuilder.getKieModule();
    }

    private InternalKieModule createKieJarWithRules(KieServices ks, ReleaseId releaseId, ReleaseId depId) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.write("src/main/resources/KBase1/r1.drl", "package org.kie.test\n" + getDRLWithRules(3, 5));

        kfs.writePomXML( getPom(releaseId, depId) );

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        kieBuilder.buildAll();
        assertTrue(kieBuilder.getResults().getMessages().isEmpty());
        return ( InternalKieModule ) kieBuilder.getKieModule();
    }
}
