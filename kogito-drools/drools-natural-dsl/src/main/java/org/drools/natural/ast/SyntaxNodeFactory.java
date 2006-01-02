package org.drools.natural.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.natural.NaturalLanguageException;
import org.drools.natural.grammar.NaturalGrammar;
import org.drools.natural.lexer.StringInterpolator;

/**
 * This factory will take a token, and work out what node type it is, and 
 * build it. The nodes that are built are flat. The nodes themselves
 * know how to arrange themselves into a syntax tree.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 */
public class SyntaxNodeFactory
{


    private NaturalGrammar       grammar;

    public SyntaxNodeFactory(NaturalGrammar g)
    {
        this.grammar = g;
    }

    public BaseSyntaxNode getNodeForSnippet(String token)
    {
        if ( !grammar.isTokenInDictionary( token ) )
        {
            return new LiteralNode( token );
        }

        String exp = grammar.getExpression( token );
        StringInterpolator in = new StringInterpolator( exp );

        List vars = in.extractVariableNames( );

        checkForMixedVariableTypes( token,
                                    vars );

        BaseSyntaxNode result = null;
        
        result = tryLeftRightStyle( token,
                                    exp,
                                    vars,
                                    result ); 

        if (result == null) {
            result = tryNumberedStyle( token,
                                       exp,
                                       vars,
                                       result );
        }
        
        if (result == null) {
            throw new NaturalLanguageException("I can't understand the expression [" + 
                                               token + "]");            
        } else {
            return result;
        }

        
        
    }

    private BaseSyntaxNode tryNumberedStyle(String token,
                                                String exp,
                                                List vars,
                                                BaseSyntaxNode result)
    {
        List numbers = makeSortedNumberList( vars );

        if ( lowestNumber( numbers ) == 1 )
        {
            result = new RightInfix( token,
                                      exp,
                                      highestNumber( numbers ) );
        }
        if ( highestNumber( numbers ) == -1 )
        {
            result = new LeftInfix( token,
                                     exp,
                                     lowestNumber( numbers ) );
        }

        if ( lowestNumber( numbers ) < 0 && highestNumber( numbers ) > 0 )
        {
            result = new LeftRightInfix( token,
                                      exp,
                                      lowestNumber( numbers ),
                                      highestNumber( numbers ) );
        }
        return result;
    }

    private BaseSyntaxNode tryLeftRightStyle(String token,
                                                 String exp,
                                                 List vars,
                                                 BaseSyntaxNode result)
    {
        if ( vars.size( ) == 0 )
        {
            result = new SubstitutionNode( token,
                                         exp );
        } else if ( vars.contains( InfixNode.LEFT ) && vars.contains( InfixNode.RIGHT ) )
        {
            result = new LeftRightInfix( token,
                                      exp,
                                      1,
                                      1 );
        } else if ( vars.contains( InfixNode.LEFT ) )
        {
            result = new LeftInfix( token,
                                     exp,
                                     1 );
        } else if ( vars.contains( InfixNode.RIGHT ) )
        {
            result = new RightInfix( token,
                                      exp,
                                      1 );
        }
        return result;
    }

    private void checkForMixedVariableTypes(String token,
                                            List vars)
    {
        if ((vars.contains(InfixNode.LEFT) || vars.contains(InfixNode.RIGHT)) 
                &&
                (vars.contains("-1") || vars.contains("1"))) 
        {
            throw new NaturalLanguageException("The following expression: ["
                                               + token + "] mixes ${left} and ${1}" +
                                                    " style notation. Please stick to one type.");
        }
    }

    
    
    int highestNumber(List numbers)
    {
        return ((Integer) numbers.get( numbers.size( ) - 1 )).intValue( );
    }

    int lowestNumber(List numbers)
    {
        return ((Integer) numbers.get( 0 )).intValue( );
    }

    private List makeSortedNumberList(List vars)
    {
        List numberList = new ArrayList( );
        for ( Iterator iter = vars.iterator( ); iter.hasNext( ); )
        {
            String var = (String) iter.next( );
            int num = Integer.parseInt( var );
            numberList.add( new Integer( num ) );
        }
        Collections.sort( numberList );
        return numberList;
    }

}
