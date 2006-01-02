package org.drools.natural.grammar;

import java.util.Properties;

import org.drools.natural.NaturalLanguageException;
import org.drools.natural.grammar.SimpleGrammar;

import junit.framework.TestCase;

public class SimpleGrammarTest extends TestCase
{

    public void testSimplestBehavior()
    {
        SimpleGrammar g = new SimpleGrammar( );
        String token = "[Get something]";
        String expression = "${all-left}.getSomething()";
        g.addToDictionary( token,
                           expression );
        assertTrue( g.isTokenInDictionary( new String( token ) ) );

        assertEquals( expression,
                      g.getExpression( new String( token ) ) );

        try
        {
            g.addToDictionary( new String( token ),
                               "XXX" );
        }
        catch ( NaturalLanguageException e )
        {
            assertNotNull( e.getMessage( ) );
        }

    }

    public void testGetList()
    {
        SimpleGrammar g = new SimpleGrammar( );
        g.addToDictionary( "A",
                           "${all-left}.getSomething()" );
        
        assertEquals( "A",
                      g.listNaturalItems( )[0] );

        g.addToDictionary( "B",
                           "XXX" );
        assertEquals( "B",
                      g.listNaturalItems( )[1] );
        
        g = new SimpleGrammar();
        assertEquals(0, g.listNaturalItems().length);
    }
    

}
