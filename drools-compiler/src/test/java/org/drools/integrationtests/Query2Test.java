package org.drools.integrationtests;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Damon
 * 
 */
public class Query2Test {

    private KnowledgeBase knowledgeBase;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        String text = "";
        text += "package org.drools.integrationtests\n";
        text += "import org.drools.integrationtests.Query2Test.Bar\n";
        text += "import org.drools.integrationtests.Query2Test.Foo\n";
        text += "import org.drools.integrationtests.Query2Test.Foo2\n";
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
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource( text.getBytes() ),
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
