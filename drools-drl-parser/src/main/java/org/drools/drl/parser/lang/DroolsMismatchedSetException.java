package org.drools.drl.parser.lang;

import java.util.Arrays;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;

/**
 * A mismatched token exception that properly resolves ID tokens
 * into soft keywords
 */
public class DroolsMismatchedSetException extends RecognitionException {
    private static final long serialVersionUID = -3708332833521751402L;
    private String[]          tokenText;

    public DroolsMismatchedSetException() {
        super();
    }

    public DroolsMismatchedSetException( String[] tokenText,
                                         IntStream input) {
        super( input );
        this.tokenText = tokenText;
    }

    public String[] getTokenText() {
        return tokenText;
    }
    
    @Override
    public String toString() {
        return "DroolsMismatchedTokenException("+getUnexpectedType()+"!="+Arrays.asList( tokenText )+")";
    }
}
