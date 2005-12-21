package org.drools.natural;

/**
 * This exception should only be used when showing a early error with the natural language format.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class NaturalLanguageException extends RuntimeException
{

    public NaturalLanguageException(String message)
    {
        super(message);
    }

}
