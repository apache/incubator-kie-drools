package org.drools.drl.parser.lang;

import org.antlr.runtime.TokenSource;
import org.drools.drl.parser.DroolsParserException;

import java.util.List;

public interface DRLLexer extends TokenSource {

    List<DroolsParserException> getErrors();
}
