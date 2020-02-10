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

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.CalcFact;
import org.drools.modelcompiler.domain.Overloaded;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class EvalTest extends BaseModelTest {

    public EvalTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testEval() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person()\n" +
                "  eval( $p.getAge() == 40 )\n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testEvalWithMethodInvocation() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person()\n" +
                "  eval( $p.getName().startsWith(\"E\") )\n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Edson", results.iterator().next().getValue() );
    }

    @Test
    public void testEvalTrue() {
        String str =
                "rule R when\n" +
                "  eval( true )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testEvalFalse() {
        String str =
                "rule R when\n" +
                "  eval( false )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );
        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testEvalOr() {
        String str =
                "rule R when\n" +
                "  eval( true ) or eval( false )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testEvalIdentity() {
        String str =
                "rule R when\n" +
                        "  eval( 1 == 1 )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession( str );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testFunction() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "function String hello(String name) {\n" +
                "    return \"Hello \"+name+\"!\";\n" +
                "}" +
                "rule R when\n" +
                "  $p : Person()\n" +
                "  eval( $p.getAge() == 40 )\n" +
                "then\n" +
                "  insert(new Result(hello($p.getName())));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Hello Mario!", results.iterator().next().getValue() );
    }

    @Test
    public void testFunction2() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "function Boolean isFortyYearsOld(Person p, Boolean booleanParameter) {\n" +
                "    return p.getAge() == 40; \n"+
                "}" +
                "rule R when\n" +
                "  $p : Person()\n" +
                "  eval( isFortyYearsOld($p, true) )\n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testEvalWith2Bindings() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p1 : Person( name == \"Mario\" )\n" +
                "  $p2 : Person( name == \"Mark\" )\n" +
                "  eval( $p1.getAge() == $p2.getAge() + 2 )\n" +
                "then\n" +
                "  insert(new Result($p1.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 38 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testEvalWith2BindingsInvokingMethod() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p1 : Person( age == 40 )\n" +
                "  $p2 : Person( age == 38 )\n" +
                "  eval( $p1.getName().equals( $p2.getName() ) )\n" +
                "then\n" +
                "  insert(new Result($p1.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mario", 38 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testEvalWithDeclaration() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p1 : Person( $a1 : age )\n" +
                "  eval( $a1 > 39 )\n" +
                "then\n" +
                "  insert(new Result($p1.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 38 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testEvalWith2Declarations() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p1 : Person( name == \"Mario\", $a1 : age > 0 )\n" +
                "  $p2 : Person( name == \"Mark\", $a2 : age > 0 )\n" +
                "  eval( $a1 == $a2 + 2 )\n" +
                "then\n" +
                "  insert(new Result($p1.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 38 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testEvalWithBinary() {
        String str = "import " + Result.class.getCanonicalName() + ";" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person()\n" +
                     "  eval( $p.getAge() + 27 == 67 )\n" +
                     "then\n" +
                     "  insert(new Result($p.getName()));\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("Mario", results.iterator().next().getValue());
    }

    @Test
    public void testEvalInvokingMethod() {
        String str = "import " + Overloaded.class.getCanonicalName() + ";" +
                     "rule OverloadedMethods\n" +
                     "when\n" +
                     "  o : Overloaded()\n" +
                     "  eval (o.method(5, 9, \"x\") == 15)\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Overloaded());
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
    }

    @Test
    public void testEvalInvokingMethod2() {
        String str = "import " + Overloaded.class.getCanonicalName() + ";" +
                     "rule OverloadedMethods\n" +
                     "when\n" +
                     "  o : Overloaded()\n" +
                     "  eval (\"helloworld150.32\".equals(o.method2(\"hello\", \"world\", 15L, 0.32)))\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Overloaded());
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
    }


    @Test
    public void testEvalInvokingFunction() {
        String str =
                "function boolean isPositive(int i){\n" +
                "  return i > 0;\n" +
                "}" +
                "rule OverloadedMethods\n when\n" +
                "  $i : Integer()\n" +
                "  eval (isPositive($i.intValue()))\n" +
                "then\n" +
                "  insert(\"matched\");\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(42);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
    }

    @Test
    public void testEvalWithGlobal() {
        final String drl1 =
                "import " + Result.class.getCanonicalName() + ";\n" +
                        "global java.lang.Integer globalInt\n" +
                        "rule R1 when\n" +
                        "	 Integer(this == eval(globalInt))\n" +
                        "then\n" +
                        "  insert(new Result(\"match\"));\n" +
                        "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.setGlobal("globalInt", 1);
        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);

        assertEquals( 1, ksession.fireAllRules() );

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(results.iterator().next().getValue().toString(), "match");
    }

    @Test
    public void testEvalWithGlobal2() {
        final String drl1 =
                "import " + Result.class.getCanonicalName() + ";\n" +
                        "global java.lang.Integer globalInt\n" +
                        "rule R1 when\n" +
                        "	 eval(globalInt == globalInt)\n" +
                        "then\n" +
                        "  insert(new Result(\"match\"));\n" +
                        "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.setGlobal("globalInt", 1);
        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);

        assertEquals( 1, ksession.fireAllRules() );

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(results.iterator().next().getValue().toString(), "match");

    }

    @Test
    public void testEvalExprWithFunctionCall() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global " + GlobalFunctions.class.getCanonicalName() + " functions;" +
                        "rule R1 when\n" +
                        "  $p : Person($age : age)\n" +
                        "  eval( functions.add($age, -1).compareTo(10) < 0)\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);
        GlobalFunctions gf = new GlobalFunctions();
        ksession.setGlobal("functions", gf);

        Person first = new Person("First", 10);
        ksession.insert(first);
        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class GlobalFunctions {
        public Integer add(int a, int b) {
            return Integer.sum(a, b);
        }
    }

    @Test
    public void testEvalCalculationWithParenthesis() {
        String str =
                "import " + CalcFact.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : CalcFact( $v1 : value1, $v2 : value2 )\n" +
                "  eval( ($v1 / ($v2 * 10) * 10) > 25 )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new CalcFact(1.0d, 1.0d));
        // (1.0 / (1.0 * 10) * 10) is 1. So this rule should not fire

        int fired = ksession.fireAllRules();
        assertEquals(0, fired);
    }
}
