package org.drools.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class FunctionsTest extends CommonTestMethodBase {

    @SuppressWarnings("unchecked")
    @Test
    public void testFunction() throws Exception {

        KnowledgeBase kbase = loadKnowledgeBase( "test_FunctionInConsequence.drl" );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                            list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        ksession.insert( stilton );

        ksession.fireAllRules();

        assertEquals( new Integer( 5 ),
                      ((List<Integer>) ksession.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testFunctionException() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_FunctionException.drl" );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final Cheese brie = new Cheese( "brie",
                                        12 );
        ksession.insert( brie );

        try {
            ksession.fireAllRules();
            fail( "Should throw an Exception from the Function" );
        } catch ( final Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    @Test
    public void testFunctionWithPrimitives() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_FunctionWithPrimitives.drl" );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        ksession.insert( stilton );

        ksession.fireAllRules();

        assertEquals( new Integer( 10 ),
                      list.get( 0 ) );
    }
    
    @Test
    public void testFunctionCallingFunctionWithEclipse() throws Exception {
        KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbconf.setProperty( "drools.dialect.java.compiler", "ECLIPSE" );
        KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_functionCallingFunction.drl" );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "results",
                            list );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( 12,
                      list.get( 0 ).intValue() );
    }

    @Test
    public void testFunctionCallingFunctionWithJanino() throws Exception {
        KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbconf.setProperty( "drools.dialect.java.compiler", "JANINO" );
        KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_functionCallingFunction.drl" );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "results",
                            list );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( 12,
                      list.get( 0 ).intValue() );
    }

    @Test
    public void testJBRULES3117() {
        String str = "package org.drools\n" +
                     "function boolean isOutOfRange( Object value, int lower ) { return true; }\n" + 
                     "function boolean isNotContainedInt( Object value, int[] values ) { return true; }\n" +
                     "rule R1\n" +
                     "when\n" +
                     "then\n" +
                     "    boolean x = isOutOfRange( Integer.MAX_VALUE, 1 );\n" +
                     "    boolean y = isNotContainedInt( Integer.MAX_VALUE, new int[] { 1, 2, 3 } );\n" +
                     "end\n";
        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        int rulesFired = ksession.fireAllRules();
        assertEquals( 1,
                      rulesFired );
    }

    

}
