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

import static org.junit.Assert.assertTrue;

public class ValueTest {


    @Test
    public void testIntegerVSInteger() throws Exception {
        final Value nroZero = new Value( 0 );
        final Value nroOne = new Value( 1 );

        assertTrue( nroZero.compareTo( nroOne ) < 0 );
        assertTrue( nroOne.compareTo( nroZero ) > 0 );
    }

    @Test
    public void testStringVSInteger() throws Exception {
        final Value hello = new Value( "hello" );
        final Value nroOne = new Value( 1 );

        assertTrue( hello.compareTo( nroOne ) > 0 );
        assertTrue( nroOne.compareTo( hello ) < 0 );
    }

    @Test
    public void testStringVSIntegerString() throws Exception {
        final Value hello = new Value( "hello" );
        final Value nroOne = new Value( "1" );

        assertTrue( hello.compareTo( nroOne ) > 0 );
        assertTrue( nroOne.compareTo( hello ) < 0 );
    }

    @Test
    public void testStringVSString() throws Exception {
        final Value a = new Value( "a" );
        final Value b = new Value( "b" );

        assertTrue( a.compareTo( b ) < 0 );
        assertTrue( b.compareTo( a ) > 0 );
    }
}