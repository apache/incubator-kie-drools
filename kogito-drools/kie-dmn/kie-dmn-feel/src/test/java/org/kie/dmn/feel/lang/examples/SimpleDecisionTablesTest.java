package org.kie.dmn.feel.lang.examples;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SimpleDecisionTablesTest
        extends ExamplesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger( ExamplesTest.class );
    private static FEEL feel;

    @BeforeClass
    public static void setupTest() {
        feel = FEEL.newInstance();
    }

    @Test
    public void testMain() {
        String expression = loadExpression( "simple_decision_tables.feel" );
        Map context = (Map) feel.evaluate( expression );
        
        System.out.println( printContext( context ) );
        
        assertThat( context.get( "result1" ), is( "Adult" ) );
        assertThat( context.get( "result2" ), is( "Medium" ) );
        assertThat( context.get( "result3" ), is( Arrays.asList( "out1b", "out2b" ) ) );
        assertThat( context.get( "result4" ), is( Arrays.asList( "io1a", "io2a" ) ) );
    }
    
    @Test
    public void testt0004simpletableU() {
        String expression = loadExpression( "t0004simpletableU.feel" );
        Map context = (Map) feel.evaluate( expression );
        
        System.out.println( printContext( context ) );
        
        assertThat( context.get( "result1" ), is( "Approved" ) );
        assertThat( context.get( "result2" ), nullValue()      );
        assertThat( context.get( "result3" ), is( "Declined" ) );
    }
    
    @Test
    public void testdthitpolicies() {
        String expression = loadExpression( "dthitpolicies.feel" );
        Map context = (Map) feel.evaluate( expression );
        
        System.out.println( printContext( context ) );
        
        assertThat( context.get( "DTunique10" ), is( "row2" ) );
        assertThat( context.get( "DTunique11" ), nullValue()      );
        
        assertThat( context.get( "DTfirst11" ), is( "row1" ) );
        
        assertThat( context.get( "DTAny10" ), is( "B" ) );
        assertThat( context.get( "DTAny11" ), nullValue()      );
        
        assertThat( context.get( "DTruleOrder10" ), is( Arrays.asList(new String[]{"B", "B"}) ) );
        assertThat( context.get( "DTruleOrder11" ), is( Arrays.asList(new String[]{"A", "B", "B"}) ) );
        
        assertTrue( ((List)context.get( "DTcollect11" )).contains("A"));
        assertTrue( ((List)context.get( "DTcollect11" )).contains("B"));
        assertTrue( ((List)context.get( "DTcollect11" )).size() == 2 );
        
        assertTrue( ((List)context.get( "DTpriority11" )).size() == 1 );
        assertTrue( ((List)context.get( "DTpriority11" )).get(0).equals("B"));
                
        assertTrue( ((List)context.get( "DToutputOrder11" )).size() == 2 );
        assertTrue( ((List)context.get( "DToutputOrder11" )).get(0).equals("B"));
        assertTrue( ((List)context.get( "DToutputOrder11" )).get(1).equals("A"));
        
        assertThat( context.get( "DTcount10" ), is( new BigDecimal(1) ) );
        assertThat( context.get( "DTcount11" ), is( new BigDecimal(2) ) );
        
        assertThat( context.get( "DTsum10" ), is( new BigDecimal(5) ) );
        assertThat( context.get( "DTsum11" ), is( new BigDecimal(6) ) );
        
        assertThat( context.get( "DTmin10" ), is( new BigDecimal(2) ) );
        assertThat( context.get( "DTmin11" ), is( new BigDecimal(1) ) );
        
        assertThat( context.get( "DTmax10" ), is( new BigDecimal(3) ) );
        assertThat( context.get( "DTmax11" ), is( new BigDecimal(3) ) );
    }
}
