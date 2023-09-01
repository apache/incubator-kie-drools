package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ImmutableFactsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ImmutableFactsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_IMMUTABLE});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_IMMUTABLE_MODEL_PATTERN});
        return parameters;
    }

    @Test
    public void testAlphaConstraint() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    String()\n" +
                "    Person( name.startsWith(\"M\") )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("immutable-facts-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        try {
            Person p = new Person("Edson");
            FactHandle fh = ksession.insert(p);

            ksession.insert(new Person("Mark"));
            assertThat(ksession.fireAllRules()).isEqualTo(0);

            Collection<?> persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            // Person("Edson") won't match any rule so it can be automatically evicted
            assertThat(persons.size()).isEqualTo(1);
            assertThat(((Person) persons.iterator().next()).getName()).isEqualTo("Mark");

            ksession.insert("test");
            assertThat(ksession.fireAllRules()).isEqualTo(1);

            p.setName("Sofia");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(0);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(1);

            p.setName("Mario");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(1);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBetaConstraint() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $s: String()\n" +
                "    Person( name.startsWith($s) )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("immutable-facts-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        try {
            Person p = new Person("Edson");
            FactHandle fh = ksession.insert(p);

            ksession.insert(new Person("Mark"));
            assertThat(ksession.fireAllRules()).isEqualTo(0);

            Collection<?> persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            // this time nothing can be said about Person("Edson") so it cannot be automatically evicted
            assertThat(persons.size()).isEqualTo(2);

            ksession.insert("M");
            assertThat(ksession.fireAllRules()).isEqualTo(1);

            p.setName("Sofia");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(0);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(2);

            p.setName("Mario");
            ksession.update(fh, p);
            assertThat(ksession.fireAllRules()).isEqualTo(1);
            persons = ksession.getObjects(new ClassObjectFilter(Person.class));
            assertThat(persons.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }
}
