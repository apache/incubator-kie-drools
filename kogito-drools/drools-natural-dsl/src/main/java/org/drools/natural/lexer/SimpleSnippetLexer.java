package org.drools.natural.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** 
 * Lowest level snippet lexing for natural language.
 * 
 * Supports "[" and "]" to group words to make one token.
 * Text between double quotes is taken as literal. 
 * Nexted square brackets are literal.
 * 
 * Validates that brackets and quotes are balanced. 
 * 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class SimpleSnippetLexer implements NaturalSnippetLexer
{

    private final char[] snippet;
    private List         tokens;
    private StringBuffer currentToken;
    private Stack        bracketStack   = new Stack( );
    private boolean      inDoubleQuotes = false;

    public SimpleSnippetLexer(String snippet)
    {

        this.snippet = snippet.toCharArray( );
        this.tokens = new ArrayList( );
        this.currentToken = new StringBuffer( );
    }

    /* (non-Javadoc)
     * @see org.drools.natural.lexer.NaturalSnippetLexer#getRawTokens()
     */
    public RawTokens getRawTokens()
    {

        for ( int i = 0; i < snippet.length; i++ )
        {
            switch ( snippet[i] )
            {
            case ' ' :
                breakBySpace( i );
                break;
            case '[' :
                breakByOpenBracket( i );
                break;
            case ']' :
                breakByCloseBracket( i );
                break;
            case '"' :
                breakByQuotes( i );
                break;
            case '\n' :
            case '\r' :
            case '\t' :
                ignoreChar( i );
                break;
            default :
                continueLexing( i );
                break;
            }

        }
        newToken( );
        checkSquareBracketsBalancedAtEnd( );
        checkClosedQuotesAndEnd( );
        return new RawTokens(snippet, tokens);

    }

    private void checkSquareBracketsBalancedAtEnd()
    {
        if ( bracketStack.size( ) > 0 )
        {
            throw new LexerException( "An unbalanced number of square brackets was found in the line: '" 
                                                + new String( snippet ) + "'. Please help me get my balance back." );
        }
    }

    private void checkClosedQuotesAndEnd()
    {
        if ( inDoubleQuotes )
        {
            throw new LexerException( "An double quote was unclosed in the line: '" + new String( snippet ) 
                                                + "'. I just hate leaving things hanging..." );

        }
    }

    private void ignoreChar(int i)
    {
    }

    private void breakByQuotes(int pos)
    {
        inDoubleQuotes = !inDoubleQuotes;
        appendCurrentChar( pos );
    }

    private void continueLexing(int pos)
    {
        appendCurrentChar( pos );
    }

    private void breakByCloseBracket(int pos)
    {
        bracketStack.pop( );
        if ( bracketStack.size( ) == 0 )
        {
            newToken( );
        }
        else
        {
            appendCurrentChar( pos );
        }
    }

    private StringBuffer appendCurrentChar(int pos)
    {
        return currentToken.append( snippet[pos] );
    }

    private void breakByOpenBracket(int pos)
    {
        if ( bracketStack.size( ) != 0 )
        {
            appendCurrentChar( pos );
        }
        else
        {
            newToken( );
        }
        bracketStack.push( "[" );

    }

    private void breakBySpace(int pos)
    {
        if (inDoubleQuotes) {
            appendCurrentChar( pos );
        } else if ( bracketStack.size( ) > 0)
        {
            if (previousIsNotSpace(pos)) {
                appendCurrentChar( pos );
            }
        }
        else
        {
            newToken( );
        }

    }

    private boolean previousIsNotSpace(int pos){
        
        if (pos == 0) return true;
        if (snippet[pos - 1] == ' ') {
            return false;
        } else {
            return true;
        }
    }

    private void newToken()
    {
        String token = currentToken.toString( ).trim( );
        if ( !"".equals( token ) )
        {
            tokens.add( currentToken.toString( ) );
            currentToken = new StringBuffer( );
        }
    }
}
