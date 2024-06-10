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
package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;

public class FEEL12ExtendedFunctionsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "modulo( 4, 3 )", new BigDecimal( "1" ), null },
                { "modulo(  12,  5 )", new BigDecimal(  "2" ), null },
                { "modulo( -12,  5 )", new BigDecimal(  "3" ), null },
                { "modulo(  12, -5 )", new BigDecimal( "-3" ), null },
                { "modulo( -12, -5 )", new BigDecimal( "-2" ), null },
                { "modulo(  10.1,  4.5 )", new BigDecimal(  "1.1" ), null },
                { "modulo( -10.1,  4.5 )", new BigDecimal(  "3.4" ), null },
                { "modulo(  10.1, -4.5 )", new BigDecimal( "-3.4" ), null },
                { "modulo( -10.1, -4.5 )", new BigDecimal( "-1.1" ), null },
                { "split( \"foo,bar,baz\", \",\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "split( delimiter: \",\", string: \"foo,bar,baz\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "split( \"foo;bar|baz\", \"[;|]\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "sqrt( 9 )", BigDecimal.valueOf( 3.0 ), null },
                { "sqrt( 10 )", new BigDecimal("3.162277660168379331998893544432719"), null },
                { "stddev( 10 )", null, FEELEvent.Severity.ERROR },
                { "stddev( 1, 2, 3 )", BigDecimal.valueOf( 1.0 ) , null},
                { "stddev( [1, 2, 3] )", BigDecimal.valueOf( 1.0 ) , null},
                { "stddev( 2, 4, 7, 5 )", new BigDecimal("2.081665999466132735282297706979931") , null},
                { "stddev( [ 47 ] )", null, FEELEvent.Severity.ERROR },
                { "stddev( 47 )", null, FEELEvent.Severity.ERROR },
                { "stddev( [ ] )", null, FEELEvent.Severity.ERROR },
                {"mode( 6, 3, 9, 6, 6 )", List.of(BigDecimal.valueOf(6)), null },
                { "mode( [6, 1, 9, 6, 1] )",  Arrays.asList(BigDecimal.valueOf( 1 ),  BigDecimal.valueOf( 6 ) ), null },
                {"mode( [ ] )", List.of(), null },
        };
        return addAdditionalParameters(cases, false);
    }
}
