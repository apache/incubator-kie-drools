/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.core.compiler.DMNTypeRegistryV11;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

public class DMNTypeTest {

    private static final DMNTypeRegistry typeRegistry = new DMNTypeRegistryV11(Collections.emptyMap());
    private static final DMNType FEEL_STRING = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "string");
    private static final DMNType FEEL_NUMBER = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "number");

    @Test
    public void testDROOLS2147() {
        // DROOLS-2147
        final String testNS = "testDROOLS2147";
        
        final Map<String, DMNType> personPrototype = prototype(entry("name", FEEL_STRING), entry("age", FEEL_NUMBER));
        final DMNType dmnPerson = typeRegistry.registerType(new CompositeTypeImpl(testNS, "person", null, false, personPrototype, null, null));
        final DMNType dmnPersonList = typeRegistry.registerType(new CompositeTypeImpl(testNS, "personList", null, true, null, dmnPerson, null));
        final DMNType dmnListOfPersonsGrouped = typeRegistry.registerType(new CompositeTypeImpl(testNS, "groups", null, true, null, dmnPersonList, null));


        final Map<String, Object> instanceBob = prototype(entry("name", "Bob"), entry("age", 42));
        final Map<String, Object> instanceJohn = prototype(entry("name", "John"), entry("age", 47));

        final Map<String, Object> instanceNOTaPerson = prototype(entry("name", "NOTAPERSON"));

        assertTrue(dmnPerson.isAssignableValue(instanceBob));
        assertTrue(dmnPerson.isAssignableValue(instanceJohn));

        assertFalse(dmnPerson.isAssignableValue(instanceNOTaPerson));

        final List<Map<String, Object>> onlyBob = Collections.singletonList(instanceBob);
        final List<Map<String, Object>> bobANDjohn = Arrays.asList(instanceBob, instanceJohn);
        final List<Map<String, Object>> johnANDnotAPerson = Arrays.asList(instanceJohn, instanceNOTaPerson);

        assertTrue(dmnPersonList.isAssignableValue(onlyBob));
        assertTrue(dmnPersonList.isAssignableValue(bobANDjohn));
        assertFalse(dmnPersonList.isAssignableValue(johnANDnotAPerson));
        assertTrue(dmnPersonList.isAssignableValue(instanceBob)); // because accordingly to FEEL spec, bob=[bob]
        
        final List<List<Map<String, Object>>> the2ListsThatContainBob = Arrays.asList(onlyBob, bobANDjohn);
        assertTrue(dmnListOfPersonsGrouped.isAssignableValue(the2ListsThatContainBob));

        final List<List<Map<String, Object>>> the3Lists = Arrays.asList(onlyBob, bobANDjohn, johnANDnotAPerson);
        assertFalse(dmnListOfPersonsGrouped.isAssignableValue(the3Lists));

        final List<Object> groupsOfBobAndBobHimself = Arrays.asList(instanceBob, onlyBob, bobANDjohn);
        assertTrue(dmnListOfPersonsGrouped.isAssignableValue(groupsOfBobAndBobHimself)); // [bob, [bob], [bob, john]] because for the property of FEEL spec a=[a] is equivalent to [[bob], [bob], [bob, john]]

        final DMNType listOfGroups = typeRegistry.registerType(new CompositeTypeImpl(testNS, "listOfGroups", null, true, null, dmnListOfPersonsGrouped, null));
        final List<Object> groupsContainingBobPartitionedBySize = Arrays.asList(the2ListsThatContainBob, Collections.singletonList(bobANDjohn));
        assertTrue(listOfGroups.isAssignableValue(groupsContainingBobPartitionedBySize)); // [ [[B], [B, J]], [[B, J]] ]
    }

    @Test
    public void testAllowedValuesForASimpleTypeCollection() {
        // DROOLS-2357
        final String testNS = "testDROOLS2357";

        final FEEL feel = FEEL.newInstance();
        final DMNType tDecision1 = typeRegistry.registerType(new SimpleTypeImpl(testNS, "tListOfVowels", null, true, feel.evaluateUnaryTests("\"a\",\"e\",\"i\",\"o\",\"u\""), FEEL_STRING, BuiltInType.STRING));

        assertTrue(tDecision1.isAssignableValue("a"));
        assertTrue(tDecision1.isAssignableValue(Collections.singletonList("a")));

        assertFalse(tDecision1.isAssignableValue("z"));

        assertTrue(tDecision1.isAssignableValue(Arrays.asList("a", "e")));

        assertFalse(tDecision1.isAssignableValue(Arrays.asList("a", "e", "zzz")));
    }

}

