package org.drools.brms.client.modeldriven;

import java.util.HashMap;

import junit.framework.TestCase;

public class SuggestionCompletionTest extends TestCase {

    public void testCompletions() {
        
        
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.factTypes = new String[] {"Person", "Vehicle"};
        com.fieldsForType = new HashMap() {{
            put("Person", new String[] {"age", "name", "rank"});
            put("Vehicle", new String[] {"type", "make"});
        }};
        com.fieldTypes = new HashMap() {{
           put("Person.age", "Numeric");
           put("Person.rank", "Comparable");
           put("Person.name", "String");
           put("Vehicle.make", "String");
           put("Vehicle.type", "String");
        }};
        com.globalTypes = new HashMap() {{
           put("bar", "Person");
           put("baz", "Vehicle");
        }};
        

        
        String[] c =com.getConditionalElements();
        assertEquals("not", c[0]);
        assertEquals("exists", c[1]);
        assertEquals("or", c[2]);
        
        c = com.getFactTypes();
        assertEquals(2, c.length);
        assertContains("Person", c);
        assertContains("Vehicle", c);

        
        c = com.getFieldCompletions( "Person" );
        assertEquals("age", c[0]);
        assertEquals("name", c[1]);
        
        c = com.getFieldCompletions( "Vehicle" );
        assertEquals("type", c[0]);
        assertEquals( "make", c[1] );
        
        c = com.getOperatorCompletions( "Person", "name" );
        assertEquals(3, c.length);
        assertEquals("==", c[0]);
        assertEquals( "!=", c[1] );
        assertEquals( "matches", c[2] );
        
        
        c = com.getOperatorCompletions( "Person", "age" );
        assertEquals(6, c.length);
        assertEquals(c[0], "==");
        assertEquals(c[1], "!=");
        assertEquals(c[2], "<" );
        assertEquals(c[3], ">" );
        
        c = com.getOperatorCompletions( "Person", "rank" );        
        assertEquals(6, c.length);
        assertEquals(c[0], "==");
        assertEquals(c[1], "!=");
        assertEquals(c[2], "<" );
        assertEquals(c[3], ">" );

        c = com.getConnectiveOperatorCompletions( "Vehicle", "make" );
        assertEquals(5, c.length);
        assertEquals("| ==", c[0]);
        
        c = com.getGlobalVariables();
        assertEquals(2, c.length);
        assertEquals("baz", c[0]);
        assertEquals("bar", c[1]);
        
        c = com.getFieldCompletionsForGlobalVariable( "bar" );
        assertEquals(3, c.length);
        assertEquals("age", c[0]);
        assertEquals("name", c[1]);

        c = com.getFieldCompletionsForGlobalVariable( "baz" );
        assertEquals(2, c.length);
        assertEquals("type", c[0]);
        assertEquals("make", c[1]);


        //check that it has default operators for general objects
        c = com.getOperatorCompletions( "Person", "wankle" );
        assertEquals(2, c.length);        
        
    }
    
    public void testAdd() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        com.factTypes = new String[] {"Foo"};
        com.fieldsForType = new HashMap() {{
           put("Foo", new String[] {"a"}); 
        }};
        
        assertEquals(1, com.getFactTypes().length);
        assertEquals("Foo", com.getFactTypes()[0]);
        
        assertEquals(1, com.getFieldCompletions( "Foo" ).length);
        assertEquals("a", com.getFieldCompletions( "Foo" )[0]);
        
    }
    
    private void assertContains(String string,
                                String[] c) {
        
        for ( int i = 0; i < c.length; i++ ) {
            if (string.equals( c[i] )) {
                return;
            }
        } 
        fail( "String array did not contain: " + string );
        
    }
    
    
    public void testGlobalAndFacts() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.globalTypes = new HashMap() {{
            put("y", "Foo");
        }};
        com.fieldsForType = new HashMap() {{
           put("Foo", new String[] {"a"}); 
        }};
        
        
        
        assertFalse(com.isGlobalVariable( "x" ));        
        assertTrue(com.isGlobalVariable( "y" ));
    }
    
    
    
    
    
}
