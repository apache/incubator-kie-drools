package org.drools.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.util.Iterator;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;

public class TerminalNodeIteratorTest {

    @Test
    public void testTerminalNodeListener() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 when\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 when\n" +
                     "    Object()" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 when\n" +
                     "    Object()" +
                     "then\n" +
                     "end\n" +
                     "rule rule5 when\n" + // this will result in two terminal nodes
                     "    Object() or\n" +
                     "    Object()\n" +
                     "then\n" +
                     "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List<String> nodes = new ArrayList<String>();
        Iterator it = TerminalNodeIterator.iterator( kbase );
        for ( TerminalNode node = (TerminalNode) it.next(); node != null; node = (TerminalNode) it.next() ) {
            nodes.add( ((RuleTerminalNode) node).getRule().getName() );
        }

        assertEquals( 6,
                      nodes.size() );
        assertTrue( nodes.contains( "rule1" ) );
        assertTrue( nodes.contains( "rule2" ) );
        assertTrue( nodes.contains( "rule3" ) );
        assertTrue( nodes.contains( "rule4" ) );
        assertTrue( nodes.contains( "rule5" ) );

        int first = nodes.indexOf( "rule5" );
        int second = nodes.lastIndexOf( "rule5" );
        assertTrue( first != second );
    }

}
