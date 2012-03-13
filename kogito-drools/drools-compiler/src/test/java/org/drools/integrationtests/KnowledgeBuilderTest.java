package org.drools.integrationtests;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KnowledgeBuilderTest {

    @Test
    public void testCompositeKnowledgeBuilder() throws Exception {
        String rule = "package org.drools.test\n" +
                "rule R1 when\n" +
                "   $fieldA : FactA( $fieldB : fieldB )\n" +
                "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                "then\n" +
                "end";

        String declarationA = "package org.drools.test\n" +
                "declare FactA\n" +
                "    fieldB: FactB\n" +
                "end\n";

        String declarationB = "package org.drools.test\n" +
                "declare FactB\n" +
                "    fieldA: FactA\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource(declarationA.getBytes()), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), ResourceType.DRL );
        assertTrue(kbuilder.hasErrors());

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(declarationA.getBytes()), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.batch()
                .add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL)
                .add(ResourceFactory.newByteArrayResource(declarationA.getBytes()), ResourceType.DRL)
                .add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), ResourceType.DRL)
                .build();

        if ( kbuilder2.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder2.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType aType = kbase.getFactType( "org.drools.test", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.test", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        bType.set( b, "fieldA", a );
        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testDifferentPackages() throws Exception {
        String rule = "package org.drools.test.rule\n" +
                "import org.drools.testA.FactA\n" +
                "import org.drools.testB.FactB\n" +
                "rule R1 when\n" +
                "   $fieldA : FactA( $fieldB : fieldB, bigint == 1 )\n" +
                "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                "then\n" +
                "   list.add(\"OK\");"+
                "end";

        String declarationA = "package org.drools.testA\n" +
                "import org.drools.testB.FactB\n" +
                "import java.math.BigInteger\n" +
                "declare FactA\n" +
                "    fieldB: FactB\n" +
                "    bigint: BigInteger\n" +
                "end\n";

        String declarationB = "package org.drools.testB\n" +
                "import org.drools.testA.FactA\n" +
                "global java.util.List list\n" +
                "declare FactB\n" +
                "    fieldA: FactA\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.batch()
                .type(ResourceType.DRL)
                .add(ResourceFactory.newByteArrayResource(rule.getBytes()))
                .add(ResourceFactory.newByteArrayResource(declarationA.getBytes()))
                .add(ResourceFactory.newByteArrayResource(declarationB.getBytes()))
                .build();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        String declarationC = "package org.drools.testA\n" +
                "declare FactC\n" +
                "    field : UnknownClass\n" +
                "end\n";

        kbuilder.add(ResourceFactory.newByteArrayResource(declarationC.getBytes()), ResourceType.DRL);

        assertTrue(kbuilder.hasErrors());
        kbuilder.undo();
        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactType aType = kbase.getFactType( "org.drools.testA", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.testB", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        aType.set( a, "bigint", new BigInteger("1"));
        bType.set( b, "fieldA", a );
        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
        assertEquals("OK", list.get(0));
    }

    @Test
    public void testUndoTypeDeclaration() throws Exception {
        String rule = "package org.drools.test\n" +
                "import org.drools.test.FactA\n" +
                "import org.drools.test.FactB\n" +
                "rule R1 when\n" +
                "   FactA( i == 1 )\n" +
                "   FactB( i == 1 )\n" +
                "then\n" +
                "   list.add(\"OK\");"+
                "end\n";

        String declarationA = "package org.drools.test\n" +
                "global java.util.List list\n" +
                "declare FactA\n" +
                "    j : int\n" +
                "end\n";

        String declarationB = "package org.drools.test\n" +
                "declare FactB\n" +
                "    i : int\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());

        kbuilder.batch()
                .type(ResourceType.DRL)
                .add(ResourceFactory.newByteArrayResource(rule.getBytes()))
                .add(ResourceFactory.newByteArrayResource(declarationA.getBytes()))
                .build();

        assertTrue(kbuilder.hasErrors());
        kbuilder.undo();
        assertFalse(kbuilder.hasErrors());

        declarationA = "package org.drools.test\n" +
                "global java.util.List list\n" +
                "declare FactA\n" +
                "    i : int\n" +
                "end\n";

        kbuilder.batch()
                .type(ResourceType.DRL)
                .add(ResourceFactory.newByteArrayResource(rule.getBytes()))
                .add(ResourceFactory.newByteArrayResource(declarationA.getBytes()))
                .build();

        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactType aType = kbase.getFactType( "org.drools.test", "FactA" );
        Object a = aType.newInstance();
        aType.set( a, "i", 1 );

        FactType bType = kbase.getFactType( "org.drools.test", "FactB" );
        Object b = bType.newInstance();
        bType.set( b, "i", 1 );

        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
        assertEquals("OK", list.get(0));
    }

    @Test
    public void testUndoRule() throws Exception {
        String rule = "package org.drools.test\n" +
                "global java.util.List list\n" +
                "import org.drools.test.FactA\n" +
                "import org.drools.test.FactB\n" +
                "rule R1 when\n" +
                "   FactA( j == 1 )\n" +
                "   FactB( i == 1 )\n" +
                "then\n" +
                "   list.add(\"OK\");"+
                "end\n";

        String declarationA = "package org.drools.test\n" +
                "declare FactA\n" +
                "    i : int\n" +
                "end\n";

        String declarationB = "package org.drools.test\n" +
                "declare FactB\n" +
                "    i : int\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(declarationB.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());

        kbuilder.batch()
                .type(ResourceType.DRL)
                .add(ResourceFactory.newByteArrayResource(rule.getBytes()))
                .add(ResourceFactory.newByteArrayResource(declarationA.getBytes()))
                .build();

        assertTrue(kbuilder.hasErrors());
        kbuilder.undo();
        assertFalse(kbuilder.hasErrors());

        rule = "package org.drools.test\n" +
                "global java.util.List list\n" +
                "import org.drools.test.FactA\n" +
                "import org.drools.test.FactB\n" +
                "rule R1 when\n" +
                "   FactA( i == 1 )\n" +
                "   FactB( i == 1 )\n" +
                "then\n" +
                "   list.add(\"OK\");"+
                "end\n";

        kbuilder.batch()
                .type(ResourceType.DRL)
                .add(ResourceFactory.newByteArrayResource(rule.getBytes()))
                .add(ResourceFactory.newByteArrayResource(declarationA.getBytes()))
                .build();

        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactType aType = kbase.getFactType( "org.drools.test", "FactA" );
        Object a = aType.newInstance();
        aType.set( a, "i", 1 );

        FactType bType = kbase.getFactType( "org.drools.test", "FactB" );
        Object b = bType.newInstance();
        bType.set( b, "i", 1 );

        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals(1, rules);
        assertEquals("OK", list.get(0));
    }
}
