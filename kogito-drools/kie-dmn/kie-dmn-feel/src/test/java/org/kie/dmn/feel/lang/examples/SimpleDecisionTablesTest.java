package org.kie.dmn.feel.lang.examples;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
        assertThat( (Map<?, ?>) context.get( "result3" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "result3" ), hasEntry("Out1", "out1b" ));
        assertThat( (Map<?, ?>) context.get( "result3" ), hasEntry("Out2", "out2b" ));
        assertThat( (Map<?, ?>) context.get( "result4" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "result4" ), hasEntry("Out1", "io1a" ));
        assertThat( (Map<?, ?>) context.get( "result4" ), hasEntry("Out2", "io2a" ));
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
        
        assertThat( context.get( "DTpriority11" ), is( "B" ) );
                
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
    
    @Test
    public void testdthitpoliciesMultipleOutput() {
        String expression = loadExpression( "dthitpolicies_multipleoutput.feel" );
        Map context = (Map) feel.evaluate( expression );
        
        System.out.println( printContext( context ) );
        
        assertThat( (Map<?, ?>) context.get( "DTunique10" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTunique10" ), hasEntry("Out1", "row2"            ));
        assertThat( (Map<?, ?>) context.get( "DTunique10" ), hasEntry("Out2", new BigDecimal(2) ));
        assertThat( context.get( "DTunique11" ), nullValue()      );
        
        assertThat( (Map<?, ?>) context.get( "DTfirst11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTfirst11" ), hasEntry("Out1", "row1"            ));
        assertThat( (Map<?, ?>) context.get( "DTfirst11" ), hasEntry("Out2", new BigDecimal(1) ));
        
        assertThat( (Map<?, ?>) context.get( "DTAny10" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTAny10" ), hasEntry("Out1", "B"               ));
        assertThat( (Map<?, ?>) context.get( "DTAny10" ), hasEntry("Out2", new BigDecimal(7) ));
        assertThat( context.get( "DTAny11" ), nullValue()      );
        
        assertThat( (Map<?, ?>) context.get( "DTruleOrder10" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTruleOrder10" ), hasEntry(is("Out1"), is( Arrays.asList("B", "B")                            )));
        assertThat( (Map<?, ?>) context.get( "DTruleOrder10" ), hasEntry(is("Out2"), is( Arrays.asList(new BigDecimal(2), new BigDecimal(3) ))));
        assertThat( (Map<?, ?>) context.get( "DTruleOrder11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTruleOrder11" ), hasEntry(is("Out1"), is( Arrays.asList("A", "B", "B")                            )));
        assertThat( (Map<?, ?>) context.get( "DTruleOrder11" ), hasEntry(is("Out2"), is( Arrays.asList(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3) ))));
        
        assertThat( (Map<?, ?>) context.get( "DTcollect11" ), hasSize(2));
        assertThat( (Map<?, Iterable<?>>) context.get( "DTcollect11" ), hasEntry(is("Out1"), containsInAnyOrder( "B", "A"                             )));
        assertThat( (Map<?, Iterable<?>>) context.get( "DTcollect11" ), hasEntry(is("Out2"), containsInAnyOrder( new BigDecimal(1), new BigDecimal(2) )));
        
        assertThat( (Map<?, ?>) context.get( "DTpriority11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTpriority11" ), hasEntry("Out1", "B"               ));
        assertThat( (Map<?, ?>) context.get( "DTpriority11" ), hasEntry("Out2", new BigDecimal(1) ));
        
        assertThat( (Map<?, ?>) context.get( "DToutputOrder11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DToutputOrder11" ), hasEntry(is("Out1"), is( Arrays.asList("B", "A")                            )));
        assertThat( (Map<?, ?>) context.get( "DToutputOrder11" ), hasEntry(is("Out2"), is( Arrays.asList(new BigDecimal(1), new BigDecimal(2) ))));
        
        assertThat( (Map<?, ?>) context.get( "DTcount10" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTcount10" ), hasEntry("Out1", new BigDecimal(1) ));
        assertThat( (Map<?, ?>) context.get( "DTcount10" ), hasEntry("Out2", new BigDecimal(2) ));
        assertThat( (Map<?, ?>) context.get( "DTcount11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTcount11" ), hasEntry("Out1", new BigDecimal(2) ));
        assertThat( (Map<?, ?>) context.get( "DTcount11" ), hasEntry("Out2", new BigDecimal(3) ));
        
        assertThat( (Map<?, ?>) context.get( "DTsum10" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTsum10" ), hasEntry("Out1", new BigDecimal(5) ));
        assertThat( (Map<?, ?>) context.get( "DTsum10" ), hasEntry(is("Out2"), nullValue()       ));
        assertThat( (Map<?, ?>) context.get( "DTsum11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTsum11" ), hasEntry("Out1", new BigDecimal(6) ));
        assertThat( (Map<?, ?>) context.get( "DTsum11" ), hasEntry(is("Out2"), nullValue()       ));
        
        assertThat( (Map<?, ?>) context.get( "DTmin10" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTmin10" ), hasEntry("Out1", new BigDecimal(2) ));
        assertThat( (Map<?, ?>) context.get( "DTmin10" ), hasEntry("Out2", "B"               ));
        assertThat( (Map<?, ?>) context.get( "DTmin11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTmin11" ), hasEntry("Out1", new BigDecimal(1) ));
        assertThat( (Map<?, ?>) context.get( "DTmin11" ), hasEntry("Out2", "A"               ));
        
        assertThat( (Map<?, ?>) context.get( "DTmax10" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTmax10" ), hasEntry("Out1", new BigDecimal(3) ));
        assertThat( (Map<?, ?>) context.get( "DTmax10" ), hasEntry("Out2", "C"               ));
        assertThat( (Map<?, ?>) context.get( "DTmax11" ), hasSize(2));
        assertThat( (Map<?, ?>) context.get( "DTmax11" ), hasEntry("Out1", new BigDecimal(3) ));
        assertThat( (Map<?, ?>) context.get( "DTmax11" ), hasEntry("Out2", "C"               ));
    }
    
    public static <K, V> Matcher<Map<K, V>> hasSize(final int z) {
        return new TypeSafeMatcher<Map<K, V>>() {
            public boolean matchesSafely(Map<K, V> arg0) {
                return arg0.size() == z;
            }
            public void describeTo(Description arg0) {
                arg0.appendText("not matching size "+z);
            }
        };
    }
}
