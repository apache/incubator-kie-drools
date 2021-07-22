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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.ChildFactWithObject;
import org.drools.modelcompiler.domain.DateTimeHolder;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TypeCoercionTest extends BaseModelTest {

    public TypeCoercionTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testEqualCoercion() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name == 40 )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("40");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("40", list.get(0));
    }

    @Test
    public void testComparisonCoercion() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name < \"50\" )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("40");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("40", list.get(0));
    }

    @Test
    public void testComparisonCoercion2() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name, age < \"50\" )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("Mario", 40);
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("Mario", list.get(0));
    }

    @Test
    public void testPrimitiveCoercion() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    $n : Number( doubleValue == 0 )" +
                "then\n" +
                "    list.add(\"\" + $n);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(0);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("0", list.get(0));
    }


    public static class DoubleHolder {
        public Double getValue() {
            return 80.0;
        }
    }

    public static class LongHolder {
        public Long getValue() {
            return 80l;
        }
    }

    @Test
    public void testDoubleToInt() {
        final String drl1 =
                "import " + DoubleHolder.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    DoubleHolder( value == 80 )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert(new DoubleHolder());
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testLongToInt() {
        final String drl1 =
                "import " + LongHolder.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    LongHolder( value == 80 )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert(new LongHolder());
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testJoinLongToDouble() {
        final String drl1 =
                "import " + DoubleHolder.class.getCanonicalName() + ";\n" +
                "import " + LongHolder.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    LongHolder( $l : value )\n" +
                "    DoubleHolder( value >= $l )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert(new LongHolder());
        ksession.insert(new DoubleHolder());
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testJoinDoubleToLong() {
        final String drl1 =
                "import " + DoubleHolder.class.getCanonicalName() + ";\n" +
                "import " + LongHolder.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    DoubleHolder( $d : value )\n" +
                "    LongHolder( value >= $d )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert(new LongHolder());
        ksession.insert(new DoubleHolder());
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testStringToDateComparison() {
        String str =
                "import " + Date.class.getCanonicalName() + ";\n" +
                "declare Flight departuretime : java.util.Date end\n" +
                "rule Init when then insert(new Flight(new Date(365L * 24 * 60 * 60 * 1000))); end\n" +
                "rule R when\n" +
                "    Flight( departuretime >= \"01-Jan-1970\" && departuretime <= \"01-Jan-2018\" )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        ksession.insert( new ChildFactWithObject(5, 1, new Object[0]) );
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testBetaJoinShortInt() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "   $p1 : Person(  $age : age )\n" +
                "   $p2 :  Person(  ageAsShort == $age )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testBetaJoinShortIntBoxed() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "   $p1 : Person(  $age : ageBoxed )\n" +
                        "   $p2 :  Person(  ageAsShort == $age )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );
        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testPrimitivePromotionInLHS() {
        // DROOLS-4717
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( age > Double.valueOf(1) )\n" +
                "then\n" +
                "  insert(new Result($p));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        final Person luca = new Person("Luca", 35);
        ksession.insert(luca);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        Assertions.assertThat(results.stream().map(Result::getValue)).containsExactlyInAnyOrder(luca);
    }

    @Test
    public void testIntToObjectCoercion() {
        // DROOLS-5320
        String str =
                "rule R when\n" +
                "    Integer($i: intValue)" +
                "    Object($i == this)" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( 3 );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testDoubleNaN() {
        // DROOLS-5692
        String str =
                "import " + DoubleNaNPojo.class.getCanonicalName() + ";\n" +
                "rule \"test_rule\"\n" +
                "	dialect \"java\"\n" +
                "	when\n" +
                "		$nanTest : DoubleNaNPojo( testDouble1 + 10 > testDouble2, testBoolean == false) \n" +
                "then\n" +
                "	System.out.println(\"rule_a fired \");\n" +
                "	$nanTest.setTestBoolean(true);\n" +
                "	update($nanTest);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        DoubleNaNPojo nan = new DoubleNaNPojo();
        nan.setTestBoolean(false);
        nan.setTestDouble1(Double.NaN);
        nan.setTestDouble2(100.0);
        ksession.insert(nan);

        assertEquals( 0, ksession.fireAllRules() );
        assertFalse( nan.getTestBoolean() );
    }

    public static class DoubleNaNPojo {

        private Boolean testBoolean;
        public Boolean getTestBoolean() {
            return testBoolean;
        }
        public void setTestBoolean(Boolean testBoolean) {
            this.testBoolean = testBoolean;
        }

        private Double testDouble1;
        private Double testDouble2;
        public Double getTestDouble1() {
            return testDouble1;
        }
        public void setTestDouble1(Double testDouble1) {
            this.testDouble1 = testDouble1;
        }
        public Double getTestDouble2() {
            return testDouble2;
        }
        public void setTestDouble2(Double testDouble2) {
            this.testDouble2 = testDouble2;
        }
    }

    public static class ClassWithIntProperty{
        private final int testInt;

        public ClassWithIntProperty( int testInt ) {
            this.testInt = testInt;
        }

        public int getTestInt() {
            return testInt;
        }
    }

    public static class ClassWithStringProperty{
        private final String testString;

        public ClassWithStringProperty( String testString ) {
            this.testString = testString;
        }

        public String getTestString() {
            return testString;
        }
    }

    public static class ClassWithShortProperty {
        private final Short testShort;

        public ClassWithShortProperty( Short testShort ) {
            this.testShort = testShort;
        }

        public Short getTestShort() {
            return testShort;
        }
    }

    @Test
    public void testStringToIntCoercion() {
        // DROOLS-5939
        String str =
                "import " + ClassWithIntProperty.class.getCanonicalName() + ";\n" +
                "import " + ClassWithStringProperty.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"test_rule_2\" when\n" +
                "     ClassWithStringProperty( $testString: testString )\n" +
                "     ClassWithIntProperty( testInt == $testString )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new ClassWithIntProperty( 10 ) );
        ksession.insert( new ClassWithStringProperty( "10" ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testIntToStringCoercion() {
        // DROOLS-5939
        String str =
                "import " + ClassWithIntProperty.class.getCanonicalName() + ";\n" +
                "import " + ClassWithStringProperty.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"test_rule_2\" when\n" +
                "     ClassWithIntProperty( $testInt : testInt )\n" +
                "     ClassWithStringProperty( testString == $testInt )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new ClassWithIntProperty( 10 ) );
        ksession.insert( new ClassWithStringProperty( "10" ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testShortToIntCoercion() {
        // DROOLS-5939
        String str =
                "import " + ClassWithShortProperty.class.getCanonicalName() + ";\n" +
                "import " + ClassWithIntProperty.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"test_rule_1\" when\n" +
                "     ClassWithShortProperty( $testShort: testShort )\n" +
                "     ClassWithIntProperty( testInt == $testShort )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new ClassWithShortProperty( (short)10 ) );
        ksession.insert( new ClassWithIntProperty( 10 ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testIntToShortCoercion() {
        // DROOLS-5939
        String str =
                "import " + ClassWithShortProperty.class.getCanonicalName() + ";\n" +
                "import " + ClassWithIntProperty.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"test_rule_1\" when\n" +
                "     ClassWithIntProperty( $testShort : testInt )\n" +
                "     ClassWithShortProperty( testShort == $testShort )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new ClassWithShortProperty( (short)10 ) );
        ksession.insert( new ClassWithIntProperty( 10 ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testCoercionOnBoundVariable() {
        // DROOLS-5945
        String str =
                "import " + ClassWithIntProperty.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"test_rule_1\" when\n" +
                "     ClassWithIntProperty( $target : \"3\", $testInt : testInt, $testInt != $target )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new ClassWithIntProperty( 3 ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testCompareDateLiteral() throws Exception {
        String str =
                "import " + DateTimeHolder.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "    DateTimeHolder( date > \"01-Jan-1970\" )\n" +
                     "then\n" +
                     "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new DateTimeHolder(ZonedDateTime.now()));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testCompareLocalDateLiteral() throws Exception {
        String str =
                "import " + DateTimeHolder.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "    DateTimeHolder( localDate > \"01-Jan-1970\" )\n" +
                     "then\n" +
                     "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new DateTimeHolder(ZonedDateTime.now()));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testCompareLocalDateTimeLiteral() throws Exception {
        String str =
                "import " + DateTimeHolder.class.getCanonicalName() + ";" +
                 "rule R when\n" +
                 "    DateTimeHolder( localDateTime > \"01-Jan-1970\" )\n" +
                 "then\n" +
                 "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new DateTimeHolder(ZonedDateTime.now()));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testCompareLocalDateTimeLiteral2() throws Exception {
        String str =
                "import " + DateTimeHolder.class.getCanonicalName() + ";" +
                 "rule R when\n" +
                 "    DateTimeHolder( localDateTime in (\"01-Jan-1970\") )\n" +
                 "then\n" +
                 "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new DateTimeHolder(ZonedDateTime.now()));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testPrimitiveDoubleToIntCoercion() {
        // DROOLS-6491
        checkDoubleToIntCoercion(true);
        checkDoubleToIntCoercion(false);
    }

    private void checkDoubleToIntCoercion(boolean boxed) {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name, age" + (boxed ? "Boxed" : "") + " < 40.5 )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mario2", 41));
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("Mario", list.get(0));
    }
}