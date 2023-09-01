package org.drools.testcoverage.regression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieSession;

/**
 * Tests handling a null value in a list used in FROM (BZ 1093174).
 */
@RunWith(Parameterized.class)
public class NullInListInFromTest {

    private static final String DRL =
            "global java.util.List list\n" +
            "\n" +
            "rule R\n" +
            "when\n" +
            "    $i : Integer( ) from list\n" +
            "then\n" +
            "end\n";

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public NullInListInFromTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testNullValueInFrom() {
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, DRL);

        final KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kbuilder);
        final KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        list.add(1);
        list.add(null);
        list.add(2);

        try {
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }
}
