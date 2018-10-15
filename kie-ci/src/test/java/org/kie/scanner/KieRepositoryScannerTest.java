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

package org.kie.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.appformer.maven.integration.MavenRepository;
import org.appformer.maven.integration.embedder.MavenEmbedderUtils;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieScanner;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.drools.core.util.FileManager;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.event.kiescanner.KieScannerEvent;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.kie.api.event.kiescanner.KieScannerStatusChangeEvent;
import org.kie.api.event.kiescanner.KieScannerUpdateResultsEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.drools.compiler.kie.builder.impl.event.KieScannerStatusChangeEventImpl;
import org.drools.compiler.kie.builder.impl.event.KieScannerUpdateResultsEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;

import static org.appformer.maven.integration.MavenRepository.getMavenRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;


@RunWith(Parameterized.class)
public class KieRepositoryScannerTest extends AbstractKieCiTest {
    private static final Logger LOG = LoggerFactory.getLogger(KieRepositoryScannerTest.class);
    
    private final boolean useWiredComponentProvider;

    private FileManager fileManager;

    @Parameterized.Parameters(name = "Manually wired component provider={0}")
    public static Collection modes() {
        Object[][] manuallyWiredProvider = new Object[][] {
                { true },
                { false }
        };
        return Arrays.asList(manuallyWiredProvider);
    }

    public KieRepositoryScannerTest( boolean useWiredComponentProvider) {
        this.useWiredComponentProvider = useWiredComponentProvider;
    }

    @Before
    public void setUp() throws Exception {
        MavenEmbedderUtils.enforceWiredComponentProvider = useWiredComponentProvider;
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
        MavenEmbedderUtils.enforceWiredComponentProvider = false;
    }

    private void resetFileManager() {
        this.fileManager.tearDown();
        this.fileManager = new FileManager();
        this.fileManager.setUp();
    }

    @Test
    public void testKScanner() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");

