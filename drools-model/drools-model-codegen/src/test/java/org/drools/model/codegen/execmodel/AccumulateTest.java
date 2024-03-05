/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.apache.commons.math3.util.Pair;
import org.drools.core.base.accumulators.IntegerMaxAccumulateFunction;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.model.functions.accumulate.GroupKey;
import org.drools.model.codegen.execmodel.domain.Adult;
import org.drools.model.codegen.execmodel.domain.Child;
import org.drools.model.codegen.execmodel.domain.Customer;
import org.drools.model.codegen.execmodel.domain.Parent;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.drools.model.codegen.execmodel.domain.StockTick;
import org.drools.model.codegen.execmodel.domain.TargetPolicy;
import org.drools.model.codegen.execmodel.oopathdtables.InternationalAddress;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);
    }

    @Test
    public void testFromOnAccumulatedValue() {
        // DROOLS-5635
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                        "                $sum : sum($p.getAge())  \n" +
                        "              )                          \n" +
                        "  $s: String() from $sum.toString()\n" +
                        "then\n" +
                        "  insert(new Result($s));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert("test");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("77");
    }

    @Test
    public void testFromOnAccumulatedValueUsingExists() {
        // DROOLS-5635
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  accumulate ( $p: Person ( getName().startsWith(\"M\") ) and exists(String()); \n" +
                        "                $sum : sum($p.getAge())  \n" +
                        "              )                          \n" +
                        "  $s: String() from $sum.toString()\n" +
                        "then\n" +
                        "  insert(new Result($s));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert("test");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("77");
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(2l);
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

        assertThat(numberOfRules).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("fired");
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(2);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);
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
        assertThat(results.size()).isEqualTo(0);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);
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
        assertThat(results).contains(new Result(38.5));
        assertThat(results).contains(new Result(77));
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
        assertThat(results).contains(new Result(38.5d));
        assertThat(results).contains(new Result(77));
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
        assertThat(results).contains(new Result(37));
        assertThat(results).contains(new Result(77));
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
        assertThat(results).contains(new Result(43));
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
        assertThat(((Number) results.iterator().next().getValue()).intValue()).isEqualTo(49);
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
        assertThat(((Number) results.iterator().next().getValue()).intValue()).isEqualTo(49);
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
        assertThat(((Number) results.iterator().next().getValue()).intValue()).isEqualTo(54);
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
        assertThat(((Number) results.iterator().next().getValue()).intValue()).isEqualTo(59);
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
        assertThat(results).contains(new Result(1));
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);
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
        assertThat(filtered).isEqualTo(1);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(112);

        if (performReverse) {
            ksession.delete(fh_Mario);
            ksession.fireAllRules();

            results = getObjectsIntoList(ksession, Integer.class);
            assertThat(results.size()).isEqualTo(2);
            assertThat(results).contains(72);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(38);

        ksession.delete(fh_Mario);
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results).contains(36);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(38 + 14);

        ksession.delete(fh_Mario);
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results).contains(36 + 14);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(8);
    }

    @Test
    public void testAccumulateFromWithConstraint() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Person.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Address.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.InternationalAddress.class.getCanonicalName() + ";" +
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains("Milan");
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

        assertThat(results).contains(4);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(((Number) results.iterator().next().getValue()).intValue()).isEqualTo(77);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(112);

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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(112);

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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).intValue()).isEqualTo(112);

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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).intValue()).isEqualTo(11);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).intValue()).isEqualTo(2);
    }

    @Test
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
                "                 $tot : sum( ((Integer)$data.values[ 0 ]) + $bias ) ) " +
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
        dataType.set(data2, "values", List.of(10));
        dataType.set(data2, "bias", 100);
        ksession.insert(data2);

        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).intValue()).isEqualTo(212);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getAge()).isEqualTo(3);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(23);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(10);
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(23);
    }

    @Test
    public void testBigDecimalOperationsInAccumulateConstraint() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule \"rule1\"\n" +
                "when\n" +
                "    $moneySummed : BigDecimal () from accumulate( " +
                "       Person( money != null, $bd: (money + money)), " +
                "       sum($bd))\n" +
                "then\n" +
                "    results.add($moneySummed);\n" +
                "end\n";

        KieSession ksession1 = getKieSession(str);

        ArrayList<BigDecimal> results = new ArrayList<>();
        ksession1.setGlobal("results", results);

        Person p1 = new Person();
        p1.setMoney( new BigDecimal(1 ));
        ksession1.insert( p1 );
        Person p2 = new Person();
        p2.setMoney( new BigDecimal(3 ));
        ksession1.insert(p2);
        assertThat(ksession1.fireAllRules()).isEqualTo(1);

        assertThat(results).containsExactly(BigDecimal.valueOf(8));

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
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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

        assertThat(ksession.fireAllRules()).isEqualTo(1);
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

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).intValue()).isEqualTo(5);
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

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).intValue()).isEqualTo(5);
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

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).intValue()).isEqualTo(5);
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

        assertThat(result.isResultAccumulated()).isEqualTo(true);
        assertThat(result.getMaxShortValue()).isEqualTo((short)40);
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

        assertThat(rulesFired).isEqualTo(1);
        assertThat(result.getBigDecimalValue()).isEqualTo(BigDecimal.valueOf(3000));
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

        assertThat(list.get(0)).isEqualTo(new BigDecimal(300));

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

        assertThat(list.get(0)).isEqualTo(63);
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

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get("G")).isEqualTo(35);
        assertThat(results.get("E")).isEqualTo(71);
        assertThat(results.get("M")).isEqualTo(126);
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get("M")).isEqualTo(81);
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get("G")).isEqualTo(40);
        assertThat(results.get("M")).isEqualTo(119);
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
                "    not( GroupKey(topic ==\"a\", key == $initial) )\n" +
                "then\n" +
                "    insert( new GroupKey( \"a\", $initial ) );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $k: GroupKey( topic ==\"a\", $initial : key )\n" +
                "    not( Person( name.substring(0,1) == $initial ) )\n" +
                "then\n" +
                "    delete( $k );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    GroupKey( topic ==\"a\", $initial : key )\n" +
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

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get("G")).isEqualTo(35);
        assertThat(results.get("E")).isEqualTo(71);
        assertThat(results.get("M")).isEqualTo(126);
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get("M")).isEqualTo(81);
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get("G")).isEqualTo(40);
        assertThat(results.get("M")).isEqualTo(119);
    }

    @Test
    public void testGroupBy3WithExists() {
        String str =
                "import java.util.*;\n" +
                "import " + GroupKey.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global Map results;\n" +
                "rule R1 when\n" +
                "    Person( $initial : name.substring(0,1) )\n" +
                "    exists( String( this == $initial) )\n " +
                "    not( GroupKey(topic ==\"a\", key == $initial) )\n" +
                "then\n" +
                "    insert( new GroupKey( \"a\", $initial ) );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $k: GroupKey( topic ==\"a\", $initial : key )\n" +
                "    not( Person( name.substring(0,1) == $initial ) )\n" +
                "then\n" +
                "    delete( $k );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    GroupKey( topic ==\"a\", $initial : key )\n" +
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

        ksession.insert( "G" );
        ksession.insert( "M" );
        ksession.insert( "X" );

        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get("G")).isEqualTo(35);
        assertThat(results.get("E")).isNull();
        assertThat(results.get("M")).isEqualTo(126);
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get("M")).isEqualTo(81);
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get("G")).isEqualTo(40);
        assertThat(results.get("M")).isEqualTo(119);
    }

    @Test
    public void testGroupBy3WithExists2() {
        String str =
                "import java.util.*;\n" +
                "import " + GroupKey.class.getCanonicalName() + ";\n" +
                "import " + Parent.class.getCanonicalName() + ";\n" +
                "import " + Child.class.getCanonicalName() + ";\n" +
                "global List results;\n" +
                "rule R1 when\n" +
                "    $p : Parent()\n" +
                "    exists( Child( this == $p.child) )\n " +
                "    not( GroupKey(topic ==\"a\", key == $p.getChild() ) )\n" +
                "then\n" +
                "    insert( new GroupKey( \"a\", $p.getChild() ) );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $group: GroupKey( topic ==\"a\", $key : key )\n" +
                "    not( $p : Parent() and exists( Child( this == $key ) ) )\n" +
                "then\n" +
                "    delete( $group );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    GroupKey( topic ==\"a\", $k : key )\n" +
                "    accumulate (\n" +
                "            $p : Parent() and exists( Child( this == $p.getChild()) );\n" +
                "            $count : count($p)\n" +
                "         )\n" +
                "then\n" +
                "    results.add(java.util.Arrays.asList($k, $count));\n" +
                "end";

        KieSession ksession = getKieSession(str);

        List results = new ArrayList();
        ksession.setGlobal( "results", results );

        Child child1 = new Child("Child1", 1);
        Parent parent1 = new Parent("Parent1", child1);
        Child child2 = new Child("Child2", 2);
        Parent parent2 = new Parent("Parent2", child2);

        ksession.insert(parent1);
        ksession.insert(parent2);
       FactHandle toRemove = ksession.insert(child1);
        ksession.insert(child2);
        ksession.fireAllRules();
        assertThat(results)
                  .containsOnly(Arrays.asList(child1, 2L), Arrays.asList(child2, 2L));

        // Remove child1, therefore it does not exist, therefore there should be no groupBy matches for the child.
        results.clear();
        ksession.delete(toRemove);

        // Yet, we still get (Child2, 0).
        ksession.fireAllRules();
        assertThat(results)
                .containsOnly(Arrays.asList(child2, 1L));
    }

    @Test
    public void testGroupBy3With2VarsKey() {
        String str =
                "import java.util.*;\n" +
                "import " + GroupKey.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global Map results;\n" +
                "rule R1 when\n" +
                "    Person( $initial : name.substring(0,1) )\n" +
                "    String( $l : length )\n " +
                "    not( GroupKey(topic ==\"a\", key == $initial + $l) )\n" +
                "then\n" +
                "    insert( new GroupKey( \"a\", $initial + $l ) );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $group: GroupKey( topic ==\"a\", $k : key )\n" +
                "    not( Person( $initial : name.substring(0,1) ) and\n" +
                "         String( $l : length, $k == $initial + $l )\n " +
                "    )\n" +
                "then\n" +
                "    delete( $group );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    GroupKey( topic ==\"a\", $k : key )\n" +
                "    accumulate (\n" +
                "            Person( $age: age, $initial : name.substring(0,1) ) and\n" +
                "            String( $l : length, $k == $initial + $l );\n" +
                "            $sumOfAges : sum($age)\n" +
                "         )\n" +
                "then\n" +
                "    results.put($k, $sumOfAges);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert( "test" );
        ksession.insert( "check" );
        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(6);
        assertThat(results.get("G4")).isEqualTo(35);
        assertThat(results.get("E4")).isEqualTo(71);
        assertThat(results.get("M4")).isEqualTo(126);
        assertThat(results.get("G5")).isEqualTo(35);
        assertThat(results.get("E5")).isEqualTo(71);
        assertThat(results.get("M5")).isEqualTo(126);
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get("M4")).isEqualTo(81);
        assertThat(results.get("M5")).isEqualTo(81);
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(4);
        assertThat(results.get("G4")).isEqualTo(40);
        assertThat(results.get("M4")).isEqualTo(119);
        assertThat(results.get("G5")).isEqualTo(40);
        assertThat(results.get("M5")).isEqualTo(119);
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

        assertThat(result.iterator().next().longValue()).isEqualTo(6);

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

        assertThat(result.iterator().next().longValue()).isEqualTo(3);
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
        ksession.setGlobal("result", result);

        ksession.insert(new Interval(
                LocalDateTime.of(2020, 1, 22, 11, 43),
                LocalDateTime.of(2020, 1, 22, 12, 43)
        ));

        ksession.fireAllRules();

        assertThat(result.iterator().next().longValue()).isEqualTo(60);

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

        assertThat(result.iterator().next().longValue()).isEqualTo(60);

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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).intValue()).isEqualTo(142);
    }

    public static class MyUtil {
        public static int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void testModifyAccumulatedFactWithNonIndexableConstraint() {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "   $s : String()" +
                "   accumulate (\n" +
                "       Person(name.equals( $s )), $result : count() " +
                "         )" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "a" );
        ksession.insert( "b" );
        ksession.insert( "c" );
        ksession.insert( "d" );

        Person a1 = new Person( "a", 1 );
        Person a2 = new Person( "a", 2 );
        Person a3 = new Person( "a", 3 );
        Person b1 = new Person( "b", 1 );
        Person b2 = new Person( "b", 2 );
        Person b3 = new Person( "b", 3 );
        Person c1 = new Person( "c", 1 );
        Person c2 = new Person( "c", 2 );
        Person c3 = new Person( "c", 3 );
        Person d1 = new Person( "d", 1 );
        Person d2 = new Person( "d", 2 );
        Person d3 = new Person( "d", 3 );

        ksession.insert( a1 );
        ksession.insert( a2 );
        ksession.insert( a3 );
        ksession.insert( b1 );
        ksession.insert( b2 );
        ksession.insert( b3 );
        ksession.insert( c1 );
        FactHandle c2fh = ksession.insert( c2 );
        ksession.insert( c3 );
        ksession.insert( d1 );
        ksession.insert( d2 );
        ksession.insert( d3 );

        assertThat(ksession.fireAllRules()).isEqualTo(4);

        c2.setName( "b" );
        ksession.update( c2fh, c2 );

        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testAccumulateWithManyBindings() {
        // DROOLS-5546
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  accumulate (\n" +
                "       Person($age : age, $name : name), $max : max( $name.length() ) " +
                "         )" +
                "then\n" +
                "  insert($max);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 40 ) );
        ksession.insert( new Person( "Luca", 40 ) );

        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).intValue()).isEqualTo(5);
    }

    @Test
    public void testFalseNodeSharing() {
        // DROOLS-5686
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "  accumulate (\n" +
                "       Person($var : age), $sum : sum( $var ) " +
                "         )" +
                "then\n" +
                "  insert($sum);\n" +
                "end\n" +
                "rule R2 when\n" +
                "  accumulate (\n" +
                "       Person($var : name.length), $sum : sum( $var ) " +
                "         )" +
                "then\n" +
                "  insert(\"\" + $sum);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 46 ) );
        ksession.insert( new Person( "Mark", 44 ) );
        ksession.insert( new Person( "Luca", 36 ) );

        ksession.fireAllRules();

        List<Number> resultsInt = getObjectsIntoList(ksession, Number.class);
        assertThat(resultsInt.size()).isEqualTo(1);
        assertThat(resultsInt.get(0).intValue()).isEqualTo(126);

        List<String> resultsString = getObjectsIntoList(ksession, String.class);
        assertThat(resultsString.size()).isEqualTo(1);
        assertThat(resultsString.get(0)).isEqualTo("13");
    }

    @Test
    public void testAccumulateWithTwoFunctions1() {
        // DROOLS-5752
        String str = "import java.time.Duration;\n" +
                "import " + Shift.class.getCanonicalName() + ";\n" +
                "rule \"R1\"\n" +
                "    when\n" +
                "        accumulate(\n" +
                "            $other : Shift(\n" +
                "                $shiftStart : startDateTime\n" +
                "            ),\n" +
                "            $shiftCount : count($other),\n" +
                "            $totalMinutes : sum(Duration.between($shiftStart, $shiftStart).toMinutes())\n" +
                "        )\n" +
                "    then\n" +
                "        System.out.println($shiftCount + \" \" + $totalMinutes);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Shift( OffsetDateTime.now()) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testAccumulateWithTwoFunctions2() {
        // DROOLS-5752
        String str = "import java.time.Duration;\n" +
                "import " + Shift.class.getCanonicalName() + ";\n" +
                "rule \"R1\"\n" +
                "    when\n" +
                "        accumulate(\n" +
                "            $other : Shift(\n" +
                "                $shiftStart : startDateTime\n" +
                "            ),\n" +
                "            $totalMinutes : sum(Duration.between($shiftStart, $shiftStart).toMinutes()),\n" +
                "            $shiftCount : count($other)\n" +
                "        )\n" +
                "    then\n" +
                "        System.out.println($shiftCount + \" \" + $totalMinutes);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Shift( OffsetDateTime.now()) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public class Shift {

        private final AtomicLong lengthInMinutes = new AtomicLong(-1);
        private OffsetDateTime startDateTime;

        private String employee = null;

        public Shift(OffsetDateTime startDateTime) {
            this.startDateTime = startDateTime;
        }

        @Override
        public String toString() {
            return startDateTime.toString();
        }

        public long getLengthInMinutes() { // Thread-safe cache.
            long currentLengthInMinutes = lengthInMinutes.get();
            if (currentLengthInMinutes >= 0) {
                return currentLengthInMinutes;
            }
            long newLengthInMinutes = startDateTime.until(startDateTime, ChronoUnit.MINUTES);
            lengthInMinutes.set(newLengthInMinutes);
            return newLengthInMinutes;
        }

        public OffsetDateTime getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(OffsetDateTime startDateTime) {
            this.startDateTime = startDateTime;
            this.lengthInMinutes.set(-1);
        }

        public String getEmployee() {
            return employee;
        }

        public void setEmployee(String employee) {
            this.employee = employee;
        }

        public OffsetDateTime getEndDateTime() {
            // Pretend a shift length is always 8 hours.
            return startDateTime.plus(8, ChronoUnit.HOURS);
        }
    }

    public static Duration between(OffsetDateTime start, OffsetDateTime end) {
        return Duration.between(start, end);
    }

    @Test
    public void testAccumulateNumberFromSum() {
        String str =
                "import " + Shift.class.getCanonicalName() + ";"
                        + "import " + AccumulateTest.class.getCanonicalName() + ";"
                        + "import " + Result.class.getCanonicalName() + ";"
                        + "rule \"dailyMinutes\"\n"
                        + "    when\n"
                        + "        accumulate(\n"
                        + "            $other : Shift(\n"
                        + "                $shiftStart : startDateTime,\n"
                        + "                $shiftEnd : endDateTime\n"
                        + "            ),\n"
                        + "            $shiftCount : count($other),\n"
                        + "            $totalMinutes : sum(AccumulateTest.between($shiftStart, $shiftEnd).toMinutes())\n"
                        + "        )\n"
                        + "        Number(this > 0) from $totalMinutes\n"
                        + "    then\n"
                        + "        insert(new Result($totalMinutes));\n"
                        + "end";



        KieSession ksession = getKieSession( str );

        Shift shift = new Shift(OffsetDateTime.now());

        ksession.insert(shift);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(8 * 60L);
    }

    private static int switchMachinesInAssignments(KieSession session, MrProcessAssignment left,
                                                   MrProcessAssignment right) {
        FactHandle leftHandle = session.getFactHandle(left);
        FactHandle rightHandle = session.getFactHandle(right);
        MrMachine original = left.getMachine();
        left.setMachine(right.getMachine());
        right.setMachine(original);
        session.update(leftHandle, left);
        session.update(rightHandle, right);
        return session.fireAllRules();
    }

    @Test
    public void testDoubleAccumulateNPE() {
        // Prepare reproducing data.
        MrMachine machine2 = new MrMachine();
        MrMachine machine3 = new MrMachine();
        MrProcessAssignment assignment1 = new MrProcessAssignment(new MrProcess(), machine3, machine3);
        MrProcessAssignment assignment2 = new MrProcessAssignment(new MrProcess(), machine2, machine2);
        MrProcessAssignment assignment3 = new MrProcessAssignment(new MrProcess(), machine2, machine2);
        MrProcessAssignment assignment4 = new MrProcessAssignment(new MrProcess(), machine3, machine3);

        String rule = "import " + MrProcessAssignment.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "rule R1\n" +
                "when\n" +
                "   $assignments: List(size > 0) from accumulate(\n" +
                "        $a: MrProcessAssignment(machine != null, this.isMoved() == true),\n" +
                "        collectList($a)\n" +
                "   )\n" +
                "    accumulate(\n" +
                "        $a2: MrProcessAssignment() from $assignments,\n" +
                "        $count: count($a2)\n" +
                "    )\n" +
                "then\n" +
                "    result.add($count);\n" +
                "end;";
        KieSession kieSession = getKieSession(rule);
        List<Long> result = new ArrayList<>();
        kieSession.setGlobal("result", result);

        // Insert facts into the session.
        kieSession.insert(assignment1);
        kieSession.insert(assignment2);
        kieSession.insert(assignment3);
        kieSession.insert(assignment4);
        int fired = kieSession.fireAllRules();
        assertThat(fired).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();

        // Execute the sequence of session events that triggers the exception.
        fired = switchMachinesInAssignments(kieSession, assignment1, assignment2);
        assertThat(fired).isEqualTo(1);
        assertThat(result.get(0).longValue()).isEqualTo(2);
        result.clear();

        fired = switchMachinesInAssignments(kieSession, assignment1, assignment2);
        assertThat(fired).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();

        fired = switchMachinesInAssignments(kieSession, assignment4, assignment3);
        assertThat(fired).isEqualTo(1);
        assertThat(result.get(0).longValue()).isEqualTo(2);
        result.clear();

        kieSession.dispose();
    }

    public static class MrProcess {

    }

    public static class MrProcessAssignment {

        private MrProcess process;
        private MrMachine originalMachine;
        private MrMachine machine;

        public MrProcessAssignment(MrProcess process, MrMachine originalMachine, MrMachine machine) {
            this.process = process;
            this.originalMachine = originalMachine;
            this.machine = machine;
        }

        public MrProcess getProcess() {
            return process;
        }

        public MrMachine getOriginalMachine() {
            return originalMachine;
        }

        public MrMachine getMachine() {
            return machine;
        }

        public void setMachine(MrMachine machine) {
            this.machine = machine;
        }

        // ************************************************************************
        // Complex methods
        // ************************************************************************

        public boolean isMoved() {
            if (machine == null) {
                return false;
            }
            return !Objects.equals(originalMachine, machine);
        }

    }

    public static class MrMachine {

    }

    @Test
    public void testInlineAccumulateWithAnd() {
        // RHDM-1549
        String str =
                "import " + Car.class.getCanonicalName() + ";" +
                        "import " + Order.class.getCanonicalName() + ";" +
                        "import " + BigDecimal.class.getCanonicalName() + ";" +
                        "global java.util.List result;\n" +
                        "rule R when\n" +
                        "        $total : BigDecimal() from accumulate( $car : Car( discontinued == true ) and Order( item == $car, $price : price ),\n" +
                        "                                               init( BigDecimal total = BigDecimal.ZERO; ),\n" +
                        "                                               action( total = total.add( $price ); ),\n" +
                        "                                               reverse( total = total.subtract( $price ); ),\n" +
                        "                                               result( total ) )\n" +
                        "    then\n" +
                        "        result.add($total);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        List<BigDecimal> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Car a = new Car("A180");
        a.setDiscontinued(false);
        ksession.insert(a);

        for (int i = 0; i < 10; i++) {
            Order order = new Order(a, new BigDecimal(30000));
            ksession.insert(order);
        }

        Car c = new Car("C180");
        c.setDiscontinued(true);
        ksession.insert(c);

        for (int i = 0; i < 5; i++) {
            Order order = new Order(c, new BigDecimal(45000));
            ksession.insert(order);
        }

        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(new BigDecimal( 225000 ));

        ksession.dispose();
    }

    @Test
    public void testInlineMvelAccumulateWithAnd() {
        // RHDM-1549
        String str =
                "import " + Car.class.getCanonicalName() + ";" +
                "import " + Order.class.getCanonicalName() + ";" +
                "import " + BigDecimal.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "dialect \"mvel\"\n" +
                "rule R when\n" +
                "        $total : BigDecimal() from accumulate( $car : Car( discontinued == true ) and Order( item == $car, $price : price ),\n" +
                "                                               init( BigDecimal total = BigDecimal.ZERO; ),\n" +
                "                                               action( total += $price; ),\n" +
                "                                               reverse( total -= $price; ),\n" +
                "                                               result( total ) )\n" +
                "    then\n" +
                "        result.add($total);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        List<BigDecimal> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Car a = new Car("A180");
        a.setDiscontinued(false);
        ksession.insert(a);

        for (int i = 0; i < 10; i++) {
            Order order = new Order(a, new BigDecimal(30000));
            ksession.insert(order);
        }

        Car c = new Car("C180");
        c.setDiscontinued(true);
        ksession.insert(c);

        for (int i = 0; i < 5; i++) {
            Order order = new Order(c, new BigDecimal(45000));
            ksession.insert(order);
        }

        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(new BigDecimal( 225000 ));

        ksession.dispose();
    }

    public static class Car {
        private String name = "";
        private String variant = "";
        private BigDecimal totalSales = BigDecimal.ZERO;
        private boolean discontinued = false;

        public Car() { }

        public Car(String name) {
            this.name = name;
        }

        public Car(String name, String variant) {
            this.name = name;
            this.variant = variant;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVariant() {
            return variant;
        }

        public void setVariant(String variant) {
            this.variant = variant;
        }

        public BigDecimal getTotalSales() {
            return totalSales;
        }

        public void setTotalSales(BigDecimal totalSales) {
            this.totalSales = totalSales;
        }

        public boolean getDiscontinued() {
            return discontinued;
        }

        public void setDiscontinued(boolean discontinued) {
            this.discontinued = discontinued;
        }

        public String toString() {
            return (name + " " + variant).trim();
        }
    }

    public static class Order {
        private Car item;
        private BigDecimal price;

        public Order() { }

        public Order(Car item, BigDecimal price) {
            this.item = item;
            this.price = price;
        }

        public Car getItem() {
            return item;
        }

        public void setItem(Car item) {
            this.item = item;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    @Test
    public void testAccumulateOnPartiallyReversibleFunction() {
        // DROOLS-5930
        String str =
                "import accumulate " + CountingIntegerMaxAccumulateFunction.class.getCanonicalName() + " countingMax;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  accumulate ( Person($age : age), $max : countingMax( $age ) )" +
                "then\n" +
                "  result.add($max);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        CountingIntegerMaxAccumulateFunction accFunction = CountingIntegerMaxAccumulateFunction.INSTANCE;

        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person mario = new Person( "Mario", 46 );

        FactHandle marioFH = ksession.insert( mario );
        FactHandle markFH = ksession.insert( new Person( "Mark", 42 ) );
        FactHandle lucaFH = ksession.insert( new Person( "Luca", 36 ) );

        ksession.fireAllRules();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).intValue()).isEqualTo(46);
        assertThat(accFunction.getAccumulateCount()).isEqualTo(3);

        result.clear();
        accFunction.resetAccumulateCount();

        // this shouldn't trigger any reaccumulate
        ksession.delete( markFH );

        ksession.fireAllRules();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).intValue()).isEqualTo(46);
        assertThat(accFunction.getAccumulateCount()).isEqualTo(0);

        result.clear();
        accFunction.resetAccumulateCount();

        mario.setAge( 18 );
        ksession.update( marioFH, mario );

        ksession.fireAllRules();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).intValue()).isEqualTo(36);
        assertThat(accFunction.getAccumulateCount()).isEqualTo(2);
    }

    public static class CountingIntegerMaxAccumulateFunction extends IntegerMaxAccumulateFunction {
        public static CountingIntegerMaxAccumulateFunction INSTANCE;

        private int counter = 0;

        public CountingIntegerMaxAccumulateFunction() {
            INSTANCE = this;
        }

        @Override
        public void accumulate( MaxData data, Object value ) {
            super.accumulate( data, value );
            counter++;
        }

        public int getAccumulateCount() {
            return counter;
        }

        public void resetAccumulateCount() {
            counter = 0;
        }
    }

    @Test
    public void testOneAccumulateOnPattern() {
        // DROOLS-5938
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Collection.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $acc1 : Collection() from accumulate( \n" +
                "    $p : Person( ), collectSet( $p ) ) \n" +
                "then\n" +
                "  result.add($acc1.iterator().next());" +
                "end";

        KieSession ksession = getKieSession( str );

        List<Person> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person lukas = new Person("Lukas", 35);
        ksession.insert(lukas);
        ksession.fireAllRules();

        System.out.println(result);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(lukas);
    }

    @Test
    public void testOneAccumulateOnPatternWithVarBinding() {
        // DROOLS-5938
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Collection.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $acc1 : Collection() from accumulate( \n" +
                "    $p : Person( $name : name ), collectSet( $p ) ) \n" +
                "then\n" +
                "  result.add($acc1.iterator().next());" +
                "end";

        KieSession ksession = getKieSession( str );

        List<Person> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person lukas = new Person("Lukas", 35);
        ksession.insert(lukas);
        ksession.fireAllRules();


        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(lukas);
    }

    @Test
    public void testTwoAccumulatesOnPattern() {
        // DROOLS-5938
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                        "import " + Pair.class.getCanonicalName() + ";\n" +
                        "import " + Collection.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule R when\n" +
                        "  $acc1 : Collection() from accumulate( \n" +
                        "    Person( $pair1 : Pair.create(name, age) ), collectSet( $pair1 ) ) \n" +
                        "  $acc2 : Collection() from accumulate( \n" +
                        "    $pair2 : Pair( ) from $acc1, collectSet( $pair2 ) )\n" +
                        "then\n" +
                        "  result.add($acc2.iterator().next());" +
                        "end";

        KieSession ksession = getKieSession( str );

        List<Pair> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("Lukas", 35));
        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(Pair.create("Lukas", 35));
    }

    @Test
    public void testTwoAccumulatesOnPatternWithVarBinding() {
        // DROOLS-5938
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Pair.class.getCanonicalName() + ";\n" +
                "import " + Collection.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "  $acc1 : Collection() from accumulate( \n" +
                "    Person( $pair1 : Pair.create(name, age) ), collectSet( $pair1 ) ) \n" +
                "  $acc2 : Collection() from accumulate( \n" +
                "    $pair2 : Pair( $k : key ) from $acc1, collectSet( $pair2 ) )\n" +
                "then\n" +
                "  result.add($acc2.iterator().next());" +
                "end";

        KieSession ksession = getKieSession( str );

        List<Pair> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("Lukas", 35));
        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(Pair.create("Lukas", 35));
    }

    @Test
    public void testBindingOrderWithInlineAccumulate() {
        // RHDM-1551
        String str =
                "import " + Aclass.class.getCanonicalName() + ";\n" +
                "import " + Bclass.class.getCanonicalName() + ";\n" +
                "import " + Cclass.class.getCanonicalName() + ";\n" +
                "import " + Dclass.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "import java.util.List\n" +
                "import java.util.Set\n" +
                "import java.util.Map\n" +
                "import java.util.HashMap\n" +
                "\n" +
                "dialect \"java\"\n" +
                "\n" +
                "rule \"rule5a\"\n" +
                "    when\n" +
                "        $b : Bclass()\n" +
                "        $c : Cclass()\n" +
                "        $d : Dclass()\n" +
                "        $eSet : Set() from accumulate( $a : Aclass(),\n" +
                "                                       init( Map map = new HashMap(); ),\n" +
                "                                       action( $a.method(map, $b, $c, $d); ),\n" +
                "                                       result( map.keySet() ) )\n" +
                "    then\n" +
                "        result.add($eSet.iterator().next());" +
                "end";

        KieSession kSession = getKieSession( str );

        List<String> result = new ArrayList<>();
        kSession.setGlobal("result", result);

        kSession.insert(new Aclass("A180"));
        kSession.insert(new Bclass("B180"));
        kSession.insert(new Cclass("C200"));
        kSession.insert(new Dclass("D250"));

        assertThat(kSession.fireAllRules()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo("B180");

        kSession.dispose();
    }

    @Test
    public void testBindingOrderWithInlineAccumulateAndLists() {
        // RHDM-1551
        String str =
                "import " + Aclass.class.getCanonicalName() + ";\n" +
                "import " + Bclass.class.getCanonicalName() + ";\n" +
                "import " + Cclass.class.getCanonicalName() + ";\n" +
                "import " + Dclass.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "import java.util.List\n" +
                "import java.util.Set\n" +
                "import java.util.Map\n" +
                "import java.util.HashMap\n" +
                "\n" +
                "dialect \"java\"\n" +
                "\n" +
                "rule \"rule5a\"\n" +
                "    when\n" +
                "        $bList : List() from collect( Bclass() )\n" +
                "        $cList : List() from collect( Cclass() )\n" +
                "        $dList : List() from collect( Dclass() )\n" +
                "        $eSet : Set() from accumulate( $a : Aclass(),\n" +
                "                                       init( Map map = new HashMap(); ),\n" +
                "                                       action( $a.methodOnList(map, $bList, $cList, $dList); ),\n" +
                "                                       result( map.keySet() ) )\n" +
                "    then\n" +
                "        result.add($eSet.iterator().next());" +
                "end";

        KieSession kSession = getKieSession( str );

        List<String> result = new ArrayList<>();
        kSession.setGlobal("result", result);

        kSession.insert(new Aclass("A180"));
        kSession.insert(new Bclass("B180"));
        kSession.insert(new Cclass("C200"));
        kSession.insert(new Dclass("D250"));

        assertThat(kSession.fireAllRules()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo("B180");

        kSession.dispose();
    }

    @Test
    public void testBindingOrderWithInlineAccumulateAndListsAndFrom() {
        // RHDM-1551
        String str =
                "import " + Aclass.class.getCanonicalName() + ";\n" +
                "import " + Bclass.class.getCanonicalName() + ";\n" +
                "import " + Cclass.class.getCanonicalName() + ";\n" +
                "import " + Dclass.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "import java.util.List\n" +
                "import java.util.Set\n" +
                "import java.util.Map\n" +
                "import java.util.HashMap\n" +
                "\n" +
                "dialect \"java\"\n" +
                "\n" +
                "rule \"rule5a\"\n" +
                "    when\n" +
                "        $aList : List() from collect( Aclass() )\n" +
                "        $bList : List() from collect( Bclass() )\n" +
                "        $cList : List() from collect( Cclass() )\n" +
                "        $dList : List() from collect( Dclass() )\n" +
                "        $eSet : Set() from accumulate( $a : Aclass() from $aList,\n" +
                "                                       init( Map map = new HashMap(); ),\n" +
                "                                       action( $a.methodOnList(map, $bList, $cList, $dList); ),\n" +
                "                                       result( map.keySet() ) )\n" +
                "    then\n" +
                "        result.add($eSet.iterator().next());" +
                "end";

        KieSession kSession = getKieSession( str );

        List<String> result = new ArrayList<>();
        kSession.setGlobal("result", result);

        kSession.insert(new Aclass("A180"));
        kSession.insert(new Bclass("B180"));
        kSession.insert(new Cclass("C200"));
        kSession.insert(new Dclass("D250"));

        assertThat(kSession.fireAllRules()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo("B180");

        kSession.dispose();
    }

    public static class Aclass {
        private String name;

        public Aclass() { }

        public Aclass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void method(Map map, Bclass b, Cclass c, Dclass d) {
            map.put(b.getName(), c.getName());
        }

        public void methodOnList(Map map, Collection<Bclass> bs, Collection<Cclass> cs, Collection<Dclass> ds) {
            map.put( bs.iterator().next().getName(), cs.iterator().next().getName());
        }
    }

    public static class Bclass {
        private String name;

        public Bclass() { }

        public Bclass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Cclass {
        private String name;

        public Cclass() { }

        public Cclass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Dclass {
        private String name;

        public Dclass() { }

        public Dclass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testMultiAccumulate() {
        // RHDM-1572
        String str =
                "global java.util.List result;\n" +
                "rule R when\n" +
                "    accumulate ( $s : String( $name : toString, $length : length );\n" +
                "                 $count : count($name),\n" +
                "                 $sum : sum($length),\n" +
                "                 $list : collectList($s) )\n" +
                "    then\n" +
                "        result.add($count);\n" +
                "        result.add($sum);\n" +
                "        result.addAll($list);\n" +
                "end";

        KieSession kSession = getKieSession( str );

        List<Object> result = new ArrayList<>();
        kSession.setGlobal( "result", result );

        kSession.insert( "test" );
        kSession.insert( "mytest" );
        kSession.insert( "anothertest" );

        assertThat(kSession.fireAllRules()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0)).isEqualTo(3L);
        assertThat(result.get(1)).isEqualTo(21);
        assertThat(result.contains("test")).isTrue();
        assertThat(result.contains("mytest")).isTrue();
        assertThat(result.contains("anothertest")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testAccumulateWithExists() {
        // RHDM-1571
        String str =
                "import " + Car.class.getCanonicalName() + ";" +
                "import " + Aclass.class.getCanonicalName() + ";" +
                "global java.util.List result;\n" +
                "rule R when\n" +
                "        $list : List() from accumulate ( $car : Car( $name : name )\n" +
                "                                         and exists Aclass( name == $name );\n" +
                "                                         collectList($car) )\n" +
                "    then\n" +
                "        result.addAll($list);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        List<Car> result = new ArrayList<>();
        ksession.setGlobal( "result", result );

        ksession.insert(new Car("A180"));
        ksession.insert(new Aclass("A180"));
        ksession.insert(new Aclass("A180"));
        ksession.insert(new Aclass("A180"));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("A180");

        ksession.dispose();
    }

    @Test
    public void testAccumulateWithForAll() {
        // DROOLS-6025
        String str =
                "import " + GrandChild.class.getCanonicalName() + ";\n" +
                "import " + GrandParent.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "        accumulate ( " +
                "        $gp: GrandParent() and\n" +
                "        forall( $gc: GrandChild( ) from $gp.grandChild " +
                "                GrandChild( this == $gc, name == \"A\" ) );\n" +
                "        $count : count())\n" +
                "    then\n" +
                "        System.out.println(\"exec \" + $count);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        GrandParent grandParent = new GrandParent();
        GrandChild grandChild = new GrandChild();
        grandChild.setName("A");
        grandParent.setGrandChild( Collections.singletonList( grandChild ));

        ksession.insert(grandParent);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class GrandChild {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class GrandParent {
        private List<GrandChild> grandChild;

        public List<GrandChild> getGrandChild() {
            return grandChild;
        }

        public void setGrandChild(List<GrandChild> grandChild) {
            this.grandChild = grandChild;
        }
    }

    @Test
    public void testAccumulateSubnetworkEval() {
        // DROOLS-6228
        String str =
                "import java.time.Duration;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global " + AtomicReference.class.getCanonicalName() + " holder;\n" +
                "rule \"R1\"\n" +
                "    when\n" +
                "        $g : String()\n" +
                "        accumulate(\n" +
                "            ($p : Person( $age: age > 30) and eval($g.equals(\"go\"))),\n" +
                "            $sum : sum($age)\n" +
                "        )\n" +
                "    then\n" +
                "        holder.set((int)holder.get() + $sum);\n" +
                "end";

        KieSession ksession = getKieSession( str );
        AtomicReference<Integer> holder = new AtomicReference<>(0);
        ksession.setGlobal("holder", holder);

        FactHandle s = ksession.insert("stop");
        FactHandle fh1 = ksession.insert( new Person("name1", 40));
        FactHandle fh2 = ksession.insert( new Person("name2", 40));
        FactHandle fh3 = ksession.insert( new Person("name3", 25));

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat((int) holder.get()).isEqualTo(0);
    }

    @Test
    public void testInnerClassInAccumulatingFunction() {
        // DROOLS-6238
        String str =
                "import java.util.*;\n" +
                        "import " + GroupByAccPersonSet.class.getCanonicalName() + ";\n" +
                        "import " + GroupByAccSetSize.class.getCanonicalName() + ";\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  $sets : List( size > 0 ) from accumulate (\n" +
                        "            Person($p: this),\n" +
                        "                init( GroupByAccPersonSet acc = new GroupByAccPersonSet(); ),\n" +
                        "                action( acc.action( $p ); ),\n" +
                        "                reverse( acc.reverse( $p ); ),\n" +
                        "                result( acc.result() )\n" +
                        "         )\n" +
                        "  $sizes : List( size > 0 ) from accumulate (\n" +
                        "            Object($s: this) from $sets,\n" +
                        "                init( GroupByAccSetSize acc2 = new GroupByAccSetSize(); ),\n" +
                        "                action( acc2.action( (Set) $s ); ),\n" +
                        "                reverse( acc2.reverse( (Set) $s ); ),\n" +
                        "                result( acc2.result() )\n" +
                        "         )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class GroupByAccPersonSet {
        private final List<Person> nonUnique  = new ArrayList<>(0);

        public GroupByAccPersonSet() {

        }

        public void action(Person person) {
            nonUnique.add(person);
        }

        public void reverse(Person person) {
            nonUnique.remove(person);
        }

        public List<Set<Person>> result() {
            return Collections.singletonList(new HashSet<>(nonUnique));
        }

    }

    public static class GroupByAccSetSize {
        private final List<Integer> sizes = new ArrayList<>(0);

        public GroupByAccSetSize() {

        }

        public void action(Set<Person> set) {
            sizes.add(set.size());
        }

        public void reverse(Set<Person> set) {
            sizes.add(set.size());
        }

        public List<Integer> result() {
            Integer max = sizes.stream()
                    .mapToInt(i -> i)
                    .max()
                    .orElse(0);
            return Collections.singletonList(max);
        }
    }

    @Test
    public void testAccumulateWithSameBindingVariable() {
        // DROOLS-6102
        String str =
                "import java.util.*;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;" +
                "rule R when\n" +
                "   accumulate ( $p : Person( age < 40 ), $tot1 : count( $p ) ) \n" +
                "   accumulate ( $p : Integer( this > 40 ), $tot2 : count( $p ) ) \n" +
                "then\n" +
                "  list.add( $tot1.intValue() ); \n" +
                "  list.add( $tot2.intValue() ); \n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(47);
        ksession.insert(42);
        ksession.insert(new Person("Edson", 38));
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(list.size()).isEqualTo(2);
        assertThat((int) list.get(0)).isEqualTo(1);
        assertThat((int) list.get(1)).isEqualTo(2);
    }

    @Test
    public void testAccumulateWithMaxCalendarNullDate() {
        //DROOLS-4990
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                        "rule AccumulateMaxDate\n" +
                        "  dialect \"java\"\n" +
                        "  when\n" +
                        "  $max1 : Number() from accumulate(\n" +
                        "    StockTick(isSetDueDate == true, $time : dueDate);\n" +
                        "    max($time.getTime().getTime()))\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        StockTick st = new StockTick("RHT");
        ksession.insert(st);
        StockTick st2 = new StockTick("IBM");
        st2.setDueDate(new GregorianCalendar(2020, Calendar.FEBRUARY, 4));
        ksession.insert(st2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testSubnetworkTuple() {
        final String drl =
                "import java.math.*; " +
                        "import " + InputDataTypes.class.getCanonicalName() + "; " +
                        "import " + ControlType.class.getCanonicalName() + "; " +
                        "global java.util.List result; \n" +
                        "rule \"AccumulateMinDate\" " +
                        "	when " +
                        "		$min : Number() from accumulate ( ControlType( booleanValue == false ) and InputDataTypes($dueDate : dueDate );" +
                        "                                           min($dueDate.getTime().getTime())" +
                        "                                         ) " +
                        " " +
                        "	then " +
                        "		result.add($min); " +
                        "end";

        final KieSession ksession = getKieSession(drl);

        List<Number> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ControlType t1 = new ControlType(false);
        ksession.insert(t1);

        Calendar year2019 = calendarFromString("2019-11-06T16:00:00.00-07:00");
        InputDataTypes i1 = new InputDataTypes(year2019);
        ksession.insert(i1);

        InputDataTypes i2 = new InputDataTypes(calendarFromString("2020-11-06T16:00:00.00-07:00"));
        ksession.insert(i2);

        try {
            assertThat(ksession.fireAllRules()).isEqualTo(1);
            assertThat(result.iterator().next().longValue()).isEqualTo(year2019.getTime().getTime());
        } finally {
            ksession.dispose();
        }
    }

    public static class InputDataTypes {

        private Calendar dueDate;

        public InputDataTypes(Calendar dueDate) {
            this.dueDate = dueDate;
        }

        public Calendar getDueDate() {
            return dueDate;
        }

        public void setDueDate(Calendar date) {
            dueDate = date;
        }
    }

    public static class ControlType {

        private boolean booleanValue;

        public ControlType(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public boolean getBooleanValue() {
            return booleanValue;
        }
    }

    private GregorianCalendar calendarFromString(String inputString) {
        return GregorianCalendar.from( ZonedDateTime.from( DateTimeFormatter.ISO_DATE_TIME.parse(inputString)));
    }

    @Test
    public void testAccumulateCountWithExists() {
//        The following rule uses an accumulate to count all the name Strings for which at least one Person
//        of that name exists. Expected behavior:
//        - A name should be counted exactly once no matter how many Persons with that name exists.
//        - The rule should fire exactly once and there should be a single Result inserted.
        String str = ""
                + "import " + Person.class.getCanonicalName() + ";\n"
                + "import " + Result.class.getCanonicalName() + ";\n"
                + "rule countUsedNames when\n"
                + "  accumulate(\n"
                + "    $name : String()\n"
                + "    and exists Person(name == $name);\n"
                + "    $count : count($name)\n"
                + "  )\n"
                + "then\n"
                + "  insert( new Result($count));\n"
                + "  System.out.println(kcontext.getMatch().getObjects());\n"
                + "end\n";
        KieSession ksession = getKieSession( str );

        ReteDumper.dumpRete(ksession);

        ksession.insert("Andy"); // used 3x => counted 1x
        ksession.insert("Bill"); // used 1x => counted 1x
        ksession.insert("Carl"); // unused => not counted
        ksession.insert(new Person("Andy", 1));
        ksession.insert(new Person("Andy", 2));
        ksession.insert(new Person("Andy", 3));
        ksession.insert(new Person("Bill", 4));
        ksession.insert(new Person("x", 8));
        ksession.insert(new Person("y", 9));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(2L);
    }

    @Test
    public void testAccumulateWithIndirectArgument() {
        String str =
                "global java.util.List resultTotal; \n" +
                        "global java.util.List resultPair; \n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "import " + Pair.class.getCanonicalName() + ";\n" +
                        "rule R " +
                        "    when\n" +
                        "        accumulate(\n" +
                        "            Person($n1 : name)\n" +
                        "            and Person($n2 : name);\n" +
                        "            $pair :  collectList(Pair.create($n1, $n2)), \n" +
                        "            $total : count(Pair.create($n1, $n2))\n" +
                        "        )\n" +
                        "    then\n" +
                        "        resultTotal.add($total);\n" +
                        "        resultPair.add($pair);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        final List<Number> resultTotal = new ArrayList<>();
        ksession.setGlobal("resultTotal", resultTotal);

        final List<List<Pair>> resultPair = new ArrayList<>();
        ksession.setGlobal("resultPair", resultPair);

        Person mario = new Person("Mario", 40);
        ksession.insert(mario);

        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isGreaterThan(0);
        assertThat(resultTotal).contains(1l);

        List<Pair> resultPairItem = resultPair.iterator().next();
        Pair firstPair = resultPairItem.iterator().next();
        assertThat(firstPair.getFirst()).isEqualTo("Mario");
        assertThat(firstPair.getSecond()).isEqualTo("Mario");
    }

    @Test
    public void testVariableWithMethodCallInAccFunc() {
        final String str = "package org.drools.mvel.compiler\n" +
                "import " + ControlFact.class.getCanonicalName() + ";" +
                "import " + Payment.class.getCanonicalName() + ";" +
                "rule r1\n" +
                "when\n" +
                "    Payment( $dueDate : dueDate)\n" +
                "    Number( longValue == $dueDate.getTime().getTime() ) from accumulate (" +
                "      ControlFact( $todaysDate : todaysDate )" +
                "      and\n" +
                "      Payment( $dueDate_acc : dueDate, eval($dueDate_acc.compareTo($todaysDate) <= 0) )\n" +
                "      ;max($dueDate_acc.getTime().getTime())\n" +
                "    )\n" +
                "then\n" +
                "end\n";

        System.out.println(str);

        KieSession ksession = getKieSession(str);

        final Payment payment = new Payment();
        payment.setDueDate(Calendar.getInstance());

        final ControlFact controlFact = new ControlFact();
        controlFact.setTodaysDate(Calendar.getInstance());

        ksession.insert(controlFact);
        ksession.insert(payment);
        final int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testVariableWithMethodCallInAccFuncSimple() {
        final String str = "package org.drools.mvel.compiler\n" +
                "import " + FactA.class.getCanonicalName() + ";" +
                "rule r1\n" +
                "when\n" +
                "    Number(  ) from accumulate (" +
                "      FactA( $valueA : value, $valueA > 0 )\n" +
                "      ;max($valueA.intValue())\n" +
                "    )\n" +
                "then\n" +
                "end\n";

        System.out.println(str);

        KieSession ksession = getKieSession(str);

        final FactA factA = new FactA();
        factA.setValue(1);

        ksession.insert(factA);
        final int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    public static class ControlFact {

        private Calendar todaysDate;

        public Calendar getTodaysDate() {
            return todaysDate;
        }

        public void setTodaysDate(Calendar todaysDate) {
            this.todaysDate = todaysDate;
        }
    }

    public static class Payment {

        private Calendar dueDate;

        public Calendar getDueDate() {
            return dueDate;
        }

        public void setDueDate(Calendar dueDate) {
            this.dueDate = dueDate;
        }
    }

    public static class FactA {

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    @Test
    public void testAccumulateOnTwoPatterns() {
        // DROOLS-5738
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R1 when\n" +
                        "  accumulate (\n" +
                        "       $p1: Person() and $p2: Person( age > $p1.age ), $sum : sum(Person.sumAges($p1, $p2)) " +
                        "         )" +
                        "then\n" +
                        "  insert($sum);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 46 ) );
        ksession.insert( new Person( "Mark", 44 ) );

        ksession.fireAllRules();

        List<Number> results = getObjectsIntoList(ksession, Number.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(90);
    }
  
    @Test
    public void testPositionalAccumulate() {
        // DROOLS-6128
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "declare Person\n" +
                "    name : String \n" +
                "    age : int \n" +
                "end" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert(new Person(\"Mark\", 37));\n" +
                "  insert(new Person(\"Edson\", 35));\n" +
                "  insert(new Person(\"Mario\", 40));\n" +
                "end\n" +
                "rule X when\n" +
                "  accumulate ( Person ( $name, $age; ); \n" +
                "                $sum : sum($age)  \n" +
                "              )                          \n" +
                "then\n" +
                "  insert(new Result($sum));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(112);
    }

    @Test
    public void testAccumulateOnSet() {
        String str =
                "import java.util.*;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global " + AtomicReference.class.getCanonicalName() + " holder;\n" +
                "rule R when\n" +
                "    Set($size: size) from accumulate( $p: Person(); collectSet($p) )" +
                "then\n" +
                "  holder.set($size); \n" +
                "end";

        KieSession ksession = getKieSession(str);
        AtomicReference<Integer> holder = new AtomicReference<>(0);
        ksession.setGlobal("holder", holder);

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();
        assertThat((int) holder.get()).isEqualTo(4);

        ksession.delete( meFH );
        ksession.fireAllRules();
        assertThat((int) holder.get()).isEqualTo(3);

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();
        assertThat((int) holder.get()).isEqualTo(4);
    }

    @Test
    public void testNestedAccumulates() {
        // DROOLS-6202
        String str =
                "import java.util.*;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + InternalMatch.class.getCanonicalName() + ";\n" +
                "global " + AtomicReference.class.getCanonicalName() + " holder;\n" +
                "rule R when\n" +
                "  accumulate( $set: Set() from accumulate( $p: Person(); collectSet($p) ); $max: max($set.size()) )\n" +
                "then\n" +
                "  InternalMatch match = (InternalMatch) drools.getMatch(); \n" +
                "  match.getObjectsDeep(); \n" +
                "  holder.set($max); \n" +
                "end";

        KieSession ksession = getKieSession(str);
        AtomicReference<Integer> holder = new AtomicReference<>(0);
        ksession.setGlobal("holder", holder);

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();
        assertThat((int) holder.get()).isEqualTo(4);

        ksession.delete( meFH );
        ksession.fireAllRules();
        assertThat((int) holder.get()).isEqualTo(3);

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();
        assertThat((int) holder.get()).isEqualTo(4);
    }

    @Test
    public void testIfInInlineAccumulate() {
        // DROOLS-6429
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $avg : Integer() from accumulate (\n" +
                "            Person( $age : age ), init( int count = 0; int sum = 0; ), " +
                "                                        action( if ($age > 18) { count++; sum += $age; } ), " +
                "                                        reverse( if ($age > 18) { count--; sum -= $age; } ), " +
                "                                        result( sum / count )\n" +
                "         )" +
                "then\n" +
                "  insert($avg);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Sofia", 10));
        FactHandle fh_Mark = ksession.insert(new Person("Mark", 37));
        FactHandle fh_Edson = ksession.insert(new Person("Edson", 35));
        FactHandle fh_Mario = ksession.insert(new Person("Mario", 42));
        ksession.fireAllRules();

        List<Integer> results = getObjectsIntoList(ksession, Integer.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(38);

        ksession.delete(fh_Mario);
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, Integer.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results).contains(36);
    }

    @Test
    public void testBindVariableUsedInSubsequentAccumulateString() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List result;\n" +
                     "rule X when\n" +
                     "  accumulate( Person($name : name);\n" +
                     "    $count : count($name),\n" +
                     "    $maxName : max($name);\n" +
                     "    $count > 1\n" +
                     "  )\n" +
                     "  accumulate( Person(name == $maxName, $age : age);\n" +
                     "    $maxAge : max($age)\n" +
                     "  )\n" +
                     "then\n" +
                     "  result.add($maxAge);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("John", 37));
        ksession.insert(new Person("John", 60));
        ksession.insert(new Person("Paul", 35));
        ksession.insert(new Person("Paul", 50));

        ksession.fireAllRules();

        assertThat(result).containsExactly(50);
    }

    @Test
    public void testBindVariableUsedInSubsequentAccumulateBigDecimal() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List result;\n" +
                     "rule X when\n" +
                     "  accumulate( Person($money : money);\n" +
                     "    $count : count($money),\n" +
                     "    $maxMoney : max($money);\n" +
                     "    $count > 1\n" +
                     "  )\n" +
                     "  accumulate( Person(money == $maxMoney, $age : age);\n" +
                     "    $maxAge : max($age)\n" +
                     "  )\n" +
                     "then\n" +
                     "  result.add($maxAge);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("John", 37, new BigDecimal("100.0")));
        ksession.insert(new Person("John", 60, new BigDecimal("100.0")));
        ksession.insert(new Person("Paul", 35, new BigDecimal("200.0")));
        ksession.insert(new Person("Paul", 50, new BigDecimal("200.0")));

        ksession.fireAllRules();

        assertThat(result).containsExactly(50);
    }

    @Test
    public void testCollectAfterAccumulate() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "import " + ArrayList.class.getCanonicalName() + ";\n" +
                     "dialect \"mvel\"\n" +
                     "global java.util.List result;\n" +
                     "rule R when\n" +
                     "  accumulate (\n" +
                     "    Person($age: age),\n" +
                     "    $maxAge: max($age)\n" +
                     "  )\n" +
                     "  $list: ArrayList() from collect (\n" +
                     "    Person(age < $maxAge)\n" +
                     "  )\n" +
                     "then\n" +
                     "  result.addAll($list)\n" +
                     "end";
        try {
            KieSession ksession = getKieSession(str);
            List<Person> result = new ArrayList<>();
            ksession.setGlobal("result", result);

            Person john = new Person("John", 37);
            Person bob = new Person("Bob", 37);
            Person paul = new Person("Paul", 35);
            Person george = new Person("George", 34);
            ksession.insert(john);
            ksession.insert(bob);
            ksession.insert(paul);
            ksession.insert(george);

            ksession.fireAllRules();

            assertThat(result).containsExactlyInAnyOrder(paul, george);
        } catch (Throwable ex) {
            fail("Should not have thrown.", ex);
        }
    }

    @Test
    public void testExistsFromAccumulate() {
        // DROOLS-6959
        String str =
                "import " + Set.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                 "  exists Set(size > 1) from accumulate ( $s: String(), collectSet($s) )\n" +
                 "then\n" +
                 "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("String1");
        ksession.insert("String2");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testAccumulateWithBetaConstraint() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  $i : Integer()" +
                "  accumulate ( $p: Person ( name.length >= $i ); \n" +
                "                $sum : sum($p.getAge())  \n" +
                "              )                          \n" +
                "then\n" +
                "  insert(new Result($sum + \":\" + $i));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(5);
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).containsExactly(new Result("75:5"));
    }

    @Test
    public void testJoinInAccumulate() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  accumulate ( $i : Integer() and $p: Person ( name.length >= $i ); \n" +
                "                $sum : sum($p.getAge())  \n" +
                "              )                          \n" +
                "then\n" +
                "  insert(new Result($sum));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).containsExactly(new Result(0));

        ksession.insert(5);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results).containsExactlyInAnyOrder(new Result(0), new Result(75));
    }

    @Test
    public void testAccumulateSumMultipleParametersExpression() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  accumulate ( $i : Integer() and $p: Person ( name.length >= $i ); \n" +
                "                $sum : sum($p.getAge(), $p.getName())  \n" +
                "              )                          \n" +
                "then\n" +
                "  insert(new Result($sum));\n" +
                "end";

        Results results = createKieBuilder(str).getResults();
        if (testRunType.isExecutableModel()) {
            assertThat(results.getMessages(Message.Level.ERROR).get(0).getText().contains(
                    "Function \"sum\" cannot have more than 1 parameter")).isTrue();
        } else {
            // At this time, this error is thrown with executable model only.
            assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isTrue();
        }
    }

    @Test
    public void testAccumulateSumUnaryExpression() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                "                $sum : sum(-$p.getAge()) \n" +
                "              )                          \n" +
                "then\n" +
                "  insert(new Result($sum));\n" +
                "end";

        KieSession kieSession = getKieSession( str );

        kieSession.insert(new Person("Mark", 37));
        kieSession.insert(new Person("Edson", 35));
        kieSession.insert(new Person("Mario", 40));

        kieSession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(kieSession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(-77);
    }

    @Test
    public void testAccumulateSumBinaryExpWithNestedUnaryExpression() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                "                $sum : sum(-$p.getAge() + $p.getAge()) \n" +
                "              )                          \n" +
                "then\n" +
                "  insert(new Result($sum));\n" +
                "end";

        KieSession kieSession = getKieSession( str );

        kieSession.insert(new Person("Mark", 37));
        kieSession.insert(new Person("Edson", 35));
        kieSession.insert(new Person("Mario", 40));

        kieSession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(kieSession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(0);
    }
}
