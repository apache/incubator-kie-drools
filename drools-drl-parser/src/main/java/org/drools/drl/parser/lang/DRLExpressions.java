package org.drools.drl.parser.lang;

import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.ast.descr.BaseDescr;

import java.util.LinkedList;
import java.util.List;

public abstract class DRLExpressions extends Parser {
    public DRLExpressions(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public abstract void setBuildDescr( boolean build );
    public abstract boolean isBuildDescr();

    public abstract void setLeftMostExpr( String value );
    public abstract String getLeftMostExpr();

    public abstract void setHasBindings( boolean value );
    public abstract boolean hasBindings();

    public abstract BaseDescr conditionalOrExpression() throws RecognitionException;

    public abstract ParserHelper getHelper();
    public abstract boolean hasErrors();
    public abstract List<DroolsParserException> getErrors();
    public abstract List<String> getErrorMessages();
    public abstract void enableEditorInterface();
    public abstract void disableEditorInterface();
    public abstract LinkedList<DroolsSentence> getEditorInterface();
}
