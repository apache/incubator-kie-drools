/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.oopath;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

public class OOPathMultilevelTest {

    @Test
    public void testClassTwoLevelPath() {
        final String drl =
                "import org.drools.compiler.oopath.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 3, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
        assertTrue( list.contains( "doll" ) );
    }

    @Test
    public void testClassThreeLevelPath() {
        final String drl =
                "import org.drools.compiler.oopath.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toyName: /wife/children/toys/name )\n" +
                        "then\n" +
                        "  list.add( $toyName );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        alice.addChild( charlie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
    }

    @Test
    public void testClassTwoLevelPathWithAlphaConstraint() {
        final String drl =
                "import org.drools.compiler.oopath.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{age > 10, name.length > 5}/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        testScenarioTwoLevelPathWithConstraint(drl);
    }

    @Test
    public void testClassTwoLevelPathWithBetaConstraint() {
        final String drl =
                "import org.drools.compiler.oopath.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $i : Integer()\n" +
                        "  Man( $toy: /wife/children{age > 10, name.length > $i}/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";
        testScenarioTwoLevelPathWithConstraint(drl);
    }

    private void testScenarioTwoLevelPathWithConstraint(final String drl) {
        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charlie", 12 );
        alice.addChild( charlie );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( debbie );
        final Child eric = new Child( "Eric", 15 );
        alice.addChild( eric );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        eric.addToy( new Toy( "bike" ) );

        ksession.insert( 5 );
        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
    }
}
