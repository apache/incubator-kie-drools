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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.util.NumberEvalHelper;

import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

public class FEEL12ExtendedForLoopTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
          //normal:
          {"for x in [1, 2, 3] return x+1", Stream.of(1, 2, 3 ).map(x -> BigDecimal.valueOf(x + 1 ) ).collect(Collectors.toList() ), null},
          {"for x in @\"2021-01-01\"..@\"2021-01-03\" return x+1", Stream.of("2021-01-02", "2021-01-03", "2021-01-04" ).map(LocalDate::parse).collect(Collectors.toList() ), null},

          //extended:
          {"for x in 1..3 return x+1", Stream.of(1, 2, 3).map(x -> BigDecimal.valueOf(x + 1)).collect(Collectors.toList()), null},
          {"for x in 1..\"ciao\" return x+1", null, FEELEvent.Severity.ERROR},
          {"for x in 3..1 return x+1", Stream.of(3, 2, 1).map(x -> BigDecimal.valueOf(x + 1)).collect(Collectors.toList()), null},
          {"for x in 1..1 return x+1", Stream.of(1).map(x -> BigDecimal.valueOf(x + 1)).collect(Collectors.toList()), null},
          {"for x in 1..3, y in 4..6 return [x+1, y-1]", l(l(2, 3), l(2, 4), l(2, 5), l(3, 3), l(3, 4), l(3, 5), l(4, 3), l(4, 4), l(4, 5)), null},
          {"{ a: 1, b : 3, c : for x in a..b return x+1}", mapOf(entry("a", BigDecimal.valueOf(1)), entry("b", BigDecimal.valueOf(3)), entry("c", Stream.of(1, 2, 3).map(x -> BigDecimal.valueOf(x + 1)).collect(Collectors.toList()))), null},
          {"{ a: 1, b : 3, c : for x in a+2..b-2 return x+1}", mapOf(entry("a", BigDecimal.valueOf(1)), entry("b", BigDecimal.valueOf(3)), entry("c", Stream.of(3, 2, 1).map(x -> BigDecimal.valueOf(x + 1)).collect(Collectors.toList()))), null},
          {"{ a: \"ciao\", b : 3, c : for x in a..b return x+1}", mapOf(entry("a", "ciao"), entry("b", BigDecimal.valueOf(3)), entry("c", null)), FEELEvent.Severity.ERROR},
          {"for i in 0..10 return if i = 0 then 1 else i * partial[-1]", Stream.of(1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800).map(BigDecimal::valueOf).collect(Collectors.toList()), null},
          {"{xs: for i in 1..10 return function() i, ys: for f in xs return f()}.ys", IntStream.range(1, 11).boxed().map(BigDecimal::valueOf).collect(Collectors.toList()), null},

        };
        return addAdditionalParameters(cases, false);
    }

    private static List<Object> l(final Object... args) {
        final List<Object> coerced = new ArrayList<>();
        for ( final Object a : args ) {
            coerced.add(NumberEvalHelper.coerceNumber(a));
        }
        return coerced;
    }
}
