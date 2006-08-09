package org.drools.natural.grammar;


/**
 * All Grammars must implement this simple interface. 
 * SimpleGrammar is the default implementation.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public interface NaturalGrammar
{
    
    public static final String IGNORE_UNKNOWN_TOKENS = "ignore.unknown";
    public static final String REQUIRE_DELIMITERS = "require.delimiters";

    
    /**
     * Return an expression matching that token. 
     */
    public abstract String getExpression(String token);

    public abstract boolean isTokenInDictionary(String token);

    /**
     * @return a list of the natural language items.
     */
    public abstract String[] listNaturalItems();
    
    /**
     * @return True if unknown tokens are to be ignored for
     * this grammar configuration.
     * Unknown does not include arguments to infix operators of course.
     */
    public boolean ignoreUnknownTokens();
    
    /**
     * @return True if tokens containing spaces must be delimited,
     * to help out the lexer.
     */
    public boolean delimitersRequired();
    
    
}