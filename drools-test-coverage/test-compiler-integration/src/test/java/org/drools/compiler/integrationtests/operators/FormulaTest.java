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
public class FormulaTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FormulaTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testConstants() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test formula constraint constants\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "    $person : Person( age == ( 2 + 3 ) )\n" +
                "then\n" +
                "    $person.setLikes( \"toys\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("formula-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person person = new Person();
            person.setAge(5);

            ksession.insert(person);
            assertThat(ksession.fireAllRules()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBoundField() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test formula constraint constants\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "    $person : Person( $age : age < ( (2 * 4) + 10 ) )\n" +
                "then\n" +
                "    $person.setLikes( \"likes cheese this old: \" + $age );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("formula-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person person = new Person();
            person.setAge(10);

            ksession.insert(person);
            assertThat(ksession.fireAllRules()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

}
