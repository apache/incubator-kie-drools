package org.drools.natural.template;

import java.util.List;

import junit.framework.TestCase;

public class TemplateFactoryTest extends TestCase {

    public void testMake() {
        TemplateFactory factory = new TemplateFactory();
        TemplateContext ctx = factory.buildContext("something {0} going {1} on.");
        assertNotNull(ctx);
    }
    
    public void testLex() {
        TemplateFactory factory = new TemplateFactory();
        List list = factory.lexChunks("one chunk");
        assertEquals(1, list.size());
        assertEquals("one chunk", list.get(0));
        
        
        list = factory.lexChunks("three {0} chunks");
        assertEquals(3, list.size());
        
        assertEquals("three", list.get(0));
        assertEquals("{0}", list.get(1));
        assertEquals("chunks", list.get(2));

        
        list = factory.lexChunks("{42} more '{0}' chunks");
        assertEquals(4, list.size());
        
        assertEquals("{42}", list.get(0));
        assertEquals("more '", list.get(1));
        assertEquals("{0}", list.get(2));
        assertEquals("' chunks", list.get(3));
        
        
    }
    
}
