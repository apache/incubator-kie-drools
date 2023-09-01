/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.components;

import org.drools.verifier.VerifierComponentMockFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LiteralRestrictionTest {

    @Test
    void testSetValue() {

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction literalBooleanRestriction = LiteralRestriction.createRestriction(pattern1,
                "true");

        assertThat(literalBooleanRestriction instanceof BooleanRestriction).isTrue();

        BooleanRestriction booleanRestriction = (BooleanRestriction) literalBooleanRestriction;

        assertThat(booleanRestriction.getValueType()).isEqualTo(Field.BOOLEAN);
        assertThat(booleanRestriction.getValue()).isEqualTo(true);

        LiteralRestriction intLiteralRestriction = LiteralRestriction.createRestriction(pattern1,
                "1");
        assertThat(intLiteralRestriction instanceof NumberRestriction).isTrue();
        NumberRestriction intRestriction = (NumberRestriction) intLiteralRestriction;

        assertThat(intRestriction.isInt()).isTrue();
        assertThat(intRestriction.getValueType()).isEqualTo(Field.INT);
        assertThat(intRestriction.getValue()).isEqualTo(1);

        LiteralRestriction doubleLiteralRestriction = LiteralRestriction.createRestriction(pattern1,
                "1.0");
        assertThat(doubleLiteralRestriction instanceof NumberRestriction).isTrue();

        NumberRestriction doubleRestriction = (NumberRestriction) doubleLiteralRestriction;

        assertThat(doubleRestriction.getValueType()).isEqualTo(Field.DOUBLE);
        assertThat(doubleRestriction.getValue()).isEqualTo(1.0);

        LiteralRestriction dateLiteralRestriction = LiteralRestriction.createRestriction(pattern1,
                "11-jan-2008");

        assertThat(dateLiteralRestriction instanceof DateRestriction).isTrue();

        DateRestriction dateRestriction = (DateRestriction) dateLiteralRestriction;

        assertThat(dateRestriction.getValueType()).isEqualTo(Field.DATE);

        LiteralRestriction stringRestriction = LiteralRestriction.createRestriction(pattern1,
                "test test");

        assertThat(stringRestriction.getValueType()).isEqualTo(Field.STRING);
        assertThat(stringRestriction.getValueAsString()).isEqualTo("test test");

        LiteralRestriction nullRestriction = LiteralRestriction.createRestriction(pattern1,
                null);

        assertThat(nullRestriction instanceof StringRestriction).isTrue();

        assertThat(nullRestriction.getValueType()).isEqualTo(Field.UNKNOWN);
        assertThat(nullRestriction.getValueAsString()).isEqualTo("");
    }
}
