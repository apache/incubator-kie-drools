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
package org.drools.model.codegen.execmodel;

import java.math.BigDecimal;

import org.drools.model.codegen.execmodel.domain.ValueHolder;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberAndStringArithmeticOperationCoercionTest extends BaseModelTest {

    public NumberAndStringArithmeticOperationCoercionTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    // NOTE: For BigDecimal specific issues, use BigDecimalTest

    @Test
    public void testMultiplyStringInt() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(100);
        holder.setStrValue("10");
        testValueHolder("ValueHolder( intValue == strValue * 10 )", holder);
    }

    @Test
    public void testMultiplyStringIntWithBindVariable() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(100);
        holder.setStrValue("10");
        testValueHolder("ValueHolder( $strValue : strValue, intValue == $strValue * 10 )", holder);
    }

    @Test
    public void testMultiplyIntStringWithBindVariable() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(100);
        holder.setStrValue("10");
        testValueHolder("ValueHolder( $strValue : strValue, intValue ==  10 * $strValue)", holder);
    }

    @Test
    public void testMultiplyStringIntWithBindVariableCompareToBigDecimal() {
        ValueHolder holder = new ValueHolder();
        holder.setStrValue("10");
        holder.setBdValue(new BigDecimal("-10"));
        testValueHolder("ValueHolder( $strValue : strValue, bdValue == $strValue * -1 )", holder);
    }

    @Test
    public void testMultiplyStringIntWithBindVariableCompareToObject() {
        ValueHolder holder = new ValueHolder();
        holder.setStrValue("20");
        holder.setObjValue("200");
        testValueHolder("ValueHolder( $strValue : strValue, objValue == $strValue * 10 )", holder);
    }

    @Test
    public void testMultiplyStringBigDecimal() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(10);
        holder.setStrValue("20");
        testValueHolder("ValueHolder( intValue == strValue * 0.5B )", holder);
    }

    @Test
    public void testMultiplyDecimalStringInt() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(5);
        holder.setStrValue("0.5");
        testValueHolder("ValueHolder( intValue == strValue * 10 )", holder);
    }

    @Test
    public void testMultiplyDecimalStringBigDecimal() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(5);
        holder.setStrValue("0.5");
        testValueHolder("ValueHolder( intValue == strValue * 10B )", holder);
    }

    @Test
    public void testMultiplyIntDecimalString() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(5);
        holder.setStrValue("0.5");
        testValueHolder("ValueHolder( intValue == 10 * strValue )", holder);
    }

    @Test
    public void testMultiplyStringDouble() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(101);
        holder.setStrValue("10");
        testValueHolder("ValueHolder( intValue == strValue * 10.1 )", holder);
    }

    @Test
    public void testAddStringIntWithBindVariableCompareToObject() {
        ValueHolder holder = new ValueHolder();
        holder.setStrValue("20");
        holder.setObjValue("2010"); // String concat
        testValueHolder("ValueHolder( $strValue : strValue, objValue == $strValue + 10 )", holder);
    }

    @Test
    public void testAddIntStringWithBindVariableCompareToObject() {
        ValueHolder holder = new ValueHolder();
        holder.setStrValue("20");
        holder.setObjValue("1020"); // String concat
        testValueHolder("ValueHolder( $strValue : strValue, objValue ==  10 + $strValue )", holder);
    }

    @Test
    public void testAddStringIntWithBindVariableCompareToObjectNonNumeric() {
        ValueHolder holder = new ValueHolder();
        holder.setStrValue("ABC");
        holder.setObjValue("ABC10"); // String concat
        testValueHolder("ValueHolder( $strValue : strValue, objValue == $strValue + 10 )", holder);
    }

    @Test
    public void testSubtractStringInt() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(40);
        holder.setStrValue("50");
        testValueHolder("ValueHolder( intValue == strValue - 10 )", holder);
    }

    @Test
    public void testModStringInt() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(2);
        holder.setStrValue("12");
        testValueHolder("ValueHolder( intValue == strValue % 10 )", holder);
    }

    @Test
    public void testDivideStringInt() {
        ValueHolder holder = new ValueHolder();
        holder.setIntValue(5);
        holder.setStrValue("50");
        testValueHolder("ValueHolder( intValue == strValue / 10 )", holder);
    }

    private void testValueHolder(String pattern, ValueHolder holder) {
        String str =
                "import " + ValueHolder.class.getCanonicalName() + "\n" +
                     "rule R dialect \"mvel\" when\n" +
                     pattern + "\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(holder);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }
}
