package org.drools.compiler.integrationtests;

import java.util.Comparator;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.Row;

import ca.odell.glazedlists.SortedList;

public class DroolsEventListTest extends CommonTestMethodBase {

    @Test
    public void testOpenQuery() throws Exception {
        String str = "";
        str += "package org.kie.test  \n";
        str += "import org.drools.compiler.Cheese \n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $price : price) \n";
        str += "    cheddar : Cheese(type == $type2, price == stilton.price) \n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
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

        FactHandle s1Fh = ksession.insert( stilton1 );
        FactHandle s2Fh = ksession.insert( stilton2 );
        FactHandle s3Fh = ksession.insert( stilton3 );
        FactHandle c1Fh = ksession.insert( cheddar1 );
        FactHandle c2Fh = ksession.insert( cheddar2 );
        FactHandle c3Fh = ksession.insert( cheddar3 );
                      
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
        ksession.fireAllRules();
        
        assertEquals( 2, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 2, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );

        // alter the price to put the last row back in
        stilton3.setPrice( 3 );
        ksession.update(  s3Fh, stilton3 );
        ksession.fireAllRules();

        assertEquals( 3, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 2, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );
        assertEquals( 3, ((Cheese)sorted.get( 2 ).get( "stilton" )).getPrice() );
        
        // alter the price to remove the middle row
        stilton2.setPrice( 4 );
        ksession.update(  s2Fh, stilton2 );
        ksession.fireAllRules();

        assertEquals( 2, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 3, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );
        
        // alter the price to add the previous middle rows to the end
        cheddar2.setPrice( 4 );
        ksession.update(  c2Fh, cheddar2 );
        ksession.fireAllRules();

        assertEquals( 3, sorted.size() );
        assertEquals( 1, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 3, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );
        assertEquals( 4, ((Cheese)sorted.get( 2 ).get( "stilton" )).getPrice() );
            
        // Check a standard retract
        ksession.retract( s1Fh );
        ksession.fireAllRules();

        assertEquals( 2, sorted.size() );
        assertEquals( 3, ((Cheese)sorted.get( 0 ).get( "stilton" )).getPrice() );
        assertEquals( 4, ((Cheese)sorted.get( 1 ).get( "stilton" )).getPrice() );

        // Close the query, we should get removed events for each row
        query.close();
        
        assertEquals( 0, sorted.size() );
       
    }

}
