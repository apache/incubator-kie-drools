package org.drools.lang.dsl.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import junit.framework.TestCase;

public class NLGrammarTest extends TestCase {

    /** Check that it sets up priorities correctly */
    public void testLoadFromProperties() throws Exception {
        NLGrammar grammar = new NLGrammar();
        grammar.addNLItem( new NLMappingItem("znumber 1", "number 1", "*") );
        grammar.addNLItem( new NLMappingItem("bnumber 2", "number 2", "*") );
        grammar.addNLItem( new NLMappingItem("anumber 3", "number 3", "*") );
        
        List list = grammar.getMappings();
        Object[] items = list.toArray();
        
        NLMappingItem item = (NLMappingItem) items[0];
        assertEquals("znumber 1", item.getNaturalTemplate());
        
        item = (NLMappingItem) items[1];        
        assertEquals("bnumber 2", item.getNaturalTemplate());
        assertEquals("*", item.getScope());
        
        item = (NLMappingItem) items[2];        
        assertEquals("anumber 3", item.getNaturalTemplate());
        
        
    }
    
    /** 
     * Should load from an input stream to a properties-like format.
     * It is not strictly properties, as it has naughty spaces etc.
     */
    public void testLoadImproperProperties() throws Exception {

        NLGrammar grammar = new NLGrammar();
        InputStream stream = this.getClass().getResourceAsStream( "test.dsl.properties" );
        
        InputStreamReader reader = new InputStreamReader(stream);
        grammar.load(reader);
        List mappings = grammar.getMappings();
        assertEquals(5, mappings.size());
        
        NLMappingItem[] items = new NLMappingItem[mappings.size()];
        mappings.toArray( items );
        NLMappingItem test = items[0];
        
        assertEquals("*", test.getScope());
        assertEquals("This is something", test.getNaturalTemplate());
        assertEquals("Another thing", test.getTargetTemplate());
        
        test = items[1];
        assertEquals("when", test.getScope());
        assertEquals("This is something for a condition", test.getNaturalTemplate());
        assertEquals("yeah", test.getTargetTemplate());
        
        
        test = items[3];
        assertEquals("then", test.getScope());
        assertEquals("this is also", test.getNaturalTemplate());
        assertEquals("woot", test.getTargetTemplate());
        
        test = items[4];
        assertEquals("*", test.getScope());
        assertEquals("This has spaces", test.getNaturalTemplate());
        assertEquals("yup yep", test.getTargetTemplate());
        
        
        
        
    }
    
    public void testFiltering() {
        NLGrammar g = new NLGrammar();
        g.addNLItem( new NLMappingItem("This is something", "boo", "then") );
        g.addNLItem( new NLMappingItem("This is another", "coo", "then") );
        g.addNLItem( new NLMappingItem("This is another2", "coo2", "*") );
        g.addNLItem( new NLMappingItem("This is another3", "coo3", "when") );
                
        assertEquals( 4, g.getMappings().size() );
        assertEquals( 3, g.getMappings("then").size() );
        assertEquals( 2, g.getMappings("when").size() );
        assertEquals( 1, g.getMappings(null).size() );
        
        
    }
    
    public void testSave() {
        NLGrammar g = new NLGrammar();
        g.addNLItem( new NLMappingItem("This is something", "boo", "then") );
        g.addNLItem( new NLMappingItem("This is another", "coo", "then") );
        g.addNLItem( new NLMappingItem("end", "it", "*") );
        g.setDescription( "my description" );
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        g.save( writer );
        
        String result = out.toString();
        assertEquals("#my description\n[then]This is something=boo\n[then]This is another=coo\nend=it\n", result);
       
        //now load it to double check
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        InputStreamReader reader = new InputStreamReader(in);
        g = new NLGrammar();
        g.load( reader );
        assertEquals(3, g.getMappings().size());
        assertEquals("my description", g.getDescription());
        
    }
    
}
