package org.drools.brms.server.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

public class SuggestionCompletionEngineBuilderTest extends TestCase {
    SuggestionCompletionEngineBuilder builder = new SuggestionCompletionEngineBuilder();

    protected void setUp() throws Exception {
        super.setUp();
        builder.newCompletionEngine();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddDSLSentence() {
        String input = "{This} is a {pattern} considered pretty \\{{easy}\\} by most \\{people\\}. What do you {say}?";
        builder.addDSLActionSentence(  input );
        builder.addDSLConditionSentence( "foo bar" );
        SuggestionCompletionEngine engine = builder.getInstance();

        assertEquals( 1,
                      engine.actionDSLSentences.length );
        assertEquals( 1,
                      engine.conditionDSLSentences.length );
        


    }

}
