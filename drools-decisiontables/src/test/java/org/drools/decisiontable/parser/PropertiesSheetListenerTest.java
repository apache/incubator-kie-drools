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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.decisiontable.parser.xls.PropertiesSheetListener;
import org.drools.template.parser.DataListener;

public class PropertiesSheetListenerTest {

    @Test
    public void testProperties() {
        final PropertiesSheetListener listener = new PropertiesSheetListener();
        listener.startSheet( "test" );

        listener.newRow( 0,
                         4 );

        listener.newCell( 0,
                          0,
                          "", DataListener.NON_MERGED );

        listener.newCell( 0,
                          1,
                          "key1", DataListener.NON_MERGED );
        listener.newCell( 0,
                          2,
                          "value1", DataListener.NON_MERGED );

        listener.newRow( 1,
                         4 );
        listener.newCell( 1,
                          1,
                          "key2", DataListener.NON_MERGED );
        listener.newCell( 1,
                          3,
                          "value2", DataListener.NON_MERGED );

        final Properties props = listener.getProperties();

        listener.newRow( 2,
                         4 );
        listener.newCell( 1,
                          1,
                          "key3", DataListener.NON_MERGED );

        assertEquals( "value1",
                      props.getProperty( "Key1" ) );
        assertEquals( "value2",
                      props.getProperty( "key2" ) );

    }

    @Test
    public void testCaseInsensitive() {
    	Properties map = new PropertiesSheetListener.CaseInsensitiveMap();
    	map.setProperty("x3", "hey");
    	map.setProperty("x4", "wHee");
    	map.setProperty("XXx", "hey2");

    	assertNull(map.getProperty("x"));
    	assertEquals("hey", map.getProperty("x3"));
    	assertEquals("hey", map.getProperty("X3"));
    	assertEquals("wHee", map.getProperty("x4"));
    	assertEquals("hey2", map.getProperty("xxx"));
    	assertEquals("hey2", map.getProperty("XXX"));
    	assertEquals("hey2", map.getProperty("XXx"));

    	assertEquals("Whee2", map.getProperty("x", "Whee2"));

    }

}
