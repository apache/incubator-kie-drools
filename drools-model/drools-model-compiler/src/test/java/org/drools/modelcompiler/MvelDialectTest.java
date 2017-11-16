/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.Collection;
import java.util.List;

import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MvelDialectTest extends BaseModelTest {

    public MvelDialectTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testMVELinsert() {
        String str = "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Integer()\n" +
                "then\n" +
                "  System.out.println(\"Hello World\");\n" +
                "  insert(\"Hello World\");\n" +
                "end";

        KieSession ksession = getKieSession(str);

        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertTrue(results.contains("Hello World"));
    }

    @Test
    public void testMVELmodify() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) { setAge(1); }\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Matteo", 47));
        ksession.fireAllRules();

        Collection<Person> results = getObjectsIntoList(ksession, Person.class);
        assertEquals(1, results.iterator().next().getAge());
        results.forEach(System.out::println);
    }

    @Test
    public void testMVELmultiple() {
        String str = "package mypackage;" +
                "dialect \"mvel\"\n" + // MVEL dialect defined at package level.
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "when\n" +
                "  Integer()\n" +
                "then\n" +
                "  System.out.println(\"Hello World\")\n" + // no ending ; as per MVEL dialect
                "  insert(new Person(\"Matteo\", 47))\n" +
                "  insert(\"Hello World\")\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) { setAge(1); }\n" +
                "  insert(\"Modified person age to 1 for: \"+$p.name)\n" + // Please notice $p.name is MVEL dialect.
                "end\n" +
                "rule R3\n" +
                "when\n" +
                "  $s : String( this == \"Hello World\")\n" +
                "  $p : Person()\n" + // this is artificially added to ensure working even with unnecessary declaration passed to on().execute().
                "then\n" +
                "  retract($s)" +
                "end\n";

        KieSession ksession = getKieSession(str);

        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        System.out.println(results);
        assertFalse(results.contains("Hello World"));
        assertTrue(results.contains("Modified person age to 1 for: Matteo"));
    }

    @Test
    public void testMVELmultipleStatements() {
        String str =
                "import " + Person.class.getPackage().getName() + ".*;\n" + // keep the package.* in order for Address to be resolvable in the RHS.
                        "rule R\n" +
                        "dialect \"mvel\"\n" +
                        "when\n" +
                        "  $p : Person()\n" +
                        "then\n" +
                        "  Address a = new Address(\"somewhere\");\n" +
                        "  insert(a);\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Matteo", 47));
        ksession.fireAllRules();

        List<Address> results = getObjectsIntoList(ksession, Address.class);
        assertEquals(1, results.size());
    }
}
