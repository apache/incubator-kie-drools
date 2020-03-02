/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.hacep;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.MavenRepository;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.hacep.consumer.FactHandlesManager;
import org.kie.hacep.consumer.KieContainerUtils;
import org.kie.hacep.core.KieSessionContext;
import org.kie.hacep.core.infra.SnapshotInfos;
import org.kie.hacep.util.GAVUtils;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;

public class UpdatableKieSessionTest {

    private final String gav = "org.kie:sample-hacep-project-kjar:800.Final";
    private final String updatedGav = "org.kie:sample-hacep-project-kjar:900.Final";
    private KieSessionContext ksCtx;
    private KieSession kieSession;
    private KieContainer kieContainer;
    private KieServices ks;

    @Before
    public void init() throws Exception {
        ks = KieServices.get();
        String drl1 = new String(Files.readAllBytes(Paths.get("target/test-classes/org/pkg1/drl1.drl")));
        String drl2 = new String(Files.readAllBytes(Paths.get("target/test-classes/org/pkg1/drl2.drl")));
        createAndDeployKJar(GAVUtils.getReleaseID(gav, KieServices.get()),
                            Collections.singletonMap("src/main/resources/org/pkg1/r0.drl", drl1));
        createAndDeployKJar(GAVUtils.getReleaseID(updatedGav, KieServices.get()),
                            Collections.singletonMap("src/main/resources/org/pkg1/r0.drl", drl2));
        ksCtx = new KieSessionContext();
    }

    private void initSessionContextFromEmbeddKjar() {
        EnvConfig envConfig = EnvConfig.getDefaultEnvConfig();
        kieContainer = KieContainerUtils.getKieContainer(envConfig, ks);
        kieSession = kieContainer.newKieSession();
        ksCtx.init(kieContainer, kieSession);
    }

    private void initSessionContextFromSpecificKjar() {
        EnvConfig envConfig = EnvConfig.getDefaultEnvConfig();
        envConfig.withUpdatableKJar("true");
        envConfig.withKJarGAV(gav);
        envConfig.skipOnDemandSnapshot("true");
        kieContainer = KieContainerUtils.getKieContainer(envConfig, ks);
        kieSession = kieContainer.newKieSession();
        ksCtx.init(kieContainer, kieSession);
    }

    @Test
    public void testEmbeddedKJar() {
        initSessionContextFromEmbeddKjar();
        Optional<String> gavUSed = ksCtx.getKjarGAVUsed();
        assertFalse(gavUSed.isPresent());
    }

    @Test
    public void testWithSpecificKJar() {
        initSessionContextFromSpecificKjar();
        Optional<String> gavUSed = ksCtx.getKjarGAVUsed();
        assertTrue(gavUSed.isPresent());
        assertEquals(gavUSed.get(), gav);
    }

    @Test
    public void testUpdateWithSpecificKJar() {
        initSessionContextFromSpecificKjar();
        Optional<String> gavUSed = ksCtx.getKjarGAVUsed();
        assertTrue(gavUSed.isPresent());
        assertEquals(gavUSed.get(), gav);
        ReleaseId releaseId = GAVUtils.getReleaseID(updatedGav, ks);
        ksCtx.getKieContainer().updateToVersion(releaseId);
        gavUSed = ksCtx.getKjarGAVUsed();
        assertTrue(gavUSed.isPresent());
        assertEquals(updatedGav, gavUSed.get());
    }

    @Test(expected = IllegalStateException.class)
    public void setClockWithoutPseudoClockTest() {
        initSessionContextFromSpecificKjar();
        ksCtx.setClockAt(1000l);
    }

    @Test
    public void initFromASnapshotTest() {
        EnvConfig envConfig = EnvConfig.getDefaultEnvConfig();
        envConfig.withUpdatableKJar("true");
        envConfig.withKJarGAV(gav);
        envConfig.skipOnDemandSnapshot("true");
        KieServices ks = KieServices.get();
        assertNotNull(ks);
        KieContainer kieContainer = KieContainerUtils.getKieContainer(envConfig, ks);
        assertNotNull(kieContainer);
        KieSession kieSession = kieContainer.newKieSession();
        assertNotNull(kieSession);
        FactHandlesManager fhManager = new FactHandlesManager(kieSession);
        assertNotNull(fhManager);

        String keyDuringSnapshot = "111";
        long offsetDuringSnapshot = 10l;
        LocalDateTime time = LocalDateTime.now();
        String kjarGAV = "org.kie:fake:1.0.0.Snapshot";
        SnapshotInfos infos = new SnapshotInfos(kieSession,
                                                kieContainer,
                                                fhManager,
                                                keyDuringSnapshot,
                                                offsetDuringSnapshot,
                                                time,
                                                kjarGAV);
        ksCtx.initFromSnapshot(infos);

        assertNotNull(ksCtx.getKieContainer());
        assertNotNull(ksCtx.getKieSession());
    }

    public void createAndDeployKJar(ReleaseId releaseId, Map<String, String> files) {
        KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML(releaseId);

        for (Map.Entry<String, String> file : files.entrySet()) {
            kfs.write(file.getKey(), file.getValue());
        }
        ks.newKieBuilder(kfs).buildAll();
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule(releaseId);
        getRepository(ks).installArtifact(releaseId,
                                          kieModule.getBytes(),
                                          kfs.read("pom.xml"));
        ks.getRepository().removeKieModule(releaseId);
    }

    public MavenRepository getRepository(KieServices srv) {
        ReleaseId initReleaseId = GAVUtils.getReleaseID("org.kie.server.initial:init-maven-repo:42", srv);
        KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML(initReleaseId);
        MavenProject minimalMavenProject = MavenProjectLoader.parseMavenPom(new ByteArrayInputStream(kfs.read("pom.xml")));
        return KieMavenRepository.getKieMavenRepository(minimalMavenProject);
    }
}
