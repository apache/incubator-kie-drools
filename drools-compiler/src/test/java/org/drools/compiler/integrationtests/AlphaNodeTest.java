/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class AlphaNodeTest extends CommonTestMethodBase {

    @Test
    public void testAlpha() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testSharedAlpha() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testBeta() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mario" );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testSharedAlphaWithBeta() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mario" );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testAlphaModify() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\")}" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testAlphaDelete() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  delete($p);" +
                "end\n"+
                "rule R2 when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  delete($p);" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testAlphaModifyDelete() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\")}" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mark\")\n" +
                "then\n" +
                "  delete($p);" +
                "end\n" +
                "rule R3 when\n" +
                "  not( Person() )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertEquals( 3, ksession.fireAllRules() );
    }

    @Test
    public void testBetaModifyWithAlpha() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\") }" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mark\")\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mario" );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testAlphaModifyWithBeta() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\") }" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mark\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mark" );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void test3Alpha() {
        String str =
                "import org.drools.compiler.Person\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setAge(2) }" +
                "  modify($p) { setAge(2) }" +
                "end\n" +
                "rule R3 when\n" +
                "  $p : Person(age > 1)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario", 0 ) );
        assertEquals( 2, ksession.fireAllRules() );
    }
}
