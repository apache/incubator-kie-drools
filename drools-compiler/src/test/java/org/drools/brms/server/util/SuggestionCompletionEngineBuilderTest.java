package org.drools.brms.server.util;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

public class SuggestionCompletionEngineBuilderTest extends TestCase {
    SuggestionCompletionEngineBuilder builder = new SuggestionCompletionEngineBuilder();

    protected void setUp() throws Exception {
        super.setUp();
        this.builder.newCompletionEngine();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddDSLSentence() {
        final String input = "{This} is a {pattern} considered pretty \\{{easy}\\} by most \\{people\\}. What do you {say}?";
        this.builder.addDSLActionSentence( input );
        this.builder.addDSLConditionSentence( "foo bar" );
        final SuggestionCompletionEngine engine = this.builder.getInstance();

        assertEquals( 1,
                      engine.actionDSLSentences.length );
        assertEquals( 1,
                      engine.conditionDSLSentences.length );

    }

}
