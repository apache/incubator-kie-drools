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
import org.assertj.core.api.Assertions;
import org.drools.compiler.oopath.model.BabyBoy;
import org.drools.compiler.oopath.model.BabyGirl;
import org.drools.compiler.oopath.model.Man;
import org.drools.compiler.oopath.model.Toy;
import org.drools.compiler.oopath.model.Woman;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

public class OOPathCastTest {

    @Test
    public void testInlineCast() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{ #BabyGirl }/toys )\n" +
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

        final BabyBoy charlie = new BabyBoy( "Charles", 12 );
        final BabyGirl debbie = new BabyGirl( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testInvalidCast() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{ #Man }/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        final Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
    }

    @Test
    public void testInlineCastWithConstraint() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( name == \"Bob\", $name: /wife/children{ #BabyGirl, favoriteDollName.startsWith(\"A\") }.name )\n" +
                        "then\n" +
                        "  list.add( $name );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final BabyBoy charlie = new BabyBoy( "Charles", 12 );
        final BabyGirl debbie = new BabyGirl( "Debbie", 8, "Anna" );
        final BabyGirl elisabeth = new BabyGirl( "Elisabeth", 5, "Zoe" );
        final BabyGirl farrah = new BabyGirl( "Farrah", 3, "Agatha" );
        alice.addChild( charlie );
        alice.addChild( debbie );
        alice.addChild( elisabeth );
        alice.addChild( farrah );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Debbie", "Farrah");
    }
}
