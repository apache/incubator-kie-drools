package org.drools.natural.template;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;

public class NLGrammarTest extends TestCase {

    /** Check that it sets up priorities correctly */
    public void testLoadFromProperties() throws Exception {
        NLGrammar grammar = new NLGrammar();
        
        
        String data = "number\\ 1=number 1\n" + 
                        "number\\ 2=number 2";
        
        Properties props = new Properties();
        
        ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
        
        props.load(stream);
        
        grammar.loadFromProperties(props);
        
        Collection list = grammar.getMappings();
        Object[] items = list.toArray();
        
        NLMappingItem item = (NLMappingItem) items[0];       
        assertEquals("number 1", item.getNaturalTemplate());
        assertEquals(1, item.getPriority());
        
        item = (NLMappingItem) items[1];        
        assertEquals("number 2", item.getNaturalTemplate());
        assertEquals(2, item.getPriority());
        
        props = new Properties();
        props.setProperty("znumber 1", "number 1");
        props.setProperty("bnumber 2", "number 2");
        props.setProperty("anumber 3", "number 3");
        
        grammar = new NLGrammar();
        grammar.loadFromProperties(props);
        
        list = grammar.getMappings();
        items = list.toArray();
        
        item = (NLMappingItem) items[0];
        assertEquals("znumber 1", item.getNaturalTemplate());
        assertEquals(1, item.getPriority());
        
        item = (NLMappingItem) items[1];        
        assertEquals("bnumber 2", item.getNaturalTemplate());
        assertEquals(2, item.getPriority());
        
        item = (NLMappingItem) items[2];        
        assertEquals("anumber 3", item.getNaturalTemplate());
        assertEquals(3, item.getPriority());
        
    }
    
}
