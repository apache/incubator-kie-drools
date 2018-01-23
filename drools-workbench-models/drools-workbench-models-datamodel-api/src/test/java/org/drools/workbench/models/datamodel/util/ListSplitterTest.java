/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.workbench.models.datamodel.util;

import org.drools.workbench.models.datamodel.util.ListSplitter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ListSplitterTest {

    @Test
    public void basicABC() throws Exception {
        final String[] split = ListSplitter.split("a,b,c");
        assertEquals(3, split.length);
        assertEquals("a", split[0]);
        assertEquals("b", split[1]);
        assertEquals("c", split[2]);
    }

    @Test
    public void keepSpacesABC() throws Exception {
        final String[] split = ListSplitter.split("a, b ,c");
        assertEquals(3, split.length);
        assertEquals("a", split[0]);
        assertEquals(" b ", split[1]);
        assertEquals("c", split[2]);
    }

    @Test
    public void specialABC() throws Exception {
        final String[] split = ListSplitter.split("'a','b','c'");
        assertEquals(3, split.length);
        assertEquals("a", split[0]);
        assertEquals("b", split[1]);
        assertEquals("c", split[2]);
    }

    @Test
    public void specialABCSpaces() throws Exception {
        final String[] split = ListSplitter.split(" 'a' , 'b' , 'c' ");
        assertEquals(3, split.length);
        assertEquals("a", split[0]);
        assertEquals("b", split[1]);
        assertEquals("c", split[2]);
    }

    @Test
    public void basicCharacters() throws Exception {
        final String[] split = ListSplitter.split("',!,%,)");
        assertEquals(4, split.length);
        assertEquals("'", split[0]);
        assertEquals("!", split[1]);
        assertEquals("%", split[2]);
        assertEquals(")", split[3]);
    }

    @Test
    public void splitItemsWithCommas() throws Exception {
        final String[] split = ListSplitter.split("'One sentence.','Another one, but with comma.','Third one. I'll make things, if possible, even more complicated.'");
        assertEquals(3, split.length);
        assertEquals("One sentence.", split[0]);
        assertEquals("Another one, but with comma.", split[1]);
        assertEquals("Third one. I'll make things, if possible, even more complicated.", split[2]);
    }

    @Test
    public void changeQuoteCharacter() throws Exception {
        final String[] split = ListSplitter.split("\"",
                                                  true,
                                                  "\"Helsinki, Finland\", Boston");
        assertEquals(2, split.length);
        assertEquals("Helsinki, Finland", split[0]);
        assertEquals("Boston", split[1]);
    }

    @Test
    public void changeQuoteCharacterSkipTrimming() throws Exception {
        final String[] split = ListSplitter.split("\"",
                                                  false,
                                                  "\"Helsinki, Finland\", Boston");
        assertEquals(2, split.length);
        assertEquals("Helsinki, Finland", split[0]);
        assertEquals(" Boston", split[1]);
    }
}