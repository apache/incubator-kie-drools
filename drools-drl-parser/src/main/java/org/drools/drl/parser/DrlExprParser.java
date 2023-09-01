package org.drools.drl.parser;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.drools.drl.parser.lang.DRLExpressions;
import org.drools.drl.parser.lang.DRLLexer;
import org.drools.drl.parser.lang.ParserHelper;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.kie.internal.builder.conf.LanguageLevelOption;

/**
 * This is a helper class that provides helper methods to parse expressions
 * using both the DRLExpressions parser and the DRLExprTree parser.
 */
public class DrlExprParser {

    private ParserHelper helper = null;

    private final LanguageLevelOption languageLevel;

    public DrlExprParser(LanguageLevelOption languageLevel) {
        this.languageLevel = languageLevel;
    }

    /** Parse an expression from text */
    public ConstraintConnectiveDescr parse( final String text ) {
        ConstraintConnectiveDescr constraint = null;
        try {
            DRLLexer lexer = DRLFactory.getDRLLexer(new ANTLRStringStream(text), languageLevel);
            CommonTokenStream input = new CommonTokenStream( lexer );
            RecognizerSharedState state = new RecognizerSharedState();
            helper = new ParserHelper( input, state, languageLevel );
            DRLExpressions parser = DRLFactory.getDRLExpressions(input, state, helper, languageLevel);
            parser.setBuildDescr( true );
            parser.setLeftMostExpr( null ); // setting initial value just in case
            BaseDescr expr = parser.conditionalOrExpression();
            if ( expr != null && !parser.hasErrors() ) {
                constraint = ConstraintConnectiveDescr.newAnd();
                constraint.addOrMerge( expr );
            }
        } catch ( RecognitionException e ) {
            helper.reportError( e );
        }
        return constraint;
    }
    
    public String getLeftMostExpr() {
        return helper != null ? helper.getLeftMostExpr() : null;
    }

    /**
     * @return true if there were parser errors.
     */
    public boolean hasErrors() {
        return helper != null && helper.hasErrors();
    }

    /**
     * @return a list of errors found while parsing. 
     */
    @SuppressWarnings("unchecked")
    public List<DroolsParserException> getErrors() {
        return helper != null ? helper.getErrors() : Collections.EMPTY_LIST;
    }

}
