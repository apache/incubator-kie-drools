package org.drools.compiler.integrationtests;

import java.util.Collection;

import org.drools.testcoverage.common.model.Cell;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CellTest extends AbstractCellTest {

    public CellTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testFreeFormExpressions() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Cell.class.getCanonicalName() + "\n" +
                "rule r1\n" +
                "when\n" +
                "    $p1 : Cell( row == 2 )\n" +
                "    $p2 : Cell( row == $p1.row + 1, row == ($p1.row + 1), row == 1 + $p1.row, row == (1 + $p1.row) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cell-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cell c1 = new Cell(1, 2, 0);
            final Cell c2 = new Cell(1, 3, 0);
            ksession.insert(c1);
            ksession.insert(c2);

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }
}
