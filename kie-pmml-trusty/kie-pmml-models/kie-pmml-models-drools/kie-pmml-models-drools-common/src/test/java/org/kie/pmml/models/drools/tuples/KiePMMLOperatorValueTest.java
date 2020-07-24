/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.drools.tuples;

import org.junit.Test;
import org.kie.pmml.commons.model.enums.OPERATOR;

import static org.junit.Assert.assertEquals;
import static org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue.VALUE_CONSTRAINT_PATTERN;

public class KiePMMLOperatorValueTest {

    @Test
    public void getConstraintsAsString() {
        OPERATOR operator = OPERATOR.LESS_THAN;
        Object value = 234;
        KiePMMLOperatorValue kiePMMLOperatorValue = new KiePMMLOperatorValue(operator, value);
        String retrieved = kiePMMLOperatorValue.getConstraintsAsString();
        String expected = String.format(VALUE_CONSTRAINT_PATTERN, operator.getOperator(), value);
        assertEquals(expected, retrieved);
    }

    @Test
    public void buildConstraintsString() {
        OPERATOR operator = OPERATOR.LESS_THAN;
        Object value = 234;
        KiePMMLOperatorValue kiePMMLOperatorValue = new KiePMMLOperatorValue(operator, value);
        String retrieved = kiePMMLOperatorValue.buildConstraintsString();
        String expected = String.format(VALUE_CONSTRAINT_PATTERN, operator.getOperator(), value);
        assertEquals(expected, retrieved);
    }
}