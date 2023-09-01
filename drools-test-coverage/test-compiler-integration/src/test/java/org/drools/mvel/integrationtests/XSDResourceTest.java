package org.drools.mvel.integrationtests;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests KIE package compilation when there is a XSD resource (BZ 1120972) - manifests only when using
 * KieClasspathContainer.
 */
public class XSDResourceTest {

    @Test
    public void testXSDResourceNotBreakingCompilation() {
        final KieContainer kcontainer = KieServices.Factory.get().getKieClasspathContainer();
        final KieBase kieBase = kcontainer.getKieBase("xsdKieBase");

        assertThat(kieBase).as("Created KieBase with XSD should not be null").isNotNull();
    }

}
