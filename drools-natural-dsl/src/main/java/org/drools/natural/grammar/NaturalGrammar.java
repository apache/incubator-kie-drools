package org.drools.natural.grammar;

import java.util.List;

/**
 * All Grammars must implement this simple interface. SimpleGrammar is the default implementation.
 * 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public interface NaturalGrammar
{

    /**
     * Return an expression matching that token. 
     */
    public abstract String getExpression(String token);

    public abstract boolean isTokenInDictionary(String token);

    /**
     * @return a list of the natural language items.
     */
    public abstract String[] listNaturalItems();
    
}