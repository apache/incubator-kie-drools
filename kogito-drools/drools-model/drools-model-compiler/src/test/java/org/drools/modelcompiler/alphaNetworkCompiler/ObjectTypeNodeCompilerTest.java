package org.drools.modelcompiler.alphaNetworkCompiler;

import java.math.BigDecimal;

import org.drools.modelcompiler.BaseModelTest;
import org.drools.modelcompiler.domain.ChildFactWithEnum1;
import org.drools.modelcompiler.domain.EnumFact1;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

public class ObjectTypeNodeCompilerTest extends BaseModelTest {

    public ObjectTypeNodeCompilerTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testAlphaConstraint() {
        String str =
                "rule \"Bind\"\n" +
                        "when\n" +
                        "  $s : String( length > 4, length < 10)\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintsSwitchString() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Luca\") \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Mario\") \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Matteo\") \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Luca"));
        ksession.insert(new Person("Asdrubale"));

        assertEquals(1, ksession.fireAllRules());
    }

    /*
        This generates the switch but not the inlining
     */
    @Test
    public void testAlphaConstraintsSwitchBigDecimal() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + BigDecimal.class.getCanonicalName() + ";" +
                        "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : Person( money == new BigDecimal(0)) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : Person( money == new BigDecimal(1)) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : Person( money == new BigDecimal(2)) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Luca", new BigDecimal(0)));
        ksession.insert(new Person("Asdrubale", new BigDecimal(10)));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintsSwitchPerson() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : Person( this == new Person(\"Luca\")) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : Person( this == new Person(\"Mario\")) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : Person( this == new Person(\"Matteo\")) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Luca"));
        ksession.insert(new Person("Asdrubale"));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintsSwitchIntegers() {
        String str =
                "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : String( length == 4) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : String( length == 5) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : String( length == 6) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testEnum() {
        String str =
                "import " + EnumFact1.class.getCanonicalName() + ";\n" +
                        "import " + ChildFactWithEnum1.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "    $factWithEnum : ChildFactWithEnum1(  enumValue == EnumFact1.FIRST ) \n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "    $factWithEnum : ChildFactWithEnum1(  enumValue == EnumFact1.SECOND ) \n" +
                        "then\n" +
                        "end\n" +
                        "rule R3 when\n" +
                        "    $factWithEnum : ChildFactWithEnum1(  enumValue == EnumFact1.THIRD ) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);
        ksession.insert(new ChildFactWithEnum1(1, 3, EnumFact1.FIRST));
        ksession.insert(new ChildFactWithEnum1(1, 3, EnumFact1.SECOND));
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintWithModification() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "rule \"Bind\"\n" +
                        "when\n" +
                        "  $r : Result()\n" +
                        "  $s : String( length > 4, length < 10)\n" +
                        "then\n" +
                        "  $r.setValue($s + \" is greater than 4 and smaller than 10\");\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        Result result = new Result();
        ksession.insert(result);

        assertEquals(1, ksession.fireAllRules());

        ksession.fireAllRules();
        assertEquals("Asdrubale is greater than 4 and smaller than 10", result.getValue());
    }

    @Test
    public void testModify() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Modify\"\n" +
                        "when\n" +
                        "  $p : Person( age == 30 )\n" +
                        "then\n" +
                        "   modify($p) { setName($p.getName() + \"30\"); }" +
                        "end";

        KieSession ksession = getKieSession(str);

        final Person luca = new Person("Luca", 30);
        ksession.insert(luca);

        assertEquals(1, ksession.fireAllRules());

        ksession.fireAllRules();
        assertEquals("Luca30", luca.getName());
    }

    @Test
    public void testModify2() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Modify\"\n" +
                        "when\n" +
                        "  $p : Person( age < 40 )\n" +
                        "then\n" +
                        "   modify($p) { setAge($p.getAge() + 1); }" +
                        "end";

        KieSession ksession = getKieSession(str);

        final Person luca = new Person("Luca", 30);
        ksession.insert(luca);

        Result result = new Result();
        ksession.insert(result);

        assertEquals(10, ksession.fireAllRules());

        ksession.fireAllRules();
        assertTrue(luca.getAge() == 40);
    }

    @Test
    public void testAlphaConstraintNagate() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    Person( !(age > 18) )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);
        try {
            ksession.insert(new Person("Mario", 45));
            assertEquals(0, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }
}