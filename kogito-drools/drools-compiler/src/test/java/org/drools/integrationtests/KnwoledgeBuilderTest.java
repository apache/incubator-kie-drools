package org.drools.integrationtests;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KnwoledgeBuilderTest {

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

        KnowledgeBuilderImpl kbuilder2 = (KnowledgeBuilderImpl)KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add(ResourceType.DRL,
                ResourceFactory.newByteArrayResource(rule.getBytes()),
                ResourceFactory.newByteArrayResource(declarationA.getBytes()),
                ResourceFactory.newByteArrayResource(declarationB.getBytes()));

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
}
