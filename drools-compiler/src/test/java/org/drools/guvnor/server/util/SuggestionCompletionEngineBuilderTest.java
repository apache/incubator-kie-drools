package org.drools.guvnor.server.util;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.lang.dsl.AbstractDSLMappingEntry;
import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLMappingEntry.Section;

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

        assertEquals( 1, engine.actionDSLSentences.length );
        assertEquals( 1, engine.conditionDSLSentences.length );

    }

    public void testAddSentenceMultipleTypes() {
    	this.builder.addDSLMapping(new DSLMap(DSLMappingEntry.CONDITION, "cond"));
    	this.builder.addDSLMapping(new DSLMap(DSLMappingEntry.CONSEQUENCE, "cons"));
    	this.builder.addDSLMapping(new DSLMap(DSLMappingEntry.ANY, "any"));
    	this.builder.addDSLMapping(new DSLMap(DSLMappingEntry.KEYWORD, "key"));

        final SuggestionCompletionEngine engine = this.builder.getInstance();

        assertEquals( 1, engine.actionDSLSentences.length );
        assertEquals( 1, engine.conditionDSLSentences.length );
        assertEquals( 1, engine.keywordDSLItems.length );
        assertEquals( 1, engine.anyScopeDSLItems.length );


        assertEquals("cond", engine.conditionDSLSentences[0].sentence);


    }

    class DSLMap extends AbstractDSLMappingEntry {
    	DSLMap(Section sec, String sentence) {
    		this.section = sec;
    		this.sentence = sentence;
    	}
    }

}
