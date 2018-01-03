package org.drools.modelcompiler;

import java.util.List;

import org.drools.modelcompiler.OOPathDTablesTest.Address;
import org.drools.modelcompiler.OOPathDTablesTest.InternationalAddress;
import org.drools.modelcompiler.OOPathDTablesTest.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

import static org.junit.Assert.*;

public class OOPathDTableTestPorted extends BaseModelTest {

    public OOPathDTableTestPorted(RUN_TYPE testRunType) {
        super(testRunType);
    }


    @Test
    @Ignore
    public void testQueryOOPathAccumulate() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "import " + InternationalAddress.class.getCanonicalName() + ";" +
                        "query listSafeCities\n" +
                            "$cities : List() from accumulate (Person ( $city: /address#InternationalAddress[state == \"Safecountry\"]/city), collectList($city))\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Person person = new Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        Person person2 = new Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults("listSafeCities");

        assertEquals("Milan", ((List) results.iterator().next().get("$cities")).iterator().next());
    }

    @Test
    public void testQueryWithOOPathTransformedToFrom() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "import " + InternationalAddress.class.getCanonicalName() + ";" +
                        "query listSafeCities\n" +
                        "$p  : Person()\n" +
                        "$a  : InternationalAddress(state == \"Safecountry\") from $p.address\n" +
                        "$cities : List() from accumulate ($city : String() from $a.city, collectList($city))\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Person person = new Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        Person person2 = new Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults("listSafeCities");

        assertEquals("Milan", ((List) results.iterator().next().get("$cities")).iterator().next());
    }


    @Test
    @Ignore("fix testQueryOOPathAccumulateTransformedInsideAccumulator")
    public void testQueryWithOOPathTransformedToFromInsideAccumulate() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "import " + InternationalAddress.class.getCanonicalName() + ";" +
                        "query listSafeCities\n" +
                        "$cities : List() from accumulate (" +
                        "   $p  : Person() " +
                        "   and $a  : InternationalAddress(state == \"Safecountry\") " +
                        "   from $p.address " +
                        "   and $city : String() " +
                        "   from $a.city, collectList($city))\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Person person = new Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        Person person2 = new Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults("listSafeCities");

        assertEquals("Milan", ((List) results.iterator().next().get("$cities")).iterator().next());
    }

    @Test
    public void testQueryWithOOPathTransformedToWithoutAccumulate() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "import " + InternationalAddress.class.getCanonicalName() + ";" +
                        "query listSafeCities\n" +
                        "   $p  : Person()\n" +
                        "   $a  : InternationalAddress(state == \"Safecountry\") from $p.address\n" +
                        "   $city : String(length > 1) from $a.city\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Person person = new Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        Person person2 = new Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults("listSafeCities");

        assertEquals("Milan", (results.iterator().next().get("$city")));
    }

    @Test
    public void testRuleWithOOPathTransformedToWithoutAccumulate() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "import " + InternationalAddress.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "   $p  : Person()\n" +
                        "   $a  : InternationalAddress(state == \"Safecountry\") from $p.address\n" +
                        "   $city : String() from $a.city\n" +
                        "then\n" +
                        "  $r.setValue($city);\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Result result = new Result();
        ksession.insert( result );

        Person person = new Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        Person person2 = new Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        ksession.fireAllRules();

        assertEquals("Milan", result.getValue());
    }
}
