package org.drools.testcoverage.kieci.withoutdomain;

import org.drools.testcoverage.kieci.withoutdomain.util.KJarLoadUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

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
        final KieContainer container = KS.newKieContainer(KJAR_RELEASE_ID);
        this.kieSession = container.newKieSession();
    }

    @After
    public void dispose() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    @Test
    public void testLoadingKJarWithDeps() {
        // BZ 1305798
        assertThat(this.kieSession).as("Failed to create KieSession.").isNotNull();
        assertThat(this.kieSession.getKieBase().getKiePackages()).as("No rules compiled.").isNotEmpty();
    }
}
