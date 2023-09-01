package org.drools.testcoverage.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class InternalMatchGroupTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public InternalMatchGroupTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    /**
     * Only one rule from activation group fires. 
     */
    @Test
    public void basicTestActivationGroup() {
        TrackingAgendaEventListener listener = prepareKSession("basicActivationGroup");

        assertThat(listener.isRuleFired("basic1")).isFalse();
        assertThat(listener.isRuleFired("basic2")).isTrue(); 
        assertThat(listener.isRuleFired("basic3")).isFalse();
    }
    
    @Test
    public void recursiveTestActivationGroup() {
        TrackingAgendaEventListener listener = prepareKSession("recursiveActivationGroup");
        
        assertThat(listener.isRuleFired("simplyRecursive1")).isFalse();
        assertThat(listener.isRuleFired("simplyRecursive2")).isTrue();
        assertThat(listener.isRuleFired("simplyRecursive3")).isTrue();
    }
    
    @Test
    public void testActivationGroupWithDefaultSalience() {
        TrackingAgendaEventListener listener = prepareKSession("defaultSalienceActivationGroup");
        
        assertThat(listener.rulesCount()).isEqualTo(1);
    }
    
    @Test
    public void testActivationGroupRecursivelyWithDefaultSalience() {
        TrackingAgendaEventListener listener = prepareKSession("defaultSalienceWithRecursion");
        
        assertThat(listener.rulesCount()).isEqualTo(2);
    }
    
    private TrackingAgendaEventListener prepareKSession(String startingRule) {
        List<Command<?>> commands = new ArrayList<Command<?>>();
        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();

        final KieSession ksession = getKieBaseForTest().newKieSession();
        try {
            ksession.addEventListener(listener);

            ksession.insert(startingRule);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
        return listener;
    }

    private KieBase getKieBaseForTest() {
        final Resource drlResource =
                KieServices.Factory.get().getResources().newClassPathResource("activation-group.drl", getClass());
        return KieBaseUtil.getKieBaseFromKieModuleFromResources(TestConstants.PACKAGE_FUNCTIONAL,
                                                                kieBaseTestConfiguration, drlResource);
    }

}
