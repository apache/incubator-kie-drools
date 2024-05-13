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
package org.kie.dmn.feel.runtime.functions.interval;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.runtime.BaseFEELTest;

public class FEELTemporalFunctionsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "before( 1, 10 )", true, null },
                { "before( 10, 1 )", false, null },
                { "before( 1, [1..10] )", false, null },
                { "before( 1, (1..10] )", true, null },
                { "before( 1, [5..10] )", true, null },
                { "before( [1..10], 10 )", false, null },
                { "before( [1..10), 10 )", true, null },
                { "before( [1..10], 15 )", true, null },
                { "before( [1..10], [15..20] )", true, null },
                { "before( [1..10], [10..20] )", false, null },
                { "before( [1..10), [10..20] )", true, null },
                { "before( [1..10], (10..20] )", true, null },
                { "before( \"@2020-01-01\", [\"@2021-01-01\"..\"@2022-01-01\"])", true, null },
                { "before( \"@2024-01-01\", [\"@2021-01-01\"..\"@2022-01-01\"])", false, null },

                { "after( 10, 5 )", true, null },
                { "after( 5, 10 )", false, null },
                { "after( 12, [1..10] )", true, null },
                { "after( 10, [1..10) )", true, null },
                { "after( 10, [1..10] )", false, null },
                { "after( [11..20], 12 )", false, null },
                { "after( [11..20], 10 )", true, null },
                { "after( (11..20], 11 )", true, null },
                { "after( [11..20], 11 )", false, null },
                { "after( [11..20], [1..10] )", true, null },
                { "after( [1..10], [11..20] )", false, null },
                { "after( [11..20], [1..11) )", true, null },
                { "after( (11..20], [1..11] )", true, null },

                { "after( \"@2020-01-01\", [\"@2021-01-01\"..\"@2022-01-01\"])", false, null },
                { "after( \"@2024-01-01\", [\"@2021-01-01\"..\"@2022-01-01\"])", true, null },
                
                { "meets( [1..5], [5..10] )", true, null },
                { "meets( [1..5), [5..10] )", false, null },
                { "meets( [1..5], (5..10] )", false, null },
                { "meets( [1..5], [6..10] )", false, null },

                { "met by( [5..10], [1..5] )", true, null },
                { "met by( [5..10], [1..5) )", false, null },
                { "met by( (5..10], [1..5] )", false, null },
                { "met by( [6..10], [1..5] )", false, null },

                { "overlaps( [1..5], [3..8] )", true, null },
                { "overlaps( [3..8], [1..5] )", true, null },
                { "overlaps( [1..8], [3..5] )", true, null },
                { "overlaps( [3..5], [1..8] )", true, null },
                { "overlaps( [1..5], [6..8] )", false, null },
                { "overlaps( [6..8], [1..5] )", false, null },
                { "overlaps( [1..5], [5..8] )", true, null },
                { "overlaps( [1..5], (5..8] )", false, null },
                { "overlaps( [1..5), [5..8] )", false, null },
                { "overlaps( [1..5), (5..8] )", false, null },
                { "overlaps( [5..8], [1..5] )", true, null },
                { "overlaps( (5..8], [1..5] )", false, null },
                { "overlaps( [5..8], [1..5) )", false, null },
                { "overlaps( (5..8], [1..5) )", false, null },

                { "overlaps before( [1..5], [3..8] )", true, null },
                { "overlaps before( [1..5], [6..8] )", false, null },
                { "overlaps before( [1..5], [5..8] )", true, null },
                { "overlaps before( [1..5], (5..8] )", false, null },
                { "overlaps before( [1..5), [5..8] )", false, null },
                { "overlaps before( [1..5), (1..5] )", true, null },
                { "overlaps before( [1..5], (1..5] )", true, null },
                { "overlaps before( [1..5), [1..5] )", false, null },
                { "overlaps before( [1..5], [1..5] )", false, null },

                { "overlaps before( [1..5], (1..5) )", false, null }, // additional
                { "overlaps before( [1..6], (1..5] )", false, null }, // additional
                { "overlaps before( (1..5], (1..5] )", false, null }, // additional
                { "overlaps before( [2..5], (1..5] )", false, null }, // additional

                { "overlaps after( [3..8], [1..5])", true, null },
                { "overlaps after( [6..8], [1..5])", false, null },
                { "overlaps after( [5..8], [1..5])", true, null },
                { "overlaps after( (5..8], [1..5])", false, null },
                { "overlaps after( [5..8], [1..5))", false, null },
                { "overlaps after( (1..5], [1..5) )", true, null },
                { "overlaps after( (1..5], [1..5] )", true, null },
                { "overlaps after( [1..5], [1..5) )", false, null },
                { "overlaps after( [1..5], [1..5] )", false, null },
                { "overlaps after( (1..5), [1..5] )", false, null }, // additional
                { "overlaps after( (1..5], [1..6] )", false, null }, // additional
                { "overlaps after( (1..5], (1..5] )", false, null }, // additional
                { "overlaps after( (1..5], [2..5] )", false, null }, // additional

                { "finishes( 10, [1..10] )", true, null },
                { "finishes( 10, [1..10) ) ", false, null },
                { "finishes( [5..10], [1..10] )", true, null },
                { "finishes( [5..10), [1..10] )", false, null },
                { "finishes( [5..10), [1..10) )", true, null },
                { "finishes( [1..10], [1..10] )", true, null },
                { "finishes( (1..10], [1..10] )", true, null },

                { "finishes( [5..11], [1..10] )", false, null }, // additional
                { "finishes( [0..10], [1..10] )", false, null }, // additional
                { "finishes( [1..10], (1..10] )", false, null }, // additional

                { "finished by( [1..10], 10 )", true, null },
                { "finished by( [1..10), 10 )  ", false, null },
                { "finished by( [1..10], [5..10] ) ", true, null },
                { "finished by( [1..10], [5..10) ) ", false, null },
                { "finished by( [1..10), [5..10) ) ", true, null },
                { "finished by( [1..10], [1..10] ) ", true, null },
                { "finished by( [1..10], (1..10] ) ", true, null },

                { "finished by( [1..10], [5..11] )", false, null }, // additional
                { "finished by( [1..10], [0..10] )", false, null }, // additional
                { "finished by( (1..10], [1..10] )", false, null }, // additional
                { "finished by( (1..10], (1..10] )", true, null }, // additional

                { "includes( [1..10], 5 )", true, null },
                { "includes( [1..10], 12 )", false, null },
                { "includes( [1..10], 1 )", true, null },
                { "includes( [1..10], 10 )", true, null },
                { "includes( (1..10], 1 )", false, null },
                { "includes( [1..10), 10 )", false, null },
                { "includes( [1..10], [4..6] )", true, null },
                { "includes( [1..10], [1..5] )", true, null },
                { "includes( (1..10], (1..5] )", true, null },
                { "includes( [1..10], (1..10) )", true, null },
                { "includes( [1..10), [5..10) )", true, null },
                { "includes( [1..10], [1..10) )", true, null },
                { "includes( [1..10], (1..10] )", true, null },
                { "includes( [1..10], [1..10] )", true, null },

                { "includes( [4..6], [1..10] )", false, null }, // additional
                { "includes( (1..5], [1..5] )", false, null }, // additional
                { "includes( [1..5), [1..5] )", false, null }, // additional
                { "includes( [1..4], [1..5] )", false, null }, // additional

                { "during( 5, [1..10] )", true, null },
                { "during( 12, [1..10] )", false, null },
                { "during( 1, [1..10] )", true, null },
                { "during( 10, [1..10] )", true, null },
                { "during( 1, (1..10] )", false, null },
                { "during( 10, [1..10) )", false, null },
                { "during( [4..6], [1..10] )", true, null },
                { "during( [1..5], [1..10] )", true, null },
                { "during( (1..5], (1..10] )", true, null },
                { "during( (1..10), [1..10] )", true, null },
                { "during( [5..10), [1..10) )", true, null },
                { "during( [1..10), [1..10] )", true, null },
                { "during( (1..10], [1..10] )", true, null },
                { "during( [1..10], [1..10] )", true, null },

                { "during( [1..10], [4..6] )", false, null }, // additional
                { "during( [1..5] , (1..5])", false, null }, // additional
                { "during( [1..5] , [1..5))", false, null }, // additional
                { "during( [1..5] , [1..4])", false, null }, // additional

                { "starts( 1, [1..10] )", true, null },
                { "starts( 1, (1..10] )", false, null },
                { "starts( 2, [1..10] )", false, null },
                { "starts( [1..5], [1..10] )", true, null },
                { "starts( (1..5], (1..10] )", true, null },
                { "starts( (1..5], [1..10] )", false, null },
                { "starts( [1..5], (1..10] )", false, null },
                { "starts( [1..10], [1..10] )", true, null },
                { "starts( [1..10), [1..10] )", true, null },
                { "starts( (1..10), (1..10) )", true, null },

                { "starts( [1..9], [1..5] )", false, null }, // additional
                { "starts( [1..5], [1..5) )", false, null }, // additional
                { "starts( [2..9], [1..5] )", false, null }, // additional

                { "started by( [1..10], 1 )", true, null },
                { "started by( (1..10], 1 )", false, null },
                { "started by( [1..10], 2 )", false, null },
                { "started by( [1..10], [1..5] )", true, null },
                { "started by( (1..10], (1..5] )", true, null },
                { "started by( [1..10], (1..5] )", false, null },
                { "started by( (1..10], [1..5] )", false, null },
                { "started by( [1..10], [1..10] )", true, null },
                { "started by( [1..10], [1..10) )", true, null },
                { "started by( (1..10), (1..10) )", true, null },

                { "started by( [1..5], [1..9] )", false, null }, // additional
                { "started by( [1..5), [1..5] )", false, null }, // additional
                { "started by( [1..5], [2..9] )", false, null }, // additional

                { "coincides( 5, 5 )", true, null },
                { "coincides( 3, 4 )", false, null },
                { "coincides( [1..5], [1..5] )", true, null },
                { "coincides( (1..5), [1..5] )", false, null },
                { "coincides( [1..5], [2..6] )", false, null },
        };
        return addAdditionalParameters(cases, false);
    }
}





