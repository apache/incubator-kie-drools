/*
 * Copyright 2015 JBoss Inc
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
 *
 */

package org.drools.core.util;

import org.drools.core.io.impl.ReaderInputStream;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class IoUtilsTest {

    @Test
    public void testReadEmptyStream() throws IOException {
        // DROOLS-971
        byte[] bytes = IoUtils.readBytesFromInputStream( new ReaderInputStream( new StringReader( "" ) ) );
        assertEquals(0, bytes.length);
    }
}
