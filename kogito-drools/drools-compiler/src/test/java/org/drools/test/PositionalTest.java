package org.drools.test;

import java.util.ArrayList;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class PositionalTest extends CommonTestMethodBase {

    @Test
    public void testPositional() {

        String drl =
                "import org.drools.test.Man;\n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "rule \"To be or not to be\"\n" +
                "when\n" +
                "    $m : Man( \"john\" , 18 , $w ; )\n" +
                "then\n" +
                "    list.add($w); " +
                "end";

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( drl.getBytes() ),
                              ResourceType.DRL );

        System.out.println( knowledgeBuilder.getErrors().toString() );
        
        assertFalse( knowledgeBuilder.hasErrors() );
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        StatefulKnowledgeSession kSession = createKnowledgeSession(kBase);

        java.util.ArrayList list = new ArrayList();
        kSession.setGlobal( "list",
                            list );

        kSession.insert( new Man( "john",
                                  18,
                                  84.2 ) );
        kSession.fireAllRules();

        assertTrue( list.contains( 84.2 ) );

    }

}
