package org.drools.decisiontable.parser;

import java.util.Properties;

import junit.framework.TestCase;

import org.drools.decisiontable.parser.xls.PropertiesSheetListener;

public class PropertiesSheetListenerTest extends TestCase
{

    public void testProperties() {
        PropertiesSheetListener listener = new PropertiesSheetListener();
        listener.startSheet("test");
        
        listener.newRow(0,4);
        
        listener.newCell(0, 0, "");
        
        listener.newCell(0, 1, "key1");
        listener.newCell(0, 2, "value1");

        listener.newRow(1,4);
        listener.newCell(1, 1, "key2");
        listener.newCell(1, 3, "value2");
        
        Properties props = listener.getProperties();
        
        listener.newRow(2, 4);
        listener.newCell(1, 1, "key3");
        
        
        
        assertEquals("value1", props.getProperty("key1"));
        assertEquals("value2", props.getProperty("key2"));
        
        
    }
}
