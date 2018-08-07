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

package org.drools.verifier.core.relations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OperatorTest {

    @Test
    public void testOperators() throws Exception {
        assertEquals( Operator.EQUALS, Operator.resolve( "==" ) );
        assertEquals( Operator.GREATER_THAN, Operator.resolve( ">" ) );
        assertEquals( Operator.LESS_THAN, Operator.resolve( "<" ) );
        assertEquals( Operator.GREATER_OR_EQUAL, Operator.resolve( ">=" ) );
        assertEquals( Operator.LESS_OR_EQUAL, Operator.resolve( "<=" ) );
        assertEquals( Operator.NOT_EQUALS, Operator.resolve( "!=" ) );
        assertEquals( Operator.IN, Operator.resolve( "in" ) );
        assertEquals( Operator.NOT_IN, Operator.resolve( "not in" ) );
        assertEquals( Operator.AFTER, Operator.resolve( "after" ) );
        assertEquals( Operator.BEFORE, Operator.resolve( "before" ) );
        assertEquals( Operator.COINCIDES, Operator.resolve( "coincides" ) );
        assertEquals( Operator.MATCHES, Operator.resolve( "matches" ) );
        assertEquals( Operator.SOUNDSLIKE, Operator.resolve( "soundslike" ) );

    }
}