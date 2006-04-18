package org.drools.decisiontable.parser;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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