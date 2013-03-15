package org.drools.lang;

import org.antlr.runtime.TokenSource;
import org.drools.compiler.compiler.DroolsParserException;

import java.util.List;

public interface DRLLexer extends TokenSource {

    List<DroolsParserException> getErrors();
}
