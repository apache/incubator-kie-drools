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
package org.drools.mvel.compiler.builder.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class KieFileSystemScannerTest {

    @Test
    public void testSnapshot() throws Exception {
        Path tempDir = Files.createTempDirectory(FileSystems.getDefault().getPath("./target"), null);
        File file = null;

        try {
            KieServices ks = KieServices.Factory.get();
            ReleaseId releaseId = ks.newReleaseId( "org.kie", "scanner-test", "1.0-SNAPSHOT" );

            createKieJar( ks, releaseId, "R1" );

            KieContainer kieContainer = ks.newKieContainer( releaseId );

            KieSession ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );

            KieScanner scanner = ks.newKieScanner( kieContainer, tempDir.toString() );
            scanner.scanNow();

            ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );

            file = write( createKieJar( ks, releaseId, "R2" ), tempDir, releaseId );
            ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );

            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R2" );
        } finally {
            if (file != null) {
                file.delete();
            }
            Files.delete(tempDir);
        }
    }

    @Test
    public void testFixedVersion() throws Exception {
        Path tempDir = Files.createTempDirectory(FileSystems.getDefault().getPath("./target"), null);
        File file2 = null;
        File file3 = null;

        try {
            KieServices ks = KieServices.Factory.get();
            ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "scanner-test", "1.0.0" );
            ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "scanner-test", "1.0.1" );
            ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "scanner-test", "1.1.0" );

            createKieJar( ks, releaseId1, "R1" );

            KieContainer kieContainer = ks.newKieContainer( releaseId1 );

            KieSession ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );

            KieScanner scanner = ks.newKieScanner( kieContainer, tempDir.toString() );
            scanner.scanNow();

            ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );

            file2 = write( createKieJar( ks, releaseId2, "R2" ), tempDir, releaseId2 );
            file3 = write( createKieJar( ks, releaseId2, "R3" ), tempDir, releaseId3 );
            ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );

            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R3" );
        } finally {
            if (file2 != null) {
                file2.delete();
            }
            if (file3 != null) {
                file3.delete();
            }
            Files.delete(tempDir);
        }
    }

    @Test
    public void testDoNotUpgradeOnOlderVersion() throws Exception {
        Path tempDir = Files.createTempDirectory(FileSystems.getDefault().getPath("./target"), null);
        File file2 = null;

        try {
            KieServices ks = KieServices.Factory.get();
            ReleaseId releaseIdNew = ks.newReleaseId( "org.kie", "scanner-test", "1.1.0" );
            ReleaseId releaseIdOld = ks.newReleaseId( "org.kie", "scanner-test", "1.0.0" );

            createKieJar( ks, releaseIdNew, "R1" );

            KieContainer kieContainer = ks.newKieContainer( releaseIdNew );

            KieSession ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );

            KieScanner scanner = ks.newKieScanner( kieContainer, tempDir.toString() );

            file2 = write( createKieJar( ks, releaseIdOld, "R2" ), tempDir, releaseIdOld );
            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession( ksession, "R1" );
        } finally {
            if (file2 != null) {
                file2.delete();
            }
            Files.delete(tempDir);
        }
    }

    private void checkKSession( KieSession ksession, Object... results ) {
        checkKSession(true, ksession, results);
    }

    private InternalKieModule createKieJar( KieServices ks, ReleaseId releaseId, String... rules) throws IOException {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML(getPom(releaseId));

        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertThat(kieBuilder.buildAll().getResults().getMessages().isEmpty()).isTrue();
        return (InternalKieModule) kieBuilder.getKieModule();
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

    private void checkKSession(boolean dispose, KieSession ksession, Object... results) {
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        if (dispose) {
            ksession.dispose();
        }

        assertThat(list.size()).isEqualTo(results.length);
        for (Object result : results) {
            assertThat(list.contains(result)).as(String.format("Expected to contain: %s, got: %s", result, Arrays.toString(list.toArray()))).isTrue();
        }
    }

    private File write( InternalKieModule kModule, Path tempDir, ReleaseId releaseId) {
        String fileName = releaseId.getArtifactId() + "-" + releaseId.getVersion() + ".jar";
        File file = new File(tempDir.toString(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(kModule.getBytes());
            fos.flush();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return file;
    }

    private String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        return
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
            "  <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
            "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
            "  <version>" + releaseId.getVersion() + "</version>\n" +
            "\n" +
            "</project>";
    }
}
