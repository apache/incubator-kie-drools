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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.scenariosimulation.backend.model.Dispute;
import org.drools.scenariosimulation.backend.model.NotEmptyConstructor;
import org.drools.scenariosimulation.backend.model.Person;
import org.drools.scenariosimulation.backend.model.SubPerson;
import org.drools.scenariosimulation.backend.runner.RuleScenarioRunnerHelperTest;
import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.junit.Test;

import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.convertValue;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.getField;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.loadClass;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.revertValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

        Object result = ScenarioBeanUtil.fillBean(Dispute.class.getCanonicalName(), paramsToSet, classLoader);

        assertTrue(result instanceof Dispute);

        Dispute dispute = (Dispute) result;
        assertEquals(dispute.getCreator().getFirstName(), FIRST_NAME);
        assertEquals(dispute.getCreator().getAge(), AGE);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanLoadClassTest() {
        ScenarioBeanUtil.fillBean("FakeCanonicalName", new HashMap<>(), classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNotEmptyConstructorTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("name"), null);

        ScenarioBeanUtil.fillBean(NotEmptyConstructor.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("fakeField"), null);

        ScenarioBeanUtil.fillBean(Dispute.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNullClassTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("fakeField"), null);

        ScenarioBeanUtil.fillBean(null, paramsToSet, classLoader);
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
        List<String> pathToProperty = Arrays.asList("fakeField");

        String message = "Impossible to find field with name 'fakeField' in class " + Dispute.class.getCanonicalName();
        Assertions.assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, true))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void navigateToObjectNoStepCreationTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = Arrays.asList("creator", "firstName");

        String message = "Impossible to reach field firstName because a step is not instantiated";
        Assertions.assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, false))
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
        assertEquals(1.0D, convertValue(double.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0D, convertValue(Double.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0F, convertValue(float.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0F, convertValue(Float.class.getCanonicalName(), "1.0", classLoader));
        assertEquals('a', convertValue(char.class.getCanonicalName(), "a", classLoader));
        assertEquals('a', convertValue(Character.class.getCanonicalName(), "a", classLoader));
        assertEquals((short) 1, convertValue(short.class.getCanonicalName(), "1", classLoader));
        assertEquals((short) 1, convertValue(Short.class.getCanonicalName(), "1", classLoader));
        assertEquals("0".getBytes()[0], convertValue(byte.class.getCanonicalName(), "0", classLoader));
        assertEquals("0".getBytes()[0], convertValue(Byte.class.getCanonicalName(), "0", classLoader));
        assertEquals(LocalDate.of(2018, 5, 20), convertValue(LocalDate.class.getCanonicalName(), "2018-05-20", classLoader));
        assertNull(convertValue(Float.class.getCanonicalName(), null, classLoader));
    }

    @Test
    public void revertValueTest() {
        assertEquals("Test", revertValue("Test"));
        assertEquals("false", revertValue(false));
        assertEquals("1", revertValue(1));
        assertEquals("1L", revertValue(1L));
        assertEquals("1.0D", revertValue(1.0D));
        assertEquals("1.0F", revertValue(1.0F));
        assertEquals("a", revertValue('a'));
        assertEquals("1", revertValue((short) 1));
        assertEquals(String.valueOf("0".getBytes()[0]), revertValue("0".getBytes()[0]));
        assertEquals("null", revertValue(null));
        assertEquals("2018-10-20", revertValue(LocalDate.of(2018, 10, 20)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailLoadClassTest() {
        convertValue("my.NotExistingClass", "Test", classLoader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailUnsupportedTest() {
        convertValue(RuleScenarioRunnerHelperTest.class.getCanonicalName(), "Test", classLoader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailPrimitiveNullTest() {
        convertValue("int", null, classLoader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailNotStringOrTypeTest() {
        convertValue(RuleScenarioRunnerHelperTest.class.getCanonicalName(), 1, classLoader);
    }

    @Test
    public void loadClassTest() {
        assertEquals(String.class, loadClass(String.class.getCanonicalName(), classLoader));
        assertEquals(int.class, loadClass(int.class.getCanonicalName(), classLoader));

        Assertions.assertThatThrownBy(() -> loadClass(null, classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class null");

        Assertions.assertThatThrownBy(() -> loadClass("NotExistingClass", classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class NotExistingClass");
    }

    @Test
    public void getFieldTest() {
        assertNotNull(getField(Person.class, "firstName"));
        assertNotNull(getField(SubPerson.class, "firstName"));
        assertNotNull(getField(SubPerson.class, "additionalField"));
        Assertions.assertThatThrownBy(() -> getField(Person.class, "notExistingField"))
                .isInstanceOf(ScenarioException.class)
                .hasMessageStartingWith("Impossible to find field with name ");
    }
}