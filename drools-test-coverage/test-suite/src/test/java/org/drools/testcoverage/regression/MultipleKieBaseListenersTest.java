package org.drools.testcoverage.regression;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.kiebase.DefaultKieBaseEventListener;
import org.kie.api.event.kiebase.KieBaseEventListener;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MultipleKieBaseListenersTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MultipleKieBaseListenersTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testKnowledgeBaseEventSupportLeak() throws Exception {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl(TestConstants.PACKAGE_REGRESSION,
                                                                           kieBaseTestConfiguration, "");

        KieBaseEventListener listener = new DefaultKieBaseEventListener();

        kieBase.addEventListener(listener);
        kieBase.addEventListener(listener);
        kieBase.addEventListener(listener);

        assertThat(kieBase.getKieBaseEventListeners().size()).isEqualTo(1);

        kieBase.removeEventListener(listener);

        assertThat(kieBase.getKieBaseEventListeners()).isEmpty();
    }

}