        // deploy it on maven
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));

        // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
        KieScanner scanner = ks.newKieScanner(kieContainer);

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();

        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");

        ks.getRepository().removeKieModule(releaseId);
    }

    @Test @Ignore("used only for check performances")
    public void testKScannerWithDependencies() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseIdNoDep = ks.newReleaseId( "org.kie", "test-no-dep", "1.0-SNAPSHOT" );
        ReleaseId releaseIdWithDep = ks.newReleaseId( "org.kie", "test-with-dep", "1.0-SNAPSHOT" );

        long start = System.nanoTime();
        InternalKieModule kJar1 = createKieJar( ks, releaseIdNoDep, false, "rule1" );
        KieContainer kieContainer1 = ks.newKieContainer( releaseIdNoDep );
        System.out.println("done in " + (System.nanoTime() - start));

        ReleaseId dep1 = ks.newReleaseId( "org.slf4j", "slf4j-api", "1.7.2" );
        ReleaseId dep2 = ks.newReleaseId( "com.google.gwt", "gwt-user", "2.7.0" );
        ReleaseId dep3 = ks.newReleaseId( "org.hibernate", "hibernate-validator", "4.1.0.Final" );

        start = System.nanoTime();
        InternalKieModule kJar2 = createKieJarWithDependencies( ks, releaseIdWithDep, false, "rule1", dep1, dep2, dep3);
        KieContainer kieContainer2 = ks.newKieContainer( releaseIdWithDep );
        System.out.println("done in " + (System.nanoTime() - start));
    }

    @Test @Ignore("avoid use external dependency")
    public void testKScannerWithTransitiveInclusion() throws Exception {
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>org.kie</groupId>\n" +
                "  <artifactId>test-with-inc</artifactId>\n" +
                "  <version>1.0-SNAPSHOT</version>\n" +
                "  \n" +
                "        <dependencies>\n" +
                "            <dependency>\n" +
                "                <groupId>io.swagger</groupId>\n" +
                "                <artifactId>swagger-jaxrs</artifactId>\n" +
                "                <version>1.5.0</version>\n" +
                "            </dependency>\n" +
                "        </dependencies>\n" +
                "</project>";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseIdWithExc = ks.newReleaseId( "org.kie", "test-with-inc", "1.0-SNAPSHOT" );
        InternalKieModule kJar = createKieJarWithPom( ks, releaseIdWithExc, false, "rule1", pom);
        KieContainer kieContainer = ks.newKieContainer( releaseIdWithExc );

        try {
            Class.forName("com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider", true, kieContainer.getClassLoader());
        } catch (ClassNotFoundException e) {
            fail("the transitive dependency class should be present");
        }
    }

    @Test @Ignore("avoid use external dependency")
    public void testKScannerWithExclusion() throws Exception {
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>org.kie</groupId>\n" +
                "  <artifactId>test-with-exc</artifactId>\n" +
                "  <version>1.0-SNAPSHOT</version>\n" +
                "  \n" +
                "        <dependencies>\n" +
                "            <dependency>\n" +
                "                <groupId>io.swagger</groupId>\n" +
                "                <artifactId>swagger-jaxrs</artifactId>\n" +
                "                <version>1.5.0</version>\n" +
                "                <exclusions>\n" +
                "                    <exclusion>\n" +
                "                        <groupId>com.fasterxml.jackson.jaxrs</groupId>\n" +
                "                        <artifactId>*</artifactId>\n" +
                "                    </exclusion>\n" +
                "                </exclusions>\n" +
                "            </dependency>\n" +
                "        </dependencies>\n" +
                "</project>";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseIdWithExc = ks.newReleaseId( "org.kie", "test-with-exc", "1.0-SNAPSHOT" );
        InternalKieModule kJar = createKieJarWithPom( ks, releaseIdWithExc, false, "rule1", pom);
        KieContainer kieContainer = ks.newKieContainer( releaseIdWithExc );

        try {
            Class.forName("com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider", true, kieContainer.getClassLoader());
            fail("the excluded class shouldn't be present");
        } catch (ClassNotFoundException e) {
        }
    }

    @Test
    public void testKieScannerScopesNotRequired() throws Exception {
        MavenRepository repository = getMavenRepository();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseIdWithDep = ks.newReleaseId( "org.kie", "test-with-dep", "1.0-SNAPSHOT" );

        Dependency dep1 = dependencyWithScope( "org.kie", "test-dep1-in-test-scope"    , "1.0-SNAPSHOT", "test"     );
        Dependency dep2 = dependencyWithScope( "org.kie", "test-dep2-in-provided-scope", "1.0-SNAPSHOT", "provided" );
        Dependency dep3 = dependencyWithScope( "org.kie", "test-dep3-in-system-scope"  , "1.0-SNAPSHOT", "system"   );
        dep3.setSystemPath(fileManager.getRootDirectory().getAbsolutePath());
        // to ensure KieBuilder is able to build correctly:
        repository.installArtifact(ks.newReleaseId("org.kie", "test-dep3-in-system-scope", "1.0-SNAPSHOT"), new byte[]{}, new byte[]{});
        
        InternalKieModule kJar2 = createKieJarWithDependencies( ks, releaseIdWithDep, false, "rule1", dep1, dep2, dep3);
        KieContainer kieContainer = ks.newKieContainer( releaseIdWithDep );
        
        // DROOLS-1051 following should not throw NPE because dependencies are not in scope of scanner
        KieScanner scanner = ks.newKieScanner(kieContainer);
        assertNotNull(scanner);
    }

    @Test
    public void testKScannerStartNotDeployed() throws Exception {
        // BZ-1200784
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-start-not-deployed-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        // starting KieScanner
        KieScanner scanner = ks.newKieScanner(kieContainer);

        // scan the maven repo to get the new kjar version before it is deployed into Maven repo
        // should not throw NPE because of uninitialized dependencies due to parsing parent pom failure
        scanner.scanNow();
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");
    }

    @Test
    public void testKScannerWithRange() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-range-test", "1.0.1");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-range-test", "1.0.2");
        ReleaseId releaseRange = ks.newReleaseId("org.kie", "scanner-range-test", "[1.0.0,)");

        InternalKieModule kJar1 = createKieJar(ks, releaseId1, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseRange);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId2, "rule2", "rule3");

        // deploy it on maven
        repository.installArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId2));

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

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
    }
    
    @Test
    public void testKScannerWithRange_withListener() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieMavenRepository repository = getKieMavenRepository();
        
        String artifactId = "scanner-range-test-withlistener";
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", artifactId, "1.0.1");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", artifactId, "1.0.2");

        // Because KieScanner also check the local repo, ensure the artifact are not deployed on the local maven repo
        repository.removeLocalArtifact( releaseId1 );
        repository.removeLocalArtifact( releaseId2 );
        
        ReleaseId releaseRange = ks.newReleaseId("org.kie", artifactId, "[1.0.0,)");

        InternalKieModule kJar1 = createKieJar(ks, releaseId1, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseRange);

        List<KieScannerEvent> events = new ArrayList<>();
        InternalKieScanner scanner = (KieRepositoryScannerImpl) ks.newKieScanner(kieContainer);
        KieScannerEventListener listener = new KieScannerEventListener() {
            public void onKieScannerStatusChangeEvent(KieScannerStatusChangeEvent statusChange) {
                LOG.info("KieScanner event: {}", statusChange); events.add(statusChange);
            }
            public void onKieScannerUpdateResultsEvent(KieScannerUpdateResultsEvent updtaeResults) {
                LOG.info("KieScanner event: {}", updtaeResults); events.add(updtaeResults);
            }
        };
        scanner.addListener( listener );        
        
        scanner.scanNow();
        // Because 1.0.2 does not exist, will perform a scan but will NOT update.
        assertThat( events, CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.SCANNING) ) );
        assertThat( events, CoreMatchers.not( CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.UPDATING) )) );
        events.clear();
        
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar 
        InternalKieModule kJar2 = createKieJar(ks, releaseId2, "rule2", "rule3");

        // deploy it on maven
        repository.installArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId2));

        // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
        assertEquals(releaseId1, scanner.getCurrentReleaseId());
        assertEquals(InternalKieScanner.Status.STOPPED, scanner.getStatus());

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();
        assertEquals(releaseId2, scanner.getCurrentReleaseId());
        assertEquals(InternalKieScanner.Status.STOPPED, scanner.getStatus());

        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
        
        assertThat( events, CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.SCANNING) ) );
        assertThat( events, CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.UPDATING) ) );
        assertTrue( events.get(2) instanceof KieScannerUpdateResultsEventImpl );
        assertFalse( ((KieScannerUpdateResultsEventImpl)events.get(2)).getResults().hasMessages(Message.Level.ERROR) );
        events.clear();

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
        
        scanner.removeListener( listener );
        assertEquals( 0, scanner.getListeners().size() );
        repository.removeLocalArtifact( releaseId1 );
        repository.removeLocalArtifact( releaseId2 );
    }
    
    @Test
    public void testKScannerWithRange_withListener_errors() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieMavenRepository repository = getKieMavenRepository();
        
        String artifactId = "scanner-range-test-withlistener-errors";
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", artifactId, "1.0.1");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", artifactId, "1.0.2");

        // Because KieScanner also check the local repo, ensure the artifact are not deployed on the local maven repo
        repository.removeLocalArtifact( releaseId1 );
        repository.removeLocalArtifact( releaseId2 );
        
        ReleaseId releaseRange = ks.newReleaseId("org.kie", artifactId, "[1.0.0,)");

        InternalKieModule kJar1 = createKieJar(ks, releaseId1, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseRange);

        List<KieScannerEvent> events = new ArrayList<>();
        InternalKieScanner scanner = (KieRepositoryScannerImpl) ks.newKieScanner(kieContainer);
        KieScannerEventListener listener = new KieScannerEventListener() {
            public void onKieScannerStatusChangeEvent(KieScannerStatusChangeEvent statusChange) {
                LOG.info("KieScanner event: {}", statusChange); events.add(statusChange);
            }
            public void onKieScannerUpdateResultsEvent(KieScannerUpdateResultsEvent updtaeResults) {
                LOG.info("KieScanner event: {}", updtaeResults); events.add(updtaeResults);
            }
        };
        scanner.addListener( listener );        
        
        scanner.scanNow();
        // Because 1.0.2 does not exist, will perform a scan but will NOT update.
        assertThat( events, CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.SCANNING) ) );
        assertThat( events, CoreMatchers.not( CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.UPDATING) )) );
        events.clear();
        
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // Create a wrong KJAR for releaseId2
        KieFileSystem kfs = ks.newKieFileSystem();
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>" + releaseId2.getGroupId() + "</groupId>\n" +
                "  <artifactId>" + releaseId2.getArtifactId() + "</artifactId>\n" +
                "  <version>" + releaseId2.getVersion() + "</version></project>\n";
        kfs.writePomXML(pomContent);
        kfs.write("rulebroken" + ".drl", "rule X when asd asd then end" );
        kfs.write("META-INF/kmodule.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n" + 
                "  <!-- This is a minimal kmodule.xml. Ref Drools documentation, see http://docs.jboss.org/drools/release/6.1.0.Final/drools-docs/html_single/index.html#d0e1112 -->\n" + 
                "</kmodule>\n");
        File tempJar = File.createTempFile("org.kie"+artifactId+"1.0.2", "tmp");
        LOG.info("Writing to temp file: {}", tempJar);
        // please notice that writeAsJar would return a new file with .jar postfixed
        tempJar = ((KieFileSystemImpl)kfs).asMemoryFileSystem().writeAsJar(tempJar.getParentFile(), tempJar.getName());
        File tempPom = File.createTempFile("org.kie"+artifactId+"1.0.2", "tmp.xml");
        LOG.info("Writing to temp file: {}", tempPom);
        FileUtils.writeStringToFile(tempPom, pomContent, "UTF-8");
        repository.installArtifact(releaseId2, tempJar, tempPom);
        
        assertEquals(releaseId1, scanner.getCurrentReleaseId());
        assertEquals(InternalKieScanner.Status.STOPPED, scanner.getStatus());

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();
        
        // Keeping at previous release
        assertEquals(releaseId1, scanner.getCurrentReleaseId());
        assertEquals(InternalKieScanner.Status.STOPPED, scanner.getStatus());

        // there should be no update performed.
        
        assertThat( events, CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.SCANNING) ) );
        assertThat( events, CoreMatchers.hasItem( new KieScannerStatusChangeEventImpl(KieScanner.Status.UPDATING) ) );
        assertTrue( events.get(2) instanceof KieScannerUpdateResultsEventImpl );
        assertTrue( ((KieScannerUpdateResultsEventImpl)events.get(2)).getResults().hasMessages(Message.Level.ERROR) );
        events.clear();

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
        
        scanner.removeListener( listener );
        assertEquals( 0, scanner.getListeners().size() );
        repository.removeLocalArtifact( releaseId1 );
        repository.removeLocalArtifact( releaseId2 );
    }
    
    

    @Test
    public void testKScannerWithKJarContainingClasses() throws Exception {
        testKScannerWithType(false);
    }

    @Test
    public void testKScannerWithKJarContainingTypeDeclaration() throws Exception {
        testKScannerWithType(true);
    }

    private void testKScannerWithType(boolean useTypeDeclaration) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieScanner scanner = ks.newKieScanner(kieContainer);

        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 14);

        InternalKieModule kJar2 = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 3, 5);
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);

        ks.getRepository().removeKieModule(releaseId);
    }

    @Test
    public void testKScannerWithFunction() throws Exception {
        String drl1 =
                "global java.util.List list;\n" +
                "\n" +
                "function boolean doSomething(String name) {\n" +
                "    return true ;\n" +
                "}\n" +
                " \n" +
                "rule R1 when\n" +
                "    $s : String( )\n" +
                "    eval(doSomething($s))\n" +
                "then\n" +
                "    list.add(\"XXX:\" + $s);\n" +
                "end";

        String drl2 =
                "global java.util.List list;\n" +
                "\n" +
                "function boolean doSomething(String name) {\n" +
                "    return true ;\n" +
                "}\n" +
                " \n" +
                "rule R1 when\n" +
                "    $s : String( )\n" +
                "    eval(doSomething($s))\n" +
                "then\n" +
                "    list.add(\"YYY:\" + $s);\n" +
                "end";

        checkUpdateDRLInSameSession(drl1, drl2);
    }

    @Test
    public void testKScannerWithNewFunction() throws Exception {
        String drl1 =
                "global java.util.List list;\n" +
                "\n" +
                " \n" +
                "rule R1 when\n" +
                "    $s : String( )\n" +
                "then\n" +
                "    list.add(\"XXX:\" + $s);\n" +
                "end";

        String drl2 =
                "global java.util.List list;\n" +
                "\n" +
                "function boolean doSomething(String name) {\n" +
                "    return true ;\n" +
                "}\n" +
                " \n" +
                "rule R1 when\n" +
                "    $s : String( )\n" +
                "    eval(doSomething($s))\n" +
                "then\n" +
                "    list.add(\"YYY:\" + $s);\n" +
                "end";

        checkUpdateDRLInSameSession(drl1, drl2);
    }

    private void checkUpdateDRLInSameSession(String drl1, String drl2) throws IOException {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJarFromDrl(ks, releaseId, drl1);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieScanner scanner = ks.newKieScanner(kieContainer);

        KieSession ksession = kieContainer.newKieSession("KSession1");

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.insert("111");
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("XXX:111", list.get(0));
        list.clear();

        InternalKieModule kJar2 = createKieJarFromDrl(ks, releaseId, drl2);
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));

        scanner.scanNow();

        ksession.insert("222");
        ksession.fireAllRules();
        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList("YYY:111", "YYY:222")));

        ks.getRepository().removeKieModule(releaseId);
    }

    private InternalKieModule createKieJarFromDrl(KieServices ks, ReleaseId releaseId, String drl) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, false);
        kfs.writePomXML(getPom(releaseId));

        kfs.write("src/main/resources/KBase1/rule1.drl", drl);

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    @Test
    public void testLoadKieJarFromMavenRepo() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        final String drl =
                " global java.util.List list; \n" +
                " rule R1 \n" +
                " when \n" +
                "     not(String()) \n" +
                " then \n" +
                "     list.add(15);\n" +
                " end ";

        InternalKieModule kJar1 = createKieJarFromDrl(ks, releaseId, drl);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);

        ks.getRepository().removeKieModule(releaseId);
    }

    @Test
    public void testScannerOnPomProject() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-test", "1.0");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-test", "2.0");

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployPomArtifact("org.kie", "scanner-master-test", "1.0", createMasterKPom("scanner-test"));

        resetFileManager();

        InternalKieModule kJar1 = createKieJarWithClass(ks, releaseId1, false, 2, 7);
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        KieContainer kieContainer = ks.newKieContainer(ks.newReleaseId("org.kie", "scanner-master-test", "LATEST"));
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 14);

        KieScanner scanner = ks.newKieScanner(kieContainer);

        InternalKieModule kJar2 = createKieJarWithClass(ks, releaseId2, false, 3, 5);
        repository.installArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId1));

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
    }

    @Test
    public void testScannerOnPomProjectWithFixedVersion() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-test", "1.0");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-test", "2.0");

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployPomArtifact("org.kie", "scanner-master-test", "1.0", createMasterKPom("scanner-test", "1.0"));

        resetFileManager();

        InternalKieModule kJar1 = createKieJarWithClass(ks, releaseId1, false, 2, 7);
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        KieContainer kieContainer = ks.newKieContainer(ks.newReleaseId("org.kie", "scanner-master-test", "LATEST"));
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, 14);

        KieScanner scanner = ks.newKieScanner(kieContainer);

        repository.deployPomArtifact("org.kie", "scanner-master-test", "2.0", createMasterKPom("scanner-test", "2.0"));
        InternalKieModule kJar2 = createKieJarWithClass(ks, releaseId2, false, 3, 5);
        repository.installArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId1));

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, 15);

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
    }

    @Test
    public void testScannerOnPomProjectSameKieSession() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-test", "1.0");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-test", "2.0");

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployPomArtifact("org.kie", "scanner-master-test", "1.0", createMasterKPom("scanner-test", "1.0"));

        resetFileManager();

        InternalKieModule kJar1 = createKieJarWithClass(ks, releaseId1, true, 2, 7);
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        KieContainer kieContainer = ks.newKieContainer(ks.newReleaseId("org.kie", "scanner-master-test", "LATEST"));
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(false, ksession, 14);

        KieScanner scanner = ks.newKieScanner(kieContainer);

        repository.deployPomArtifact("org.kie", "scanner-master-test", "2.0", createMasterKPom("scanner-test", "2.0"));
        InternalKieModule kJar2 = createKieJarWithClass(ks, releaseId2, true, 3, 5);
        repository.installArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId1));

        scanner.scanNow();

        checkKSession(ksession, 10, 15);

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
    }

    private File createMasterKPom(String depArtifactId) throws IOException {
        return createMasterKPom(depArtifactId, "LATEST");
    }

    private File createMasterKPom(String depArtifactId, String depVersion) throws IOException {
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
                "        <artifactId>" + depArtifactId + "</artifactId>\n" +
                "        <version>" + depVersion + "</version>\n" +
                "      </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";

        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, pom);
        return pomFile;
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

    @Test
    public void testTypeAndRuleInDifferentKieModules() throws Exception {
        KieServices ks = KieServices.Factory.get();

        ReleaseId depId = ks.newReleaseId("org.kie", "test-types", "1.0");
        InternalKieModule kJar1 = createKieJarWithType(ks, depId);
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(depId, kJar1, createKPom(fileManager, depId));

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

    @Test
    public void testScannerOnPomRuleProject() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-test", "1.0");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-test", "2.0");

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployPomArtifact("org.kie", "scanner-master-test", "1.0", createMasterKPom("scanner-test"));

        resetFileManager();

        InternalKieModule kJar1 = createKieJar(ks, releaseId1, "rule1");
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));

        KieContainer kieContainer = ks.newKieContainer(ks.newReleaseId("org.kie", "scanner-master-test", "LATEST"));
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1");

        KieScanner scanner = ks.newKieScanner(kieContainer);

        InternalKieModule kJar2 = createKieJar(ks, releaseId2, "rule2");
        repository.installArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId2));

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2");

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
    }

    @Test
    public void testMissingDependency() throws Exception {
        KieServices ks = KieServices.Factory.get();
        MavenRepository repository = getMavenRepository();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0");
        ReleaseId missingDep = ks.newReleaseId("org.kie", "missing-dep", "1.0");

        KieFileSystem kfs = createKieFileSystemWithKProject(ks, false);
        kfs.writePomXML(getPom(releaseId, missingDep));

        kfs.write("src/main/resources/KBase1/rule1.drl", createDRLWithTypeDeclaration(1, 1));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).toString().contains("missing-dep"));

        ks.getRepository().removeKieModule(releaseId);
    }

    @Test
    public void testScanIncludedDependency() throws Exception {
        KieMavenRepository repository = getKieMavenRepository();
        KieServices ks = KieServices.Factory.get();

        ReleaseId containerReleaseId = KieServices.Factory.get().newReleaseId( "org.kie", "test-container", "1.0.0-SNAPSHOT" );
        ReleaseId includedReleaseId = KieServices.Factory.get().newReleaseId( "org.kie", "test-project", "1.0.0-SNAPSHOT" );

        InternalKieModule kJar1 = createKieJar(ks, includedReleaseId, "rule1");
        repository.installArtifact(includedReleaseId, kJar1, createKPom(fileManager, includedReleaseId));

        resetFileManager();

        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel("KBase2").addInclude("KBase1").newKieSessionModel("KSession2");
        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML(getPom(containerReleaseId, includedReleaseId));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        InternalKieModule containerKJar = (InternalKieModule) kieBuilder.getKieModule();
        repository.installArtifact(containerReleaseId, containerKJar, createKPom(fileManager, containerReleaseId, includedReleaseId));

        KieContainer kieContainer = ks.newKieContainer(containerReleaseId);
        KieSession ksession = kieContainer.newKieSession("KSession2");
        checkKSession(ksession, "rule1");

        KieScanner scanner = ks.newKieScanner(kieContainer);

        InternalKieModule kJar2 = createKieJar(ks, includedReleaseId, "rule2");
        repository.installArtifact(includedReleaseId, kJar2, createKPom(fileManager, includedReleaseId));

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession2");
        checkKSession(ksession2, "rule2");

        ks.getRepository().removeKieModule(containerReleaseId);
        ks.getRepository().removeKieModule(includedReleaseId);
    }

    @Test @Ignore
    public void testScanIncludedAndIncludingDependency() throws Exception {
        KieMavenRepository repository = getKieMavenRepository();
        KieServices ks = KieServices.Factory.get();

        ReleaseId containerReleaseId = KieServices.Factory.get().newReleaseId( "org.kie", "test-container", "1.0.0-SNAPSHOT" );
        ReleaseId includedReleaseId = KieServices.Factory.get().newReleaseId( "org.kie", "test-project", "1.0.0-SNAPSHOT" );

        InternalKieModule kJar1 = createKieJar(ks, includedReleaseId, "rule1");
        repository.installArtifact(includedReleaseId, kJar1, createKPom(fileManager, includedReleaseId));

        resetFileManager();

        InternalKieModule containerKJar = createIncludingKJar(containerReleaseId, includedReleaseId, "ruleX");
        repository.installArtifact(containerReleaseId, containerKJar, createKPom(fileManager, containerReleaseId, includedReleaseId));

        KieContainer kieContainer = ks.newKieContainer(containerReleaseId);
        KieSession ksession = kieContainer.newKieSession("KSession2");
        checkKSession(ksession, "rule1", "ruleX");

        resetFileManager();

        KieScanner scanner = ks.newKieScanner(kieContainer);

        InternalKieModule kJar2 = createKieJar(ks, includedReleaseId, "rule2");
        repository.installArtifact(includedReleaseId, kJar2, createKPom(fileManager, includedReleaseId));
        resetFileManager();

        InternalKieModule containerKJar2 = createIncludingKJar(containerReleaseId, includedReleaseId, "ruleY");
        repository.installArtifact(containerReleaseId, containerKJar2, createKPom(fileManager, containerReleaseId, includedReleaseId));
        resetFileManager();

        scanner.scanNow();

        KieSession ksession2 = kieContainer.newKieSession("KSession2");
        checkKSession(ksession2, "rule2", "ruleY");

        ks.getRepository().removeKieModule(containerReleaseId);
        ks.getRepository().removeKieModule(includedReleaseId);
    }

    private InternalKieModule createIncludingKJar(ReleaseId containerReleaseId, ReleaseId includedReleaseId, String rule) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        String file = "org/test/" + rule + ".drl";
        kfs.write("src/main/resources/KBase2/" + file, createDRL(rule));

        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel("KBase2").addInclude("KBase1").newKieSessionModel("KSession2");
        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML(getPom(containerReleaseId, includedReleaseId));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    @Test
    public void testKScannerWithGDRL() throws Exception {
        // BZ-1310261
        String rule1 =
                "import java.util.List;\n" +
                "rule R when\n" +
                "  $s : String()\n" +
                "then\n" +
                "  list.add(\"Hello \" + $s);\n" +
                "end";

        String rule2 =
                "import java.util.List;\n" +
                "rule R when\n" +
                "  $s : String()\n" +
                "then\n" +
                "  list.add(\"Hi \" + $s);\n" +
                "end";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJarWithGDRL(ks, releaseId, rule1);
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.insert( "Mario" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "Hello Mario", list.get(0) );
        list.clear();

        ksession.dispose();

        // create a new kjar
        InternalKieModule kJar2 = createKieJarWithGDRL(ks, releaseId, rule2);

        // deploy it on maven
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));

        // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
        KieScanner scanner = ks.newKieScanner(kieContainer);

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();

        // create a ksesion and check it works as expected
        // create a ksesion and check it works as expected
        ksession = kieContainer.newKieSession("KSession1");
        list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.insert( "Mario" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "Hi Mario", list.get(0) );
        list.clear();

        ksession.dispose();

        ks.getRepository().removeKieModule(releaseId);
    }

    private InternalKieModule createKieJarWithGDRL(KieServices ks, ReleaseId releaseId, String rule) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, false);
        kfs.writePomXML(getPom(releaseId));

        kfs.write("src/main/resources/rule.drl", rule);
        kfs.write("src/main/resources/global.gdrl", "global java.util.List list;");

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    @Test
    public void testKJarContainingClassLoadedFromClassLoaderSameKBase() throws Exception {
        testKScannerWithKJarContainingClassLoadedFromClassLoader(false);
    }

    @Test
    public void testKJarContainingClassLoadedFromClassLoaderDifferentKBase() throws Exception {
        testKScannerWithKJarContainingClassLoadedFromClassLoader(true);
    }

    @Test
    public void testKScannerStartScanNow() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        KieScanner scanner = ks.newKieScanner(kieContainer);

        // Starting scanner with 200ms interval.
        scanner.start(100);
        try {
            // Manual scan, should continue interval scanning afterwards.
            scanner.scanNow();

            InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");

            repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));

            // Check each 100ms until scanner finds the new kjar (or timeout is reached)
            long timeSpent = 0;
            while (timeSpent <= 5000) {
                // create a ksession and check the ksession produces the expected results
                KieSession ksession2 = kieContainer.newKieSession("KSession1");
                if (producesResults(ksession2, "rule2", "rule3")) {
                    break;
                }
                Thread.sleep(100);
                timeSpent += 100;
            }
        } finally {
            scanner.stop();
            ks.getRepository().removeKieModule(releaseId);
        }
    }



    private void testKScannerWithKJarContainingClassLoadedFromClassLoader(boolean differentKbases) throws Exception {
        // DROOLS-1231
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJarWithJavaClass(ks, releaseId, "KBase1", 7);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));
        ks.getRepository().removeKieModule(releaseId);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieScanner scanner = ks.newKieScanner(kieContainer);

        Class<?> beanClass = kieContainer.getClassLoader().loadClass( "org.kie.test.Bean" );
        KieSession ksession = kieContainer.newKieSession("KSession1");

        Object bean = beanClass.getDeclaredConstructors()[0].newInstance( 2 );
        ksession.insert( bean );

        checkKSession(ksession, 14);

        InternalKieModule kJar2 = createKieJarWithJavaClass(ks, releaseId, differentKbases ? "KBase2" : "KBase1", 5);
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));
        ks.getRepository().removeKieModule(releaseId);

        // forces cache of old Bean class definition before updating to newer release
        Class<?> beanClass1 = Class.forName( "org.kie.test.Bean", true, kieContainer.getClassLoader() );

        kieContainer.updateToVersion( releaseId );

        beanClass = kieContainer.getClassLoader().loadClass( "org.kie.test.Bean" );
        KieSession ksession2 = kieContainer.newKieSession("KSession1");

        bean = beanClass.getDeclaredConstructors()[0].newInstance( 3 );
        ksession2.insert( bean );

        checkKSession(ksession2, 15);

        ks.getRepository().removeKieModule(releaseId);
    }

    private InternalKieModule createKieJarWithJavaClass(KieServices ks, ReleaseId releaseId, String kbaseName, int factor) throws IOException {
        String drl = "package org.kie.test.drl\n" +
                     "import org.kie.test.Bean\n" +
                     "global java.util.List list\n" +
                     "rule R1\n" +
                     "when\n" +
                     "   $b : Bean( value > 0 )\n" +
                     "then\n" +
                     "   list.add( $b.getValue() );\n" +
                     "end\n";

        KieFileSystem kfs = createKieFileSystemWithKProject(ks, false, kbaseName, "KSession1");
        kfs.writePomXML(getPom(releaseId));

        kfs.write("src/main/resources/KBase1/rule1.drl", drl)
           .write("src/main/java/org/kie/test/Bean.java", createJavaSource(factor));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    @Test
    public void testReleaseDowngrade() throws Exception {
        // DROOLS-1720
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "downgrade-test", "1.1");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "downgrade-test", "1.2");
        ReleaseId kcRelId = ks.newReleaseId("org.kie", "downgrade-test", "[1.0,)");

        final String drl1 =
                "global java.util.List list; \n" +
                "rule R1 when then \n" +
                "    list.add(1);\n" +
                "end ";

        final String drl2 =
                "global java.util.List list; \n" +
                "rule R1 when then \n" +
                "    list.add(2);\n" +
                "end ";

        InternalKieModule kJar1 = createKieJarFromDrl(ks, releaseId1, drl1);
        InternalKieModule kJar2 = createKieJarFromDrl(ks, releaseId2, drl2);

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId1, kJar1, createKPom(fileManager, releaseId1));
        repository.installArtifact(releaseId2, kJar2, createKPom(fileManager, releaseId2));

        KieContainer kieContainer = ks.newKieContainer(kcRelId);
        checkKSession(kieContainer.newKieSession("KSession1"), 2);

        kieContainer.updateToVersion( releaseId1 );
        checkKSession(kieContainer.newKieSession("KSession1"), 1);

        KieScanner kieScanner = ks.newKieScanner( kieContainer );
        kieScanner.scanNow();
        checkKSession(kieContainer.newKieSession("KSession1"), 2);

        kieContainer.updateToVersion( releaseId1 );
        checkKSession(kieContainer.newKieSession("KSession1"), 1);

        kieScanner.scanNow();
        checkKSession(kieContainer.newKieSession("KSession1"), 2);

        ks.getRepository().removeKieModule(releaseId1);
        ks.getRepository().removeKieModule(releaseId2);
    }

    @Test
    public void testKeepDefaultOnTopLevelProject() throws Exception {
        // DROOLS-1740
        KieMavenRepository repository = getKieMavenRepository();
        KieServices ks = KieServices.get();

        ReleaseId depReleaseId = ks.newReleaseId( "org.kie", "test-dep", "1.0.0-SNAPSHOT" );
        KieFileSystem kfs1 = ks.newKieFileSystem();
        KieModuleModel kproj1 = ks.newKieModuleModel();
        kproj1.newKieBaseModel("KBase1").setDefault( true ).newKieSessionModel("KSession1").setDefault( true );
        kfs1.writeKModuleXML(kproj1.toXML());
        kfs1.writePomXML(getPom(depReleaseId));
        kfs1.write("src/main/resources/org/test/rule1.drl", createDRL("rule1"));

        KieBuilder kieBuilder1 = ks.newKieBuilder(kfs1);
        assertTrue(kieBuilder1.buildAll().getResults().getMessages().isEmpty());
        InternalKieModule kJar1 = (InternalKieModule) kieBuilder1.getKieModule();
        repository.installArtifact(depReleaseId, kJar1, createKPom(fileManager, depReleaseId));

        ReleaseId topReleaseId = KieServices.Factory.get().newReleaseId( "org.kie", "test-top", "1.0.0-SNAPSHOT" );
        KieFileSystem kfs2 = ks.newKieFileSystem();
        KieModuleModel kproj2 = ks.newKieModuleModel();
        kproj2.newKieBaseModel("KBase2").setDefault( true ).newKieSessionModel("KSession2").setDefault( true );
        kfs2.writeKModuleXML(kproj2.toXML());
        kfs2.writePomXML(getPom(topReleaseId, depReleaseId));
        kfs2.write("src/main/resources/org/test/rule2.drl", createDRL("rule2"));

        KieBuilder kieBuilder2 = ks.newKieBuilder(kfs2);
        assertTrue(kieBuilder2.buildAll().getResults().getMessages().isEmpty());
        InternalKieModule kJar2 = (InternalKieModule) kieBuilder2.getKieModule();
        repository.installArtifact(topReleaseId, kJar2, createKPom(fileManager, topReleaseId, depReleaseId));

        KieContainer kieContainer = ks.newKieContainer(topReleaseId);
        KieSession ksession = kieContainer.newKieSession();
        checkKSession(ksession, "rule2");

        kieContainer = ks.newKieContainer(depReleaseId);
        ksession = kieContainer.newKieSession();
        checkKSession(ksession, "rule1");
    }

    @Test
    public void testKScannerNewContainer() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        ks.getRepository().removeKieModule( releaseId );

        KieContainer kieContainer = ks.newKieContainer(releaseId);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");

        // deploy it on maven
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));

        ks.getRepository().removeKieModule( releaseId );

        // create new KieContainer
        KieContainer kieContainer2 = ks.newKieContainer(releaseId);

        // create a ksession for the new container and check it works as expected
        KieSession ksession2 = kieContainer2.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");

        ks.getRepository().removeKieModule(releaseId);
    }
}