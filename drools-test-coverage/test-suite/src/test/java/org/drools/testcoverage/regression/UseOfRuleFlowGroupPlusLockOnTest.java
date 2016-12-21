package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.core.common.DefaultAgenda;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import java.io.StringReader;

/**
 * Tests if ruleflow-group + lock-on-activate causes a ClassCastException.
 * bz845000
 */
// @Test(groups = { "bre", "regression" })
public class UseOfRuleFlowGroupPlusLockOnTest {

    private static final String DRL = "package org.drools.testcoverage.regression\n"
            + "import org.drools.testcoverage.regression.UseOfRuleFlowGroupPlusLockOnTest.Person;\n"
            + "import org.drools.testcoverage.regression.UseOfRuleFlowGroupPlusLockOnTest.Cheese;\n" + "rule R1\n"
            + "ruleflow-group \"group1\"\n" + "lock-on-active true\n" + "when\n" + "   $p : Person()\n" + "then\n"
            + "   $p.setName(\"John\");\n" + "   update ($p);\n" + "end\n" + "rule R2\n"
            + "ruleflow-group \"group1\"\n" + "lock-on-active true\n" + "when\n" + "   $p : Person( name == null )\n"
            + "   forall ( Cheese ( type == \"cheddar\" ))\n" + "then\n" + "end\n";

    @Test
    public void test() {

        final Resource resource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL));
        resource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBuilder kbuilder = KieBaseUtil.getKieBuilderFromResources(true, resource);

        final KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kbuilder);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person());
        ksession.insert(new Cheese("eidam"));
        ((DefaultAgenda) ksession.getAgenda()).activateRuleFlowGroup("group1");
        Assertions.assertThat(1).isEqualTo(ksession.fireAllRules());
        ksession.dispose();
    }

    public class Cheese {
        private String type;
        private int price;

        public Cheese() {
        }

        public Cheese(String type) {
            this.type = type;
        }

        public int getPrice() {
            return this.price;
        }

        public String getType() {
            return this.type;
        }

        public void setPrice(final int price) {
            this.price = price;
        }

    }

    public class Person {
        private String name;

        public Person() {
        }

        public Person(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "[Person name='" + this.name + "']";
        }
    }
}
