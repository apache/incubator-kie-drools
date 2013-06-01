package org.kie.scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.KieContainer;

import static org.junit.Assert.*;

public class KieModuleMavenTest extends AbstractKieCiTest {

    @Test
    public void testKieModuleFromMavenNoDependencies() throws Exception {
        final KieServices ks = new KieServicesImpl(){
            @Override
            public KieRepository getRepository() {
                return new KieRepositoryImpl(); // override repository to not store the artifact on deploy to trigger load from maven repo
            }
        };

        ReleaseId releaseId = ks.newReleaseId("org.kie", "maven-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, true, "rule1", "rule2");
        String pomText = getPom(releaseId, null);
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().deployArtifact(releaseId, kJar1, pomFile);


        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieBaseModel kbaseModel = ((KieContainerImpl)kieContainer).getKieProject().getDefaultKieBaseModel();
        assertNotNull("Default kbase was not found", kbaseModel);
        String kbaseName = kbaseModel.getName();
        assertEquals("KBase1", kbaseName);
    }

    @Test
    public void testKieModuleFromMavenWithDependencies() throws Exception {
        final KieServices ks = new KieServicesImpl(){
            @Override
            public KieRepository getRepository() {
                return new KieRepositoryImpl(); // override repository to not store the artifact on deploy to trigger load from maven repo
            }
        };

        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        ReleaseId releaseId = ks.newReleaseId("org.kie", "maven-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, true, "rule1", "rule2");
        String pomText = getPom(releaseId, dependency);
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().deployArtifact(releaseId, kJar1, pomFile);


        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieBaseModel kbaseModel = ((KieContainerImpl)kieContainer).getKieProject().getDefaultKieBaseModel();
        assertNotNull("Default kbase was not found", kbaseModel);
        String kbaseName = kbaseModel.getName();
        assertEquals("KBase1", kbaseName);
    }
}
