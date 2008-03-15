package org.drools.brms.client.modeldriven.brl;

import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * This represents a DSL sentence.
 * @author Michael Neale
 */
public class DSLSentence
    implements
    IPattern,
    IAction {

    public String sentence;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        sentence    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(sentence);
    }
    /**
     * This will strip off any residual "{" stuff...
     */
    public String toString() {
        final char[] chars = this.sentence.toCharArray();
        String result = "";
        for ( int i = 0; i < chars.length; i++ ) {
            final char c = chars[i];
            if ( c != '{' && c != '}' ) {
                result += c;
            }
        }
        return result;
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
