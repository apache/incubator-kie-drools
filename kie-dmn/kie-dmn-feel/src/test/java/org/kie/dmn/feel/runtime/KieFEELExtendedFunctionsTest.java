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
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;

import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

public class KieFEELExtendedFunctionsTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "string(\"Happy %.0fth birthday, Mr %s!\", 38, \"Doe\")", "Happy 38th birthday, Mr Doe!", null},
                { "now()", ZonedDateTime.class , null},
                { "today()", LocalDate.class, null },
                { "string join([\"a\",\"b\",\"c\"], \"_and_\")", "a_and_b_and_c", null},
                { "string join([\"a\",\"b\",\"c\"], \"\")", "abc", null},
                { "string join([\"a\",\"b\",\"c\"], null)", "abc", null},
                { "string join([\"a\"], \"X\")", "a", null},
                { "string join([\"a\",null,\"c\"], \"X\")", "aXc", null},
                { "string join([], \"X\")", "", null},
                { "string join([\"a\",\"b\",\"c\"])", "abc", null},
                { "string join([\"a\",null,\"c\"])", "ac", null},
                { "string join([])", "", null},
                { "string join([\"a\",123,\"c\"], null)", null, FEELEvent.Severity.ERROR},
                { "string join(null, null)", null, FEELEvent.Severity.ERROR},
                { "nn sum( 10, null, 20, 40, null )", new BigDecimal("70", MathContext.DECIMAL128), null },
                { "nn sum( [] )", BigDecimal.ZERO, null },
                { "nn sum( [ null ] )", BigDecimal.ZERO, null },
                { "nn sum( [ null, null ] )", BigDecimal.ZERO, null },
                { "floor(1.5)", new BigDecimal("1"), null },
                { "floor(n:1.5)", new BigDecimal("1"), null },
                { "floor(-1.56, 1)", new BigDecimal("-1.6"), null },
                { "floor(n:-1.56, scale:1)", new BigDecimal("-1.6"), null },
                { "ceiling(1.5)", new BigDecimal("2"), null },
                { "ceiling(n:-.33333)", new BigDecimal("0"), null },
                { "ceiling(-1.56, 1) ", new BigDecimal("-1.5"), null },
                { "ceiling(n:-1.56, scale:1) ", new BigDecimal("-1.5"), null },
                { "round up(5.5, 0)", new BigDecimal("6"), null },
                { "round up(-5.5, 0) ", new BigDecimal("-6"), null },
                { "round up(1.121, 2) ", new BigDecimal("1.13"), null },
                { "round up(-1.126, 2) ", new BigDecimal("-1.13"), null },
                { "round up(1.126, 6177) ", null, FEELEvent.Severity.ERROR },
                { "round up(1.126, -6112) ", null, FEELEvent.Severity.ERROR },
                { "round down(5.5, 0)", new BigDecimal("5"), null },
                { "round down(-5.5, 0) ", new BigDecimal("-5"), null },
                { "round down(1.121, 2) ", new BigDecimal("1.12"), null },
                { "round down(-1.126, 2) ", new BigDecimal("-1.12"), null },
                { "round down(1.126, 6177) ", null, FEELEvent.Severity.ERROR },
                { "round down(1.126, -6112) ", null, FEELEvent.Severity.ERROR },
                { "round half up(5.5, 0)", new BigDecimal("6"), null },
                { "round half up(-5.5, 0) ", new BigDecimal("-6"), null },
                { "round half up(1.121, 2) ", new BigDecimal("1.12"), null },
                { "round half up(-1.126, 2) ", new BigDecimal("-1.13"), null },
                { "round half up(1.126, 6177) ", null, FEELEvent.Severity.ERROR },
                { "round half up(1.126, -6112) ", null, FEELEvent.Severity.ERROR },
                { "round half down(5.5, 0)", new BigDecimal("5"), null },
                { "round half down(-5.5, 0) ", new BigDecimal("-5"), null },
                { "round half down(1.121, 2) ", new BigDecimal("1.12"), null },
                { "round half down(-1.126, 2) ", new BigDecimal("-1.13"), null },
                { "round half down(1.126, 6177) ", null, FEELEvent.Severity.ERROR },
                { "round half down(1.126, -6112) ", null, FEELEvent.Severity.ERROR },
                { "after( 1, 2 )", Boolean.FALSE, null },
                { "after( date(\"2018-08-15\"), date(\"2018-07-25\") )", Boolean.TRUE, null },
                { "after( date(\"2018-08-15\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "after( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-07-25\") )", Boolean.TRUE, null },
                { "after( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-07-25\")..date(\"2018-07-31\")] )", Boolean.TRUE, null },
                { "before( date(\"2018-08-15\"), date(\"2018-07-25\") )", Boolean.FALSE, null },
                { "before( date(\"2018-08-15\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.FALSE, null },
                { "before( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-07-25\") )", Boolean.FALSE, null },
                { "before( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-07-25\")..date(\"2018-07-31\")] )", Boolean.FALSE, null },
                { "coincides( date(\"2018-08-15\"), date(\"2018-08-15\") )", Boolean.TRUE, null },
                { "coincides( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "starts( date(\"2018-07-25\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "starts( [date(\"2018-08-15\")..date(\"2018-08-20\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "started by( [date(\"2018-07-25\")..date(\"2018-08-31\")], date(\"2018-07-25\") )", Boolean.TRUE, null },
                { "started by( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-15\")..date(\"2018-08-20\")] )", Boolean.TRUE, null },
                { "finishes( date(\"2018-08-10\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "finishes( [date(\"2018-08-25\")..date(\"2018-08-31\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "finished by( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-08-31\") )", Boolean.TRUE, null },
                { "finished by( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-25\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "during( date(\"2018-07-29\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "during( [date(\"2018-08-17\")..date(\"2018-08-20\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "includes( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-08-25\") )", Boolean.TRUE, null },
                { "includes( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-20\")..date(\"2018-08-22\")] )", Boolean.TRUE, null },
                { "overlaps( [date(\"2018-08-15\")..date(\"2018-08-28\")], [date(\"2018-08-20\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "context put({name: \"John Doe\"}, \"age\", 47)", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47))), null },
                { "context put({name: \"John Doe\"}, [\"age\"], 47)", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47))), null },
                { "context put({name: \"John Doe\"}, [], 47)", null, FEELEvent.Severity.ERROR },
                { "context put({name: \"John Doe\", address: { street: \"St.\", country:\"US\"}}, [\"address\", \"country\"], \"IT\")", mapOf(entry("name", "John Doe"),entry("address",mapOf(entry("street","St."), entry("country","IT")))), null },
                { "context put({name: \"John Doe\", age: 0}, \"age\", 47)", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47))), null },
                { "context put({name: \"John Doe\", age: 0, z:999}, \"age\", 47)", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47)),entry("z", new BigDecimal(999))), null },
                { "context merge([{name: \"John Doe\"}, {age: 47}])", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47))), null },
                { "context merge([{name: \"John Doe\", age: 0}, {age: 47}])", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47))), null },
                { "context([{key: \"name\", value: \"John Doe\"},{\"key\":\"age\", \"value\":47}])", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47))), null },
                { "context([{key: \"name\", value: \"John Doe\"},{\"key\":\"age\", \"value\":47, \"something\":\"else\"}])", mapOf(entry("name", "John Doe"),entry("age", new BigDecimal(47))), null },
                { "context([{key: \"name\", value: \"John Doe\"},{\"key\":\"age\"}])", null, FEELEvent.Severity.ERROR },
                { "time(10, 20, 30)", LocalTime.of(10, 20, 30), null },
                { "date( 2020, 2, 31 )", null, FEELEvent.Severity.ERROR},
                { "date( \"2020-02-31\" )", null, FEELEvent.Severity.ERROR},
                { "range(\"[1..10]\")", Range.class, null },
                { "range(\"[1..10]\").start included", true, null },
                { "range(\"[1..10]\").end included", true, null },
                { "range(\"[1..10]\").start", new BigDecimal("1"), null },
                { "range(\"[1..10]\").end", new BigDecimal("10"), null },
                { "range(\"(1..10]\").start included", false, null },
                { "range(\"]1..10]\").start included", false, null },
                { "range(\"[1..10)\").end included", false, null },
                { "range(\"[1..10[\").end included", false, null },
                { "range(\"[date(\\\"2022-01-01\\\")..date(\\\"2023-01-01\\\"))\")", Range.class, null },
                { "range(\"[date(\\\"2022-01-01\\\")..date(\\\"2023-01-01\\\"))\").end included", false, null },
                { "range(\"[date(\\\"2022-01-01\\\")..date(\\\"2023-01-01\\\"))\").end.year", new BigDecimal("2023"), null },
        };
        return addAdditionalParameters(cases, true);
    }
}
