package org.drools.guvnor.client.modeldriven.brl;


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
        final char[] chars = this.sentence.toCharArray();
        boolean inBracket = false;
        boolean inBracketAfterColon = false;

        String result = "";
        for ( int i = 0; i < chars.length; i++ ) {
            final char c = chars[i];
            if ( c != '{' && c != '}' && c != ':' && !inBracketAfterColon ) {
                result += c;
            } else if ( c == '{' ) {
                inBracket = true;
            } else if ( c == '}' ) {
                inBracket = false;
                inBracketAfterColon = false;
            } else if ( c == ':' && inBracket ) {
                inBracketAfterColon = true;
            } else if ( c == ':' && !inBracket ) {
                result += c;
            }
        }
        return result.replace( "\\n",
                               "\n" );
    }

    /**
     * This is used by the GUI when adding a sentence to LHS or RHS.
     * @return
     */
    public DSLSentence copy() {
        final DSLSentence newOne = new DSLSentence();
        newOne.sentence = this.sentence;
        return newOne;
    }
}
