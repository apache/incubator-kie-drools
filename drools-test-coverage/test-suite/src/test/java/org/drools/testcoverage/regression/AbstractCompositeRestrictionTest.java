package org.drools.testcoverage.regression;

import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Bugfix test for bz#724655 'NPE in AbstractCompositionRestriction when using
 * unbound variables'
 */
@RunWith(Parameterized.class)
public class AbstractCompositeRestrictionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AbstractCompositeRestrictionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void test() {

        final KieBuilder builder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false,
                KieServices.Factory.get().getResources().newClassPathResource("abstractCompositeRestrictionTest.drl", getClass()));

        final List<Message> msgs = builder.getResults().getMessages();

        final String[] lines = msgs.get(0).getText().split("\n");
        final String unable = "Unable to Analyse Expression valueType == Field.INT || valueType == Field.DOUBLE:";
        assertThat(lines[0]).isEqualTo(unable);
    }
}
