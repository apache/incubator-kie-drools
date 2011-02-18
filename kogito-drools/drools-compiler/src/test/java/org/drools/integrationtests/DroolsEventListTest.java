package org.drools.integrationtests;

import java.util.Comparator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.Row;

import ca.odell.glazedlists.SortedList;

public class DroolsEventListTest {
    protected RuleBase getRuleBase() throws Exception {

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setMultithreadEvaluation( false );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    @Test
    public void testOpenQuery() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Cheese \n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $price : price) \n";
        str += "    cheddar : Cheese(type == $type2, price == stilton.price) \n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Cheese stilton1 = new Cheese( "stilton",
                                      1 );
        Cheese cheddar1 = new Cheese( "cheddar",
                                      1 );
        Cheese stilton2 = new Cheese( "stilton",
                                      2 );
        Cheese cheddar2 = new Cheese( "cheddar",
                                      2 );
        Cheese stilton3 = new Cheese( "stilton",
                                      3 );
        Cheese cheddar3 = new Cheese( "cheddar",
                                      3 );

        org.drools.runtime.rule.FactHandle s1Fh = ksession.insert( stilton1 );
        org.drools.runtime.rule.FactHandle s2Fh = ksession.insert( stilton2 );
        org.drools.runtime.rule.FactHandle s3Fh = ksession.insert( stilton3 );
        org.drools.runtime.rule.FactHandle c1Fh = ksession.insert( cheddar1 );
        org.drools.runtime.rule.FactHandle c2Fh = ksession.insert( cheddar2 );
        org.drools.runtime.rule.FactHandle c3Fh = ksession.insert( cheddar3 );
                      
        DroolsEventList list = new DroolsEventList();
        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery( "cheeses", new Object[] { "cheddar", "stilton" } , list );
        
        SortedList<Row> sorted = new SortedList<Row>( list, new Comparator<Row>() {

            public int compare(Row r1,
                               Row r2) {
                Cheese c1 = ( Cheese ) r1.get( "stilton" );
                Cheese c2 = ( Cheese ) r2.get( "stilton" );
                return c1.getPrice() - c2.getPrice();
            }
        });

        
        assertEquals( 3, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 2, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );
        assertEquals( 3, ((Cheese)sorted.get( 2 ).get( "stilton" )).getPrice() );

        // alter the price to remove the last row
        stilton3.setPrice( 4 );
        ksession.update(  s3Fh, stilton3 );
        
        assertEquals( 2, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 2, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );

        // alter the price to put the last row back in
        stilton3.setPrice( 3 );
        ksession.update(  s3Fh, stilton3 );
        
        assertEquals( 3, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 2, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );
        assertEquals( 3, ((Cheese)sorted.get( 2 ).get( "stilton" )).getPrice() );
        
        // alter the price to remove the middle row
        stilton2.setPrice( 4 );
        ksession.update(  s2Fh, stilton2 );
        
        assertEquals( 2, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 3, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );
        
        // alter the price to add the previous middle rows to the end
        cheddar2.setPrice( 4 );
        ksession.update(  c2Fh, cheddar2 );

        assertEquals( 3, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 3, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );
        assertEquals( 4, ((Cheese)sorted.get( 2 ).get( "stilton" )).getPrice() );
            
        // Check a standard retract
        ksession.retract( s1Fh );
        assertEquals( 2, sorted.size() );
        assertEquals( 3, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 4, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );

        // Close the query, we should get removed events for each row
        query.close();
        
        assertEquals( 0, sorted.size() );
       
    }

}
