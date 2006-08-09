package org.drools.natural.lexer;

import java.util.List;

public class RawTokens
{
    private char[] inputSnippet;
    private List    rawTokens;
    
    public RawTokens(char[] input, List tokens) {
        this.inputSnippet = input;
        this.rawTokens = tokens;
    }
    
    public String toString() {
        return new String(inputSnippet);
    }
    
    public List getTokens() {
        return rawTokens;
    }

}
