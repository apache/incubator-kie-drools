package org.kie.dmn.feel.lang.examples;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

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
}
