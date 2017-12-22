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

package org.drools.modelcompiler.builder.generator;

import org.junit.Test;

import static org.drools.modelcompiler.util.StringUtil.toId;
import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    @Test
    public void test() {
        assertEquals("__123stella", toId("123stella") );
        assertEquals("__123__stella", toId("123_stella") );
        assertEquals("__stella", toId("_stella") );
        assertEquals("__stella__123", toId("_stella_123") );
        assertEquals("my_32stella", toId("my stella") );
        assertEquals("$tella", toId("$tella") );
        assertEquals("$tella_40123_41", toId("$tella(123)") );
        assertEquals("my_45stella", toId("my-stella") );
        assertEquals("my_43stella", toId("my+stella") );
        assertEquals("o_39stella", toId("o'stella") );
        assertEquals("stella_38you", toId("stella&you") );
        assertEquals("stella_32_38_32Co_46", toId("stella & Co.") );
    }
}
