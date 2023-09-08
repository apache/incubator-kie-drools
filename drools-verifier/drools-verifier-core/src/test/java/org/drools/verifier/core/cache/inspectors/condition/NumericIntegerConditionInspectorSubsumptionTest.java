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
package org.drools.verifier.core.cache.inspectors.condition;

import java.util.Arrays;
import java.util.Collection;

import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getAssertDescriptionForRedundant;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getNumericCondition;

@ExtendWith(MockitoExtension.class)
public class NumericIntegerConditionInspectorSubsumptionTest {

	@Mock
    private Field field;

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // op1, val1, op2, val2, aSubsumesB, bSubsumesA
                {"==", 0, "==", 0, true, true},
                {"!=", 0, "!=", 0, true, true},
                {">", 0, ">", 0, true, true},
                {">=", 0, ">=", 0, true, true},
                {"<", 0, "<", 0, true, true},
                {"<=", 0, "<=", 0, true, true},

                {"==", 0, "==", 1, false, false},
                {"==", 0, "!=", 0, false, false},
                {"==", 0, ">", 0, false, false},
                {"==", 0, ">", 10, false, false},
                {"==", 0, ">=", 1, false, false},
                {"==", 0, ">=", 10, false, false},
                {"==", 0, "<", 0, false, false},
                {"==", 0, "<", -10, false, false},
                {"==", 0, "<=", -1, false, false},
                {"==", 0, "<=", -10, false, false},

                {"==", 0, "!=", 1, true, false},
                {"==", 0, ">", -1, false, true},
                {"==", 0, ">", -10, false, true},
                {"==", 0, ">=", 0, false, true},
                {"==", 0, ">=", -10, false, true},
                {"==", 0, "<", 1, false, true},
                {"==", 0, "<", 10, false, true},
                {"==", 0, "<=", 0, false, true},
                {"==", 0, "<=", 10, false, true},

                {"!=", 0, "!=", 1, false, false},
                {"!=", 0, ">", -1, false, false},
                {"!=", 0, ">", -10, false, false},
                {"!=", 0, ">=", 0, false, false},
                {"!=", 0, ">=", -10, false, false},
                {"!=", 0, "<", 1, false, false},
                {"!=", 0, "<", 10, false, false},
                {"!=", 0, "<=", 0, false, false},
                {"!=", 0, "<=", 10, false, false},

                {"!=", 0, ">", 0, true, false},
                {"!=", 0, ">", 10, true, false},
                {"!=", 0, ">=", 1, true, false},
                {"!=", 0, ">=", 10, true, false},
                {"!=", 0, "<", 0, true, false},
                {"!=", 0, "<", -10, true, false},
                {"!=", 0, "<=", -1, true, false},
                {"!=", 0, "<=", -10, true, false},

                {">", 0, "<", 1, false, false},
                {">", 0, "<", -10, false, false},
                {">", 0, "<", 10, false, false},
                {">", 0, "<=", 0, false, false},
                {">", 0, "<=", -10, false, false},
                {">", 0, "<=", 10, false, false},

                {">", 0, ">", 1, true, false},
                {">", 0, ">", 10, true, false},
                {">", 0, ">=", 0, false, true},
                {">", 0, ">=", 10, true, false},

                {">=", 0, "<", 0, false, false},
                {">=", 0, "<", -10, false, false},
                {">=", 0, "<", 10, false, false},
                {">=", 0, "<=", -1, false, false},
                {">=", 0, "<=", -10, false, false},
                {">=", 0, "<=", 10, false, false},

                {">=", 0, ">=", 1, true, false},
                {">=", 0, ">=", 10, true, false},

                {"<", 0, "<", 1, false, true},
                {"<", 0, "<", 10, false, true},
                {"<", 0, "<=", 0, false, true},
                {"<", 0, "<=", 10, false, true},

                {"<=", 0, "<=", 1, false, true},
                {"<=", 0, "<=", 10, false, true},

                // integer specific
                {">", 0, ">=", 1, true, true},
                {"<", 0, "<=", -1, true, true},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testASubsumesB(String operator1, Integer value1, String operator2, Integer value2, boolean aSubsumesB, boolean bSubsumesA) {
        NumericIntegerConditionInspector a = getNumericCondition(field, value1, operator1);
        NumericIntegerConditionInspector b = getNumericCondition(field, value2, operator2);

        assertThat(a.subsumes(b)).as(getAssertDescriptionForRedundant(a, b, aSubsumesB)).isEqualTo(aSubsumesB);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testBSubsumesA(String operator1, Integer value1, String operator2, Integer value2, boolean aSubsumesB, boolean bSubsumesA) {
        NumericIntegerConditionInspector a = getNumericCondition(field, value1, operator1);
        NumericIntegerConditionInspector b = getNumericCondition(field, value2, operator2);

        assertThat(b.subsumes(a)).as(getAssertDescriptionForRedundant(b, a, bSubsumesA)).isEqualTo(bSubsumesA);
    }
}