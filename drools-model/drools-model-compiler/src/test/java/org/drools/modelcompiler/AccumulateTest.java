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
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Customer;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.StockTick;
import org.drools.modelcompiler.domain.TargetPolicy;
import org.drools.modelcompiler.oopathdtables.InternationalAddress;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.FactHandle;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
    public void testAccumulateWithoutParameters() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $count : count()  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result($count));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(2l, results.iterator().next().getValue());
    }

    @Test
    public void testAccumulateWithLessParameter() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( Person (); \n" +
                        "                count()  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result(\"fired\"));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        int numberOfRules = ksession.fireAllRules();

        assertEquals(1, numberOfRules);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("fired", results.iterator().next().getValue());
    }

    @Test
    public void testAccumulateOverConstant() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum(1)  \n" +
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
        assertEquals(2, results.iterator().next().getValue());
    }

    @Test
    public void testAccumulateConstrainingValue() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($p.getAge()); $sum > 50  \n" +
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
    public void testAccumulateConstrainingValue2() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($p.getAge()); $sum > 100  \n" +
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
        assertEquals(0, results.size());
    }

    @Test
    public void testAccumulateConstrainingValueInPattern() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  $sum : Integer( this > 50 ) from accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                sum($p.getAge())  \n" +
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
    public void testAccumulateMultipleFunctions() {
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
    public void testAccumulateMultipleFunctionsConstrainingValues() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( Person ( $age : age > 36); \n" +
                        "                $sum : sum($age),  \n" +
                        "                $min : min($age)  \n" +
                        "                ; $sum > 50, $min > 30\n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "  insert(new Result($min));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result(37)));
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
    public void testAccumulateWithAnd2() {
        String str =
                "import " + Adult.class.getCanonicalName() + ";\n" +
                "import " + Child.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate( $c : Child( age < 10 ) and $a : Adult( name == $c.parent ), $parentAge : sum($a.getAge() + $c.getAge()) )\n" +
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
        // The original DSL test returns a double while the exec model returns an integer
        assertEquals(((Number)results.iterator().next().getValue()).intValue(), 49);
    }

    @Test
    public void testAccumulateWithAnd3() {
        String str =
                "import " + Adult.class.getCanonicalName() + ";\n" +
                "import " + Child.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate( $x : Child( age < 10 ) and $y : Adult( name == $x.parent ), $parentAge : sum($x.getAge() + $y.getAge()) )\n" +
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
        // The original DSL test returns a double while the exec model returns an integer
        assertEquals(((Number)results.iterator().next().getValue()).intValue(), 49);
    }

    @Test
    public void testAccumulateWithAnd3Binds() {
        String str =
                "import " + Adult.class.getCanonicalName() + ";\n" +
                        "import " + Child.class.getCanonicalName() + ";\n" +
                        "import " + Result.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  accumulate( $c : Child( age < 10 ) and $a : Adult( name == $c.parent ) and $s : String( this == $a.name ), " +
                        "$parentAge : sum($a.getAge() + $c.getAge() + $s.length()) )\n" +
                        "then\n" +
                        "  insert(new Result($parentAge));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        Adult a = new Adult( "Mario", 43 );
        Child c = new Child( "Sofia", 6, "Mario" );

        ksession.insert( a );
        ksession.insert( c );
        ksession.insert( "Mario" );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        // The original DSL test returns a double while the exec model returns an integer
        assertEquals(54, ((Number)results.iterator().next().getValue()).intValue());
    }

    @Test
    public void testAccumulateWithAnd4Binds() {
        String str =
                "import " + Adult.class.getCanonicalName() + ";\n" +
                        "import " + Child.class.getCanonicalName() + ";\n" +
                        "import " + Result.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  accumulate( $c : Child( age < 10 ) and $a : Adult( name == $c.parent ) and $s1 : String( this == $a.name ) and $s2 : String( this == $c.name ), " +
                        "$parentAge : sum($a.getAge() + $c.getAge() + $s1.length() + $s2.length()) )\n" +
                        "then\n" +
                        "  insert(new Result($parentAge));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        Adult a = new Adult( "Mario", 43 );
        Child c = new Child( "Sofia", 6, "Mario" );

        ksession.insert( a );
        ksession.insert( c );
        ksession.insert( "Mario" );
        ksession.insert( "Sofia" );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        // The original DSL test returns a double while the exec model returns an integer
        assertEquals(59, ((Number)results.iterator().next().getValue()).intValue());
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

    @Test
    public void testFromAccumulate() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  $sum : Number( intValue() > 0 ) from accumulate ( $p: Person ( age > 10, name.startsWith(\"M\") ); \n" +
                "                sum($p.getAge())  \n" +
                "              )\n" +
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
    public void testFromCollect() {
        String str =
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + TargetPolicy.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "rule \"Customer can only have one Target Policy for Product p1 with coefficient 1\" when\n" +
                "  $customer : Customer( $code : code )\n" +
                "  $target : TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1 )\n" +
                "  List(size > 1) from collect ( TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1) )\n" +
                "then\n" +
                "  $target.setCoefficient(0);\n" +
                "  update($target);\n" +
                "end";

        checkCollect( str );
    }

    @Test
    public void testFromCollectWithAccumulate() {
        String str =
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + TargetPolicy.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "rule \"Customer can only have one Target Policy for Product p1 with coefficient 1\" when\n" +
                "  $customer : Customer( $code : code )\n" +
                "  $target : TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1 )\n" +
                "  List(size > 1) from accumulate ( $tp: TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1); collectList( $tp ) )\n" +
                "then\n" +
                "  $target.setCoefficient(0);\n" +
                "  update($target);\n" +
                "end";

        checkCollect( str );
    }

    @Test
    public void testFromCollectWithExpandedAccumulate() {
        String str =
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + TargetPolicy.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "import " + ArrayList.class.getCanonicalName() + ";\n" +
                "rule \"Customer can only have one Target Policy for Product p1 with coefficient 1\" when\n" +
                "  $customer : Customer( $code : code )\n" +
                "  $target : TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1 )\n" +
                "  List(size > 1) from accumulate ( $tp: TargetPolicy( customerCode == $code, productCode == \"p1\", coefficient == 1); " +
                "            init( ArrayList myList = new ArrayList(); ),\n" +
                "            action( myList.add($tp); ),\n" +
                "            reverse( myList.remove($tp); ),\n" +
                "            result( myList ) )\n" +
                "then\n" +
                "  $target.setCoefficient(0);\n" +
                "  update($target);\n" +
                "end";

        checkCollect( str );
    }

    private void checkCollect( String str ) {
        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        TargetPolicy target1 = new TargetPolicy();
        target1.setCustomerCode("code1");
        target1.setProductCode("p1");
        target1.setCoefficient(1);
        TargetPolicy target2 = new TargetPolicy();
        target2.setCustomerCode("code1");
        target2.setProductCode("p1");
        target2.setCoefficient(1);
        TargetPolicy target3 = new TargetPolicy();
        target3.setCustomerCode("code1");
        target3.setProductCode("p1");
        target3.setCoefficient(1);

        ksession.insert(customer);
        ksession.insert(target1);
        ksession.insert(target2);
        ksession.insert(target3);
        ksession.fireAllRules();

        List<TargetPolicy> targetPolicyList = Arrays.asList(target1, target2, target3);
        long filtered = targetPolicyList.stream().filter(c -> c.getCoefficient() == 1).count();
        assertEquals(1, filtered);
    }

    @Test
    public void testFromCollectWithExpandedAccumulate2() {
        testFromCollectWithExpandedAccumulate2(false);
    }

    @Test
    public void testFromCollectWithExpandedAccumulate2WithReverse() {
        testFromCollectWithExpandedAccumulate2(true);
    }

    public void testFromCollectWithExpandedAccumulate2(boolean performReverse) {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                     "rule R when\n" +
                     "  $sum : Integer() from accumulate (\n" +
                     "            Person( age > 18, $age : age ), init( int sum = 0; ), action( sum += $age; ), reverse( sum -= $age; ), result( sum )\n" +
                     "         )" +
                     "then\n" +
                     "  insert($sum);\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        FactHandle fh_Mark = ksession.insert(new Person("Mark", 37));
        FactHandle fh_Edson = ksession.insert(new Person("Edson", 35));
        FactHandle fh_Mario = ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem(112));

        if (performReverse) {
            ksession.delete(fh_Mario);
            ksession.fireAllRules();

            results = getObjectsIntoList(ksession, Integer.class);
            assertEquals(2, results.size());
            assertThat(results, hasItem(72));
        }
    }

    @Test
    public void testExpandedAccumulateWith2Args() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $avg : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age ), init( int count = 0; int sum = 0; ), " +
                "                                            action( count++; sum += $age; ), " +
                "                                            reverse( count--; sum -= $age; ), " +
                "                                            result( sum / count )\n" +
                "         )" +
                "then\n" +
                "  insert($avg);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        FactHandle fh_Mark = ksession.insert(new Person("Mark", 37));
        FactHandle fh_Edson = ksession.insert(new Person("Edson", 35));
        FactHandle fh_Mario = ksession.insert(new Person("Mario", 42));
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem(38));

        ksession.delete(fh_Mario);
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(2, results.size());
        assertThat(results, hasItem(36));
    }

    @Test
    public void testExpandedAccumulateWith2Args2Bindings() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $avg : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age, $name : name ), " +
                "                                            init( int count = 0; int sum = 0; String allNames = \"\"; ), " +
                "                                            action( count++; sum += $age; allNames = allNames + $name; ), " +
                "                                            reverse( count--; sum -= $age; ), " +
                "                                            result( (sum / count) + allNames.length() )\n" +
                "         )" +
                "then\n" +
                "  insert($avg);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        FactHandle fh_Mark = ksession.insert(new Person("Mark", 37));
        FactHandle fh_Edson = ksession.insert(new Person("Edson", 35));
        FactHandle fh_Mario = ksession.insert(new Person("Mario", 42));
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem(38 + 14));

        ksession.delete(fh_Mario);
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(2, results.size());
        assertThat(results, hasItem(36 + 14));
    }


    @Test
    public void testExpandedAccumulateWith3Args() {
        String str =
                "rule \"TestAccumulate2\" when\n" +
                "    $dx : Number () from accumulate ( $d : Double (),\n" +
                "                init   ( double ex = 0; double ex2 = 0; int count = 0; ),\n" +
                "                action ( count++; ex += $d; ex2 += $d * $d; ),\n" +
                "                reverse( count--; ex -= $d; ex2 -= $d * $d; ),\n" +
                "                result ( (ex / count) * (ex / count) + (ex2 / count) ) )\n" +
                "then\n" +
                "   insert($dx.intValue());\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(1.0);
        ksession.insert(2.0);
        ksession.insert(3.0);
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem(8));
    }

    @Test
    public void testAccumulateFromWithConstraint() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                "import " + org.drools.modelcompiler.oopathdtables.Person.class.getCanonicalName() + ";" +
                "import " + org.drools.modelcompiler.oopathdtables.Address.class.getCanonicalName() + ";" +
                "import " + org.drools.modelcompiler.oopathdtables.InternationalAddress.class.getCanonicalName() + ";" +
                "rule listSafeCities when\n" +
                "  $a : InternationalAddress()\n" +
                "  $cities : List(size > 0) from accumulate ($city : String() from $a.city, collectList($city))\n" +
                "then\n" +
                "   insert($cities.get(0));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.fireAllRules();

        List<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem("Milan"));
    }

    @Test
    public void testAccumulateWithThis() {
        final String drl1 =
                "import java.util.*;\n" +
                        "rule B\n" +
                        "when\n" +
                        "    $eventCodeDistinctMois : Integer( intValue>0 ) from accumulate ( String( $id : this ),\n" +
                        "                                                                init( Set set = new HashSet(); ),\n" +
                        "                                                                action( set.add($id); ),\n" +
                        "                                                                reverse( set.remove($id); ),\n" +
                        "                                                                result( set.size()) )\n" +
                        "then\n" +
                        "   insert($eventCodeDistinctMois);\n" +
                        "end";
        KieSession ksession = getKieSession( drl1 );

        ksession.insert("1");
        ksession.insert("3");
        ksession.insert("3");
        ksession.insert("5");
        ksession.insert("7");
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);

        assertThat(results, hasItem(4));
    }

    @Test
    public void testAccumulateWithExternalBind() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  String( $l : length )" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                        "                $sum : sum($p.getAge() * $l)  \n" +
                        "              )                          \n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert("x");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(77, ((Number) results.iterator().next().getValue()).intValue());
    }

    @Test
    public void testFromCollectWithExpandedAccumulateExternalBindInInit() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  String( $l : length )\n" +
                "  $sum : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age ), init( int sum = 0 * $l; ), action( sum += $age; ), reverse( sum -= $age; ), result( sum )\n" +
                "         )\n" +
                "then\n" +
                "  insert($sum);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("x");
        FactHandle fh_Mark = ksession.insert(new Person("Mark", 37));
        FactHandle fh_Edson = ksession.insert(new Person("Edson", 35));
        FactHandle fh_Mario = ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem(112));

    }

    @Test
    public void testFromCollectWithExpandedAccumulateExternalBindInAction() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  String( $l : length )" +
                "  $sum : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age ), init( int sum = 0; ), action( sum += ($age * $l); ), reverse( sum -= $age; ), result( sum )\n" +
                "         )" +
                "then\n" +
                "  insert($sum);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("x");
        FactHandle fh_Mark = ksession.insert(new Person("Mark", 37));
        FactHandle fh_Edson = ksession.insert(new Person("Edson", 35));
        FactHandle fh_Mario = ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem(112));

    }

    @Test
    public void testUseAccumulateFunctionWithOperationInBinding() {

        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate (\n" +
                "       $p : Person(), $result : sum( $p.getAge() * 1) "+
                "         )" +
                "then\n" +
                "  insert($result);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("x");
        FactHandle fh_Mark = ksession.insert(new Person("Mark", 37));
        FactHandle fh_Edson = ksession.insert(new Person("Edson", 35));
        FactHandle fh_Mario = ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertEquals(1, results.size());
        assertEquals(112, results.get(0).intValue());

    }

    @Test
    public void testUseAccumulateFunctionWithArrayAccessOperation() {

        String str = "import " + Adult.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate (\n" +
                "       $p : Adult(), $result : sum( $p.getChildrenA()[0].getAge() + 10) " +
                "         )" +
                "then\n" +
                "  insert($result);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("x");
        Adult luca = new Adult("Luca", 33);
        Person leonardo = new Person("Leonardo", 1);
        luca.setChildrenA(new Person[]{leonardo});

        ksession.insert(luca);
        ksession.insert(leonardo);

        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertEquals(1, results.size());
        assertEquals(11, results.get(0).intValue());
    }

    @Test
    public void testUseAccumulateFunctionWithListMvelDialectWithoutBias() throws Exception {
        String str = "package org.test;" +
                "import java.util.*; " +
                "declare Data " +
                "  values : List " +
                "end " +
                "rule R " +
                "  dialect 'mvel' " +
                "when\n" +
                "    accumulate ( $data : Data( )," +
                "                 $tot : sum( $data.values[ 0 ] ) ) " +
                "then\n" +
                "  insert($tot);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        FactType dataType = ksession.getKieBase().getFactType("org.test", "Data");
        Object data1 = dataType.newInstance();
        dataType.set(data1, "values", Arrays.asList(2, 3, 4));
        ksession.insert(data1);

        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertEquals(1, results.size());
        assertEquals(2, results.get(0).intValue());
    }

    @Test
    @Ignore("this should use a strongly typed declared type")
    public void testUseAccumulateFunctionWithListMvelDialect() throws Exception {
        String str = "package org.test;" +
                "import java.util.*; " +
                "declare Data " +
                "  values : List " +
                "  bias : int = 0 " +
                "end " +
                "rule R " +
                "  dialect 'mvel' " +
                "when\n" +
                "    accumulate ( $data : Data( $bias : bias )," +
                "                 $tot : sum( $data.values[ 0 ] + $bias ) ) " +
                "then\n" +
                "  insert($tot);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        FactType dataType = ksession.getKieBase().getFactType("org.test", "Data");
        Object data1 = dataType.newInstance();
        dataType.set(data1, "values", Arrays.asList(2, 3, 4));
        dataType.set(data1, "bias", 100);
        ksession.insert(data1);

        Object data2 = dataType.newInstance();
        dataType.set(data2, "values", Arrays.asList(10));
        dataType.set(data2, "bias", 100);
        ksession.insert(data2);

        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertEquals(1, results.size());
        assertEquals(212, results.get(0).intValue());
    }

    @Test
    public void testTypedResultOnAccumulate() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  $max : Integer() from accumulate ( String( $l : length ); \n" +
                "                max($l)  \n" +
                "              ) \n" +
                "then\n" +
                "  insert(new Person(\"test\", $max));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert("xyz");

        ksession.fireAllRules();

        Collection<Person> results = getObjectsIntoList(ksession, Person.class);
        assertEquals(1, results.size());
        assertEquals(3, results.iterator().next().getAge());
    }

    @Test
    public void testExtractorInPattern() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( Person( $a : age ); \n" +
                        "                $max : max($a)  \n" +
                        "              ) \n" +
                        "then\n" +
                        "  insert(new Result($max));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("a", 23));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(23, results.iterator().next().getValue());
    }


    @Test
    public void testThisInPattern() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( Integer( $i : this ); \n" +
                        "                $max : max($i)  \n" +
                        "              ) \n" +
                        "then\n" +
                        "  insert(new Result($max));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(2);
        ksession.insert(10);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(10, results.iterator().next().getValue());
    }



    @Test
    public void testExtractorInFunction() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  accumulate ( Person( $p : this ); \n" +
                "                $max : max($p.getAge())  \n" +
                "              ) \n" +
                "then\n" +
                "  insert(new Result($max));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("a", 23));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(23, results.iterator().next().getValue());
    }


    // do also the test with two functions
    @Test
    public void testAccumulateWithMax() {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule AccumulateMaxDate when\n" +
                        "  $max1 : Number() from accumulate(\n" +
                        "    StockTick($time : getTimeFieldAsDate());\n" +
                        "    max($time.getTime())" +
                        " )" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        StockTick st = new StockTick("RHT");
        st.setTimeField(new Date().getTime());
        ksession.insert(st);
        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testAccumulateWithMaxCalendar() {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                        "rule AccumulateMaxDate\n" +
                        "  dialect \"java\"\n" +
                        "  when\n" +
                        "  $max1 : Number() from accumulate(\n" +
                        "    StockTick($time : dueDate);\n" +
                        "    max($time.getTime().getTime()))\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        StockTick st = new StockTick("RHT");
        st.setDueDate(Calendar.getInstance());
        ksession.insert(st);
        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testAccumulateWithMaxCalendarAndConstraint() {
        String str =
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                        "rule AccumulateMaxDate\n" +
                        "  dialect \"java\"\n" +
                        "  when\n" +
                        "  $customer : Customer( code == \"RHT\" )\n" +
                        "  $max1 : Number() from accumulate(\n" +
                        "    StockTick( company == $customer.code\n" +
                        "    , $time : dueDate);\n" +
                        "    max($time.getTime().getTime()))\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        StockTick st = new StockTick("RHT");
        st.setDueDate(Calendar.getInstance());
        Customer c = new Customer();
        c.setCode("RHT");
        ksession.insert(st);
        ksession.insert(c);
        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNoBinding() {

        final String str = "rule foo\n" +
                "when\n" +
                "Object() from accumulate( Object(),\n" +
                "init( Object res = 2; )\n" +
                "action( res = 2; )\n" +
                "result( res ) )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);
        ksession.insert("xyz");
        ksession.fireAllRules();
    }

    public static class ShortValue {
        public Short getValue() {
            return 1;
        }
    }

    @Test
    public void testImplicitCastInAccumulateFunction() {
        String str =
                "import " + ShortValue.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  $max : Double(doubleValue != Double.MAX_VALUE) from accumulate ( ShortValue( $v : value ); max($v) ) \n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new ShortValue());

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAccumulateWithFunctionWithExternalBinding() {
        final String drl =
                "import " + Converter.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "   Integer (this == 1)\n" +
                "   String( $length : length )\n" +
                "   accumulate ( $c : Converter(), $result : sum( $c.convert($length) ) )\n" +
                "then\n" +
                "    list.add($result);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        final List<Number> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("hello");
        ksession.insert(new Converter());
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(5, list.get(0).intValue());
    }

    public static class Converter {

        public static int convert(final int i) {
            return i;
        }
    }

    @Test
    public void testAccumulateWithFunctionWithExternalBindingAndOR() {
        final String drl =
                "import " + Converter.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  (or\n" +
                "    Integer (this == 1)\n" +
                "    Integer (this == 2)\n" +
                "  )\n" +
                "   String( $length : length )\n" +
                "   accumulate ( $c : Converter(), $result : sum( $c.convert($length) ) )\n" +
                "then\n" +
                "    list.add($result);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        final List<Number> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("hello");
        ksession.insert(new Converter());
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(5, list.get(0).intValue());
    }

    @Test
    public void testAccumulateWithOR() {
        final String drl =
                "import " + Converter.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  (or\n" +
                "    Integer (this == 1)\n" +
                "    Integer (this == 2)\n" +
                "  )\n" +
                "   accumulate ( String( $length : length ), $result : sum( $length ) )\n" +
                "then\n" +
                "    list.add($result);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        final List<Number> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert("hello");
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(5, list.get(0).intValue());
    }

    @Test
    public void testPatternMatchingOverNumberWhileAccumulatingShort() {
        String drl=
                "import " + AccumulateResult.class.getCanonicalName() + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "\n" +
                "rule \"accumulate_max_short_using_double\"\n" +
                "	dialect \"java\"\n" +
                "	when\n" +
                "		$accumulateResult : AccumulateResult( resultAccumulated == false ) \n" +
                "		\n" +
                "		$maxOfAllShort : Number() from accumulate (\n" +
                "		$accumulateTest_short_max : Person( $age : ageAsShort);max($age))\n" +
                "		\n" +
                "then\n" +
                "		$accumulateResult.setResultAccumulated(true);\n" +
                "		$accumulateResult.setMaxShortValue($maxOfAllShort.shortValue()\n);\n" +
                "		update($accumulateResult);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        AccumulateResult result = new AccumulateResult(false);

        ksession.insert(result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertEquals(true, result.isResultAccumulated());
        assertEquals(40, result.getMaxShortValue());
    }

    public static class AccumulateResult {

        private boolean resultAccumulated;
        private short maxShortValue;
        private BigDecimal bigDecimalValue;

        public AccumulateResult(boolean resultAccumulated) {
            this.resultAccumulated = resultAccumulated;
        }

        public boolean isResultAccumulated() {
            return resultAccumulated;
        }

        public void setResultAccumulated(boolean resultAccumulated) {
            this.resultAccumulated = resultAccumulated;
        }

        public short getMaxShortValue() {
            return maxShortValue;
        }

        public void setMaxShortValue(short maxShortValue) {
            this.maxShortValue = maxShortValue;
        }

        public BigDecimal getBigDecimalValue() {
            return bigDecimalValue;
        }

        public void setBigDecimalValue(BigDecimal bigDecimalValue) {
            this.bigDecimalValue = bigDecimalValue;
        }
    }


    @Test
    public void testAccumulateOverField() {
        String str =
                "import java.lang.Number;\n" +
                        "import java.math.BigDecimal;\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AccumulateResult.class.getCanonicalName() + ";" +
                        "global java.util.List list;\n" +
                        "rule \"rule\"\n" +
                        "  dialect \"mvel\"\n" +
                        "  when\n" +
                        "    $r : AccumulateResult()\n" +
                        "    $sumMoney : BigDecimal( ) from accumulate ( $p : Person( money != null ),\n" +
                        "      sum($p.money)) \n" +
                        "  then\n" +
                        "    modify( $r ) {\n" +
                        "        setBigDecimalValue( $sumMoney )\n" +
                        "    }\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario", BigDecimal.valueOf(1000)));
        ksession.insert(new Person("Luca", BigDecimal.valueOf(2000)));

        AccumulateResult result = new AccumulateResult(false);
        ksession.insert(result);

        int rulesFired = ksession.fireAllRules();

        assertEquals(1, rulesFired);
        assertEquals(BigDecimal.valueOf(3000), result.getBigDecimalValue());
    }

    @Test
    public void testFromAccumulateBigDecimalMvel() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List list;\n" +
                     "dialect \"mvel\"\n" +
                     "rule R when\n" +
                     "  $b : BigDecimal() from accumulate (\n" +
                     "            Person( $money : money ),\n" +
                     "                init( BigDecimal sum = 0; ),\n" +
                     "                action( sum += $money; ),\n" +
                     "                reverse( sum -= $money; ),\n" +
                     "                result( sum )\n" +
                     "         )\n" +
                     "then\n" +
                     "  list.add($b);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        final List<Number> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person john = new Person("John");
        john.setMoney(new BigDecimal(100));
        ksession.insert(john);
        Person paul = new Person("Paul");
        paul.setMoney(new BigDecimal(200));
        ksession.insert(paul);

        ksession.fireAllRules();

        assertEquals(new BigDecimal(300), list.get(0));

    }

    @Test
    public void testSemicolonMissingInInit() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $sum : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age ),\n" +
                "                init( int sum = 0),\n" +
                "                action( sum += $age; ),\n" +
                "                reverse( sum -= $age; ),\n" +
                "                result( sum )\n" +
                "         )\n" +
                "then\n" +
                "  list.add($sum);\n" +
                "end";

        KieSession ksession = getKieSession(str);
        final List<Number> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person john = new Person("John", 23);
        ksession.insert(john);

        Person paul = new Person("Paul", 40);
        ksession.insert(paul);

        Person jones = new Person("Jones", 16);
        ksession.insert(jones);

        ksession.fireAllRules();

        assertEquals(63, list.get(0));
    }


    @Test(expected = AssertionError.class)
    public void testSemicolonMissingInAction() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $sum : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age ),\n" +
                "                init( int sum = 0; ),\n" +
                "                action( sum += $age ),\n" +
                "                reverse( sum -= $age; ),\n" +
                "                result( sum )\n" +
                "         )\n" +
                "then\n" +
                "  list.add($sum);\n" +
                "end";

       getKieSession(str);
    }

    @Test(expected = AssertionError.class)
    public void testSemicolonMissingInReverse() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $sum : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age ),\n" +
                "                init( int sum = 0; ),\n" +
                "                action( sum += $age; ),\n" +
                "                reverse( sum -= $age ),\n" +
                "                result( sum )\n" +
                "         )\n" +
                "then\n" +
                "  list.add($sum);\n" +
                "end";

        getKieSession(str);
    }

    @Test
    public void testGroupBy() {
        // DROOLS-4737
        String str =
                "import java.util.*;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $map : Map( size > 0 ) from accumulate (\n" +
                "            Person( $age : age, $firstLetter : name.substring(0,1) ), " +
                "                init( Map m = new HashMap(); ), " +
                "                action( Integer i = (Integer) m.get( $firstLetter );" +
                "                        m.put( $firstLetter, i == null ? $age : i + $age ); ), " +
                "                reverse( Integer i = (Integer) m.get( $firstLetter );" +
                "                         m.put( $firstLetter, i - $age ); ), " +
                "                result( m )\n" +
                "         )\n" +
                "  Map.Entry( $initial : key, $sumOfAges : value ) from $map.entrySet()\n" +
                "then\n" +
                "  System.out.println(\"Sum of ages of person with initial '\" + $initial + \"' is \" + $sumOfAges);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        System.out.println("----");

        ksession.delete( meFH );
        ksession.fireAllRules();
    }

    public static class GroupByAcc {
        private final Map<String, Integer> map = new HashMap<>();
        private final Set<String> keys = new HashSet<>();
        private final List<Pair<String, Integer>> results = new ArrayList<>();

        public void action(String key, Integer value) {
            keys.add( key );
            Integer i = map.get( key );
            map.put( key, i == null ? value : i + value );
        }

        public void reverse(String key, Integer value) {
            keys.add( key );
            Integer i = map.get( key );
            map.put( key, i - value );
        }

        public List<Pair<String, Integer>> result() {
            results.clear();
            for (String k : keys) {
                results.add( new Pair( k, map.get(k) ) );
            }
            keys.clear();
            return results;
        }

        public static class Pair<K,V> {
            public final K key;
            public final V value;

            public Pair( K key, V value ) {
                this.key = key;
                this.value = value;
            }

            public K getKey() {
                return key;
            }

            public V getValue() {
                return value;
            }
        }
    }

    @Test
    public void testGroupBy2() {
        // DROOLS-4737
        String str =
                "import java.util.*;\n" +
                "import " + GroupByAcc.class.getCanonicalName() + ";\n" +
                "import " + GroupByAcc.Pair.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global Map results;\n" +
                "rule R when\n" +
                "  $pairs : List( size > 0 ) from accumulate (\n" +
                "            Person( $age : age, $firstLetter : name.substring(0,1) ), " +
                "                init( GroupByAcc acc = new GroupByAcc(); ), " +
                "                action( acc.action( $firstLetter, $age ); ), " +
                "                reverse( acc.reverse( $firstLetter, $age ); ), " +
                "                result( acc.result() )\n" +
                "         )\n" +
                "  GroupByAcc.Pair( $initial : key, $sumOfAges : value ) from $pairs\n" +
                "then\n" +
                "  results.put($initial, $sumOfAges);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 3, results.size() );
        assertEquals( 35, results.get("G") );
        assertEquals( 71, results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );
    }

    public static class GroupKey {
        private final Object key;

        public GroupKey( Object key ) {
            this.key = key;
        }

        public Object getKey() {
            return key;
        }

        @Override
        public boolean equals( Object o ) {
            GroupKey groupKey = ( GroupKey ) o;
            return key.equals( groupKey.key );
        }

        @Override
        public int hashCode() {
            return Objects.hash( key );
        }
    }

    @Test
    public void testGroupBy3() {
        // DROOLS-4737
        String str =
                "import java.util.*;\n" +
                "import " + GroupKey.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global Map results;\n" +
                "rule R1 when\n" +
                "    Person( $initial : name.substring(0,1) )\n" +
                "    not( GroupKey(key == $initial) )\n" +
                "then\n" +
                "    insert( new GroupKey( $initial ) );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $k: GroupKey( $initial : key )\n" +
                "    not( Person( name.substring(0,1) == $initial ) )\n" +
                "then\n" +
                "    delete( $k );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    GroupKey( $initial : key )\n" +
                "    accumulate (\n" +
                "            Person( $age: age, name.substring(0,1) == $initial );\n" +
                "            $sumOfAges : sum($age)\n" +
                "         )\n" +
                "then\n" +
                "    results.put($initial, $sumOfAges);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 3, results.size() );
        assertEquals( 35, results.get("G") );
        assertEquals( 71, results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );
    }

    @Test
    public void testFromAfterAccumulate() {
        // DROOLS-4737
        String str =
                "import " + List.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $persons : List( size > 0 ) from collect ( Person() )\n" +
                "  Person( $age : age, $name : name ) from $persons\n" +
                "then\n" +
                "  System.out.println($name + \"' is \" + $age);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        ksession.insert(new Person("Mario", 45));
        ksession.fireAllRules();
    }

    @Test
    public void testCoercionInAccumulate() {
        String str =
                        "global java.util.List result;\n" +
                        "rule \"Row 1 moveToBiggerCities\"\n" +
                        "  dialect \"mvel\"\n" +
                                "  when\n" +
                        "    $count : Integer( intValue() > 5 , intValue() <= 10 ) " +
                        "      from accumulate ( Integer(), count(1)) \n" +
                        "  then\n" +
                        " result.add($count);" +
                        "end";

        List<Long> result = new ArrayList<>();

        KieSession ksession = getKieSession(str);
        ksession.setGlobal("result", result);

        IntStream.range(1, 7).forEach(ksession::insert);

        ksession.fireAllRules();

        assertEquals(6, result.iterator().next().longValue());

    }

    @Test
    public void testCoercionInAccumulate2() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "global java.util.List result; \n" +
                        "rule \"rule\"\n" +
                        "    when\n" +
                        "        Person($age : age)\n" +
                        "        $count : Number(intValue <= $age) from accumulate(\n" +
                        "            $i : Integer(),\n" +
                        "            count($i)\n" +
                        "        )\n" +
                        "    then\n" +
                        "       result.add($count);\n" +
                        "end\n";

        List<Long> result = new ArrayList<>();

        KieSession ksession = getKieSession(drl);
        ksession.setGlobal("result", result);

        ksession.insert(new Person("Luca", 35));

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);

        ksession.fireAllRules();

        assertEquals(3, result.iterator().next().longValue());
    }

    public static class Interval {

        private LocalDateTime start;
        private LocalDateTime end;

        public Interval(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
        }

        public long between(LocalDateTime start, LocalDateTime end) {
            return Duration.between(start, end).toMinutes();
        }
    }

    @Test
    public void testAccumulateOnStaticMethod() {
        // DROOLS-4979
        final String drl =
                "import java.time.Duration\n" +
                "import " + Interval.class.getCanonicalName() + ";\n" +
                "global java.util.List result; \n" +
                "\n" +
                "rule \"Rule1\"\n" +
                "when\n" +
                "    $count : Number() from accumulate(\n" +
                "       Interval($start : start, $end : end), " +
                "       sum(Duration.between($start, $end).toMinutes())  " +
                "    ) " +
                "then\n" +
                "       result.add($count);\n" +
                "end\n";

        List<Long> result = new ArrayList<>();

        KieSession ksession = getKieSession(drl);
        ReteDumper.dumpRete( ksession );
        ksession.setGlobal("result", result);

        ksession.insert(new Interval(
                LocalDateTime.of(2020, 1, 22, 11, 43),
                LocalDateTime.of(2020, 1, 22, 12, 43)
        ));

        ksession.fireAllRules();

        assertEquals(60, result.iterator().next().longValue());

    }

    @Test
    public void testAccumulateOfDurationBetweenDateTime() {
        // DROOLS-4979
        final String drl =
                "import java.time.Duration\n" +
                "import " + Interval.class.getCanonicalName() + ";\n" +
                "global java.util.List result; \n" +
                "\n" +
                "rule \"Rule1\"\n" +
                "when\n" +
                "    $count : Number() from accumulate(\n" +
                "       $i : Interval($start : start, $end : end), " +
                "       sum($i.between($start, $end))  " +
                "    ) " +
                "then\n" +
                "       result.add($count);\n" +
                "end\n";

        List<Long> result = new ArrayList<>();

        KieSession ksession = getKieSession(drl);
        ksession.setGlobal("result", result);

        ksession.insert(new Interval(
                LocalDateTime.of(2020, 1, 22, 11, 43),
                LocalDateTime.of(2020, 1, 22, 12, 43)
        ));

        ksession.fireAllRules();

        assertEquals(60, result.iterator().next().longValue());

    }

    @Test
    public void testGroupByRegrouped() {
        // DROOLS-5283
        String str =
                "import java.util.*;\n" +
                        "import " + GroupByAcc.class.getCanonicalName() + ";\n" +
                        "import " + GroupByAcc.Pair.class.getCanonicalName() + ";\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  $pairs : List( size > 0 ) from accumulate (\n" +
                        "            Person( $age : age, $firstLetter : name.substring(0,1) ),\n" +
                        "                init( GroupByAcc acc = new GroupByAcc(); ),\n" +
                        "                action( acc.action( $firstLetter, $age ); ),\n" +
                        "                reverse( acc.reverse( $firstLetter, $age ); ),\n" +
                        "                result( acc.result() )\n" +
                        "         )\n" +
                        "  $pairs2 : List( size > 0 ) from accumulate (\n" +
                        "            GroupByAcc.Pair( $initial : key, $sumOfAges : value ) from $pairs,\n" +
                        "                init( GroupByAcc acc2 = new GroupByAcc(); ),\n" +
                        "                action( acc2.action( (String) $initial, (Integer) $sumOfAges ); ),\n" +
                        "                reverse( acc2.reverse( (String) $initial, (Integer) $sumOfAges ); ),\n" +
                        "                result( acc2.result() )\n" +
                        "         )\n" +
                        "  $p: GroupByAcc.Pair() from $pairs2\n" +
                        "then\n" +
                        "  System.out.println($p.toString());\n" +
                        "end";
        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        System.out.println("----");

        ksession.delete( meFH );
        ksession.fireAllRules();

        System.out.println("----");

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();
    }

    @Test
    public void testAccumulateStaticMethodWithPatternBindVar() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + MyUtil.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate (\n" +
                "       $p : Person(), $result : sum( MyUtil.add($p.getAge(), 10) ) "+
                "         )" +
                "then\n" +
                "  insert($result);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("x");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertEquals(1, results.size());
        assertEquals(142, results.get(0).intValue());
    }

    public static class MyUtil {
        public static int add(int a, int b) {
            return a + b;
        }
    }
}
