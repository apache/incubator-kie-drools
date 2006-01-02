package org.drools.natural.lexer;

import org.apache.commons.lang.StringUtils;
import org.drools.natural.grammar.NaturalGrammar;

/**
 * This lexer makes use of the simple lexer, but first uses the grammar/dictionary to try and lex the string.
 * Works best when the dictionary items are single words.
 * 
 * This means that in a lot of cases you can get away without using the square brackets to group words into
 * a single token.
 * 
 * This is essentially a decorated helper version of the simple lexer.
 * 
 * TODO: This really needs to be aware of context, rather then just string replacing.
 * Currently there are limitations when you are mixing the usage of brackets. This will agressively
 * replace anything it finds with stuff from the dictionary, regardless if it is inside a demarcated 
 * token or not.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class GrammarAwareLexer
    implements
    NaturalSnippetLexer
{

    private NaturalGrammar grammar;
    private String snippet;
    

    public GrammarAwareLexer(NaturalGrammar g, String naturalSnippet) {
        grammar = g;
        snippet = naturalSnippet;
    }

    public RawTokens getRawTokens()
    {
        //once we add in the square brackets, we can then use the simple lexer.
        SimpleSnippetLexer simpleLexer = new SimpleSnippetLexer(addInHelperMarkers());
        return simpleLexer.getRawTokens();
    }

    /**
     * This will add in square brackets, where there weren't ones before. 
     */
    String addInHelperMarkers()
    {       
        String result = snippet;
        String[] items = grammar.listNaturalItems();
        
        for ( int i = 0; i < items.length; i++ )
        {
            if (hasSpaces( items, i )) {
                result = StringUtils.replace(result, items[i], "[" + items[i] + "]");
            }
        }
        return result;
    }

    private boolean hasSpaces(String[] items,
                              int i)
    {
        return !(items[i].indexOf(' ') == -1);
    }
    
    

    
}
