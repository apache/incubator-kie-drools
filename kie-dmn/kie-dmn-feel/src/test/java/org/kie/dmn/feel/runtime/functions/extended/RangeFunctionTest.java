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
package org.kie.dmn.feel.runtime.functions.extended;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import static org.assertj.core.api.Assertions.assertThat;

class RangeFunctionTest {

    private RangeFunction rangeFunction;

    @BeforeEach
    void setUp() {
        rangeFunction = new RangeFunction();
    }

    @Test
    void invokeNull() {
        List<String> from = Arrays.asList(null, " ", "", "[..]");
        from.forEach(it ->  FunctionTestUtil.assertResultError(rangeFunction.invoke(it), InvalidParametersEvent.class, it));
    }

    @Test
    void invokeDifferentTypes() {
        List<String> from = Arrays.asList("[1..\"cheese\"]",
                "[1..date(\"1978-09-12\")]",
                "[1..date(\"1978-09-12\")]",
                "[1..\"upper case(\"aBc4\")\"]");
        from.forEach(it ->  FunctionTestUtil.assertResultError(rangeFunction.invoke(it), InvalidParametersEvent.class, it));
    }

    @Test
    void invokeInvalidTypes() {
        String from = "[if(false)..if(true)]";
        FunctionTestUtil.assertResultError(rangeFunction.invoke(from), InvalidParametersEvent.class, from);
    }

