package org.drools.rule.builder.dialect.mvel;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.antlr.runtime.RecognitionException;

public class MVELExprAnalyzerTest extends TestCase {

    private MVELExprAnalyzer analyzer;

    protected void setUp() throws Exception {
        analyzer = new MVELExprAnalyzer();
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void FIXME_testGetExpressionIdentifiers() {
        try {
            String expression = "order.id == 10";
            List[] identifiers = analyzer.analyzeExpression( expression, new Set[0] );
            
            assertEquals( 1, identifiers.length );
            assertEquals( 1, identifiers[0].size() );
            assertEquals( "order", identifiers[0].get( 0 ));
        } catch ( RecognitionException e ) {
            e.printStackTrace();
            fail( "Unexpected exception: "+e.getMessage());
        }
    }

}
