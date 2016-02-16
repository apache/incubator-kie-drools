package org.drools.testcoverage.kieci.withoutdomain;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.kieci.withoutdomain.util.KJarLoadUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Tests loading a KJAR with non-trivial pom.xml (dependencies, parent pom, ...).
 *
 * Tests must NOT have access to domain classes in test-domain module (BZ 1305798).
 */
public class KJarLoadingTest {

    private static final KieServices KS = KieServices.Factory.get();

    private static final ReleaseId KJAR_RELEASE_ID = KJarLoadUtils.loadKJarGAV("testKJarGAV.properties", KJarLoadingTest.class);

    private KieSession kieSession;

    @Before
    public void init() {
        final KieContainer container = KS.newKieContainer(KJAR_RELEASE_ID, this.getClass().getClassLoader());
        this.kieSession = container.newKieSession();
    }

    @After
    public void dispose() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    @Test
    @Ignore("BZ 1305798")
    public void testLoadingKJarWithDeps() {
        // BZ 1305798
        Assertions.assertThat(this.kieSession).as("Failed to create KieSession.").isNotNull();
        Assertions.assertThat(this.kieSession.getKieBase().getKiePackages()).as("No rules compiled.").isNotEmpty();
    }
}
