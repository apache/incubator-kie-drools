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
package org.drools.scenariosimulation.backend.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.backend.model.Dispute;
import org.drools.scenariosimulation.backend.model.NotEmptyConstructor;
import org.drools.scenariosimulation.backend.model.Person;
import org.drools.scenariosimulation.backend.model.SubPerson;
import org.drools.scenariosimulation.backend.runner.RuleScenarioRunnerHelperTest;
import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.drools.scenariosimulation.backend.util.model.EnumTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.backend.runner.model.ValueWrapper.errorEmptyMessage;
import static org.drools.scenariosimulation.backend.runner.model.ValueWrapper.of;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.convertValue;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.getField;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.loadClass;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.revertValue;

public class ScenarioBeanUtilTest {

    private static String FIRST_NAME = "firstNameToSet";
    private static int AGE = 10;
    private static ClassLoader classLoader = ScenarioBeanUtilTest.class.getClassLoader();

    @Test
    public void fillBeanTest() {
        Map<List<String>, Object> paramsToSet = Map.of(List.of("creator", "firstName"), FIRST_NAME, List.of("creator", "age"), AGE);

        Object result = ScenarioBeanUtil.fillBean(errorEmptyMessage(), Dispute.class.getCanonicalName(), paramsToSet, classLoader);

        assertThat(result).isInstanceOf(Dispute.class);

        Dispute dispute = (Dispute) result;
        assertThat(dispute.getCreator().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(dispute.getCreator().getAge()).isEqualTo(AGE);
    }

    @Test
    public void fillBeanTestWithInitialInstanceTest() {
        Dispute dispute = new Dispute();

        Map<List<String>, Object> paramsToSet = Map.of(List.of("creator", "firstName"), FIRST_NAME, List.of("creator", "age"), AGE);

        Object result = ScenarioBeanUtil.fillBean(of(dispute), Dispute.class.getCanonicalName(), paramsToSet, classLoader);

        assertThat(result).isInstanceOf(Dispute.class);
        assertThat(result).isSameAs(dispute);

        assertThat(FIRST_NAME).isEqualTo(dispute.getCreator().getFirstName());
        assertThat(AGE).isEqualTo(dispute.getCreator().getAge());
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanLoadClassTest() {
        ScenarioBeanUtil.fillBean(errorEmptyMessage(), "FakeCanonicalName", new HashMap<>(), classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNotEmptyConstructorTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(List.of("name"), null);

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), NotEmptyConstructor.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(List.of("fakeField"), null);

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), Dispute.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNullClassTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(List.of("fakeField"), null);

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), null, paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailWrongTypeTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(List.of("description"), new ArrayList<>());

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), Dispute.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test
    public void fillBeanEmptyValueTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(List.of(), null);

