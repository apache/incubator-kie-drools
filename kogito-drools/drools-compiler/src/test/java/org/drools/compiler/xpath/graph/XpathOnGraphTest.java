/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.xpath.graph;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.reteoo.ReteDumper;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XpathOnGraphTest {

    @Test
    public void testXpathOnGraph() {
        String drl =
                "import org.drools.compiler.xpath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $a : /outVs/outVs/it{ #Person, age > 40 } )\n" +
                "then\n" +
                "  list.add( $a.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "Mario" ) );
        list.clear();

        Vertex<?> book = library.getOutVs().get(0);
        Vertex<Person> alan = new Vertex<Person>( new Person( "Alan", 53 ) );
        book.connectTo( alan );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( "Alan" ) );
    }

    @Test
    public void testXpathOnGraphWithReactiveContentModification() {
        String drl =
                "import org.drools.compiler.xpath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $a : /outVs/outVs/it{ #Person, age > 25 } )\n" +
                "then\n" +
                "  list.add( $a.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "Mario" ) );
        list.clear();

        Person raoul = (Person)library.getOutVs().get(0).getOutVs().get(0).getIt();
        assertEquals( "Raoul", raoul.getName() );
        raoul.setAge( raoul.getAge() + 1 );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( "Raoul" ) );
    }

    @Test
    public void testXpathOnGraphWithReactiveContentModificationInSubgraph() {
        String drl =
                "import org.drools.compiler.xpath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $v : /outVs/outVs{ /it{ #Person, age > 25 } } )\n" +
                "then\n" +
                "  list.add( ((Person)$v.getIt()).getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        ReteDumper.dumpRete( ksession );

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "Mario" ) );
        list.clear();

        Person raoul = (Person)library.getOutVs().get(0).getOutVs().get(0).getIt();
        assertEquals( "Raoul", raoul.getName() );
        raoul.setAge( raoul.getAge() + 1 );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( "Raoul" ) );
    }

    @Test
    public void testXpathOnGraphWithNonReactiveContentModification() {
        String drl =
                "import org.drools.compiler.xpath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $v : /outVs/outVs{ it#Person.age > 25 } )\n" +
                "then\n" +
                "  list.add( ((Person)$v.getIt()).getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "Mario" ) );
        list.clear();

        Person raoul = (Person)library.getOutVs().get(0).getOutVs().get(0).getIt();
        assertEquals( "Raoul", raoul.getName() );
        raoul.setAge( raoul.getAge() + 1 );

        ksession.fireAllRules();
        assertEquals( 0, list.size() );
    }

    private Vertex<Library> getGraph() {
        Vertex<Library> library = new Vertex<Library>( new Library() );

        Vertex<Book> java8inAction = new Vertex<Book>( new Book( "Java 8 in Action" ) );
        library.connectTo( java8inAction );

        Vertex<Person> raoul = new Vertex<Person>( new Person( "Raoul", 25 ) );
        java8inAction.connectTo( raoul );

        Vertex<Person> mario = new Vertex<Person>( new Person( "Mario", 41 ) );
        java8inAction.connectTo( mario );

        return library;
    }

    public static class Library {
    }

    public static class Book {
        private final String title;

        public Book( String title ) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class Person extends AbstractReactiveObject {
        private final String name;
        private int age;

        public Person( String name, int age ) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public void setAge( int age ) {
            this.age = age;
            notifyModification();
        }
    }
}
