/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.backend.runner.model.ValueWrapper.errorEmptyMessage;
import static org.drools.scenariosimulation.backend.runner.model.ValueWrapper.of;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.convertValue;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.getField;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.loadClass;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.revertValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ScenarioBeanUtilTest {

    private static String FIRST_NAME = "firstNameToSet";
    private static int AGE = 10;
    private static ClassLoader classLoader = ScenarioBeanUtilTest.class.getClassLoader();

    @Test
    public void fillBeanTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("creator", "firstName"), FIRST_NAME);
        paramsToSet.put(Arrays.asList("creator", "age"), AGE);

        Object result = ScenarioBeanUtil.fillBean(errorEmptyMessage(), Dispute.class.getCanonicalName(), paramsToSet, classLoader);

        assertTrue(result instanceof Dispute);

        Dispute dispute = (Dispute) result;
        assertEquals(dispute.getCreator().getFirstName(), FIRST_NAME);
        assertEquals(dispute.getCreator().getAge(), AGE);
    }

    @Test
    public void fillBeanTestWithInitialInstanceTest() {
        Dispute dispute = new Dispute();

        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("creator", "firstName"), FIRST_NAME);
        paramsToSet.put(Arrays.asList("creator", "age"), AGE);

        Object result = ScenarioBeanUtil.fillBean(of(dispute), Dispute.class.getCanonicalName(), paramsToSet, classLoader);

        assertTrue(result instanceof Dispute);
        assertSame(dispute, result);

        assertEquals(dispute.getCreator().getFirstName(), FIRST_NAME);
        assertEquals(dispute.getCreator().getAge(), AGE);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanLoadClassTest() {
        ScenarioBeanUtil.fillBean(errorEmptyMessage(), "FakeCanonicalName", new HashMap<>(), classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNotEmptyConstructorTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(singletonList("name"), null);

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), NotEmptyConstructor.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(singletonList("fakeField"), null);

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), Dispute.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNullClassTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(singletonList("fakeField"), null);

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), null, paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailWrongTypeTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(singletonList("description"), new ArrayList<>());

        ScenarioBeanUtil.fillBean(errorEmptyMessage(), Dispute.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test
    public void fillBeanEmptyValueTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(emptyList(), null);

        assertNull(ScenarioBeanUtil.fillBean(of(null), String.class.getCanonicalName(), paramsToSet, classLoader));
    }

    @Test
    public void navigateToObjectTest() {
        Dispute dispute = new Dispute();
        Person creator = new Person();
        creator.setFirstName(FIRST_NAME);
        dispute.setCreator(creator);
        List<String> pathToProperty = Arrays.asList("creator", "firstName");

        ScenarioBeanWrapper<?> scenarioBeanWrapper = ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, true);
        Object targetObject = scenarioBeanWrapper.getBean();

        assertEquals(targetObject, FIRST_NAME);

        assertNull(ScenarioBeanUtil.navigateToObject(null, Collections.emptyList()).getBean());
    }

    @Test
    public void navigateToObjectFakeFieldTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = singletonList("fakeField");

        String message = "Impossible to find field with name 'fakeField' in class " + Dispute.class.getCanonicalName();
        assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, true))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void navigateToObjectNoStepCreationTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = Arrays.asList("creator", "firstName");

        String message = "Impossible to reach field firstName because a step is not instantiated";
        assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, false))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void convertValueTest() {
        assertEquals("Test", convertValue(String.class.getCanonicalName(), "Test", classLoader));
        assertEquals(false, convertValue(boolean.class.getCanonicalName(), "false", classLoader));
        assertEquals(true, convertValue(Boolean.class.getCanonicalName(), "true", classLoader));
        assertEquals(1, convertValue(int.class.getCanonicalName(), "1", classLoader));
        assertEquals(1, convertValue(Integer.class.getCanonicalName(), "1", classLoader));
        assertEquals(1L, convertValue(long.class.getCanonicalName(), "1", classLoader));
        assertEquals(1L, convertValue(Long.class.getCanonicalName(), "1", classLoader));
        assertEquals(1.0d, convertValue(double.class.getCanonicalName(), "1", classLoader));
        assertEquals(1.0d, convertValue(Double.class.getCanonicalName(), "1", classLoader));
        assertEquals(1.0f, convertValue(float.class.getCanonicalName(), "1", classLoader));
        assertEquals(1.0f, convertValue(Float.class.getCanonicalName(), "1", classLoader));
        assertEquals(1.0d, convertValue(double.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0d, convertValue(Double.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0f, convertValue(float.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0f, convertValue(Float.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0d, convertValue(double.class.getCanonicalName(), "1.0d", classLoader));
        assertEquals(1.0d, convertValue(Double.class.getCanonicalName(), "1.0d", classLoader));
        assertEquals(1.0f, convertValue(float.class.getCanonicalName(), "1.0f", classLoader));
        assertEquals(1.0f, convertValue(Float.class.getCanonicalName(), "1.0f", classLoader));
        assertEquals(1.0d, convertValue(double.class.getCanonicalName(), "1.0D", classLoader));
        assertEquals(1.0d, convertValue(Double.class.getCanonicalName(), "1.0D", classLoader));
        assertEquals(1.0f, convertValue(float.class.getCanonicalName(), "1.0F", classLoader));
        assertEquals(1.0f, convertValue(Float.class.getCanonicalName(), "1.0F", classLoader));
        assertEquals('a', convertValue(char.class.getCanonicalName(), "a", classLoader));
        assertEquals('a', convertValue(Character.class.getCanonicalName(), "a", classLoader));
        assertEquals((short) 1, convertValue(short.class.getCanonicalName(), "1", classLoader));
        assertEquals((short) 1, convertValue(Short.class.getCanonicalName(), "1", classLoader));
        assertEquals("0".getBytes()[0], convertValue(byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader));
        assertEquals("0".getBytes()[0], convertValue(Byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader));
        assertEquals(LocalDate.of(2018, 5, 20), convertValue(LocalDate.class.getCanonicalName(), "2018-05-20", classLoader));
        assertEquals(EnumTest.FIRST, convertValue(EnumTest.class.getCanonicalName(), "FIRST", classLoader));
        assertNull(convertValue(Float.class.getCanonicalName(), null, classLoader));
    }

    @Test
    public void revertValueTest() {
        assertEquals("Test", revertValue("Test"));
        assertEquals("false", revertValue(false));
        assertEquals("true", revertValue(Boolean.TRUE));
        assertEquals("1", revertValue(1));
        assertEquals("1", revertValue(1L));
        assertEquals("1.0d", revertValue(1.0d));
        assertEquals("NaN", revertValue(Double.NaN));
        assertEquals("Infinity", revertValue(Double.POSITIVE_INFINITY));
        assertEquals("-Infinity", revertValue(Double.NEGATIVE_INFINITY));
        assertEquals("1.0f", revertValue(1.0f));
        assertEquals("a", revertValue('a'));
        assertEquals("1", revertValue((short) 1));
        assertEquals(String.valueOf("0".getBytes()[0]), revertValue("0".getBytes()[0]));
        assertEquals("null", revertValue(null));
        assertEquals("2018-10-20", revertValue(LocalDate.of(2018, 10, 20)));
        assertEquals("FIRST", revertValue(EnumTest.FIRST));
    }

    @Test
    public void convertAndRevertValue() {
        assertEquals("Test", revertValue(convertValue(String.class.getCanonicalName(), "Test", classLoader)));
        assertEquals("false", revertValue(convertValue(boolean.class.getCanonicalName(), "false", classLoader)));
        assertEquals("true", revertValue(convertValue(Boolean.class.getCanonicalName(), "true", classLoader)));
        assertEquals("1", revertValue(convertValue(int.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1", revertValue(convertValue(Integer.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1", revertValue(convertValue(long.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1", revertValue(convertValue(Long.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(double.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(Double.class.getCanonicalName(), "1", classLoader)));
        assertEquals("NaN", revertValue(convertValue(double.class.getCanonicalName(), "NaN", classLoader)));
        assertEquals("NaN", revertValue(convertValue(Double.class.getCanonicalName(), "NaN", classLoader)));
        assertEquals("Infinity", revertValue(convertValue(double.class.getCanonicalName(), "Infinity", classLoader)));
        assertEquals("Infinity", revertValue(convertValue(Double.class.getCanonicalName(), "Infinity", classLoader)));
        assertEquals("-Infinity", revertValue(convertValue(double.class.getCanonicalName(), "-Infinity", classLoader)));
        assertEquals("-Infinity", revertValue(convertValue(Double.class.getCanonicalName(), "-Infinity", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(float.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(Float.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(double.class.getCanonicalName(), "1.0", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(Double.class.getCanonicalName(), "1.0", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(float.class.getCanonicalName(), "1.0", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(Float.class.getCanonicalName(), "1.0", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(double.class.getCanonicalName(), "1.0d", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(Double.class.getCanonicalName(), "1.0d", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(float.class.getCanonicalName(), "1.0f", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(Float.class.getCanonicalName(), "1.0f", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(double.class.getCanonicalName(), "1.0D", classLoader)));
        assertEquals("1.0d", revertValue(convertValue(Double.class.getCanonicalName(), "1.0D", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(float.class.getCanonicalName(), "1.0F", classLoader)));
        assertEquals("1.0f", revertValue(convertValue(Float.class.getCanonicalName(), "1.0F", classLoader)));
        assertEquals("a", revertValue(convertValue(char.class.getCanonicalName(), "a", classLoader)));
        assertEquals("a", revertValue(convertValue(Character.class.getCanonicalName(), "a", classLoader)));
        assertEquals("1", revertValue(convertValue(short.class.getCanonicalName(), "1", classLoader)));
        assertEquals("1", revertValue(convertValue(Short.class.getCanonicalName(), "1", classLoader)));
        assertEquals(Byte.toString("0".getBytes()[0]), revertValue(convertValue(byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader)));
        assertEquals(Byte.toString("0".getBytes()[0]), revertValue(convertValue(Byte.class.getCanonicalName(), Byte.toString("0".getBytes()[0]), classLoader)));
        assertEquals("2018-05-20", revertValue(convertValue(LocalDate.class.getCanonicalName(), "2018-05-20", classLoader)));
        assertEquals("FIRST", revertValue(convertValue(EnumTest.class.getCanonicalName(), "FIRST", classLoader)));
        assertEquals("null", revertValue(convertValue(Float.class.getCanonicalName(), null, classLoader)));
    }

    @Test
    public void revertAndConvertValueTest() {
        assertEquals("Test", convertValue(String.class.getCanonicalName(), revertValue("Test"), classLoader));
        assertEquals(false, convertValue(boolean.class.getCanonicalName(), revertValue(false), classLoader));
        assertEquals(Boolean.TRUE, convertValue(Boolean.class.getCanonicalName(), revertValue(Boolean.TRUE), classLoader));
        assertEquals(1, convertValue(int.class.getCanonicalName(), revertValue(1), classLoader));
        assertEquals(1, convertValue(Integer.class.getCanonicalName(), revertValue(1), classLoader));
        assertEquals(1L, convertValue(long.class.getCanonicalName(), revertValue(1L), classLoader));
        assertEquals(1L, convertValue(Long.class.getCanonicalName(), revertValue(1L), classLoader));
        assertEquals(1d, convertValue(double.class.getCanonicalName(), revertValue(1), classLoader));
        assertEquals(1d, convertValue(Double.class.getCanonicalName(), revertValue(1), classLoader));
        assertEquals(1f, convertValue(float.class.getCanonicalName(), revertValue(1), classLoader));
        assertEquals(1f, convertValue(Float.class.getCanonicalName(), revertValue(1), classLoader));
        assertEquals(1d, convertValue(double.class.getCanonicalName(), revertValue(1.0), classLoader));
        assertEquals(1d, convertValue(Double.class.getCanonicalName(), revertValue(1.0), classLoader));
        assertEquals(1f, convertValue(float.class.getCanonicalName(), revertValue(1.0), classLoader));
        assertEquals(1f, convertValue(Float.class.getCanonicalName(), revertValue(1.0), classLoader));
        assertEquals(1d, convertValue(double.class.getCanonicalName(), revertValue(1.0d), classLoader));
        assertEquals(1d, convertValue(Double.class.getCanonicalName(), revertValue(1.0d), classLoader));
        assertEquals(1f, convertValue(float.class.getCanonicalName(), revertValue(1.0f), classLoader));
        assertEquals(1f, convertValue(Float.class.getCanonicalName(), revertValue(1.0f), classLoader));
        assertEquals(1d, convertValue(double.class.getCanonicalName(), revertValue(1.0D), classLoader));
        assertEquals(1d, convertValue(Double.class.getCanonicalName(), revertValue(1.0D), classLoader));
        assertEquals(1f, convertValue(float.class.getCanonicalName(), revertValue(1.0F), classLoader));
        assertEquals(1f, convertValue(Float.class.getCanonicalName(), revertValue(1.0F), classLoader));
        assertEquals('a', convertValue(char.class.getCanonicalName(), revertValue('a'), classLoader));
        assertEquals('a', convertValue(Character.class.getCanonicalName(), revertValue('a'), classLoader));
        assertEquals((short) 1, convertValue(short.class.getCanonicalName(), revertValue((short) 1), classLoader));
        assertEquals((short) 1, convertValue(Short.class.getCanonicalName(), revertValue((short) 1), classLoader));
        assertEquals("0".getBytes()[0], convertValue(byte.class.getCanonicalName(), revertValue("0".getBytes()[0]), classLoader));
        assertEquals("0".getBytes()[0], convertValue(Byte.class.getCanonicalName(), revertValue("0".getBytes()[0]), classLoader));
        assertEquals(LocalDate.of(2018, 10, 20), convertValue(LocalDate.class.getCanonicalName(), revertValue(LocalDate.of(2018, 10, 20)), classLoader));
        assertEquals(EnumTest.FIRST, convertValue(EnumTest.class.getCanonicalName(), revertValue(EnumTest.FIRST), classLoader));
        assertNull(convertValue(String.class.getCanonicalName(), revertValue(null), classLoader));
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
        assertEquals(String.class, loadClass(String.class.getCanonicalName(), classLoader));
        assertEquals(int.class, loadClass(int.class.getCanonicalName(), classLoader));
        assertEquals(RuleScenarioRunnerHelperTest.class, loadClass(RuleScenarioRunnerHelperTest.class.getCanonicalName(), classLoader));
        assertEquals(EnumTest.class, loadClass(EnumTest.class.getCanonicalName(), classLoader));

        assertThatThrownBy(() -> loadClass(null, classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class null");

        assertThatThrownBy(() -> loadClass("NotExistingClass", classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class NotExistingClass");
    }

    @Test
    public void getFieldTest() {
        assertNotNull(getField(Person.class, "firstName"));
        assertNotNull(getField(SubPerson.class, "firstName"));
        assertNotNull(getField(SubPerson.class, "additionalField"));
        assertThatThrownBy(() -> getField(Person.class, "notExistingField"))
                .isInstanceOf(ScenarioException.class)
                .hasMessageStartingWith("Impossible to find field with name ");
    }
}