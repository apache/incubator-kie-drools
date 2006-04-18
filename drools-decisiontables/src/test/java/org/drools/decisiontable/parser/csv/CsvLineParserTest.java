package org.drools.decisiontable.parser.csv;
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





import java.util.List;

import junit.framework.TestCase;

public class CsvLineParserTest extends TestCase
{

    public void testSimpleLineParse() {
        CsvLineParser parser = new CsvLineParser();
        String s = "a,b,c";
        List list = parser.parse(s);
        assertEquals(3, list.size());
        
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));        
    }
    
    public void testLineParse() {
        CsvLineParser parser = new CsvLineParser();
        String s = "a,\"b\",c";
        List list = parser.parse(s);
        assertEquals(3, list.size());
        
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));        
    }
    
    public void testDoubleQuotes() {
        CsvLineParser parser = new CsvLineParser();
        String s = "a,\"\"\"b\"\"\",c";
        List list = parser.parse(s);
        assertEquals(3, list.size());
        
        assertEquals("a", list.get(0));
        assertEquals("\"b\"", list.get(1));
        assertEquals("c", list.get(2));        
    }
    
    
    

}