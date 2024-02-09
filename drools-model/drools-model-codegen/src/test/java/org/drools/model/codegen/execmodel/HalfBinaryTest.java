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

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class HalfBinaryTest extends BaseModelTest {

    public HalfBinaryTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testHalfBinary() {
        final String drl1 =
                "rule R1 when\n" +
                "    Integer(this > 2 && < 5)\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( 3 );
        ksession.insert( 4 );
        ksession.insert( 6 );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testHalfBinaryWithParenthesis() {
        // DROOLS-6006
        final String drl1 =
                "rule R1 when\n" +
                "    Integer(intValue (> 2 && < 5))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( 3 );
        ksession.insert( 4 );
        ksession.insert( 6 );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testHalfBinaryOrWithParenthesis() {
        // DROOLS-6006
        final String drl1 =
                "rule R1 when\n" +
                "    Integer(intValue (< 2 || > 5))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( 3 );
        ksession.insert( 4 );
        ksession.insert( 6 );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testComplexHalfBinary() {
        // DROOLS-6006
        final String drl1 =
                "rule R1 when\n" +
                "    Integer(intValue ((> 2 && < 4) || (> 5 && < 7)) )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( 3 );
        ksession.insert( 4 );
        ksession.insert( 6 );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testHalfBinaryOnComparable() {
        // DROOLS-6421
        final String drl1 =
                "rule R1 when\n" +
                "    String(this (> \"C\" && < \"K\"))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( "B" );
        ksession.insert( "D" );
        ksession.insert( "H" );
        ksession.insert( "S" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testHalfBinaryOrOnComparable() {
        // DROOLS-6421
        final String drl1 =
                "rule R1 when\n" +
                "    String(this (< \"C\" || > \"K\"))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( "B" );
        ksession.insert( "D" );
        ksession.insert( "H" );
        ksession.insert( "S" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testComplexHalfBinaryOnComparable() {
        // DROOLS-6421
        final String drl1 =
                "rule R1 when\n" +
                "    String(this ((> \"C\" && < \"K\") || (> \"P\" && < \"R\")))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( "B" );
        ksession.insert( "D" );
        ksession.insert( "Q" );
        ksession.insert( "S" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testComplexHalfBinaryOnComparableField() {
        // DROOLS-6421
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    Person(name ((> \"C\" && < \"K\") || (> \"P\" && < \"R\")))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( new Person("B") );
        ksession.insert( new Person("D") );
        ksession.insert( new Person("Q") );
        ksession.insert( new Person("S") );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testComplexHalfBinaryOnComparableInternalField() {
        // DROOLS-6421
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    Person(address.city ((> \"C\" && < \"K\") || (> \"P\" && < \"R\")))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        Person b = new Person("B");
        b.setAddress(new Address("B"));
        Person d = new Person("D");
        d.setAddress(new Address("D"));
        Person q = new Person("Q");
        q.setAddress(new Address("Q"));
        Person s = new Person("S");
        s.setAddress(new Address("s"));

        ksession.insert( b );
        ksession.insert( d );
        ksession.insert( q );
        ksession.insert( s );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testComplexHalfBinaryOnComparableInternalNullSafeField() {
        // DROOLS-6421
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    Person(address!.city ((> \"C\" && < \"K\") || (> \"P\" && < \"R\")))\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        Person a = new Person("A");
        a.setAddress(new Address(null));
        Person b = new Person("B");
        b.setAddress(new Address("B"));
        Person d = new Person("D");
        d.setAddress(new Address("D"));
        Person q = new Person("Q");
        q.setAddress(new Address("Q"));
        Person s = new Person("S");
        s.setAddress(new Address("s"));

        ksession.insert( a );
        ksession.insert( b );
        ksession.insert( d );
        ksession.insert( q );
        ksession.insert( s );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testHalfBinaryOrAndAmpersand() {
        final String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "global java.util.List result;\n" +
                           "rule R1 when\n" +
                           "    $p : Person(age < 15 || > 20 && < 30)\n" +
                           "then\n" +
                           "    result.add($p.getName());\n" +
                           "end\n";

        KieSession ksession = getKieSession(drl);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("A", 12));
        ksession.insert(new Person("B", 18));
        ksession.insert(new Person("C", 25));
        ksession.insert(new Person("D", 40));

        ksession.fireAllRules();
        assertThat(result).containsExactlyInAnyOrder("A", "C");
    }

    @Test
    public void testNestedHalfBinaryOrAndAmpersand() {
        final String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "global java.util.List result;\n" +
                           "rule R1 when\n" +
                           "    $p : Person(name != \"X\" && (age < 15 || > 20 && < 30))\n" +
                           "then\n" +
                           "    result.add($p.getName());\n" +
                           "end\n";

        KieSession ksession = getKieSession(drl);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("A", 12));
        ksession.insert(new Person("B", 18));
        ksession.insert(new Person("C", 25));
        ksession.insert(new Person("D", 40));

        ksession.fireAllRules();
        assertThat(result).containsExactlyInAnyOrder("A", "C");
    }
}
