/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.index.keys;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValueNullTest {

    @Test
    public void testNull01() throws Exception {
        assertEquals( 0,
                      new Value( null ).compareTo( new Value( null ) ) );

    }

    @Test
    public void testNull02() throws Exception {
        assertTrue( new Value( -1 ).compareTo( new Value( 0 ) ) < 0 );
    }

    @Test
    public void testNull03() throws Exception {
        assertTrue( new Value( 0 ).compareTo( new Value( null ) ) > 0 );

    }
}