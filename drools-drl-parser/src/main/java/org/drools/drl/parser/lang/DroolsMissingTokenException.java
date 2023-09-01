package org.drools.drl.parser.lang;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.MissingTokenException;

/**
 * A mismatched token exception that properly resolves ID tokens
 * into soft keywords
 */
public class DroolsMissingTokenException extends MissingTokenException {
    private static final long serialVersionUID = -3708332833521751402L;
    private String            tokenText;

    public DroolsMissingTokenException() {
        super();
    }

    public DroolsMissingTokenException(int expecting,
                                       String text,
                                       IntStream input,
                                       Object inserted) {
        super( expecting,
               input,
               inserted );
        this.tokenText = text;
    }


    public String getTokenText() {
        return tokenText;
    }

    @Override
    public String toString() {
        return "Drools"+super.toString();
    }
}
