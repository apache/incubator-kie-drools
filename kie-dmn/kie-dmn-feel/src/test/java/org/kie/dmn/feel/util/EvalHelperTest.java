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

package org.kie.dmn.feel.util;

import java.math.BigDecimal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.dmn.feel.util.EvalHelper.getBigDecimalOrNull;
import static org.kie.dmn.feel.util.EvalHelper.normalizeVariableName;

public class EvalHelperTest {

    @Test
    public void testNormalizeSpace() {
        assertNull(normalizeVariableName(null));
        assertEquals("", normalizeVariableName(""));
        assertEquals("", normalizeVariableName(" "));
        assertEquals("", normalizeVariableName("\t"));
        assertEquals("", normalizeVariableName("\n"));
        assertEquals("", normalizeVariableName("\u0009"));
        assertEquals("", normalizeVariableName("\u000B"));
        assertEquals("", normalizeVariableName("\u000C"));
        assertEquals("", normalizeVariableName("\u001C"));
        assertEquals("", normalizeVariableName("\u001D"));
        assertEquals("", normalizeVariableName("\u001E"));
        assertEquals("", normalizeVariableName("\u001F"));
        assertEquals("", normalizeVariableName("\f"));
        assertEquals("", normalizeVariableName("\r"));
        assertEquals("a", normalizeVariableName("  a  "));
        assertEquals("a b c", normalizeVariableName("  a  b   c  "));
        assertEquals("a b c", normalizeVariableName("a\t\f\r  b\u000B   c\n"));
        assertEquals("a b c", normalizeVariableName("a\t\f\r  \u00A0\u00A0b\u000B   c\n"));
        assertEquals("b", normalizeVariableName(" b"));
        assertEquals("b", normalizeVariableName("b "));
        assertEquals("ab c", normalizeVariableName("ab c  "));
        assertEquals("a b", normalizeVariableName("a\u00A0b"));
    }
    
    @Test
    public void testGetBigDecimalOrNull() {
        assertEquals(new BigDecimal("10"), getBigDecimalOrNull(10d));
        assertEquals(new BigDecimal("10"), getBigDecimalOrNull(10.00000000D));
        assertEquals(new BigDecimal("10000000000.5"), getBigDecimalOrNull(10000000000.5D));
    }
    
}
