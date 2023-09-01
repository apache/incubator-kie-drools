package org.drools.testcoverage.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.event.DefaultRuleRuntimeEventListener;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FactHandleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FactHandleTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }



    @Test
    public void testFactHandleSequence() throws Exception {
        String drlString = "package org.jboss.brms\n" +
                "import " +  Cheese.class.getCanonicalName() + ";\n" +
                "rule \"FactHandleId\"\n" +
                "    when\n" +
                "        $c : Cheese()\n" +
                "    then\n" +
                "        // do something;\n" +
                "end";

        KieBase kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drlString);

        List<Long> factHandleIDs = new ArrayList<>();

        KieSession kieSession = kBase.newKieSession();
        kieSession.addEventListener(createCollectEventListener(factHandleIDs));

        kieSession.insert(new Cheese("mozzarella"));
        kieSession.insert(new Cheese("pecorino"));

        kieSession.fireAllRules();
        kieSession.dispose();

        // This should reset Fact Handle IDs
        kieSession = kBase.newKieSession();
        kieSession.addEventListener(createCollectEventListener(factHandleIDs));

        kieSession.insert(new Cheese("parmigiano"));

        kieSession.fireAllRules();

        assertThat(factHandleIDs).containsExactly(1L, 2L, 1L);
    }

    private DefaultRuleRuntimeEventListener createCollectEventListener(List<Long> factHandleIDs) {
        return new DefaultRuleRuntimeEventListener() {
            public void objectInserted(ObjectInsertedEvent event) {
                InternalFactHandle ifh = (InternalFactHandle) event.getFactHandle();
                factHandleIDs.add(ifh.getId());
            }
        };
    }
}
