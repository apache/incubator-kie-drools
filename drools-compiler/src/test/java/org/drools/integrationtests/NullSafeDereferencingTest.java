package org.drools.integrationtests;

import org.drools.Address;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.Person;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class NullSafeDereferencingTest extends CommonTestMethodBase {

    @Test
    public void testNullSafeBinding() {
        String str = "import org.drools.*;\n" +
                "rule R1 when\n" +
                "   Person( $streetName : address!.street ) \n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertEquals(2, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNullSafeNullComparison() {
        String str = "import org.drools.*;\n" +
                "rule R1 when\n" +
                "   Person( address!.street == null ) \n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNullSafeNullComparisonReverse() {
        // DROOLS-82
        String str =
                "import org.drools.*;\n" +
                "rule R1 when\n" +
                " Person( \"Main Street\".equalsIgnoreCase(address!.street) )\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNullSafeNullComparisonReverseComplex() {
        // DROOLS-82
        String str =
                "import org.drools.*;\n" +
                "rule R1 when\n" +
                " Person( \"Main\".equalsIgnoreCase(address!.street!.substring(0, address!.street!.indexOf(' '))) )\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testDoubleNullSafe() {
        String str = "import org.drools.*;\n" +
                "rule R1 when\n" +
                "   Person( address!.street!.length > 15 ) \n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        Person alex = new Person("Alex", 34);
        alex.setAddress(new Address("The Main Very Big Street"));
        ksession.insert(alex);

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNullSafeMemberOf() {
        // DROOLS-50
        String str =
                "declare A\n" +
                "    list : java.util.List\n" +
                "end\n" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "    insert( new A( java.util.Arrays.asList( \"test\" ) ) );" +
                "    insert( \"test\" );" +
                "end\n" +
                "rule R when\n" +
                "    $a : A()\n" +
                "    $s : String( this memberOf $a!.list )\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        assertEquals(2, ksession.fireAllRules());
        ksession.dispose();
    }
}