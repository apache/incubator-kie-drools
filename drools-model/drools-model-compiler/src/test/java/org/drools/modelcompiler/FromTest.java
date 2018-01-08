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
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FromTest extends BaseModelTest {

    public FromTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testFromGlobal() throws Exception {
        String str = "global java.util.List list         \n" +
                     "rule R when                        \n" +
                     "  $o : String(length > 3) from list\n" +
                     "then                               \n" +
                     "  insert($o);                      \n" +
                     "end                                ";

        KieSession ksession = getKieSession(str);

        List<String> strings = Arrays.asList("a", "Hello World!", "xyz");

        ksession.setGlobal("list", strings);

        assertEquals( 1, ksession.fireAllRules() );

        List<String> results = getObjectsIntoList(ksession, String.class);
        assertFalse(results.contains("a"));
        assertTrue(results.contains("Hello World!"));
        assertFalse(results.contains("xyz"));
    }

    @Test
    public void testFromVariable() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " Man( $children : wife.children )\n" +
                " $child: Child( age > 10 ) from $children\n" +
                "then\n" +
                "  list.add( $child.getName() );\n" +
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

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Charles");
    }

    @Test
    public void testFromExpression() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " Man( $wife : wife )\n" +
                " $child: Child( age > 10 ) from $wife.children\n" +
                "then\n" +
                "  list.add( $child.getName() );\n" +
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

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Charles");
    }
}
