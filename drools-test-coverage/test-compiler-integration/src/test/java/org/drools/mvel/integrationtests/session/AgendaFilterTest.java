package org.drools.mvel.integrationtests.session;

import java.util.Collection;

import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.DirectFiringOption;
import org.kie.api.runtime.rule.AgendaFilter;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class AgendaFilterTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AgendaFilterTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testAgendaFilterRuleNameStartsWith() {
        testAgendaFilter(new RuleNameStartsWithAgendaFilter("B"), "Bbb");
    }

    @Test
    public void testAgendaFilterRuleNameEndsWith() {
        testAgendaFilter(new RuleNameEndsWithAgendaFilter("a"), "Aaa");
    }

    @Test
    public void testAgendaFilterRuleNameMatches() {
        testAgendaFilter(new RuleNameMatchesAgendaFilter(".*b."), "Bbb");
    }

    @Test
    public void testAgendaFilterRuleNameEquals() {
        testAgendaFilter(new RuleNameEqualsAgendaFilter("Aaa"), "Aaa");
    }

    private void testAgendaFilter(final AgendaFilter agendaFilter, final String expectedMatchingRuleName) {
        final String str = "package org.drools.compiler\n" +
                "rule Aaa when then end\n" +
                "rule Bbb when then end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);

        final int rules = ksession.fireAllRules(agendaFilter);
        assertThat(rules).isEqualTo(1);

        final ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> arg = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael).afterMatchFired(arg.capture());
        assertThat(arg.getValue().getMatch().getRule().getName()).isEqualTo(expectedMatchingRuleName);
    }

    @Test
    public void testDirectFiringIgnoresAgendaFilter() {
        // DROOLS-6510
        String str =
                "rule R when\n" +
                "  String() \n" +
                "then\n" +
                "  throw new IllegalStateException();\n" +
                "end";
        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
            KieSessionConfiguration config = KieServices.get().newKieSessionConfiguration();
            config.setOption(DirectFiringOption.YES);
            Environment environment = KieServices.get().newEnvironment();
            KieSession ksession = kbase.newKieSession(config, environment);
            ksession.insert("Lukas");
            assertThat(ksession.fireAllRules(match -> false)).isEqualTo(0);
        } catch (Throwable ex) {
            fail("Should not have thrown.", ex);
        }
    }
}
