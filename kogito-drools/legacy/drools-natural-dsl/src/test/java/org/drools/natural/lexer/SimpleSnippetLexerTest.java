package org.drools.natural.lexer;

import java.util.ArrayList;
import java.util.List;

import org.drools.natural.lexer.SimpleSnippetLexer;

import junit.framework.TestCase;

public class SimpleSnippetLexerTest extends TestCase
{
    
    public void testNoBrackets() {
        String sample = "1 ab   ccc d ";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        List rawTokens = lex.getRawTokens().getTokens();
        
        List expected = new ArrayList();
        expected.add("1");
        expected.add("ab");
        expected.add("ccc");
        expected.add("d");
        
        assertEquals(expected, rawTokens);
    }   
    
    public void testOneBigBracket() {
        String sample = "[1 2 ab \"c c\" ]";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        List rawTokens = lex.getRawTokens().getTokens();
        
        List expected = new ArrayList();
        expected.add("1 2 ab \"c c\" ");

        
        assertEquals(expected, rawTokens);
    }       
    
    public void testBrackets() {
        String sample = "[Age of] bob [less than] 35";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        List rawTokens = lex.getRawTokens().getTokens();        
        
        List expected = new ArrayList();
        expected.add("Age of");
        expected.add("bob");
        expected.add("less than");
        expected.add("35");
        
        assertEquals(expected, rawTokens);
    }
    
    public void testNestedBracketsEscape() {
        String sample = "[Age of] bob [less [] than] 35";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        List rawTokens = lex.getRawTokens().getTokens();
        
        List expected = new ArrayList();
        expected.add("Age of");
        expected.add("bob");
        expected.add("less [] than");
        expected.add("35");
        
        assertEquals(expected, rawTokens);
    }
    
    
    public void testBadNumberOfBrackets() {
        String sample = "[Age of] bob [less [ than] 35";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        try {
            lex.getRawTokens();
        } catch (LexerException e) {
            //System.out.println(e.getMessage());
            assertNotNull(e.getMessage());
        }
        
    }  
    
    public void testUnclosedDoubleQuotes() {
        String sample = "[Age of] bob [\"less [ than] 35";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        try {
            lex.getRawTokens();
        } catch (LexerException e) {
            //System.out.println(e.getMessage());
            assertNotNull(e.getMessage());
        }
        
    }      

    public void testSpacesAndQuotesEscape() {
        String sample = "[Age of] \" bob is \" [less [] than] 35";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        List rawTokens = lex.getRawTokens().getTokens();
        
        List expected = new ArrayList();
        expected.add("Age of");
        expected.add("\" bob is \"");
        expected.add("less [] than");
        expected.add("35");
        
        assertEquals(expected, rawTokens);
    }
    
    public void testRemoveTabsAndNewLines() {
        String sample = "[Age of] \n    \r bob \t [less [] than] 35";
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        List rawTokens = lex.getRawTokens().getTokens();
        
        List expected = new ArrayList();
        expected.add("Age of");
        expected.add("bob");
        expected.add("less [] than");
        expected.add("35");
        
        assertEquals(expected, rawTokens);
    }
    
    public void testNormaliseWhitespaceInTokens() {
        String sample ="[Age  of][Age\n    of]";
        NaturalSnippetLexer lex = new SimpleSnippetLexer(sample);
        List rawTokens = lex.getRawTokens().getTokens();
        
        List expected = new ArrayList();
        expected.add("Age of");        
        expected.add("Age of");
        
        assertEquals(expected, rawTokens);
        
    }
        
    
    
}
