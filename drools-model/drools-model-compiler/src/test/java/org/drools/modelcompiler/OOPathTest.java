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
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.InternationalAddress;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class OOPathTest extends BaseModelTest {

    public OOPathTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testOOPath() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $man: Man( /wife/children[age > 10] )\n" +
                "then\n" +
                "  list.add( $man.getName() );\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        final Man carl = new Man( "Carl", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        ksession.insert( bob );
        ksession.insert( carl );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testReactiveOOPath() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("doll");
    }


    @Test
    public void testBackReferenceConstraint() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children/toys[ name.length == ../name.length ] )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Carl", 12 );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        debbie.addToy( new Toy( "guitar" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("ball", "guitar");
    }

    @Test
    public void testSimpleOOPathCast1() {
        final String str = "import org.drools.modelcompiler.domain.*;\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R when\n" +
                           "  $man : Man( $italy: /address#InternationalAddress[ state == \"Italy\" ] )\n" +
                           "then\n" +
                           "  list.add( $man.getName() );\n" +
                           "end\n";

        KieSession ksession = getKieSession(str);

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        bob.setAddress(new InternationalAddress("Via Verdi", "Italy"));
        ksession.insert(bob);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testSimpleOOPathCast2() {
        final String str = "import org.drools.modelcompiler.domain.*;\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R when\n" +
                           "  Man( $name : name, $italy: /address#InternationalAddress[ state == \"Italy\" ] )\n" +
                           "then\n" +
                           "  list.add( $name );\n" +
                           "end\n";

        KieSession ksession = getKieSession(str);

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        bob.setAddress(new InternationalAddress("Via Verdi", "Italy"));
        ksession.insert(bob);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testSimpleOOPathCast3() {
        final String str = "import org.drools.modelcompiler.domain.*;\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R when\n" +
                           "  Man( $italy: /address#InternationalAddress[ state == \"Italy\" ], $name : name != null )\n" +
                           "then\n" +
                           "  list.add( $name );\n" +
                           "end\n";

        KieSession ksession = getKieSession(str);

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        bob.setAddress(new InternationalAddress("Via Verdi", "Italy"));
        ksession.insert(bob);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Bob");
    }
}
