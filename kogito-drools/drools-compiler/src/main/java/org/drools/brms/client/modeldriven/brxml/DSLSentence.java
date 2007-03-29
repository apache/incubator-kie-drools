package org.drools.brms.client.modeldriven.brxml;


/**
 * This represents a DSL sentence.
 * @author Michael Neale
 */
public class DSLSentence
    implements
    IPattern,
    IAction {

    public String sentence;
    
    /**
     * This will strip off any residual "{" stuff...
     */
    public String toString() {
        char[] chars = sentence.toCharArray();
        String result = "";
        for ( int i = 0; i < chars.length; i++ ) {
            char c = chars[i];
            if (c != '{' && c != '}') {
                result += c;
            }
        }
        return result;
    }
    
    public DSLSentence copy() {
        DSLSentence newOne = new DSLSentence();
        newOne.sentence = this.sentence;
        return newOne;
    }
}