        assertThat((String) ScenarioBeanUtil.fillBean(of(null), String.class.getCanonicalName(), paramsToSet, classLoader)).isNull();
    }

    @Test
    public void navigateToObjectTest() {
        Dispute dispute = new Dispute();
        Person creator = new Person();
        creator.setFirstName(FIRST_NAME);
        dispute.setCreator(creator);
        List<String> pathToProperty = List.of("creator", "firstName");

        ScenarioBeanWrapper<?> scenarioBeanWrapper = ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, true);
        Object targetObject = scenarioBeanWrapper.getBean();

        assertThat(FIRST_NAME).isEqualTo(targetObject);

        assertThat(ScenarioBeanUtil.navigateToObject(null, List.of()).getBean()).isNull();
    }

    @Test
    public void navigateToObjectFakeFieldTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = List.of("fakeField");

        String message = "Impossible to find field with name 'fakeField' in class " + Dispute.class.getCanonicalName();
        assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, true))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void navigateToObjectNoStepCreationTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = List.of("creator", "firstName");

        String message = "Impossible to reach field firstName because a step is not instantiated";
        assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, false))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void convertValue_manyCases() {
        assertThat(convertValue(String.class.getCanonicalName(), "Test", classLoader)).isEqualTo("Test");
        assertThat(convertValue(BigDecimal.class.getCanonicalName(), "13.33", classLoader)).isEqualTo(BigDecimal.valueOf(13.33));
        assertThat(convertValue(BigDecimal.class.getCanonicalName(), "13", classLoader)).isEqualTo(BigDecimal.valueOf(13));
        assertThat(convertValue(BigDecimal.class.getCanonicalName(), "1,232,113.33", classLoader)).isEqualTo(BigDecimal.valueOf(1232113.33));
        assertThat(convertValue(BigDecimal.class.getCanonicalName(), "1232113.33", classLoader)).isEqualTo(BigDecimal.valueOf(1232113.33));
        assertThat(convertValue(BigInteger.class.getCanonicalName(), "13.33", classLoader)).isEqualTo(BigInteger.valueOf(13));
        assertThat(convertValue(BigInteger.class.getCanonicalName(), "13", classLoader)).isEqualTo(BigInteger.valueOf(13));
        assertThat(convertValue(BigInteger.class.getCanonicalName(), "1,232,113.33", classLoader)).isEqualTo(BigInteger.valueOf(1232113));
        assertThat(convertValue(BigInteger.class.getCanonicalName(), "1232113", classLoader)).isEqualTo(BigInteger.valueOf(1232113));
        assertThat(convertValue(boolean.class.getCanonicalName(), "false", classLoader)).isEqualTo(false);
        assertThat(convertValue(Boolean.class.getCanonicalName(), "true", classLoader)).isEqualTo(true);
        assertThat(convertValue(int.class.getCanonicalName(), "1", classLoader)).isEqualTo(1);
        assertThat(convertValue(Integer.class.getCanonicalName(), "1", classLoader)).isEqualTo(1);
        assertThat(convertValue(long.class.getCanonicalName(), "1", classLoader)).isEqualTo(1L);
        assertThat(convertValue(Long.class.getCanonicalName(), "1", classLoader)).isEqualTo(1L);
        assertThat(convertValue(double.class.getCanonicalName(), "1", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(Double.class.getCanonicalName(), "1", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(float.class.getCanonicalName(), "1", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(Float.class.getCanonicalName(), "1", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(double.class.getCanonicalName(), "1.0", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(Double.class.getCanonicalName(), "1.0", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(float.class.getCanonicalName(), "1.0", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(Float.class.getCanonicalName(), "1.0", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(double.class.getCanonicalName(), "1.0d", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(Double.class.getCanonicalName(), "1.0d", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(float.class.getCanonicalName(), "1.0f", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(Float.class.getCanonicalName(), "1.0f", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(double.class.getCanonicalName(), "1.0D", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(Double.class.getCanonicalName(), "1.0D", classLoader)).isEqualTo(1.0d);
        assertThat(convertValue(float.class.getCanonicalName(), "1.0F", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(Float.class.getCanonicalName(), "1.0F", classLoader)).isEqualTo(1.0f);
        assertThat(convertValue(char.class.getCanonicalName(), "a", classLoader)).isEqualTo('a');
        assertThat(convertValue(Character.class.getCanonicalName(), "a", classLoader)).isEqualTo('a');
        assertThat(convertValue(short.class.getCanonicalName(), "1", classLoader)).isEqualTo((short) 1);
        assertThat(convertValue(Short.class.getCanonicalName(), "1", classLoader)).isEqualTo((short) 1);
        assertThat(convertValue(byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader)).isEqualTo("0".getBytes()[0]);
        assertThat(convertValue(Byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader)).isEqualTo("0".getBytes()[0]);
        assertThat(convertValue(LocalDate.class.getCanonicalName(), "2018-05-20", classLoader)).isEqualTo(LocalDate.of(2018, 5, 20));
        assertThat(convertValue(LocalDateTime.class.getCanonicalName(), "2017-02-18T10:30", classLoader)).isEqualTo(LocalDateTime.of(2017, 2, 18, 10, 30));
        assertThat(convertValue(LocalDateTime.class.getCanonicalName(), "1982-04-04T00:20", classLoader)).isEqualTo(LocalDateTime.of(1982, 4, 4, 0, 20, 0));
        assertThat(convertValue(LocalDateTime.class.getCanonicalName(), "1982-10-13T02:09:00.999999999", classLoader)).isEqualTo(LocalDateTime.of(1982, 10, 13, 2, 9, 0, 999999999));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), "01:09:00", classLoader)).isEqualTo(LocalTime.of(1, 9, 0));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), "04:59:07.009999999", classLoader)).isEqualTo(LocalTime.of(4, 59, 07, 9999999));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), "23:45", classLoader)).isEqualTo(LocalTime.of(23, 45));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), "01:09:00", classLoader)).isEqualTo(LocalTime.of(1, 9, 0));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), "04:59:07.009999999", classLoader)).isEqualTo(LocalTime.of(4, 59, 07, 9999999));
        assertThat(convertValue(EnumTest.class.getCanonicalName(), "FIRST", classLoader)).isEqualTo(EnumTest.FIRST);
        assertThat(convertValue(Float.class.getCanonicalName(), null, classLoader)).isNull();
    }

    @Test
    public void revertValue_manyCases() {
        assertThat(revertValue("Test")).isEqualTo("Test");
        assertThat(revertValue(BigDecimal.valueOf(10000.83))).isEqualTo("10000.83");
        assertThat(revertValue(BigDecimal.valueOf(10000))).isEqualTo("10000");
        assertThat(revertValue(BigInteger.valueOf(10000))).isEqualTo("10000");
        assertThat(revertValue(Boolean.FALSE)).isEqualTo("false");
        assertThat(revertValue(Boolean.TRUE)).isEqualTo("true");
        assertThat(revertValue(false)).isEqualTo("false");
        assertThat(revertValue(true)).isEqualTo("true");
        assertThat(revertValue(1)).isEqualTo("1");
        assertThat(revertValue(new Integer(1))).isEqualTo("1");
        assertThat(revertValue(1L)).isEqualTo("1");
        assertThat(revertValue(new Long(1))).isEqualTo("1");
        assertThat(revertValue(1.1d)).isEqualTo("1.1d");
        assertThat(revertValue(new Double(1.1))).isEqualTo("1.1d");
        assertThat(revertValue(Double.NaN)).isEqualTo("NaN");
        assertThat(revertValue(Double.POSITIVE_INFINITY)).isEqualTo("Infinity");
        assertThat(revertValue(Double.NEGATIVE_INFINITY)).isEqualTo("-Infinity");
        assertThat(revertValue(1.1f)).isEqualTo("1.1f");
        assertThat(revertValue(new Float(1.1))).isEqualTo("1.1f");
        assertThat(revertValue('a')).isEqualTo("a");
        assertThat(revertValue(new Character('a'))).isEqualTo("a");
        assertThat(revertValue((short) 1)).isEqualTo("1");
        assertThat(revertValue("0".getBytes()[0])).isEqualTo(String.valueOf("0".getBytes()[0]));
        assertThat(revertValue(new Byte("0".getBytes()[0]))).isEqualTo(String.valueOf("0".getBytes()[0]));
        assertThat(revertValue(null)).isEqualTo("null");
        assertThat(revertValue(LocalDate.of(2018, 10, 20))).isEqualTo("2018-10-20");
        assertThat(revertValue(LocalDateTime.of(2018, 10, 20, 2, 13))).isEqualTo("2018-10-20T02:13:00");
        assertThat(revertValue(LocalDateTime.of(2018, 10, 20, 2, 13, 3))).isEqualTo("2018-10-20T02:13:03");
        assertThat(revertValue(LocalDateTime.of(2018, 10, 20, 2, 13, 3, 9999))).isEqualTo("2018-10-20T02:13:03.000009999");
        assertThat(revertValue(LocalTime.of(2, 13))).isEqualTo("02:13:00");
        assertThat(revertValue(LocalTime.of(2, 13, 3))).isEqualTo("02:13:03");
        assertThat(revertValue(LocalTime.of(2, 13, 3, 9999))).isEqualTo("02:13:03.000009999");
        assertThat(revertValue(EnumTest.FIRST)).isEqualTo("FIRST");
    }

    @Test
    public void convertAndRevertValue() {
        assertThat(revertValue(convertValue(String.class.getCanonicalName(), "Test", classLoader))).isEqualTo("Test");
        assertThat(revertValue(convertValue(boolean.class.getCanonicalName(), "false", classLoader))).isEqualTo("false");
        assertThat(revertValue(convertValue(Boolean.class.getCanonicalName(), "true", classLoader))).isEqualTo("true");
        assertThat(revertValue(convertValue(BigDecimal.class.getCanonicalName(), "1000", classLoader))).isEqualTo("1000");
        assertThat(revertValue(convertValue(BigDecimal.class.getCanonicalName(), "1000.23", classLoader))).isEqualTo("1000.23");
        assertThat(revertValue(convertValue(BigDecimal.class.getCanonicalName(), "1,000.23", classLoader))).isEqualTo("1000.23");
        assertThat(revertValue(convertValue(BigInteger.class.getCanonicalName(), "1000", classLoader))).isEqualTo("1000");
        assertThat(revertValue(convertValue(BigInteger.class.getCanonicalName(), "1000.23", classLoader))).isEqualTo("1000");
        assertThat(revertValue(convertValue(BigInteger.class.getCanonicalName(), "1,000.23", classLoader))).isEqualTo("1000");
        assertThat(revertValue(convertValue(int.class.getCanonicalName(), "1", classLoader))).isEqualTo("1");
        assertThat(revertValue(convertValue(Integer.class.getCanonicalName(), "1", classLoader))).isEqualTo("1");
        assertThat(revertValue(convertValue(long.class.getCanonicalName(), "1", classLoader))).isEqualTo("1");
        assertThat(revertValue(convertValue(Long.class.getCanonicalName(), "1", classLoader))).isEqualTo("1");
        assertThat(revertValue(convertValue(double.class.getCanonicalName(), "1", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(Double.class.getCanonicalName(), "1", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(double.class.getCanonicalName(), "NaN", classLoader))).isEqualTo("NaN");
        assertThat(revertValue(convertValue(Double.class.getCanonicalName(), "NaN", classLoader))).isEqualTo("NaN");
        assertThat(revertValue(convertValue(double.class.getCanonicalName(), "Infinity", classLoader))).isEqualTo("Infinity");
        assertThat(revertValue(convertValue(Double.class.getCanonicalName(), "Infinity", classLoader))).isEqualTo("Infinity");
        assertThat(revertValue(convertValue(double.class.getCanonicalName(), "-Infinity", classLoader))).isEqualTo("-Infinity");
        assertThat(revertValue(convertValue(Double.class.getCanonicalName(), "-Infinity", classLoader))).isEqualTo("-Infinity");
        assertThat(revertValue(convertValue(float.class.getCanonicalName(), "1", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(Float.class.getCanonicalName(), "1", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(double.class.getCanonicalName(), "1.0", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(Double.class.getCanonicalName(), "1.0", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(float.class.getCanonicalName(), "1.0", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(Float.class.getCanonicalName(), "1.0", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(double.class.getCanonicalName(), "1.0d", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(Double.class.getCanonicalName(), "1.0d", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(float.class.getCanonicalName(), "1.0f", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(Float.class.getCanonicalName(), "1.0f", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(double.class.getCanonicalName(), "1.0D", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(Double.class.getCanonicalName(), "1.0D", classLoader))).isEqualTo("1.0d");
        assertThat(revertValue(convertValue(float.class.getCanonicalName(), "1.0F", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(Float.class.getCanonicalName(), "1.0F", classLoader))).isEqualTo("1.0f");
        assertThat(revertValue(convertValue(char.class.getCanonicalName(), "a", classLoader))).isEqualTo("a");
        assertThat(revertValue(convertValue(Character.class.getCanonicalName(), "a", classLoader))).isEqualTo("a");
        assertThat(revertValue(convertValue(short.class.getCanonicalName(), "1", classLoader))).isEqualTo("1");
        assertThat(revertValue(convertValue(Short.class.getCanonicalName(), "1", classLoader))).isEqualTo("1");
        assertThat(revertValue(convertValue(byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader))).isEqualTo(Byte.toString("0".getBytes()[0]));
        assertThat(revertValue(convertValue(Byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader))).isEqualTo(Byte.toString("0".getBytes()[0]));
        assertThat(revertValue(convertValue(LocalDate.class.getCanonicalName(), "2018-05-20", classLoader))).isEqualTo("2018-05-20");
        assertThat(revertValue(convertValue(LocalDateTime.class.getCanonicalName(), "2018-05-20T03:04", classLoader))).isEqualTo("2018-05-20T03:04:00");
        assertThat(revertValue(convertValue(LocalDateTime.class.getCanonicalName(), "2018-05-20T03:04:01", classLoader))).isEqualTo("2018-05-20T03:04:01");
        assertThat(revertValue(convertValue(LocalDateTime.class.getCanonicalName(), "2018-05-20T03:04:01.000009999", classLoader))).isEqualTo("2018-05-20T03:04:01.000009999");
        assertThat(revertValue(convertValue(LocalTime.class.getCanonicalName(), "03:04", classLoader))).isEqualTo("03:04:00");
        assertThat(revertValue(convertValue(LocalTime.class.getCanonicalName(), "03:04:01", classLoader))).isEqualTo("03:04:01");
        assertThat(revertValue(convertValue(LocalTime.class.getCanonicalName(), "03:04:01.000009999", classLoader))).isEqualTo("03:04:01.000009999");
        assertThat(revertValue(convertValue(EnumTest.class.getCanonicalName(), "FIRST", classLoader))).isEqualTo("FIRST");
        assertThat(revertValue(convertValue(Float.class.getCanonicalName(), null, classLoader))).isEqualTo("null");
    }

    @Test
    public void revertAndConvertValueTest() {
        assertThat(convertValue(String.class.getCanonicalName(), revertValue("Test"), classLoader)).isEqualTo("Test");
        assertThat(convertValue(boolean.class.getCanonicalName(), revertValue(false), classLoader)).isEqualTo(false);
        assertThat(convertValue(Boolean.class.getCanonicalName(), revertValue(Boolean.TRUE), classLoader)).isEqualTo(Boolean.TRUE);
        assertThat(convertValue(BigInteger.class.getCanonicalName(), revertValue(BigInteger.valueOf(1000)), classLoader)).isEqualTo(BigInteger.valueOf(1000));
        assertThat(convertValue(BigDecimal.class.getCanonicalName(), revertValue(BigDecimal.valueOf(1000)), classLoader)).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(convertValue(BigDecimal.class.getCanonicalName(), revertValue(BigDecimal.valueOf(1000.13)), classLoader)).isEqualTo(BigDecimal.valueOf(1000.13));
        assertThat(convertValue(int.class.getCanonicalName(), revertValue(1), classLoader)).isEqualTo(1);
        assertThat(convertValue(Integer.class.getCanonicalName(), revertValue(1), classLoader)).isEqualTo(1);
        assertThat(convertValue(long.class.getCanonicalName(), revertValue(1L), classLoader)).isEqualTo(1L);
        assertThat(convertValue(Long.class.getCanonicalName(), revertValue(1L), classLoader)).isEqualTo(1L);
        assertThat(convertValue(double.class.getCanonicalName(), revertValue(1), classLoader)).isEqualTo(1d);
        assertThat(convertValue(Double.class.getCanonicalName(), revertValue(1), classLoader)).isEqualTo(1d);
        assertThat(convertValue(float.class.getCanonicalName(), revertValue(1), classLoader)).isEqualTo(1f);
        assertThat(convertValue(Float.class.getCanonicalName(), revertValue(1), classLoader)).isEqualTo(1f);
        assertThat(convertValue(double.class.getCanonicalName(), revertValue(1.0), classLoader)).isEqualTo(1d);
        assertThat(convertValue(Double.class.getCanonicalName(), revertValue(1.0), classLoader)).isEqualTo(1d);
        assertThat(convertValue(float.class.getCanonicalName(), revertValue(1.0), classLoader)).isEqualTo(1f);
        assertThat(convertValue(Float.class.getCanonicalName(), revertValue(1.0), classLoader)).isEqualTo(1f);
        assertThat(convertValue(double.class.getCanonicalName(), revertValue(1.0d), classLoader)).isEqualTo(1d);
        assertThat(convertValue(Double.class.getCanonicalName(), revertValue(1.0d), classLoader)).isEqualTo(1d);
        assertThat(convertValue(float.class.getCanonicalName(), revertValue(1.0f), classLoader)).isEqualTo(1f);
        assertThat(convertValue(Float.class.getCanonicalName(), revertValue(1.0f), classLoader)).isEqualTo(1f);
        assertThat(convertValue(double.class.getCanonicalName(), revertValue(1.0D), classLoader)).isEqualTo(1d);
        assertThat(convertValue(Double.class.getCanonicalName(), revertValue(1.0D), classLoader)).isEqualTo(1d);
        assertThat(convertValue(float.class.getCanonicalName(), revertValue(1.0F), classLoader)).isEqualTo(1f);
        assertThat(convertValue(Float.class.getCanonicalName(), revertValue(1.0F), classLoader)).isEqualTo(1f);
        assertThat(convertValue(char.class.getCanonicalName(), revertValue('a'), classLoader)).isEqualTo('a');
        assertThat(convertValue(Character.class.getCanonicalName(), revertValue('a'), classLoader)).isEqualTo('a');
        assertThat(convertValue(short.class.getCanonicalName(), revertValue((short) 1), classLoader)).isEqualTo((short) 1);
        assertThat(convertValue(Short.class.getCanonicalName(), revertValue((short) 1), classLoader)).isEqualTo((short) 1);
        assertThat(convertValue(byte.class.getCanonicalName(), revertValue("0".getBytes()[0]), classLoader)).isEqualTo("0".getBytes()[0]);
        assertThat(convertValue(Byte.class.getCanonicalName(), revertValue("0".getBytes()[0]), classLoader)).isEqualTo("0".getBytes()[0]);
        assertThat(convertValue(LocalDate.class.getCanonicalName(), revertValue(LocalDate.of(2018, 10, 20)), classLoader)).isEqualTo(LocalDate.of(2018, 10, 20));
        assertThat(convertValue(LocalDateTime.class.getCanonicalName(), revertValue(LocalDateTime.of(2018, 10, 20, 2, 3)), classLoader)).isEqualTo(LocalDateTime.of(2018, 10, 20, 2, 3));
        assertThat(convertValue(LocalDateTime.class.getCanonicalName(), revertValue(LocalDateTime.of(2018, 10, 20, 2, 3, 1)), classLoader)).isEqualTo(LocalDateTime.of(2018, 10, 20, 2, 3, 1));
        assertThat(convertValue(LocalDateTime.class.getCanonicalName(), revertValue(LocalDateTime.of(2018, 10, 20, 2, 3, 1, 9999)), classLoader)).isEqualTo(LocalDateTime.of(2018, 10, 20, 2, 3, 1, 9999));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), revertValue(LocalTime.of(2, 3)), classLoader)).isEqualTo(LocalTime.of(2, 3));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), revertValue(LocalTime.of(2, 3, 1)), classLoader)).isEqualTo(LocalTime.of(2, 3, 1));
        assertThat(convertValue(LocalTime.class.getCanonicalName(), revertValue(LocalTime.of(2, 3, 1, 9999)), classLoader)).isEqualTo(LocalTime.of(2, 3, 1, 9999));
        assertThat(convertValue(EnumTest.class.getCanonicalName(), revertValue(EnumTest.FIRST), classLoader)).isEqualTo(EnumTest.FIRST);
        assertThat(convertValue(String.class.getCanonicalName(), revertValue(null), classLoader)).isNull();
    }

    @Test
    public void convertValueFailLoadClassTest() {
        assertThatThrownBy(() -> convertValue("my.NotExistingClass", "Test", classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Impossible to load ");
    }

    @Test
    public void convertValueFailUnsupportedTest() {
        assertThatThrownBy(() -> convertValue(RuleScenarioRunnerHelperTest.class.getCanonicalName(), "Test", classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageEndingWith("Please use an MVEL expression to use it.");
    }

    @Test
    public void convertValueFailPrimitiveNullTest() {
        assertThatThrownBy(() -> convertValue("int", null, classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(" is not a String or an instance of");
    }

    @Test
    public void convertValueFailNotStringOrTypeTest() {
        assertThatThrownBy(() -> convertValue(RuleScenarioRunnerHelperTest.class.getCanonicalName(), 1, classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Object 1 is not a String or an instance of");
    }

    @Test
    public void convertValueFailParsing() {
        String integerCanonicalName = Integer.class.getCanonicalName();
        assertThatThrownBy(() -> convertValue(integerCanonicalName, "wrongValue", classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Impossible to parse 'wrongValue' as " + integerCanonicalName);
    }

    @Test
    public void convertValueEnumWrongValue() {
        String enumTestCanonicalName = EnumTest.class.getCanonicalName();
        assertThatThrownBy(() -> convertValue(EnumTest.class.getCanonicalName(), "FIRS", classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Impossible to parse 'FIRS' as " + enumTestCanonicalName);
    }

    @Test
    public void loadClassTest() {
        assertThat(loadClass(String.class.getCanonicalName(), classLoader)).isEqualTo(String.class);
        assertThat(loadClass(int.class.getCanonicalName(), classLoader)).isEqualTo(int.class);
        assertThat(loadClass(RuleScenarioRunnerHelperTest.class.getCanonicalName(), classLoader)).isEqualTo(RuleScenarioRunnerHelperTest.class);
        assertThat(loadClass(EnumTest.class.getCanonicalName(), classLoader)).isEqualTo(EnumTest.class);

        assertThatThrownBy(() -> loadClass(null, classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class null");

        assertThatThrownBy(() -> loadClass("NotExistingClass", classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class NotExistingClass");
    }

    @Test
    public void getFieldTest() {
        assertThat(getField(Person.class, "firstName")).isNotNull();
        assertThat(getField(SubPerson.class, "firstName")).isNotNull();
        assertThat(getField(SubPerson.class, "additionalField")).isNotNull();
        assertThatThrownBy(() -> getField(Person.class, "notExistingField"))
                .isInstanceOf(ScenarioException.class)
                .hasMessageStartingWith("Impossible to find field with name ");
    }
}