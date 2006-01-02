package org.drools.natural;

import org.drools.natural.ast.ExpressionContext;
import org.drools.natural.grammar.NaturalGrammar;
import org.drools.natural.lexer.GrammarAwareLexer;
import org.drools.natural.lexer.NaturalSnippetLexer;
import org.drools.natural.lexer.SimpleSnippetLexer;
import org.drools.natural.lexer.RawTokens;

/**
 * This is the main client class for natural language expression compiling.
 * 
 * Pseudo natural language can be supported by infix expressions.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class NaturalLanguageCompiler
{
    private NaturalGrammar grammar;
    
    public NaturalLanguageCompiler(NaturalGrammar g) {
        grammar = g;
    }
    
    
    
    /**
     * Will parse a natural language expression into a compiled version, according to 
     * the current dictionary.
     * @param naturalExpression A natural language expression, using "[multi word]" style notation
     * to provide demarcation of multi word tokens. If a multi word term is not in brackets, then it will
     * be interpreted as 2 seperate tokens.
     *  
     */
    public String compileExpression(String naturalExpression) {
        ExpressionContext ctx = new ExpressionContext(grammar);
        ctx.addTokens(lexicallyAnalyseSimple( naturalExpression ));
        return ctx.render();
    }
    
    /**
     * Will parse a natural language expression into a compiled version, according to 
     * the current dictionary.
     * This version will automatically detect multi word "tokens" from the dictionary.
     * 
     * So you can write: "bob likes cheese and bob can drive" as opposed to:
     * "bob [likes cheese] and bob [can drive]". 
     * 
     * Use this method carefully, as it can be confusing
     * if there are phrases in the dictionary which are used in a string, or you are already using brackets etc.
     * If in doubt, use the other method. 
     * 
     * <b>BEWARE</b> that this can be confusing when you have ambiguous language. It
     * simply will insert brackets to help the lexer. Single word items are left alone.
     * 
     * @param naturalExpression A natural language expression.
     *  
     */
    public String compileNaturalExpression(String naturalExpression) {
        ExpressionContext ctx = new ExpressionContext(grammar);
        ctx.addTokens(lexicallyAnalyseWithGrammar( naturalExpression ));
        return ctx.render();
    }
    

    private RawTokens lexicallyAnalyseSimple(String naturalExpression)
    {
        NaturalSnippetLexer lexer = new SimpleSnippetLexer(naturalExpression);
        return lexer.getRawTokens();
    }
    
    private RawTokens lexicallyAnalyseWithGrammar(String naturalExpression)
    {
        NaturalSnippetLexer lexer = new GrammarAwareLexer(grammar, naturalExpression);
        return lexer.getRawTokens();
    }
    
    
}
