package org.drools.natural.grammar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.natural.NaturalLanguageException;

/**
 * Holds the simple grammar for a natural expressions.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class SimpleGrammar implements Serializable, NaturalGrammar
{


    private static final long serialVersionUID = -6587260296556962105L;    
    private final Map dictionary;
    
    
    public SimpleGrammar() {
        dictionary = new HashMap();
    }
    
    public SimpleGrammar(Properties props) {
        dictionary = props;
    }
    
    public void addToDictionary(String token, String expression) {
        if (dictionary.containsKey(token)) {
            throw new NaturalLanguageException("The token [" + token + "] is already in the dictionary.");
        }
        dictionary.put(token, expression);
    }
    
    /* (non-Javadoc)
     * @see org.drools.natural.grammar.NaturalGrammar#getExpression(java.lang.String)
     */
    public String getExpression(String token) {        
        return (String) dictionary.get(token);
    }
    
    /* (non-Javadoc)
     * @see org.drools.natural.grammar.NaturalGrammar#isTokenInDictionary(java.lang.String)
     */
    public boolean isTokenInDictionary(String token) {
        return dictionary.containsKey(token);
    }

    public String[] listNaturalItems()
    {
        return (String[]) this.dictionary.keySet().toArray(new String[dictionary.size()]);
    }

    
    

}
