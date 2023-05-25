/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.core.util.ConstraintIdentifierAnalysis.analyze;

public class ConstraintIdentifierAnalysisTest {

    @Test
    public void methodCall() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue($var) == 10");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var");
        assertThat(analysis.getMethodArgumentList()).containsExactly("$var");
    }

    @Test
    public void methodCallInverted() {
        ConstraintIdentifierAnalysis analysis = analyze("10 == convertValue($var)");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var");
        assertThat(analysis.getMethodArgumentList()).containsExactly("$var");
    }

    @Test
    public void methodCallWithMultipleArguments() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue($var1, \"ABC\", $var2, $var3) == 10");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var1", "$var2", "$var3");
        assertThat(analysis.getMethodArgumentList()).containsExactlyInAnyOrder("$var1", "$var2", "$var3");
    }

    @Test
    public void methodCallWithNoArgument() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue() == 10");
        assertThat(analysis.getIdentifierList()).containsExactly("convertValue");
        assertThat(analysis.getMethodArgumentList()).isEmpty();
    }

    @Test
    public void mutipleMethodCalls() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue($var1) > calculateValue($var2)");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var1", "calculateValue", "$var2");
        assertThat(analysis.getMethodArgumentList()).containsExactlyInAnyOrder("$var1", "$var2");
    }

    @Test
    public void nestedProperty() {
        ConstraintIdentifierAnalysis analysis = analyze("$var1.address.city == city");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("$var1", "address", "city", "city");
        assertThat(analysis.getMethodArgumentList()).isEmpty();
    }

    @Test
    public void nestedMethodCall() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue(calculateValue($var), rate) < 10");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "calculateValue", "$var", "rate");
        assertThat(analysis.getMethodArgumentList()).containsExactlyInAnyOrder("$var", "rate");
    }

    @Test
    public void logicalAnd() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue($var) == 10 && convertValue(price) < 100");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var", "convertValue", "price");
        assertThat(analysis.getMethodArgumentList()).containsExactlyInAnyOrder("$var", "price");
    }

    @Test
    public void logicalOr() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue($var) == 10 || convertValue(price) < 100");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var", "convertValue", "price");
        assertThat(analysis.getMethodArgumentList()).containsExactlyInAnyOrder("$var", "price");
    }

    @Test
    public void listAccess() {
        ConstraintIdentifierAnalysis analysis = analyze("myList[0] == name");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("myList", "name");
        assertThat(analysis.getMethodArgumentList()).isEmpty();
    }

    @Test
    public void mapAccess() {
        ConstraintIdentifierAnalysis analysis = analyze("myMap[\"Key\"] == name");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("myMap", "name");
        assertThat(analysis.getMethodArgumentList()).isEmpty();
    }

    @Test
    public void in() {
        ConstraintIdentifierAnalysis analysis = analyze("age in (ONE, TWO, THREE)");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("age", "in", "ONE", "TWO", "THREE");
        assertThat(analysis.getMethodArgumentList()).isEmpty();
    }

    @Test
    public void eval() {
        ConstraintIdentifierAnalysis analysis = analyze("eval(convertValue($var) == 10)");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var");
        assertThat(analysis.getMethodArgumentList()).containsExactly("$var");
    }

    @Test
    public void whiteSpaces() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue ( $var ) == price");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var", "price");
        assertThat(analysis.getMethodArgumentList()).containsExactly("$var");
    }

    @Test
    public void noWhiteSpaces() {
        ConstraintIdentifierAnalysis analysis = analyze("convertValue($var)==price");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("convertValue", "$var", "price");
        assertThat(analysis.getMethodArgumentList()).containsExactly("$var");
    }

    @Test
    public void unicode() {
        ConstraintIdentifierAnalysis analysis = analyze("値変換($変数) == 値段");
        assertThat(analysis.getIdentifierList()).containsExactlyInAnyOrder("値変換", "$変数", "値段");
        assertThat(analysis.getMethodArgumentList()).containsExactly("$変数");
    }
}
