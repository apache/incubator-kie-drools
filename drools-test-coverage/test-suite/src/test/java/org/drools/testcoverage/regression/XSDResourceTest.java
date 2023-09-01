package org.drools.testcoverage.regression;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests KIE package compilation when there is a XSD resource (BZ 1120972).
 */
public class XSDResourceTest {

    /**
     * Verifies that a XSD resource on the classpath does not break KIE package compilation.
     */
    @Test
    public void testXSDResourceNotBreakingCompilation() {
        final KieContainer kcontainer = KieServices.Factory.get().getKieClasspathContainer();

        assertThat(kcontainer.getKieBase("kbaseXsdResource"))
                .as("Created KieBase with XSD should not be null").isNotNull();
    }
}