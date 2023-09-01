package org.drools.drl.parser.lang;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedTokenException;

/**
 * A mismatched token exception that properly resolves ID tokens
 * into soft keywords
 */
public class DroolsMismatchedTokenException extends MismatchedTokenException {
    private static final long serialVersionUID = -3708332833521751402L;
    private String            tokenText;

    public DroolsMismatchedTokenException() {
        super();
    }

    public DroolsMismatchedTokenException(int expecting,
                                          String tokenText,
                                          IntStream input) {
        super( expecting,
               input );
        this.tokenText = tokenText;
    }

    public String getTokenText() {
        return tokenText;
    }
    
    @Override
    public String toString() {
        return "DroolsMismatchedTokenException("+getUnexpectedType()+"!="+expecting+( tokenText != null ? "["+tokenText+"]" : "" )+")";
    }
}
