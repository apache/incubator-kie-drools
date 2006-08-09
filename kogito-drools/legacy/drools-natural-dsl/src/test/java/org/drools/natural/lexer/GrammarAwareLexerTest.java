package org.drools.natural.lexer;

import java.util.ArrayList;
import java.util.List;

import org.drools.natural.grammar.NaturalGrammar;

import junit.framework.TestCase;

public class GrammarAwareLexerTest extends TestCase
{
    
    public void testAddInBrackets() {
        
        MockGrammar grammar = new MockGrammar(new String[]{"likes cheese"});
        
        GrammarAwareLexer lexer = new GrammarAwareLexer(grammar, "bob likes cheese");
        
        assertEquals("bob [likes cheese]", lexer.addInHelperMarkers());

        lexer = new GrammarAwareLexer(grammar, "likes cheese bob likes cheese yeah");
        assertEquals("[likes cheese] bob [likes cheese] yeah", lexer.addInHelperMarkers());
        
        lexer = new GrammarAwareLexer(grammar, "this has no brackets");
        assertEquals("this has no brackets", lexer.addInHelperMarkers());
    }
    
    
    public void testBasicOperation() {
        
        MockGrammar g = new MockGrammar(new String[] {"Age of", "less than"});
        String sample = "Age of bob less than 35";
        
        NaturalSnippetLexer lex = new GrammarAwareLexer(g, sample);
        List rawTokens = lex.getRawTokens().getTokens();        
        
        List expected = new ArrayList();
        expected.add("Age of");
        expected.add("bob");
        expected.add("less than");
        expected.add("35");
        
        assertEquals(expected, rawTokens);        
    }
    
    public void testDontTouchSingleWords() {
        
        MockGrammar g = new MockGrammar(new String[] {"Age of", "is", "young"});
        String sample = "Age of bob is young";
        
        GrammarAwareLexer lex = new GrammarAwareLexer(g, sample);        
        assertEquals("[Age of] bob is young", lex.addInHelperMarkers());
        
    }    
    
    
    

    static class MockGrammar implements NaturalGrammar {
        
        String[] items;
        public MockGrammar(String[] naturalItems) {
            items = naturalItems;
        }

        public String getExpression(String token)
        {
            return null;
        }

        public boolean isTokenInDictionary(String token)
        {
            return false;
        }

        public String[] listNaturalItems()
        {
            return items;
        }

        public boolean ignoreUnknownTokens() {
            return false;
        }

        public boolean delimitersRequired() {
            return false;
        }
        
    }
    
}
