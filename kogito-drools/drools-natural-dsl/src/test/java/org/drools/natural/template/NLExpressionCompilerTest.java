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
        
        grammar = new NLGrammar();
        grammar.loadFromProperties(props);
        compiler = new NLExpressionCompiler(grammar);
        result = compiler.compile("bob likes cheese");
        
        assertEquals("likesCheese(bob)", result);
        
        result = compiler.compile("the date between bob and michael");
        assertEquals("dateCompare(bob, michael)", result);
        
        
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
        System.out.println("Runtime for compile: " + runtime + "ms");
        assertEquals("michael.likesCheese() and michael.isHappy()", result);
        
    }
    
}
