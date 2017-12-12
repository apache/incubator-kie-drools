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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;

import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.conf.AccumulateFunctionOption;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class AccumulateTest extends BaseModelTest {

    public AccumulateTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testAccumulate1() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($p.getAge())  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(77, results.iterator().next().getValue());
    }

    @Test
    public void testAccumulateWithProperty() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $person: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($person.getAge())  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(77, results.iterator().next().getValue());
    }

    @Test
    public void testAccumulate2() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($p.getAge()),  \n" +
                        "                $average : average($p.getAge())  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "  insert(new Result($average));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result(38.5)));
        assertThat(results, hasItem(new Result(77)));
    }

    @Test
    public void testAccumulate3() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( Person ( $age : age > 36); \n" +
                        "                $sum : sum($age),  \n" +
                        "                $average : average($age)  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "  insert(new Result($average));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result(38.5d)));
        assertThat(results, hasItem(new Result(77)));
    }

    @Test
    public void testAccumulateWithAnd() {
        String str =
                "import " + Adult.class.getCanonicalName() + ";\n" +
                "import " + Child.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate( $c : Child( age < 10 ) and $a : Adult( name == $c.parent ), $parentAge : sum($a.getAge()) )\n" +
                "then\n" +
                "  insert(new Result($parentAge));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Adult a = new Adult( "Mario", 43 );
        Child c = new Child( "Sofia", 6, "Mario" );

        ksession.insert( a );
        ksession.insert( c );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result(43)));
    }

    @Test
    public void testAccumulateWithCustomImport() {
        String str =
                "import accumulate " + TestFunction.class.getCanonicalName() + " f;\n" +
                "import " + Adult.class.getCanonicalName() + ";\n" +
                "import " + Child.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate( $c : Child( age < 10 ) and $a : Adult( name == $c.parent ), $parentAge : f($a.getAge()) )\n" +
                "then\n" +
                "  insert(new Result($parentAge));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Adult a = new Adult( "Mario", 43 );
        Child c = new Child( "Sofia", 6, "Mario" );

        ksession.insert( a );
        ksession.insert( c );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result(1)));
    }

    public static class TestFunction implements AccumulateFunction<Serializable> {
        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        }

        @Override
        public Serializable createContext() {
            return null;
        }

        @Override
        public void init( Serializable context ) throws Exception {
        }

        @Override
        public void accumulate(Serializable context, Object value ) {
        }

        @Override
        public void reverse( Serializable context, Object value ) throws Exception {
        }

        @Override
        public Object getResult( Serializable context ) throws Exception {
            return Integer.valueOf( 1 );
        }

        @Override
        public boolean supportsReverse() {
            return true;
        }

        @Override
        public Class<?> getResultType() {
            return Number.class;
        }
    }

}
