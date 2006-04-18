package org.drools.lang.dsl.template;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.Properties;

import junit.framework.TestCase;

public class NLExpressionCompilerTest extends TestCase {

    public void testIntegration() {
        NLGrammar grammar = new NLGrammar();
        grammar.addNLItem(new NLMappingItem("{0} likes cheese", "likesCheese({0})", "*"));
        
        NLExpressionCompiler compiler = new NLExpressionCompiler(grammar);
        String result = compiler.compile("bob likes cheese", "when");
        
        assertEquals("likesCheese(bob)", result);
                
        grammar = new NLGrammar();
        grammar.addNLItem( new NLMappingItem("{0} likes cheese", "likesCheese({0})", "*") );
        grammar.addNLItem( new NLMappingItem("the date between {0} and {1}", "dateCompare({0}, {1})", "*") );
        grammar.addNLItem( new NLMappingItem("bind", "=>", "*") );
        
        compiler = new NLExpressionCompiler(grammar);
        result = compiler.compile("bob likes cheese", "when");
        
        assertEquals("likesCheese(bob)", result);
        
        result = compiler.compile("the date between bob and michael", "when");
        assertEquals("dateCompare(bob, michael)", result);
        
        result = compiler.compile("bind", "then");
        assertEquals("=>", result);
        
    }
    
    public void testLargeGrammar() {
        
        NLGrammar grammar = new NLGrammar();
        for (int i = 0; i < 1000; i++ ) {
            grammar.addNLItem(new NLMappingItem("some {0} grammar" + i, "some mapping{0}", "*"));
            if (i == 42) {
                grammar.addNLItem( new NLMappingItem("{0} likes cheese", "{0}.likesCheese()", "*") );
                grammar.addNLItem( new NLMappingItem("{0} is happy", "{0}.isHappy()", "*") );
            }
        }
        
        
        
        
        
        
        NLExpressionCompiler compiler = new NLExpressionCompiler(grammar);
        
        long start = System.currentTimeMillis();
        String result = compiler.compile("michael likes cheese and michael is happy", "then");
        long runtime = System.currentTimeMillis() - start;
        System.out.println("Runtime for compile with dictionary of 1000: " + runtime + "ms");
        assertEquals("michael.likesCheese() and michael.isHappy()", result);
        
    }
    
    
    public void testNestingAndOrderOfExpressions() {
        NLGrammar grammar = new NLGrammar();
        
        grammar.addNLItem(new NLMappingItem("{0} likes cheese", "{0}.likesCheese()", "*" ));
        grammar.addNLItem(new NLMappingItem("print out cheese fan status {0}", "print({0})", "*" ));
       
        
        NLExpressionCompiler compiler = new NLExpressionCompiler(grammar);
        String nl = "print out cheese fan status bob likes cheese";
        String expected = "print(bob.likesCheese())";
        
        String result = compiler.compile(nl, "when");
        
        assertEquals(expected, result);
        
        grammar = new NLGrammar();
        
        grammar.addNLItem(new NLMappingItem("date of '{0}'", "dateOf({0})", "*"));
        grammar.addNLItem(new NLMappingItem("age of [{0}]", "{0}.getAge()", "*"));
        grammar.addNLItem(new NLMappingItem("Today", "new java.util.Date()", "*"));
        grammar.addNLItem(new NLMappingItem("{0} is before {1}", "({0}).compareTo({1}) > 0", "*"));
        
        nl = "date of '10-jul-2006' is before Today";
        
        compiler = new NLExpressionCompiler(grammar);
        expected = "(dateOf(10-jul-2006)).compareTo(new java.util.Date()) > 0";
        assertEquals(expected, compiler.compile(nl, "when"));
        
        //test repeating...
        nl = "age of [bob] < age of [michael]";
        expected = "bob.getAge() < michael.getAge()";
        assertEquals(expected, compiler.compile(nl, "when"));
        
        //test no change to output
        nl = "nothing relevant here... move along";
        expected = nl;
        
        try {
            assertEquals(expected, compiler.compile(nl, "when"));
            fail("should have thrown an exception for non expansion.");
        } catch (RuntimeException e) {
            
        }
        
    }
    
    
    public void testProcessWhenAndThenSeperately() {
        NLGrammar g = new NLGrammar();
        g.addNLItem( new NLMappingItem("something", "blah", "when") );
        g.addNLItem( new NLMappingItem("boo", "ya", "when") );
        g.addNLItem( new NLMappingItem("coo", "eee", "then") );
        g.addNLItem( new NLMappingItem("ska", "fa", "*") );
        
        NLExpressionCompiler compiler = new NLExpressionCompiler(g);
        String result = compiler.compile( "something boo coo ska", "when" );
        assertEquals("blah ya coo fa", result);
        
        result = compiler.compile( "something boo coo ska", "then" );
        assertEquals( "something boo eee fa", result );
    }
    
}