package org.drools.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Collect errors while parsing DRL
 */
public class DRLErrorListener extends BaseErrorListener {

    private final List<DRLParserError> errors = new ArrayList<>();

    public List<DRLParserError> getErrors() {
        return errors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        errors.add(new DRLParserError(line, charPositionInLine, msg));
    }
}
