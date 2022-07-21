/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.List;

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BindingTest extends BaseModelTest {

    public BindingTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testBindUnaryBooleanExpression() {
        // RHDM-1612
        final String str =
                "global java.util.List result;\n" +
                "\n" +
                "rule R1 when\n" +
                "    String( Boolean.valueOf(\"TRUE\") )\n" +
                "then\n" +
                "    result.add(\"R1\");\n" +
                "end\n" +
                "rule R2 when\n" +
                "    String( $flag : Boolean.valueOf(\"TRUE\") )\n" +
                "then\n" +
                "    result.add(\"R2\");\n" +
                "end\n" +
                "rule R3 when\n" +
                "    String( Boolean.valueOf(\"FALSE\") )\n" +
                "then\n" +
                "    result.add(\"R3\");\n" +
                "end\n" +
                "rule R4 when\n" +
                "    String( $flag : Boolean.valueOf(\"FALSE\") )\n" +
                "then\n" +
                "    result.add(\"R4\");\n" +
                "end\n" +
                "rule R5 when\n" +
                "    String( length == 4 )\n" +
                "then\n" +
                "    result.add(\"R5\");\n" +
                "end\n" +
                "rule R6 when\n" +
                "    String( $length : length == 4 )\n" +
                "then\n" +
                "    result.add(\"R6\");\n" +
                "end\n" +
                "rule R7 when\n" +
                "    String( length == 5 )\n" +
                "then\n" +
                "    result.add(\"R7\");\n" +
                "end\n" +
                "rule R8 when\n" +
                "    String( $length : length == 5 )\n" +
                "then\n" +
                "    result.add(\"R8\");\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<String> result = new ArrayList<>();
        ksession.setGlobal( "result", result );

        ksession.insert( "test" );
        ksession.fireAllRules();
        System.out.println(result);
        assertEquals( 5, result.size() );

        assertTrue( result.contains( "R1" ) );
        assertTrue( result.contains( "R2" ) );

        // an unary expression is used as a constrint only when not bound
        assertFalse( result.contains( "R3" ) );
        assertTrue( result.contains( "R4" ) );

        assertTrue( result.contains( "R5" ) );
        assertTrue( result.contains( "R6" ) );

        // an binary expression is used as a constrint regardless if it's bound or not
        assertFalse( result.contains( "R7" ) );
        assertFalse( result.contains( "R8" ) );
    }

    @Test
    public void testBindMethodCall() {
        // DROOLS-6521
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "\n" +
                "rule R1 when\n" +
                "    Person( $value: name.charAt(2) == 'r' )\n" +
                "then\n" +
                "    result.add($value);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Character> result = new ArrayList<>();
        ksession.setGlobal( "result", result );

        ksession.insert( new Person("Mario", 47));

        assertEquals( 1, ksession.fireAllRules() );
        assertEquals( 1, result.size() );
        assertEquals( 'r', (char) result.get(0) );
    }

    @Test
    public void testEnclosedBinding() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List result;\n" +
                        "rule R when\n" +
                        "  $p : Person( ($n : name == \"Mario\") )\n" +
                        "then\n" +
                        "  result.add($n);\n" +
                        "end";

        KieSession ksession = getKieSession(str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactly("Mario");
    }

    @Test
    public void testComplexEnclosedBinding() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person( ($n : name == \"Mario\") && (age > 20) )\n" +
                "then\n" +
                "  result.add($n);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactly("Mario");
    }

    @Test
    public void testComplexEnclosedDoubleBinding() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person( ($n : name == \"Mario\") && ($a : age > 20) )\n" +
                "then\n" +
                "  result.add($n);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactly("Mario");
    }

    @Test
    public void testBindingOnRight() {
        // DROOLS-6611
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\" && $a : age > 20)\n" +
                "then\n" +
                "  result.add($a);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactly(40);
    }

    @Test
    public void testBindingOnBoth() {
        // DROOLS-6611
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person($n : name == \"Mario\" && $a : age > 20)\n" +
                "then\n" +
                "  result.add($n);\n" +
                "  result.add($a);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactlyInAnyOrder("Mario", 40);
    }

    @Test
    public void test3BindingOn3Conditions() {
        // DROOLS-6611
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person($n : name == \"Mario\" && $a : age > 20 && $l : likes != null)\n" +
                "then\n" +
                "  result.add($n);\n" +
                "  result.add($a);\n" +
                "  result.add($l);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Mario", 40);
        me.setLikes("Cheddar");
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactlyInAnyOrder("Mario", 40, "Cheddar");
    }

    @Test
    public void test2BindingOn3Conditions() {
        // DROOLS-6611
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\" && $a : age > 20 && $l : likes != null)\n" +
                "then\n" +
                "  result.add($a);\n" +
                "  result.add($l);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Mario", 40);
        me.setLikes("Cheddar");
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactlyInAnyOrder(40, "Cheddar");
    }

    @Test
    public void testBindingOnRightWithOr() {
        // DROOLS-6920
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List result;\n" +
                        "rule R when\n" +
                        "  $p : Person(name == \"Mario\" || $a : age > 20)\n" +
                        "then\n" +
                        "  result.add($a);\n" +
                        "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Toshiya", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactly(40);
    }

    @Test
    public void testBindingOnBothWithOr() {
        // DROOLS-6920
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person($n : name == \"Mario\" || $a : age > 20)\n" +
                "then\n" +
                "  result.add($n);\n" +
                "  result.add($a);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Toshiya", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactlyInAnyOrder("Toshiya", 40);
    }

    @Test
    public void test3BindingOn3ConditionsWithOr() {
        // DROOLS-6920
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person($n : name == \"Mario\" || $a : age > 20 || $l : likes != null)\n" +
                "then\n" +
                "  result.add($n);\n" +
                "  result.add($a);\n" +
                "  result.add($l);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Toshiya", 10);
        me.setLikes("Cheddar");
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactlyInAnyOrder("Toshiya", 10, "Cheddar");
    }

    @Test
    public void test2BindingOn3ConditionsWithOr() {
        // DROOLS-6920
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\" || $a : age > 20 || $l : likes != null)\n" +
                "then\n" +
                "  result.add($a);\n" +
                "  result.add($l);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person me = new Person("Toshiya", 10);
        me.setLikes("Cheddar");
        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(result).containsExactlyInAnyOrder(10, "Cheddar");
    }

    @Test
    public void test3BindingOn3ConditionsWithAndOr() {
        // DROOLS-6920
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person($n : name == \"Mario\" && $a : age > 20 || $l : likes != null)\n" +
                "then\n" +
                "  result.add($n);\n" +
                "  result.add($a);\n" +
                "  result.add($l);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        // name == "Mario" && age > 20
        Person p1 = new Person("Mario", 40);
        p1.setLikes(null);
        ksession.insert(p1);
        ksession.fireAllRules();
        assertThat(result).containsExactlyInAnyOrder("Mario", 40, null);
        result.clear();

        // likes != null
        Person p2 = new Person("Toshiya", 10);
        p2.setLikes("Cheddar");
        ksession.insert(p2);
        ksession.fireAllRules();
        assertThat(result).containsExactlyInAnyOrder("Toshiya", 10, "Cheddar");
        result.clear();

        // Only name == "Mario"
        Person p3 = new Person("Mario", 10);
        p3.setLikes(null);
        ksession.insert(p3);
        ksession.fireAllRules();
        assertThat(result).isEmpty();
        result.clear();
    }

    @Test
    public void test3BindingOn3ConditionsWithOrAnd() {
        // DROOLS-6920
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $p : Person($n : name == \"Mario\" || $a : age > 20 && $l : likes != null)\n" +
                "then\n" +
                "  result.add($n);\n" +
                "  result.add($a);\n" +
                "  result.add($l);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        // name == "Mario"
        Person p1 = new Person("Mario", 10);
        p1.setLikes(null);
        ksession.insert(p1);
        ksession.fireAllRules();
        assertThat(result).containsExactlyInAnyOrder("Mario", 10, null);
        result.clear();

        // age > 20 && likes != null
        Person p2 = new Person("Toshiya", 40);
        p2.setLikes("Cheddar");
        ksession.insert(p2);
        ksession.fireAllRules();
        assertThat(result).containsExactlyInAnyOrder("Toshiya", 40, "Cheddar");
        result.clear();

        // Only age > 20
        Person p3 = new Person("Toshiya", 40);
        p3.setLikes(null);
        ksession.insert(p3);
        ksession.fireAllRules();
        assertThat(result).isEmpty();
        result.clear();
    }
}
