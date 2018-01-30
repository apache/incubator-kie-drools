package org.drools.modelcompiler.oopathlogicalbranchestest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.BaseModelTest;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class OOPathTest extends BaseModelTest {

    public OOPathTest(BaseModelTest.RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testOOPathMultipleConditions() {
        final String drl =
                "import org.drools.modelcompiler.oopathlogicalbranchestest.Employee;\n" +
                "import org.drools.modelcompiler.oopathlogicalbranchestest.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: /address[ street == 'Elm', city == 'Big City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = this.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = this.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        Assertions.assertThat(results).containsExactlyInAnyOrder("Big City");
    }

    @Test
    public void testOOPathMultipleConditionsWithBinding() {
        final String drl =
                "import org.drools.modelcompiler.oopathlogicalbranchestest.Employee;\n" +
                "import org.drools.modelcompiler.oopathlogicalbranchestest.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $employee: (\n" +
                "  Employee( /address[ street == 'Elm', city == 'Big City' ] )\n" +
                " )\n" +
                "then\n" +
                "  list.add( $employee.getName() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = this.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = this.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        Assertions.assertThat(results).containsExactlyInAnyOrder("Alice");
    }

    @Test
    @Ignore
    public void testOrConditionalElementNoBinding() {
        final String drl =
                "import org.drools.modelcompiler.oopathlogicalbranchestest.Employee;\n" +
                "import org.drools.modelcompiler.oopathlogicalbranchestest.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $employee: (\n" +
                "  Employee( /address[ street == 'Elm', city == 'Big City' ] )\n" +
                " or " +
                "  Employee( /address[ street == 'Elm', city == 'Small City' ] )\n" +
                " )\n" +
                "then\n" +
                "  list.add( $employee.getName() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = this.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = this.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        Assertions.assertThat(results).containsExactlyInAnyOrder("Bruno", "Alice");
    }

    @Test
    @Ignore
    public void testOrConditionalElement() {
        final String drl =
                "import org.drools.modelcompiler.oopathlogicalbranchestest.Employee;\n" +
                        "import org.drools.modelcompiler.oopathlogicalbranchestest.Address;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Employee( $address: /address[ street == 'Elm', city == 'Big City' ] )\n" +
                        " or " +
                        "  Employee( $address: /address[ street == 'Elm', city == 'Small City' ] )\n" +
                        "then\n" +
                        "  list.add( $address.getCity() );\n" +
                        "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = this.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = this.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        Assertions.assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    private Employee createEmployee(final String name, final Address address) {
        final Employee employee = new Employee(name);
        employee.setAddress(address);
        return employee;
    }
}
