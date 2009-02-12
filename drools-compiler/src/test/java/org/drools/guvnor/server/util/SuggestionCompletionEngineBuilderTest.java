package org.drools.guvnor.server.util;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.lang.dsl.AbstractDSLMappingEntry;
import org.drools.lang.dsl.DSLMappingEntry;
import org.jmock.Expectations;
import org.jmock.Mockery;

public class SuggestionCompletionEngineBuilderTest extends TestCase {
    SuggestionCompletionEngineBuilder builder = new SuggestionCompletionEngineBuilder();
    Mockery context = new Mockery();

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
        final DSLMappingEntry mapping1 = context.mock(DSLMappingEntry.class, "mapping1");
        final DSLMappingEntry mapping2 = context.mock(DSLMappingEntry.class, "mapping2");
        final DSLMappingEntry mapping3 = context.mock(DSLMappingEntry.class, "mapping3");
        final DSLMappingEntry mapping4 = context.mock(DSLMappingEntry.class, "mapping4");
        
        context.checking( new Expectations() {{
            // setting expectations for entry1
            allowing(mapping1).getSection(); will(returnValue(DSLMappingEntry.CONDITION ));
            allowing(mapping1).getMappingKey(); will(returnValue("cond"));
            
            // setting expectations for entry2
            allowing(mapping2).getSection(); will(returnValue(DSLMappingEntry.CONSEQUENCE ));
            allowing(mapping2).getMappingKey(); will(returnValue("cons"));

            // setting expectations for entry3
            allowing(mapping3).getSection(); will(returnValue(DSLMappingEntry.ANY ));
            allowing(mapping3).getMappingKey(); will(returnValue("any"));
            
            // setting expectations for entry4
            allowing(mapping4).getSection(); will(returnValue(DSLMappingEntry.KEYWORD ));
            allowing(mapping4).getMappingKey(); will(returnValue("key"));
        }}
        );
        
        this.builder.addDSLMapping(mapping1);
        this.builder.addDSLMapping(mapping2);
        this.builder.addDSLMapping(mapping3);
        this.builder.addDSLMapping(mapping4);

        final SuggestionCompletionEngine engine = this.builder.getInstance();

        assertEquals( 1, engine.actionDSLSentences.length );
        assertEquals( 1, engine.conditionDSLSentences.length );
        assertEquals( 1, engine.keywordDSLItems.length );
        assertEquals( 1, engine.anyScopeDSLItems.length );

        assertEquals("cond", engine.conditionDSLSentences[0].sentence);
    }

}
