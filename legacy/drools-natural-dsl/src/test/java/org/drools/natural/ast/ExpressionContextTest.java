package org.drools.natural.ast;

import junit.framework.TestCase;

import org.drools.natural.grammar.NaturalGrammar;
import org.drools.natural.grammar.SimpleGrammar;
import org.drools.natural.lexer.NaturalSnippetLexer;
import org.drools.natural.lexer.SimpleSnippetLexer;
import org.drools.natural.lexer.RawTokens;

public class ExpressionContextTest extends TestCase
{

    public void testLeftAllAndRight()
    {
        String snip = "[a + b] C [Some method] D";
        String[][] dict = {{"Some method", "calcSomething(${left}, ${right})"}, 
                           {"a + b", "a_PLUS_b"}};
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(snip);
        ExpressionContext ctx = new ExpressionContext(grammarFrom( dict ));
        ctx.addTokens(lex.getRawTokens());
        
        assertEquals("a_PLUS_b calcSomething(C, D)", ctx.render());
    }
    
    public void testMixture()
    {
        String snip = "Today before [date of] 10-Jul-2006 [the end] x";
        String[][] dict = {
                           {"before", "${left}.before(${right})"}, 
                           {"date of", "dateOf(${1})"},
                           {"Today", "(new Date())"},
                           {"the end", "<<.yeah(${right})"} //note the "<<" to stop a space
                           };
        
        NaturalSnippetLexer lex = new SimpleSnippetLexer(snip);
        ExpressionContext ctx = new ExpressionContext(grammarFrom( dict ));
        ctx.addTokens(lex.getRawTokens());
        
        assertEquals("(new Date()).before(dateOf(10-Jul-2006)).yeah(x)", ctx.render());
    }
    
    public void testRightTimesTwoSeperatedByLiteral() {
        String snip = "func1 arg1 || func2 arg2";
        String[][] dict = {
                           {"func1", "func(${right})"}, 
                           {"func2", "func(${1})"},
                           };
        

        NaturalSnippetLexer lex = new SimpleSnippetLexer(snip);
        ExpressionContext ctx = new ExpressionContext(grammarFrom( dict ));
        ctx.addTokens(lex.getRawTokens());
        
        assertEquals("func(arg1) || func(arg2)", ctx.render());
     
    }    
    
    
    NaturalGrammar getEmptyGrammar()
    {
        return new SimpleGrammar( );
    }

    NaturalGrammar grammarFrom(String[][] dictionary)
    {
        SimpleGrammar grammar = new SimpleGrammar( );
        for ( int i = 0; i < dictionary.length; i++ )
        {
            String[] row = dictionary[i];
            grammar.addToDictionary( row[0],
                                     row[1] );
        }
        return grammar;
    }

}
