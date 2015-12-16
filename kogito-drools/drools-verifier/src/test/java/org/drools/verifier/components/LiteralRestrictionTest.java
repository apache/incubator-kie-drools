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

package org.drools.verifier.components;

import org.drools.verifier.VerifierComponentMockFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LiteralRestrictionTest {

    @Test
    public void testSetValue() {

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalBooleanRestriction = LiteralRestriction.createRestriction(pattern1,
                                                                                            "true");

        assertTrue(literalBooleanRestriction instanceof BooleanRestriction);

        BooleanRestriction booleanRestriction = (BooleanRestriction) literalBooleanRestriction;

        assertEquals(Field.BOOLEAN,
                     booleanRestriction.getValueType());
        assertEquals(true,
                     booleanRestriction.getValue());

        LiteralRestriction intLiteralRestriction = LiteralRestriction.createRestriction(pattern1,
                                                                                        "1");
        assertTrue(intLiteralRestriction instanceof NumberRestriction);
        NumberRestriction intRestriction = (NumberRestriction) intLiteralRestriction;

        assertTrue(intRestriction.isInt());
        assertEquals(Field.INT,
                     intRestriction.getValueType());
        assertEquals(1,
                     intRestriction.getValue());

        LiteralRestriction doubleLiteralRestriction = LiteralRestriction.createRestriction(pattern1,
                                                                                           "1.0");
        assertTrue(doubleLiteralRestriction instanceof NumberRestriction);

        NumberRestriction doubleRestriction = (NumberRestriction) doubleLiteralRestriction;

        assertEquals(Field.DOUBLE,
                     doubleRestriction.getValueType());
        assertEquals(1.0,
                     doubleRestriction.getValue());

        LiteralRestriction dateLiteralRestriction = LiteralRestriction.createRestriction(pattern1,
                                                                                         "11-jan-2008");

        assertTrue(dateLiteralRestriction instanceof DateRestriction);

        DateRestriction dateRestriction = (DateRestriction) dateLiteralRestriction;

        assertEquals(Field.DATE,
                     dateRestriction.getValueType());

        LiteralRestriction stringRestriction = LiteralRestriction.createRestriction(pattern1,
                                                                                    "test test");

        assertEquals(Field.STRING,
                     stringRestriction.getValueType());
        assertEquals("test test",
                     stringRestriction.getValueAsString());

        LiteralRestriction nullRestriction = LiteralRestriction.createRestriction(pattern1,
                                                                                  null);

        assertTrue(nullRestriction instanceof StringRestriction);

        assertEquals(Field.UNKNOWN,
                     nullRestriction.getValueType());
        assertEquals("",
                     nullRestriction.getValueAsString());
    }
}
