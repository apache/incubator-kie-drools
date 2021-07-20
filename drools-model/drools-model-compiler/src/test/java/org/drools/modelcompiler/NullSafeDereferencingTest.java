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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class NullSafeDereferencingTest extends BaseModelTest {

    public NullSafeDereferencingTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testNullSafeDereferncing() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $p : Person( name!.length == 4 )\n" +
                        "then\n" +
                        "  $r.setValue(\"Found: \" + $p);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( null, 40 ) );
        ksession.fireAllRules();

        assertEquals( "Found: Mark", result.getValue() );
    }

    public static class NullUnsafeA {

        private NullUnsafeB someB;

        public NullUnsafeB getSomeB() {
            return someB;
        }

        public void setSomeB(NullUnsafeB someB) {
            this.someB = someB;
        }
    }

    public static class NullUnsafeB {

        private NullUnsafeC someC;

        public NullUnsafeC getSomeC() {
            return someC;
        }

        public void setSomeC(NullUnsafeC someC) {
            this.someC = someC;
        }
    }

    public static class NullUnsafeC {

        private NullUnsafeD someD;

        public NullUnsafeD getSomeD() {
            return someD;
        }

        public void setSomeD(NullUnsafeD someD) {
            this.someD = someD;
        }

    }

    public static class NullUnsafeD {

        private String something;

        public String getSomething() {
            return something;
        }

        public void setSomething(String something) {
            this.something = something;
        }
    }

    @Test
    public void testNullSafeMultiple() {
        String str = "import " + NullUnsafeA.class.getCanonicalName() + ";" +
                "import " + NullUnsafeB.class.getCanonicalName() + ";" +
                "import " + NullUnsafeD.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $a : NullUnsafeA( someB!.someC!.someD!.something == \"Hello\" )\n" +
                "then\n" +
                "  insert(\"matched\");\n" +
                "end";

        for (int i = 0; i <= 4; i++) {
            KieSession ksession = getKieSession(str);

            NullUnsafeA a = new NullUnsafeA();
            NullUnsafeB b = new NullUnsafeB();
            NullUnsafeC x = new NullUnsafeC();
            NullUnsafeD c = new NullUnsafeD();
            // trap #0
            if (i != 0) {
                c.setSomething("Hello");
            }
            // trap #1
            if (i != 1) {
                b.setSomeC(x);
            }
            // trap #2
            if (i != 2) {
                x.setSomeD(c);
            }
            // trap #3
            if (i != 3) {
                a.setSomeB(b);
            }
            ksession.insert(a);
            ksession.fireAllRules();

            Collection<String> results = getObjectsIntoList(ksession, String.class);
            if (i < 4) {
                assertEquals(0, results.size());
            } else if (i == 4) {
                // iteration #3 has no null-traps
                assertEquals(1, results.size());
            }
        }
    }

    @Test
    public void testNullSafeDereferncingOnFieldWithMethodInvocation() {
        String str = "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( address!.city.startsWith(\"M\") )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("John1", 41, (Address) null));
        ksession.insert(new Person("John2", 42, new Address("Milan")));
        ksession.fireAllRules();

        List<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("John2", results.get(0).getValue());
    }

    @Test
    public void testNullSafeDereferncingOnMethodInvocation() {
        String str = "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( address.city!.startsWith(\"M\") )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("John1", 41, new Address(null)));
        ksession.insert(new Person("John2", 42, new Address("Milan")));
        ksession.fireAllRules();

        List<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("John2", results.get(0).getValue());
    }

    @Test
    public void testNullSafeDereferncingOnFirstField() {
        String str = "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( address!.city.length == 5 )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("John1", 41, (Address) null));
        ksession.insert(new Person("John2", 42, new Address("Milan")));
        ksession.fireAllRules();

        List<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("John2", results.get(0).getValue());
    }

    @Test
    public void testNullSafeDereferncingOnSecondField() {
        String str = "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( address.city!.length == 5 )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("John1", 41, new Address(null)));
        ksession.insert(new Person("John2", 42, new Address("Milan")));
        ksession.fireAllRules();

        List<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("John2", results.get(0).getValue());
    }

    @Test
    public void testNullSafeDereferncingWithOrHalfBinary() {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule R\n" +
                     "when\n" +
                     "  $p : Person( name == \"John\" || == address!.city )\n" +
                     "then\n" +
                     "  result.add($p.getName());\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("John", 24, new Address("ABC")));
        ksession.insert(new Person("Paul", 22, (Address) null));
        ksession.insert(new Person("George", 21, new Address("George")));
        ksession.fireAllRules();

        Assertions.assertThat(result).containsExactlyInAnyOrder("John", "George");
    }
}
