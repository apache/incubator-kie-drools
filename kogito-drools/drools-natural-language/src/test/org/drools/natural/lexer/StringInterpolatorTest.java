package org.drools.natural.lexer;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

public class StringInterpolatorTest extends TestCase
{
    public void testEdgeOfStringCheck() {
        String s = "something bad is wrong $";
        StringInterpolator in = new StringInterpolator(s);
        
        assertFalse(in.nextCharIsBracket(s.toCharArray(), 23));
    }
    
    public void testExtractSingle() {
        String s = "this contains ${var} in the middle.";
        StringInterpolator in = new StringInterpolator(s);
        
        List result = in.extractVariableNames();
        
        assertTrue(result.contains("var"));
        assertEquals(1, result.size());
    }
    
    public void testExtractSingleAtEnd() {
        String s = "this contains ${var}";
        StringInterpolator in = new StringInterpolator(s);
        
        List result = in.extractVariableNames();
        
        assertTrue(result.contains("var"));
        assertEquals(1, result.size());
    }
    
    public void testMultipleExtraction() {
        String s = "this contains ${var1} and ${var2}";
        StringInterpolator in = new StringInterpolator(s);
        
        List result = in.extractVariableNames();
        
        assertEquals(2, result.size());
        assertTrue(result.contains("var1"));
        assertTrue(result.contains("var2"));
    }

    
    public void testIgnoreNonVars() {
        String s = "this contains $var1 and ${var2}}";
        StringInterpolator in = new StringInterpolator(s);
        
        List result = in.extractVariableNames();
        
        assertEquals(1, result.size());        
        assertTrue(result.contains("var2"));
    }
    
    public void testExtractFromStart() {
        String s = "${var1} blah blah blah";
        StringInterpolator in = new StringInterpolator(s);
        
        List result = in.extractVariableNames();
        
        assertEquals(1, result.size());
        assertTrue(result.contains("var1"));        
    }
    
    public void testExtractDuplicate() {
        String s = "${var1} blah ${var1} blah";
        StringInterpolator in = new StringInterpolator(s);
        
        List result = in.extractVariableNames();
        
        //only want to see it once.
        assertEquals(1, result.size());
        assertTrue(result.contains("var1"));        
        
    }
    
    public void testInterpolateReplace() {
        String s = "${var1}";
        StringInterpolator in = new StringInterpolator(s);
        
        Properties props = new Properties();
        props.setProperty("var1", "111");
                
        assertEquals("111", 
                     in.interpolate(props));        
    }
    
    public void testInterpolateSingle() {
        String s = "${var1} xxx";
        StringInterpolator in = new StringInterpolator(s);
        
        Properties props = new Properties();
        props.setProperty("var1", "111");
                
        assertEquals("111 xxx", 
                     in.interpolate(props));        
    }    
    
    
    
    public void testInterpolateMulti() {
        String s = "this contains ${var1} and ${var2}.";
        StringInterpolator in = new StringInterpolator(s);
        
        Properties props = new Properties();
        props.setProperty("var1", "111");
        props.setProperty("var2", "222");
        
        assertEquals("this contains 111 and 222.", 
                     in.interpolate(props));
        
    }
    
    
    
    
}
