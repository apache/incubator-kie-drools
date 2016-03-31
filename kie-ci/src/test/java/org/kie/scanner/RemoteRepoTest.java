/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.apache.commons.io.FileUtils;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;

import java.io.File;

public class RemoteRepoTest extends AbstractKieCiTest {

    private File tmpDir = new File("target");

    @Test
    // BZ-1319808
    public void testGetSnapshotAsLATESTFromRemoteRepo() throws Exception {
        KieServices ks = KieServices.Factory.get();
        String groupId = "org.kie.test";
        String artifactId = "remote-repo-test-kjar";
        String version = "1.0.0-SNAPSHOT";

        Artifact jarArtifact = new DefaultArtifact(groupId, artifactId, "jar", version);
        // arbitrary jar file needed
        File jarFile = new File(getClass().getResource("/kjar/simple-kjar.jar").getFile());
        jarArtifact = jarArtifact.setFile(jarFile);

        Artifact pomXMLArtifact = new DefaultArtifact(groupId, artifactId, "pom", version);
        File pomXMLFile = new File(tmpDir, artifactId + "-" + version + ".pom");
        String pom = getPom(new ReleaseIdImpl(groupId, artifactId, version));
        FileUtils.write(pomXMLFile, pom);
        pomXMLArtifact = pomXMLArtifact.setFile(pomXMLFile);

        File remoteRepoDir = new File(tmpDir, "remote-repo");
        // make sure the repo is empty to avoid stale content
        FileUtils.deleteQuietly(remoteRepoDir);
        RemoteRepository remoteRepo = new RemoteRepository.Builder("remote-repo", "default", "file://" + remoteRepoDir.getAbsolutePath()).build();

        final DeployRequest deployRequest = new DeployRequest();
        deployRequest
                .addArtifact(jarArtifact)
                .addArtifact(pomXMLArtifact)
                .setRepository(remoteRepo);

        Aether.getAether().getSystem().deploy(Aether.getAether().getSession(), deployRequest);

        MavenRepository mavenRepository = new MavenRepository(Aether.getAether());
        mavenRepository.addRemoteRepository(remoteRepo);

        Artifact artifact = mavenRepository.resolveArtifact(ks.newReleaseId(groupId, artifactId, "LATEST"));
        Assert.assertNotNull("Can not resolve artifact " + groupId + ":" + artifactId + ":LATEST", artifact);
    }

}
