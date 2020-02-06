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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Pet;
import org.drools.modelcompiler.domain.PetPerson;
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

    public static Integer getLength(String ignoredParameter, String s, Integer offset) {
        return s.length() + offset;
    }

    @Test
    public void testFromExternalFunction() {
        final String str =
                "import " + FromTest.class.getCanonicalName() + ";\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $s : String()\n" +
                        "  $i : Integer( this > 10 ) from FromTest.getLength(\"ignoredArgument\", $s, 0)\n" +
                        "then\n" +
                        "  list.add( \"received long message: \" + $s);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert("Hello World!");
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("received long message: Hello World!");
    }

    @Test
    public void testFromExternalFunctionMultipleBindingArguments() {
        final String str =
                "import " + FromTest.class.getCanonicalName() + ";\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $s : String()\n" +
                        "  $n : Integer()\n" +
                        "  $i : Integer( this >= 10 ) from FromTest.getLength(\"ignoredArgument\", $s, $n)\n" +
                        "then\n" +
                        "  list.add( \"received long message: \" + $s);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert("Hello!");
        ksession.insert(4);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("received long message: Hello!");
    }

    @Test
    public void testFromConstant() {
        String str =
                "package org.drools.compiler.test  \n" +
                "global java.util.List list\n" +
                "rule R\n" +
                "when\n" +
                "    $s : String() from \"test\"\n" +
                "then\n" +
                "   list.add( $s );\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "test", list.get(0) );
    }

    @Test
    public void testFromConstructor() {
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R\n" +
                "when\n" +
                "    $s : String()\n" +
                "    $i : Integer()\n" +
                "    $p : Person() from new Person($s, $i)\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert( "Mario" );
        ksession.insert( 44 );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( new Person("Mario", 44), list.get(0) );
    }

    @Test
    public void testFromMapValues() {
        // DROOLS-3661
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + PetPerson.class.getCanonicalName() + "\n" +
                "import " + Pet.class.getCanonicalName() + "\n" +
                "rule R\n" +
                "when\n" +
                "    $p : PetPerson ( )\n" +
                "    $pet : Pet ( type == Pet.PetType.dog ) from $p.getPets().values()\n\n" +
                "then\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        PetPerson petPerson = new PetPerson( "me" );
        Map<String, Pet> petMap = new HashMap<>();
        petMap.put("Dog", new Pet( Pet.PetType.dog ));
        petMap.put("Cat", new Pet( Pet.PetType.cat ));
        petPerson.setPets( petMap );

        ksession.insert( petPerson );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testGlobalInFromExpression() {
        // DROOLS-4999
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + PetPerson.class.getCanonicalName() + "\n" +
                "import " + Pet.class.getCanonicalName() + "\n" +
                "global String petName;\n" +
                "rule R\n" +
                "when\n" +
                "    $p : PetPerson ( )\n" +
                "    $pet : Pet ( type == Pet.PetType.dog ) from $p.getPet(petName)\n" +
                "then\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal( "petName", "Dog" );

        PetPerson petPerson = new PetPerson( "me" );
        Map<String, Pet> petMap = new HashMap<>();
        petMap.put("Dog", new Pet( Pet.PetType.dog ));
        petMap.put("Cat", new Pet( Pet.PetType.cat ));
        petPerson.setPets( petMap );

        ksession.insert( petPerson );
        assertEquals( 1, ksession.fireAllRules() );
    }

}
