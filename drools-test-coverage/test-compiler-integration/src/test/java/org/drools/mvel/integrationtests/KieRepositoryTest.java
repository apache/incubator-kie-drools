package org.drools.mvel.integrationtests;

import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;

import static org.assertj.core.api.Assertions.assertThat;

public class KieRepositoryTest {

    @Test
    public void testLoadKjarFromClasspath() {
        // DROOLS-1335
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{this.getClass().getResource( "/kie-project-simple-1.0.0.jar" )} );
        Thread.currentThread().setContextClassLoader( urlClassLoader );

        try {
            KieServices ks = KieServices.Factory.get();
            KieRepository kieRepository = ks.getRepository();
            ReleaseId releaseId = ks.newReleaseId( "org.test", "kie-project-simple", "1.0.0" );
            KieModule kieModule = kieRepository.getKieModule( releaseId );
            assertThat(kieModule).isNotNull();
            assertThat(kieModule.getReleaseId()).isEqualTo(releaseId);
        } finally {
            Thread.currentThread().setContextClassLoader( cl );
        }
    }

    @Test
    public void testTryLoadNotExistingKjarFromClasspath() {
        // DROOLS-1335
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{this.getClass().getResource( "/kie-project-simple-1.0.0.jar" )} );
        Thread.currentThread().setContextClassLoader( urlClassLoader );

        try {
            KieServices ks = KieServices.Factory.get();
            KieRepository kieRepository = ks.getRepository();
            ReleaseId releaseId = ks.newReleaseId( "org.test", "kie-project-simple", "1.0.1" );
            KieModule kieModule = kieRepository.getKieModule( releaseId );
            assertThat(kieModule).isNull();
        } finally {
            Thread.currentThread().setContextClassLoader( cl );
        }
    }
    
    @Test
    public void testLoadingNotAKJar() {
        // DROOLS-1351
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{this.getClass().getResource( "/only-jar-pojo-not-kjar-no-kmodule-1.0.0.jar" )} );
        Thread.currentThread().setContextClassLoader( urlClassLoader );

        try {
            KieServices ks = KieServices.Factory.get();
            KieRepository kieRepository = ks.getRepository();
            ReleaseId releaseId = ks.newReleaseId( "org.test", "only-jar-pojo-not-kjar-no-kmodule", "1.0.0" );
            KieModule kieModule = kieRepository.getKieModule( releaseId );
            assertThat(kieModule).isNull();
        } finally {
            Thread.currentThread().setContextClassLoader( cl );
        }
    }
}
