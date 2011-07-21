package org.drools.integrationtests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AgendaItem;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Activation;
import org.junit.Test;

public class DynamicAgendaTest {

    @Test
    public void test1() {
        String str = "";
        str += "package org.domain.test \n";
        str += "import " + Activation.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1\n";
        str += "    @key1(value1)";
        str += "when \n";
        str += "     s : String() \n";
        str += "then \n";
        str += "    insert( kcontext.getActivation() ); \n";
        str += "end \n";
        str += "rule rule2 dialect 'java'\n";
        str += "when \n";
        str += "    $i : Activation( s == 'tada', key1 == 'value1' ) \n";
        str += "then \n";
        str += "    list.add( $i ); \n";
        str += "end \n";
        
        System.out.println( str );
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();                
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list);
        ksession.insert(  "tada" );
        ksession.fireAllRules();
        
        System.out.println( list );
        
        ksession.dispose();
        //new AgendaItem( 100, null, -5, null, null );         
    }
}
