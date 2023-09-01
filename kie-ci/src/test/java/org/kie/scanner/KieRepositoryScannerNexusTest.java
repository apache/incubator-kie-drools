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
package org.kie.scanner;

import java.io.File;
import java.io.IOException;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

/*
 * Test to verify handling of SNAPSHORT artifacts with a remote Nexus repository
 *
 * Prerequisites for this test:
 *
 * 1. Download and extract https://sonatype-download.global.ssl.fastly.net/nexus/3/nexus-3.6.0-02-unix.tar.gz
 * 2. Start the Nexus server
 * $ ./bin/nexus start
 * 3. Note that this test uses 'http://localhost:8081' as nexus target, with the default nexus user name 'admin' and password 'admin123'
 *
 */
@Ignore("ignored because it needs a running nexus server")
public class KieRepositoryScannerNexusTest extends AbstractKieCiTest {
    private static final Logger LOG = LoggerFactory.getLogger(KieRepositoryScannerNexusTest.class);

    private FileManager fileManager;

    @Before
    public void setUp() throws Exception {
        System.setProperty("kie.maven.settings.custom", new File("target/test-classes/org/kie/scanner/settings_nexus.xml").getAbsolutePath());
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");
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


    @Test
    public void testKScannerNewContainer() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("com.redhat.gss", "scanner-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, getPomWithDistributionManagement(releaseId), true, "rule1", "rule2");

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, createKPomWithDistributionManagement(fileManager, releaseId));

        // remove kjar from KieRepo
        ks.getRepository().removeKieModule( releaseId );

        KieContainer kieContainer = ks.newKieContainer(releaseId);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");
        
        // Store the module originally being loaded from Nexus to re-insert it, after generating a new version
        KieModule firstModuleLoadedFromNexus = ks.getRepository().removeKieModule( releaseId );

        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, getPomWithDistributionManagement(releaseId), true, "rule2", "rule3");

        // deploy it on maven
        repository.deployArtifact(releaseId, kJar2, createKPomWithDistributionManagement(fileManager, releaseId));

        // remove kjar from KieRepo
        ks.getRepository().removeKieModule( releaseId );
        
        // Insert the module once again - should be an older snapshot than the second one and therefore be replaced when getting the second one
        ks.getRepository().addKieModule(firstModuleLoadedFromNexus);

        // create new KieContainer
        KieContainer kieContainer2 = ks.newKieContainer(releaseId);

        // create a ksession for the new container and check it works as expected
        KieSession ksession2 = kieContainer2.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");

        ks.getRepository().removeKieModule(releaseId);
    }

    protected String getPomWithDistributionManagement(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                        "  <modelVersion>4.0.0</modelVersion>\n" +
                        "\n" +
                        "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                        "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                        "  <version>" + releaseId.getVersion() + "</version>\n" +
                        "\n" +
                        "<distributionManagement>\n" +
                        "  <snapshotRepository>\n" +
                        "    <id>local-nexus</id>\n" +
                        "    <name>Local Nexus Instance</name>\n" +
                        "    <url>http://localhost:8081/repository/maven-snapshots/</url>\n" +
                        "    <layout>default</layout>\n" +
                        "   </snapshotRepository>\n" +
                        "</distributionManagement>\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }

    protected File createKPomWithDistributionManagement(FileManager fileManager, ReleaseId releaseId, ReleaseId... dependencies) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPomWithDistributionManagement(releaseId, dependencies));
        return pomFile;
    }

}
