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
package org.drools.mvel.compiler.oopath.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class OOPathOnGraphTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OOPathOnGraphTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testOOPathOnGraph() {
        String drl =
                "import org.drools.mvel.compiler.oopath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $a : /outVs/outVs/it#Person[ age > 40 ] )\n" +
                "then\n" +
                "  list.add( $a.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Mario")).isTrue();
        list.clear();

        Vertex<?> book = library.getOutVs().get(0);
        Vertex<Person> alan = new Vertex<Person>( new Person( "Alan", 53 ) );
        book.connectTo( alan );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Alan")).isTrue();
    }

    @Test
    public void testOOPathOnGraphWithReactiveContentModification() {
        String drl =
                "import org.drools.mvel.compiler.oopath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $a : /outVs/outVs/it#Person[ age > 25 ] )\n" +
                "then\n" +
                "  list.add( $a.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Mario")).isTrue();
        list.clear();

        Person raoul = (Person)library.getOutVs().get(0).getOutVs().get(0).getIt();
        assertThat(raoul.getName()).isEqualTo("Raoul");
        raoul.setAge( raoul.getAge() + 1 );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Raoul")).isTrue();
    }

    @Test
    public void testOOPathOnGraphWithReactiveContentModificationInSubgraph() {
        String drl =
                "import org.drools.mvel.compiler.oopath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $v : /outVs/outVs[ /it#Person[ age > 25 ] ] )\n" +
                "then\n" +
                "  list.add( ((Person)$v.getIt()).getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Mario")).isTrue();
        list.clear();

        Person raoul = (Person)library.getOutVs().get(0).getOutVs().get(0).getIt();
        assertThat(raoul.getName()).isEqualTo("Raoul");
        raoul.setAge( raoul.getAge() + 1 );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Raoul")).isTrue();
    }

    @Test
    public void testOOPathOnGraphWithNonReactiveContentModification() {
        String drl =
                "import org.drools.mvel.compiler.oopath.graph.*;\n" +
                "import " + Library.class.getCanonicalName() + ";\n" +
                "import " + Book.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Vertex( it instanceof Library, $v : /outVs/outVs[ it#Person.age > 25 ] )\n" +
                "then\n" +
                "  list.add( ((Person)$v.getIt()).getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Vertex<Library> library = getGraph();
        ksession.insert( library );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains("Mario")).isTrue();
        list.clear();

        Person raoul = (Person)library.getOutVs().get(0).getOutVs().get(0).getIt();
        assertThat(raoul.getName()).isEqualTo("Raoul");
        raoul.setAge( raoul.getAge() + 1 );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);
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
