package org.drools.drl.parser.lang;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognizerSharedState;

public abstract class AbstractDRLLexer extends Lexer implements DRLLexer {
    public AbstractDRLLexer() { }

    public AbstractDRLLexer(CharStream input) {
        super(input);
    }

    public AbstractDRLLexer(CharStream input, RecognizerSharedState state) {
        super(input, state);
    }
}
