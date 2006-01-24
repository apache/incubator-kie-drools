package org.drools.natural.template;

import java.util.Properties;

import junit.framework.TestCase;

public class NLExpressionCompilerTest extends TestCase {

    public void testIntegration() {
        NLGrammar grammar = new NLGrammar();
        grammar.addNLItem(new NLMappingItem(0, "{0} likes cheese", "likesCheese({0})"));
        
        NLExpressionCompiler compiler = new NLExpressionCompiler(grammar);
        String result = compiler.compile("bob likes cheese");
        
        assertEquals("likesCheese(bob)", result);
                
        //now lets use a properties
        Properties props = new Properties();
        props.setProperty("{0} likes cheese", "likesCheese({0})");
        props.setProperty("the date between {0} and {1}", "dateCompare({0}, {1})");
        props.setProperty("bind", "=>");
        
        grammar = new NLGrammar();
        grammar.loadFromProperties(props);
        compiler = new NLExpressionCompiler(grammar);
        result = compiler.compile("bob likes cheese");
        
        assertEquals("likesCheese(bob)", result);
        
        result = compiler.compile("the date between bob and michael");
        assertEquals("dateCompare(bob, michael)", result);
        
        result = compiler.compile("bind");
        assertEquals("=>", result);
        
    }
    
    /** This is surprisingly fast. I didn't build it for speed. */
    public void testLargeGrammar() {
        Properties props = new Properties();
        
        for (int i = 0; i < 1000; i++ ) {
            props.put("some {0} grammar" + i, "some mapping{0}");
            if (i == 42) {
                props.put("{0} likes cheese", "{0}.likesCheese()");
                props.put("{0} is happy", "{0}.isHappy()");
            }
        }
        
        
        
        NLGrammar grammar = new NLGrammar();
        grammar.loadFromProperties(props);
        
        NLExpressionCompiler compiler = new NLExpressionCompiler(grammar);
        
        long start = System.currentTimeMillis();
        String result = compiler.compile("michael likes cheese and michael is happy");
        long runtime = System.currentTimeMillis() - start;
        System.out.println("Runtime for compile with dictionary of 1000: " + runtime + "ms");
        assertEquals("michael.likesCheese() and michael.isHappy()", result);
        
    }
    
    
    public void testNestingAndOrderOfExpressions() {
        NLGrammar grammar = new NLGrammar();
        
        grammar.addNLItem(new NLMappingItem(0, "{0} likes cheese", "{0}.likesCheese()" ));
        grammar.addNLItem(new NLMappingItem(1, "print out cheese fan status {0}", "print({0})" ));
       
        
        NLExpressionCompiler compiler = new NLExpressionCompiler(grammar);
        String nl = "print out cheese fan status bob likes cheese";
        String expected = "print(bob.likesCheese())";
        
        String result = compiler.compile(nl);
        
        assertEquals(expected, result);

        
        grammar = new NLGrammar();
        
        grammar.addNLItem(new NLMappingItem(1, "date of '{0}'", "dateOf({0})"));
        grammar.addNLItem(new NLMappingItem(2, "age of [ {0} ]", "{0}.getAge()"));
        grammar.addNLItem(new NLMappingItem(3, "Today", "new java.util.Date()"));
        grammar.addNLItem(new NLMappingItem(4, "{0} is before {1}", "({0}).compareTo({1}) > 0"));
        
        nl = "date of '10-jul-2006' is before Today";
        
        compiler = new NLExpressionCompiler(grammar);
        expected = "(dateOf(10-jul-2006)).compareTo(new java.util.Date()) > 0";
        assertEquals(expected, compiler.compile(nl));
        
        nl = "age of [ bob ] < age of [ michael ]";
        expected = "bob.getAge() < michael.getAge()";
        assertEquals(expected, compiler.compile(nl));
        
        
        
    }
    
}
