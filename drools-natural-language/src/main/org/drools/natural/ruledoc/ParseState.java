package org.drools.natural.ruledoc;

/**
 * This is the super class of all document parse states.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public abstract class ParseState
{
    abstract void parseChunk(String text);

}
