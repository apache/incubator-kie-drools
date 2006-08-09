package org.drools.natural;

import java.util.Properties;

import org.drools.natural.grammar.NaturalGrammar;
import org.drools.natural.grammar.SimpleGrammar;

import junit.framework.TestCase;

/** 
 * This is kind of an overall integration test for the natural expression compiler
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
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
        grammar.addToDictionary("less than", "${left} < ${right}");
        grammar.addToDictionary("ignore.unknown", "true");
    }
    
    public void testNaturalLanguage() {
        String snippet = "bob [likes cheese] or [age of] bob [less than] 21 " +
                "or bob equals mark and bob -> health equals good";
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(grammar);
        
        String result = parser.compileExpression(snippet);
        System.out.println(result);
        assertEquals("bob.likesCheese() || ageOf(bob) < 21 || bob.equals(mark) && bob.health().equals(good)", result);
        
    }
    
    public void testLookNoBracketsMum() {
        String snippet = "check that bob likes cheese or check the age of bob less than 21 " +
                "or bob equals mark and bob -> health equals good";
               
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(grammar);
        
        String result = parser.compileNaturalExpression(snippet);
        assertEquals("bob.likesCheese() || ageOf(bob) < 21 || bob.equals(mark) && bob.health().equals(good)", result);

        //now check it to make sure it doesn't require brackets by default
        result = parser.compile(snippet);
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
        
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(new SimpleGrammar(new Properties()));
        assertEquals(snippet, parser.compileExpression(snippet));
        
        snippet = "[well some is like bob likes cheese, but by using brackets is all escaped]";     
        String bracketsRemoved = "well some is like bob likes cheese, but by using brackets is all escaped";  
        assertEquals(bracketsRemoved, parser.compileExpression(snippet));
        
    }
    
    public void testIgnoreUnknown() {
        NaturalLanguageCompiler parser = new NaturalLanguageCompiler(grammar);
        String snippet = "that bob likes cheese";
        assertEquals("bob.likesCheese()", parser.compileNaturalExpression(snippet));
    }


}