    @Test
    void invoke_LeftNull() {
        String from = "(..2)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, null, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN),
                from);
        from = "(..\"z\")";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, null, "z", Range.RangeBoundary.OPEN),
                from);
        from = "(..\"yz\")";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, null, "yz", Range.RangeBoundary.OPEN),
                from);
        from = "(..date(\"1978-10-13\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, null, LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN),
                from);
        from = "(..duration(\"P3DT20H14M\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, null, Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN),
                from);
        from = "(..duration(\"P2Y6M\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN,
                        null,
                        new ComparablePeriod(Period.parse("P2Y6M")),
                        Range.RangeBoundary.OPEN),
                from);
    }

    @Test
    void invoke_RightNull() {
        String from = "(1..)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, null, Range.RangeBoundary.OPEN),
                from);
        from = "(\"a\"..)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, "a", null, Range.RangeBoundary.OPEN),
                from);
        from = "(\"ab\"..)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, "ab", null, Range.RangeBoundary.OPEN),
                from);
        from = "(date(\"1978-09-12\")..)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), null, Range.RangeBoundary.OPEN),
                from);
        from = "(duration(\"P2DT20H14M\")..)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), null, Range.RangeBoundary.OPEN),
                from);
        from = "(duration(\"P1Y6M\")..)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN,
                        new ComparablePeriod(Period.parse("P1Y6M")),
                        null,
                        Range.RangeBoundary.OPEN),
                from);
    }

    @Test
    void invoke_OpenOpenBoundaries() {
        String from = "(1..2)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN),
                from);
        from = "(\"a\"..\"z\")";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.OPEN),
                from);
        from = "(\"ab\"..\"yz\")";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, "ab", "yz", Range.RangeBoundary.OPEN),
                from);
        from = "(date(\"1978-09-12\")..date(\"1978-10-13\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN),
                from);
        from = "(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN),
                from);
        from = "(duration(\"P1Y6M\")..duration(\"P2Y6M\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN,
                        new ComparablePeriod(Period.parse("P1Y6M")),
                        new ComparablePeriod(Period.parse("P2Y6M")),
                        Range.RangeBoundary.OPEN),
                from);
    }

    @Test
    void invoke_OpenClosedBoundaries() {
        String from = "(1..2]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED),
                from);
        from = "(\"a\"..\"z\"]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.CLOSED),
                from);
        from = "(date(\"1978-09-12\")..date(\"1978-10-13\")]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED),
                from);
        from = "(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED),
                from);
    }

    @Test
    void invoke_ClosedOpenBoundaries() {
        String from = "[1..2)";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN),
                from);
        from = "[\"a\"..\"z\")";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN),
                from);
        from = "[date(\"1978-09-12\")..date(\"1978-10-13\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN),
                from);
        from = "[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))";
        FunctionTestUtil.assertResult(rangeFunction.invoke("[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))"),
                new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN),
                from);
    }

    @Test
    void invoke_ClosedClosedBoundaries() {
        String from = "[1..2)";
        FunctionTestUtil.assertResult(rangeFunction.invoke("[1..2]"),
                new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED),
                from);
        from = "[2..1]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(2), BigDecimal.ONE, Range.RangeBoundary.CLOSED),
                from);
        from = "[\"a\"..\"z\"]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.CLOSED),
                from);
        from = "[date(\"1978-09-12\")..date(\"1978-10-13\")]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED),
                from);
        from = "[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED),
                from);
    }

    @Test
    void invoke_WithOneFunctionNode() {
        String from = "[number(\"1\", \",\", \".\")\"..2]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED),
                from);
        from = "[\"a\"..lower case(\"Z\")]";
        FunctionTestUtil.assertResult(rangeFunction.invoke(from),
                new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.CLOSED),
                from);
    }

    @Test
    void nodeIsAllowed_True() {
        BaseNode node = rangeFunction.getNullNode();
        assertThat(rangeFunction.nodeIsAllowed(node)).withFailMessage(node.getText()).isTrue();
        node = getNumberNode();
        assertThat(rangeFunction.nodeIsAllowed(node)).withFailMessage(node.getText()).isTrue();
        node = getStringNode();
        assertThat(rangeFunction.nodeIsAllowed(node)).withFailMessage(node.getText()).isTrue();
        node = getAtLiteralNode();
        assertThat(rangeFunction.nodeIsAllowed(node)).withFailMessage(node.getText()).isTrue();
        node = getFunctionInvocationNodeA();
        assertThat(rangeFunction.nodeIsAllowed(node)).withFailMessage(node.getText()).isTrue();
    }

    @Test
    void nodeIsAllowed_False() {
        BaseNode node = rangeFunction.parse("if(true)");
        assertThat(rangeFunction.nodeIsAllowed(node)).withFailMessage(node.getText()).isFalse();
        node = getBooleanNode();
        assertThat(rangeFunction.nodeIsAllowed(node)).withFailMessage(node.getText()).isFalse();
    }

    @Test
    void nodeValueIsAllowed_True() {
        Object value = null;
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isTrue();
        value = 12;
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isTrue();
        value = BigDecimal.valueOf(23.3243);
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isTrue();
        value = DateTimeFormatter.ISO_DATE.parse("2016-07-29", LocalDate::from);
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isTrue();
        value = DateTimeFormatter.ISO_TIME.parse("23:59:00", LocalTime::from);
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isTrue();
        value = LocalDateTime.of(2016, 7, 29, 5, 48, 23, 0);
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isTrue();
        value = Duration.parse("P2DT20H14M");
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isTrue();
    }

    @Test
    void nodeValueIsAllowed_False() {
        Object value = Boolean.TRUE;
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isFalse();
        value = Collections.emptyMap();
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isFalse();
        value = Collections.emptyList();
        assertThat(rangeFunction.nodeValueIsAllowed(value))
                .withFailMessage(String.format("%s", value)).isFalse();
    }

    @Test
    void nodesReturnsSameType_True() {
        assertThat(rangeFunction.nodesReturnsSameType(null, null))
                .withFailMessage("null - null")
                .isTrue();
        assertThat(rangeFunction.nodesReturnsSameType("Hello", "world"))
                .withFailMessage("\"Hello\" - \"world\"")
                .isTrue();
        assertThat(rangeFunction.nodesReturnsSameType(null, "world"))
                .withFailMessage("null - \"world\"")
                .isTrue();
        assertThat(rangeFunction.nodesReturnsSameType(1, null))
                .withFailMessage("1 - null")
                .isTrue();
    }

    @Test
    void nodesReturnsSameType_False() {
        assertThat(rangeFunction.nodesReturnsSameType("1", 1))
                .withFailMessage("\"1\" - 1")
                .isFalse();
    }

    @Test
    void evaluateWithValidFunctionInvocationNode() {
        Object[][] data = validFunctionInvocationNodeData();
        Arrays.stream(data).forEach(objects -> {
            String expression = String.format("[%1$s..%1$s]", objects[0]);
            FEELFnResult<Range> retrieved = rangeFunction.invoke(expression);
            assertThat(retrieved.isRight())
                    .withFailMessage(() -> String.format("Expected 'retrieved.isRight()' from, %s", expression))
                    .isTrue();
        });
    }

    @Test
    void evaluateWithInvalidFunctionInvocationNode() {
        Object[][] data = invalidFunctionInvocationNodeData();
        Arrays.stream(data).forEach(objects -> {
            String expression = String.format("[%1$s..%1$s]", objects[0]);
            FEELFnResult<Range> retrieved = rangeFunction.invoke(expression);
            assertThat(retrieved.isLeft())
                    .withFailMessage(() -> String.format("Expected 'retrieved.isLeft()' from, %s", expression))
                    .isTrue();
        });
    }

    @Test
    void parse_NotEmptyString() {
        String input = "";
        assertThat(rangeFunction.parse(input))
                .withFailMessage(String.format("Check `%s`", input))
                .isInstanceOf(NullNode.class);
        input = "null";
        assertThat(rangeFunction.parse("null"))
                .withFailMessage(String.format("Check `%s`", input))
                .isInstanceOf(NullNode.class);
        input = "1";
        assertThat(rangeFunction.parse("1"))
                .withFailMessage(String.format("Check `%s`", input))
                .isInstanceOf(NumberNode.class);
        input = "\"a\"";
        assertThat(rangeFunction.parse(input)).withFailMessage(String.format("Check `%s`", input)).isInstanceOf(StringNode.class);
        input = "false";
        assertThat(rangeFunction.parse(input)).withFailMessage(String.format("Check `%s`", input)).isInstanceOf(BooleanNode.class);
        input = "@\"2019-01-01\"";
        assertThat(rangeFunction.parse(input)).withFailMessage(String.format("Check `%s`", input)).isInstanceOf(AtLiteralNode.class);
        input = "duration(\"P2DT20H14M\")";
        assertThat(rangeFunction.parse(input)).withFailMessage(String.format("Check `%s`", input)).isInstanceOf(FunctionInvocationNode.class);
    }

    @Test
    void parse_emptyString() {
        assertThat(rangeFunction.parse("")).withFailMessage("Check ``").isInstanceOf(NullNode.class);
    }

    @Test
    void getNullNode() {
        assertThat(rangeFunction.getNullNode()).isInstanceOf(NullNode.class);
    }


    private NumberNode getNumberNode() {
        return (NumberNode) rangeFunction.parse("1");
    }

    private StringNode getStringNode() {
        return (StringNode) rangeFunction.parse("\"a\"");
    }

    private BooleanNode getBooleanNode() {
        return (BooleanNode) rangeFunction.parse("false");
    }

    private AtLiteralNode getAtLiteralNode() {
        return (AtLiteralNode) rangeFunction.parse("@\"2019-01-01\"");
    }

    private FunctionInvocationNode getFunctionInvocationNodeA() {
        return (FunctionInvocationNode) rangeFunction.parse("duration(\"P2DT20H14M\")");
    }

    // 10.3.2.7 Endpoints can be either a literal or a qualified name of the following types: number, string, date, time, date and
    //time, or duration.
    private static Object[][] validFunctionInvocationNodeData() {
        // Subset of FEELFunctionsTest.data
        return new Object[][]{
                // constants
                {"string(1.1)", "1.1"},
                {"replace( \"  foo   bar zed  \", \"^(\\s)+|(\\s)+$|\\s+(?=\\s)\", \"\" )", "foo bar zed"},
                {"string(null)", null},
                {"string(date(\"2016-08-14\"))", "2016-08-14"},
                {"string(\"Happy %.0fth birthday, Mr %s!\", 38, \"Doe\")", "Happy 38th birthday, Mr Doe!"},
                {"number(null, \",\", \".\")", null},
                {"number(\"1,000.05\", \",\", \".\")", new BigDecimal("1000.05")},
                {"number(\"1.000,05\", \".\", \",\")", new BigDecimal("1000.05")},
                {"number(\"1000,05\", null, \",\")", new BigDecimal("1000.05")},
                {"number(\"1,000.05e+12\", \",\", \".\")", new BigDecimal("1000.05e+12")},
                {"number(\"1.000,05e+12\", \".\", \",\")", new BigDecimal("1000.05e+12")},
                {"number(\"1000,05e+12\", null, \",\")", new BigDecimal("1000.05e+12")},
                {"substring(\"foobar\", 3)", "obar"},
                {"substring(\"foobar\", 3, 3)", "oba"},
                {"substring(\"foobar\", -2, 1)", "a"},
                {"substring(\"foobar\", -2, 5)", "ar"},
                {"substring(\"foobar\", 15, 5)", null},
                {"string length(\"foobar\")", BigDecimal.valueOf(6)},
                {"string length(null)", null},
                {"upper case(\"aBc4\")", "ABC4"},
                {"upper case(null)", null},
                {"lower case(\"aBc4\")", "abc4"},
                {"lower case(null)", null},
                {"substring before( \"foobar\", \"bar\")", "foo"},
                {"substring before( \"foobar\", \"xyz\")", ""},
                {"substring before( \"foobar\", \"foo\")", ""},
                {"substring after( \"foobar\", \"foo\")", "bar"},
                {"substring after( \"foobar\", \"xyz\")", ""},
                {"substring after( \"foobar\", \"bar\")", ""},
                {"replace(\"banana\",\"a\",\"o\")", "bonono"},
                {"replace(\"banana\",\"(an)+\", \"**\")", "b**a"},
                {"replace(\"banana\",\"[aeiouy]\",\"[$0]\")", "b[a]n[a]n[a]"},
                {"replace(\"0123456789\",\"(\\d{3})(\\d{3})(\\d{4})\",\"($1) $2-$3\")", "(012) 345-6789"},
                {"count([1, 2, 3])", BigDecimal.valueOf(3)},
                {"count( 1, 2, 3 )", BigDecimal.valueOf(3)},
                {"min( \"a\", \"b\", \"c\" )", "a"},
                {"min([ \"a\", \"b\", \"c\" ])", "a"},
                {"max( 1, 2, 3 )", BigDecimal.valueOf(3)},
                {"max([ 1, 2, 3 ])", BigDecimal.valueOf(3)},
                {"max(duration(\"PT1H6M\"), duration(\"PT1H5M\"))", Duration.parse("PT1H6M")},
                {"max(duration(\"P6Y\"), duration(\"P5Y\"))", ComparablePeriod.parse("P6Y")},
                {"sum( 1, 2, 3 )", BigDecimal.valueOf(6)},
                {"sum([ 1, 2, 3 ])", BigDecimal.valueOf(6)},
                {"sum([])", null},
                {"product( 2, 3, 4 )", BigDecimal.valueOf(24)},
                {"product([ 2, 3, 4 ])", BigDecimal.valueOf(24)},
                {"product([])", null},
                {"mean( 1, 2, 3 )", BigDecimal.valueOf(2)},
                {"mean([ 1, 2, 3 ])", BigDecimal.valueOf(2)},
                {"decimal( 1/3, 2 )", new BigDecimal("0.33")},
                {"decimal( 1.5, 0 )", new BigDecimal("2")},
                {"decimal( 2.5, 0 )", new BigDecimal("2")},
                {"decimal( null, 0 )", null},
                {"floor( 1.5 )", new BigDecimal("1")},
                {"floor( -1.5 )", new BigDecimal("-2")},
                {"floor( null )", null},
                {"ceiling( 1.5 )", new BigDecimal("2")},
                {"ceiling( -1.5 )", new BigDecimal("-1")},
                {"ceiling( null )", null},
                {"ceiling( n : 1.5 )", new BigDecimal("2")},
                {"abs( 10 )", new BigDecimal("10")},
                {"abs( -10 )", new BigDecimal("10")},
                {"abs( n: -10 )", new BigDecimal("10")},
                {"abs(@\"PT5H\")", Duration.parse("PT5H")},
                {"abs(@\"-PT5H\")", Duration.parse("PT5H")},
                {"abs(n: @\"-PT5H\")", Duration.parse("PT5H")},
                {"abs(duration(\"P1Y\"))", ComparablePeriod.parse("P1Y")},
                {"abs(duration(\"-P1Y\"))", ComparablePeriod.parse("P1Y")},

                {"day of year( date(2019, 9, 17) )", BigDecimal.valueOf(260)},
                {"day of week( date(2019, 9, 17) )", "Tuesday"},
                {"month of year( date(2019, 9, 17) )", "September"},
                {"week of year( date(2019, 9, 17) )", BigDecimal.valueOf(38)},
                {"week of year( date(2003, 12, 29) )", BigDecimal.valueOf(1)}, // ISO defs.
                {"week of year( date(2004, 1, 4) )", BigDecimal.valueOf(1)},
                {"week of year( date(2005, 1, 3) )", BigDecimal.valueOf(1)},
                {"week of year( date(2005, 1, 9) )", BigDecimal.valueOf(1)},
                {"week of year( date(2005, 1, 1) )", BigDecimal.valueOf(53)},
                {"median( 8, 2, 5, 3, 4 )", new BigDecimal("4")},
                {"median( [6, 1, 2, 3] )", new BigDecimal("2.5")},
                {"median( [ ] ) ", null}, // DMN spec, Table 69: Semantics of list functions
        };
    }

    // 10.3.2.7 Endpoints can be either a literal or a qualified name of the following types: number, string, date, time, date and
    //time, or duration.
    private static Object[][] invalidFunctionInvocationNodeData() {
        // Subset of FEELFunctionsTest.data
        return new Object[][]{
                // constants
                {"contains(\"foobar\", \"ob\")", Boolean.TRUE},
                {"contains(\"foobar\", \"of\")", Boolean.FALSE},
                {"starts with(\"foobar\", \"of\")", Boolean.FALSE},
                {"starts with(\"foobar\", \"fo\")", Boolean.TRUE},
                {"ends with(\"foobar\", \"of\")", Boolean.FALSE},
                {"ends with(\"foobar\", \"bar\")", Boolean.TRUE},
                {"matches(\"foo\", \"[a-z]{3}\")", Boolean.TRUE},
                {"matches(\"banana\", \"[a-z]{3}\")", Boolean.TRUE},
                {"matches(\"two \\n lines\", \"two.*lines\")", Boolean.FALSE},
                {"matches(\"two \\n lines\", \"two.*lines\", \"s\")", Boolean.TRUE}, // DOT_ALL flag set by "s"
                {"matches(\"one\\ntwo\\nthree\", \"^two$\")", Boolean.FALSE},
                {"matches(\"one\\ntwo\\nthree\", \"^two$\", \"m\")", Boolean.TRUE}, // MULTILINE flag set by "m"
                {"matches(\"FoO\", \"foo\")", Boolean.FALSE},
                {"matches(\"FoO\", \"foo\", \"i\")", Boolean.TRUE}, // CASE_INSENSITIVE flag set by "i"
                {"list contains([1, 2, 3], 2)", Boolean.TRUE},
                {"list contains([1, 2, 3], 5)", Boolean.FALSE},
                {"sublist( [1, 2, 3, 4, 5 ], 3, 2 )", Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(4))},
                {"sublist( [1, 2, 3, 4, 5 ], -2, 1 )", Collections.singletonList(BigDecimal.valueOf(4))},
                {"sublist( [1, 2, 3, 4, 5 ], -5, 3 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))},
                {"sublist( [1, 2, 3, 4, 5 ], 1, 3 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))},
                {"append( [1, 2], 3, 4 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4))},
                {"append( [], 3, 4 )", Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(4))},
                {"append( [1, 2] )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2))},
                {"append( [1, 2], null, 4 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), null, BigDecimal.valueOf(4))},
                {"append( 0, 1, 2 )", Arrays.asList(BigDecimal.valueOf(0), BigDecimal.valueOf(1), BigDecimal.valueOf(2))},
                {"concatenate( [1, 2], [3] )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))},
                {"concatenate( [1, 2], 3, [4] )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4))},
                {"insert before( [1, 2, 3], 1, 4 )", Arrays.asList(BigDecimal.valueOf(4), BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))},
                {"insert before( [1, 2, 3], 3, 4 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(4), BigDecimal.valueOf(3))},
                {"insert before( [1, 2, 3], 3, null )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), null, BigDecimal.valueOf(3))},
                {"insert before( [1, 2, 3], -3, 4 )", Arrays.asList(BigDecimal.valueOf(4), BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))},
                {"insert before( [1, 2, 3], -1, 4 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(4), BigDecimal.valueOf(3))},
                {"remove( [1, 2, 3], 1 )", Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(3))},
                {"remove( [1, 2, 3], 3 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2))},
                {"remove( [1, 2, 3], -1 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2))},
                {"remove( [1, 2, 3], -3 )", Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(3))},
                {"reverse( [1, 2, 3] )", Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(2), BigDecimal.valueOf(1))},
                {"index of( [1, 2, 3, 2], 2 )", Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(4))},
                {"index of( [1, 2, null, null], null )", Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(4))},
                {"index of( [1, 2, null, null], 1 )", Collections.singletonList(BigDecimal.valueOf(1))},
                {"union( [1, 2, 1], [2, 3], 2, 4 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4))},
                {"union( [1, 2, null], 4 )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), null, BigDecimal.valueOf(4))},
                {"union( null, 4 )", Arrays.asList(null, BigDecimal.valueOf(4))},
                {"distinct values( [1, 2, 3, 2, 4] )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4))},
                {"distinct values( [1, 2, null, 2, 4] )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), null, BigDecimal.valueOf(4))},
                {"distinct values( 1 )", Collections.singletonList(BigDecimal.valueOf(1))},
                {"sort( [3, 1, 4, 5, 2], function(x,y) x < y )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3),
                        BigDecimal.valueOf(4), BigDecimal.valueOf(5))},
                {"sort( [3, 1, 4, 5, 2] )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3),
                        BigDecimal.valueOf(4), BigDecimal.valueOf(5))},
                {"sort( list : [3, 1, 4, 5, 2] )", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3),
                        BigDecimal.valueOf(4), BigDecimal.valueOf(5))},
                {"sort( [\"c\", \"e\", \"d\", \"a\", \"b\"], function(x,y) x < y )", Arrays.asList("a", "b", "c", "d", "e")},
                {"sort( list : [\"c\", \"e\", \"d\", \"a\", \"b\"], precedes : function(x,y) x < y )", Arrays.asList("a", "b", "c", "d", "e")},
                {"sort( precedes : function(x,y) x < y, list : [\"c\", \"e\", \"d\", \"a\", \"b\"] )", Arrays.asList("a", "b", "c", "d", "e")},
                {"all( true )", true},
                {"all( false )", false},
                {"all( [true] )", true},
                {"all( [false] )", false},
                {"all( true, false )", false},
                {"all( true, true )", true},
                {"all( [true, false] )", false},
                {"all( [true, true] )", true},
                {"all( [false,null,true] )", false},
                {"all( [] )", true},
                {"any( true )", true},
                {"any( false )", false},
                {"any( [true] )", true},
                {"any( [false] )", false},
                {"any( true, false )", true},
                {"any( true, true )", true},
                {"any( [true, false] )", true},
                {"any( [true, true] )", true},
                {"any( [false,null,true] )", true},
                {"any( [] )", false},
        };
    }


}