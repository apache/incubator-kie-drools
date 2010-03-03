package org.drools.guvnor.server.util;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.lang.dsl.AbstractDSLMappingEntry;
import org.drools.lang.dsl.DSLMappingEntry;
import static org.mockito.Mockito.*;

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
        final DSLMappingEntry mapping1 = mock(DSLMappingEntry.class, "mapping1");
        final DSLMappingEntry mapping2 = mock(DSLMappingEntry.class, "mapping2");
        final DSLMappingEntry mapping3 = mock(DSLMappingEntry.class, "mapping3");
        final DSLMappingEntry mapping4 = mock(DSLMappingEntry.class, "mapping4");
        
        
        // setting expectations for entry1
        when(mapping1.getSection()).thenReturn(DSLMappingEntry.CONDITION );
        when(mapping1.getMappingKey()).thenReturn("cond");
        
        // setting expectations for entry2
        when(mapping2.getSection()).thenReturn(DSLMappingEntry.CONSEQUENCE );
        when(mapping2.getMappingKey()).thenReturn("cons");

        // setting expectations for entry3
        when(mapping3.getSection()).thenReturn(DSLMappingEntry.ANY );
        when(mapping3.getMappingKey()).thenReturn("any");
        
        // setting expectations for entry4
        when(mapping4.getSection()).thenReturn(DSLMappingEntry.KEYWORD );
        when(mapping4.getMappingKey()).thenReturn("key");
        
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
