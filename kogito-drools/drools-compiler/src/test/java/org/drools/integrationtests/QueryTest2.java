package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Order;
import org.drools.OrderItem;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;


public class QueryTest2 extends CommonTestMethodBase {
    
    @Test
    public void testEvalRewrite() throws Exception {
        String str = "" +
        "package org.drools;\n" +
        "global java.util.List results;\n" +
        "rule \"eval rewrite\"\n" +
        "    when\n" +
        "        $o1 : OrderItem( order.number == 11, $seq : seq == 1 )\n" +
        //"        $o2 : OrderItem( order.number == $o1.order.number, seq != $seq )\n" +
        "        $o2 : Order( items[(Integer) 1] == $o1 ) \n" +
        "    then\n" +
        "        System.out.println( $o1 + \":\" + $o2 );\n" +
        "end        \n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        final Order order1 = new Order( 11,
                                        "Bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.insert( order1 );
        ksession.insert( item11 );
        ksession.insert( item12 );
        
        ksession.fireAllRules();
        

    }
}
