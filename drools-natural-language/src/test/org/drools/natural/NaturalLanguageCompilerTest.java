package org.drools.natural;

import org.drools.natural.grammar.SimpleGrammar;

import junit.framework.TestCase;

public class NaturalLanguageCompilerTest extends TestCase
{
    
    //a reasonable test grammar
    private static SimpleGrammar grammar;
    static {
        grammar = new SimpleGrammar();
        grammar.addToDictionary("likes cheese", "${left}.likesCheese()");
        grammar.addToDictionary("equals", "${left}.equals(${right})" );
        grammar.addToDictionary("age of", "ageOf(${1})");
        grammar.addToDictionary("->", "${left}.${right}()");
        grammar.addToDictionary("or", "||");
        grammar.addToDictionary("and", "&&");
    }
    
    public void testNaturalLanguage() {
        String snippet = "bob [likes cheese] or [age of] bob < 21 " +
                "or bob equals mark and bob -> health equals good";
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(grammar);
        
        String result = parser.compileExpression(snippet);
        System.out.println(result);
        assertEquals("bob.likesCheese() || ageOf(bob) < 21 || bob.equals(mark) && bob.health().equals(good)", result);
        
    }
    
    public void testLookNoBracketsMum() {
        String snippet = "bob likes cheese or age of bob < 21 " +
                "or bob equals mark and bob -> health equals good";
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(grammar);
        
        String result = parser.compileNaturalExpression(snippet);
        assertEquals("bob.likesCheese() || ageOf(bob) < 21 || bob.equals(mark) && bob.health().equals(good)", result);
        
    }    
    
    public void testNaturalLanguageNesting() {
        String snippet = "[age of] [age of] [age of] bob";
                
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(grammar);
        
        String result = parser.compileExpression(snippet);
        assertEquals("ageOf(ageOf(ageOf(bob)))", result);
    }    
    
    public void testNotInDictionary() {
        String snippet = "nothing is in the dictionary";
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(grammar);
        assertEquals(snippet, parser.compileExpression(snippet));
        
        snippet = "[well some is like bob likes cheese, but by using brackets is all escaped]";     
        String bracketsRemoved = "well some is like bob likes cheese, but by using brackets is all escaped";  
        assertEquals(bracketsRemoved, parser.compileExpression(snippet));
        
    }

}
