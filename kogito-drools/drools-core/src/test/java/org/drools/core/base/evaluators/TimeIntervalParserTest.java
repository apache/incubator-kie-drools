/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.evaluators;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimeIntervalParserTest {

    @Test
    public void testParse() {
        String input = "2d10h49m10s789ms";
        long expected = 211750789;
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 1, result.length );
        assertEquals( expected, result[0] );
    }

    @Test
    public void testParse2() {
        String input = "10h49m789ms";
        long expected = 10 * 3600000 + 49 * 60000 + 789;
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 1, result.length );
        assertEquals( expected, result[0] );
    }

    @Test
    public void testParse3() {
        // ms are optional
        String input = " 10h49m789 , 12h ";
        long expected1 = 10 * 3600000 + 49 * 60000 + 789;
        long expected2 = 12 * 3600000;
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 2, result.length );
        assertEquals( expected1, result[0] );
        assertEquals( expected2, result[1] );
    }

    @Test
    public void testParse4() {
        // raw ms without the unit declared
        String input = " 15957, 3500000 ";
        long expected1 = 15957;
        long expected2 = 3500000;
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 2, result.length );
        assertEquals( expected1, result[0] );
        assertEquals( expected2, result[1] );
    }

    @Test
    public void testParse5() {
        // empty input
        String input = "";
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 0, result.length );
    }
    
    @Test
    public void testParse6() {
        // null input
        String input = null;
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 0, result.length );
    }
    
    @Test
    public void testParse7() {
        // empty input
        String input = "  ";
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 0, result.length );
    }
    
    @Test
    public void testParse8() {
        // raw ms without the unit declared
        String input = " -15957, 3500000 ";
        long expected1 = -15957;
        long expected2 = 3500000;
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 2, result.length );
        assertEquals( expected1, result[0] );
        assertEquals( expected2, result[1] );
    }

    @Test
    public void testParse9() {
        // ms are optional
        String input = " -10h49m789 , -8h ";
        long expected1 = -( 10 * 3600000 + 49 * 60000 + 789 );
        long expected2 = -( 8 * 3600000 );
        long[] result = TimeIntervalParser.parse( input );
        assertEquals( 2, result.length );
        assertEquals( expected1, result[0] );
        assertEquals( expected2, result[1] );
    }

}
