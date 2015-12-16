/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

public class QueryTest3 {

    private KnowledgeBase knowledgeBase;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        String text = "";
        text += "package org.drools.integrationtests\n";
        text += "import " + QueryTest3.Bar.class.getCanonicalName() + "\n";
        text += "import " + QueryTest3.Foo.class.getCanonicalName() + "\n";
        text += "import " + QueryTest3.Foo2.class.getCanonicalName() + "\n";
        text += "query \"testDifferent\"\n";
        text += "    foo : Foo();\n";
        text += "    bar : Bar(id == foo.id)\n";
        text += "end\n";
        text += "query \"testSame\"\n";
        text += "    foo : Foo();\n";
        text += "    foo2 : Foo(id == foo.id);\n";
        text += "end\n";
        text += "query \"testExtends\"\n";
        text += "    foo : Foo();\n";
        text += "    foo2 : Foo2(id == foo.id);\n";
        text += "end\n";

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource(text.getBytes()),
                              ResourceType.DRL );
        assertFalse( knowledgeBuilder.hasErrors() );
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
    }

    private void doIt(Object o1,
                      Object o2,
                      String query,
                      int expected,
                      boolean doUpdate,
                      boolean doRetract) {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            knowledgeSession.insert( o1 );
            FactHandle handle2 = knowledgeSession.insert( o2 );
            if ( doUpdate ) {
                knowledgeSession.update( handle2,
                                         o2 );
            } else if ( doRetract ) {
                knowledgeSession.retract( handle2 );
                handle2 = knowledgeSession.insert( o2 );
            }
            QueryResults queryResults = knowledgeSession.getQueryResults( query );
            assertEquals( expected,
                          queryResults.size() );
        } finally {
            knowledgeSession.dispose();
        }
    }

    @Test
    public void testDifferent() {
        Foo foo = new Foo();
        foo.setId( "x" );
        Bar bar = new Bar();
        bar.setId( "x" );
        doIt( foo,
              bar,
              "testDifferent",
              1,
              false,
              false );
    }

    @Test
    public void testDifferentWithUpdate() {
        Foo foo = new Foo();
        foo.setId( "x" );
        Bar bar = new Bar();
        bar.setId( "x" );
        doIt( foo,
              bar,
              "testDifferent",
              1,
              true,
              false );
    }

    @Test
    public void testSame() {
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo foo2 = new Foo();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testSame",
              4,
              false,
              false );
    }

    @Test
    public void testSameWithUpdate() {
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo foo2 = new Foo();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testSame",
              4,
              true,
              false );
    }

    @Test
    public void testExtends() {
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo2 foo2 = new Foo2();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testExtends",
              2,
              false,
              false );
    }

    @Test
    public void testExtendsWithUpdate() {
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo2 foo2 = new Foo2();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testExtends",
              2,
              true,
              false );
    }

    @Test
    public void testExtendsWithRetract() {
        Foo foo = new Foo();
        foo.setId( "x" );
        Foo2 foo2 = new Foo2();
        foo2.setId( "x" );
        doIt( foo,
              foo2,
              "testExtends",
              2,
              false,
              true );
    }

    public static class Bar {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public static class Foo {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public static class Foo2 extends Foo {

    }
}
