package org.drools.drl.parser.lang;

import org.antlr.runtime.RecognitionException;

public class GeneralParseException extends RecognitionException {

    private static final long serialVersionUID = 510l;
    private String            message;

    public GeneralParseException(final String message,
                                 final int line) {
        this.message = message;
        this.line = line;
    }

    public String getMessage() {
        return this.message;
    }

}
