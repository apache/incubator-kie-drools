package org.drools.rule.builder.dialect.mvel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MVELExprAnalyzerTest {

    private MVELExprAnalyzer analyzer;

    @Before
    public void setUp() throws Exception {
        analyzer = new MVELExprAnalyzer();
    }

    @Test
    public void testGetExpressionIdentifiers() {
//        try {
//            String expression = "order.id == 10";
//            List[] identifiers = analyzer.analyzeExpression( expression, new Set[0] );
//            
//            assertEquals( 1, identifiers.length );
//            assertEquals( 1, identifiers[0].size() );
//            assertEquals( "order", identifiers[0].get( 0 ));
//        } catch ( RecognitionException e ) {
//            e.printStackTrace();
//            fail( "Unexpected exception: "+e.getMessage());
//        }
    }

}
