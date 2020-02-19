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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NodeSharingTest extends BaseModelTest {

    public NodeSharingTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testShareAlpha() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : java.util.Set()\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "  $p2 : Person(name != \"Edson\", age > $p1.age)\n" +
                "  $p3 : Person(name != \"Edson\", age > $p1.age, this != $p2)\n" +
                "then\n" +
                "  $r.add($p2);\n" +
                "  $r.add($p3);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Set result = new HashSet<>();
        ksession.insert( result );

        Person mark = new Person( "Mark", 37 );
        Person edson = new Person( "Edson", 35 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( edson );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertTrue( result.contains( mark ) );
        assertTrue( result.contains( mario ) );

        // Alpha node "name != Edson" should be shared between 3rd and 4th pattern.
        // therefore alpha nodes should be a total of 2: name == Edson, name != Edson
        assertEquals( 2, ReteDumper.collectNodes( ksession ).stream().filter( AlphaNode.class::isInstance ).count() );
    }

    @Test
    public void testShareAlphaInDifferentRules() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        assertEquals( 1, ReteDumper.collectNodes( ksession ).stream().filter( AlphaNode.class::isInstance ).count() );
    }

    @Test
    public void testShareAlphaInDifferentPackages() {
        String str1 =
                "package org.drools.a\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end\n";
        String str2 =
                "package org.drools.b\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R2 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str1, str2 );

        assertEquals( 1, ReteDumper.collectNodes( ksession ).stream().filter( AlphaNode.class::isInstance ).count() );
    }

    @Test
    public void testShareBetaWithConstraintReordering() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $s : String()\n" +
                "  $p : Person(name != $s, age == $s.length)\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $s : String()\n" +
                "  $p : Person(name != $s, age == $s.length)\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        assertEquals( 1, ReteDumper.collectNodes( ksession ).stream().filter( BetaNode.class::isInstance ).count() );

        EntryPointNode epn = (( InternalKnowledgeBase ) ksession.getKieBase()).getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( Person.class ) );
        assertEquals( 1, otn.getSinks().length );
    }

    @Test
    public void testTrimmedConstraint() {

        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "   Person( name == \"Mark\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $p: Person( name==\"Mark\" )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        assertEquals( 1, ReteDumper.collectNodes( ksession ).stream().filter( AlphaNode.class::isInstance ).count() );
    }

    @Test
    public void testOrWithTrimmedConstraint() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R1 when\n" +
                "   Person( $name: name == \"Mark\", $age: age ) or\n" +
                "   Person( $name: name == \"Mario\", $age : age )\n" +
                "then\n" +
                "  list.add( $name + \" is \" + $age);\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $p: Person( name==\"Mark\", $age: age ) or\n" +
                "   $p: Person( name==\"Mario\", $age : age )\n" +
                "then\n" +
                "  list.add( $p + \" has \" + $age + \" years\");\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        assertEquals( 2, ReteDumper.collectNodes( ksession ).stream().filter( AlphaNode.class::isInstance ).count() );

        List<String> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        assertEquals(4, results.size());
        assertTrue(results.contains("Mark is 37"));
        assertTrue(results.contains("Mark has 37 years"));
        assertTrue(results.contains("Mario is 40"));
        assertTrue(results.contains("Mario has 40 years"));
    }
}
