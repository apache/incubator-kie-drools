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

package org.drools.decisiontable.parser;

import java.util.Properties;

import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.decisiontable.parser.xls.PropertiesSheetListener;
import org.drools.decisiontable.parser.xls.PropertiesSheetListener.CaseInsensitiveMap;
import org.drools.template.parser.DataListener;

public class PropertiesSheetListenerTest {

    @Test
    public void testProperties() {
        final PropertiesSheetListener listener = new PropertiesSheetListener();
        listener.startSheet( "test" );

        listener.newRow( 0, 4 );

        listener.newCell( 0, 0,
                          "", DataListener.NON_MERGED );

        listener.newCell( 0, 1,
                          "key1", DataListener.NON_MERGED );
        listener.newCell( 0, 2,
                          "value1", DataListener.NON_MERGED );

        listener.newRow( 1, 4 );
        listener.newCell( 1, 1,
                          "key2", DataListener.NON_MERGED );
        listener.newCell( 1, 3,
                          "value2", DataListener.NON_MERGED );

        final CaseInsensitiveMap props = listener.getProperties();

        listener.newRow( 2, 4 );
        listener.newCell( 1, 1,
                          "key3", DataListener.NON_MERGED );

        assertEquals( "value1", props.getSingleProperty( "Key1" ) );
        assertEquals( "value2", props.getSingleProperty( "key2" ) );
        
    }

    @Test
    public void testCaseInsensitive() {
    	CaseInsensitiveMap map = new PropertiesSheetListener.CaseInsensitiveMap();
    	map.addProperty("x3", new String[]{ "hey", "B2" } );
    	map.addProperty("x4", new String[]{ "wHee", "C3" } );
    	map.addProperty("XXx", new String[]{ "hey2", "D4" } );

    	assertNull( map.getProperty("x") );
    	assertEquals("hey", map.getSingleProperty("x3"));
    	assertEquals("hey", map.getSingleProperty("X3"));
    	assertEquals("wHee", map.getSingleProperty("x4"));
    	assertEquals("hey2", map.getSingleProperty("xxx"));
    	assertEquals("hey2", map.getSingleProperty("XXX"));
    	assertEquals("hey2", map.getSingleProperty("XXx"));

    	assertEquals("Whee2", map.getSingleProperty("x", "Whee2"));

    }

}
