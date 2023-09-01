package org.drools.compiler.integrationtests.operators;

import java.util.Collection;

import org.drools.testcoverage.common.model.Person;
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
public class InTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public InTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testInOperator() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test in\"\n" +
                "when\n" +
                "    Person( $name : name in (\"bob\", \"mark\") )\n" +
                "then\n" +
                "    boolean test = $name != null;" +
                "end\n" +
                "rule \"test not in\"\n" +
                "when\n" +
                "    Person( $name : name not in (\"joe\", \"doe\") )\n" +
                "then\n" +
                "    boolean test = $name != null;" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("in-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person person = new Person("bob");
            ksession.insert(person);

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNegatedIn() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test negated in\"\n" +
                "when\n" +
                "    Person( !(name in (\"joe\", \"doe\")) )\n" +
                "then\n" +
                "end\n" +
                "rule \"test not negated in\"\n" +
                "when\n" +
                "    Person( !(name not in (\"bob\", \"mark\")) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("in-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person person = new Person("bob");
            ksession.insert(person);

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }
}
