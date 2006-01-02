package org.drools.natural.lexer;

import org.drools.natural.NaturalLanguageException;

/**
 * This represents an error in recognising tokens.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class LexerException extends NaturalLanguageException {

    public LexerException(String message)
    {
        super(message);
    }	
	
}
