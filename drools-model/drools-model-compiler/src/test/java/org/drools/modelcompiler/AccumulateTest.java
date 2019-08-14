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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Customer;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.TargetPolicy;
import org.drools.modelcompiler.oopathdtables.InternationalAddress;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.FactHandle;

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
                "  String( $l : length )" +
                "  $sum : Integer() from accumulate (\n" +
                "            Person( age > 18, $age : age ), init( int sum = 0 * $l; ), action( sum += $age; ), reverse( sum -= $age; ), result( sum )\n" +
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

}
