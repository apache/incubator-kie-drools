package org.drools.testcoverage.functional.oopath;

import java.util.Collection;

import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests basic usage of OOPath expressions.
 */
@RunWith(Parameterized.class)
public class OOPathSmokeTest {
    private static final KieServices KIE_SERVICES = KieServices.Factory.get();
    private static final ReleaseId RELEASE_ID = KIE_SERVICES.newReleaseId("org.drools.testcoverage.oopath", "marshalling-test", "1.0");

    private KieSession kieSession;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OOPathSmokeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @After
    public void disposeKieSession() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
            this.kieSession = null;
        }
    }

    @Test
    public void testBuildKieBase() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration,
                "oopath.drl");
        assertThat(kieBase).isNotNull();
    }

    @Test
    public void testBuildTwoKieBases() {
        final Resource drlResource = KIE_SERVICES.getResources().newClassPathResource("oopath.drl", this.getClass());
        KieUtil.getKieModuleFromResources(RELEASE_ID, KieBaseTestConfiguration.CLOUD_IDENTITY, drlResource);

        // creating two KieContainers and KieBases may trigger deep cloning
        for (int i = 0; i < 2; i++) {
            final KieContainer kieContainer = KIE_SERVICES.newKieContainer(RELEASE_ID);
            final KieBase kieBase = kieContainer.getKieBase();
            assertThat(kieBase).isNotNull();
        }
    }

    @Test
    public void testFireRule() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration,
                "oopath.drl");
        this.kieSession = kieBase.newKieSession();

        final Person person = new Person("Bruno", 21);
        person.setAddress(new Address("Some Street", 10, "Beautiful City"));
        this.kieSession.insert(person);
        assertThat(this.kieSession.fireAllRules()).isEqualTo(1);
    }

}
