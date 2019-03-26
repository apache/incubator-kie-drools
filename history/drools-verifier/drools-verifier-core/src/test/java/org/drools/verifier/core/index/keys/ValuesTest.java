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
import static org.junit.Assert.assertFalse;

public class ValuesTest {


    @Test
    public void testNull() throws
                           Exception {
        final Values<Comparable> values = new Values<>();
        values.add( null );

        assertFalse( values.isEmpty() );
        assertEquals( null,
                      values.iterator()
                              .next() );
    }

    @Test
    public void testChanges() throws
                              Exception {
        final Values a = new Values();
        final Values b = new Values();

        assertFalse( a.isThereChanges( b ) );
    }
}