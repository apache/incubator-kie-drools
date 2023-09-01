package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Child;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class InTest extends BaseModelTest {

    public InTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testInWithNullFire() {
        String str = "import " + Child.class.getCanonicalName() + "; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (\"Gustav\", \"Alice\", null))\n" +
                "then                               \n" +
                "end                                ";

        KieSession ksession = getKieSession(str);
        ksession.insert(new Child("Ben", 10));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testInWithNullNoFire() {
        String str = "import " + Child.class.getCanonicalName() + "; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (\"Gustav\", \"Alice\"))\n" +
                "then                               \n" +
                "end                                ";

        KieSession ksession = getKieSession(str);
        ksession.insert(new Child("Ben", 10));
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @Test
    public void testInWithNullComments() {
        String str = "import " + Child.class.getCanonicalName() + "; \n" +
                "global java.util.List results; \n" +
                "global java.util.List results; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (" +
                "   \"Gustav\", // comment\n" +
                "   \"Alice\"\n" +
                "))\n" +
                "then                               \n" +
                " results.add($c);\n" +
                "end                                ";

        KieSession ksession = getKieSession(str);
        List<Child> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        String parentName = "Gustav";
        Person gustav = new Person(parentName);
        Child ben = new Child("Ben", 10, parentName);
        ksession.insert(ben);
        ksession.insert(gustav);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results).containsExactly(ben);
    }

    @Test
    public void testInWithJoin() {
        String str = "import " + Address.class.getCanonicalName() + "; \n" +
                "rule R when \n" +
                "    $a1: Address($street: street, city in (\"Brno\", \"Milan\", \"Bratislava\")) \n" +
                "    $a2: Address(city in (\"Krakow\", \"Paris\", $a1.city)) \n" +
                "then \n" +
                "end\n";

        KieSession ksession = getKieSession(str);
        ksession.insert(new Address("Brno"));
        ksession.insert(new Address("Milan"));
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }
}
