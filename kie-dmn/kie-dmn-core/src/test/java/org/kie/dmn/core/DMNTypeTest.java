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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

public class DMNTypeTest {

    private static final DMNTypeRegistry typeRegistry = new DMNTypeRegistry();
    private static final DMNType FEEL_STRING = typeRegistry.resolveType(DMNModelInstrumentedBase.URI_FEEL, "string");
    private static final DMNType FEEL_NUMBER = typeRegistry.resolveType(DMNModelInstrumentedBase.URI_FEEL, "number");

    @Test
    public void testDROOLS2147() {
        // DROOLS-2147
        final String testNS = "testDROOLS2147";
        
        Map<String, DMNType> personPrototype = prototype(entry("name", FEEL_STRING), entry("age", FEEL_NUMBER));
        DMNType dmnPerson = typeRegistry.registerType(new CompositeTypeImpl(testNS, "person", null, false, personPrototype, null, null));
        DMNType dmnPersonList = typeRegistry.registerType(new CompositeTypeImpl(testNS, "personList", null, true, null, dmnPerson, null));
        DMNType dmnListOfPersonsGrouped = typeRegistry.registerType(new CompositeTypeImpl(testNS, "groups", null, true, null, dmnPersonList, null));


        Map<String, Object> instanceBob = prototype(entry("name", "Bob"), entry("age", 42));
        Map<String, Object> instanceJohn = prototype(entry("name", "John"), entry("age", 47));

        Map<String, Object> instanceNOTaPerson = prototype(entry("name", "NOTAPERSON"));

        assertTrue(dmnPerson.isAssignableValue(instanceBob));
        assertTrue(dmnPerson.isAssignableValue(instanceJohn));

        assertFalse(dmnPerson.isAssignableValue(instanceNOTaPerson));

        List<Map<String, Object>> onlyBob = Arrays.asList(instanceBob);
        List<Map<String, Object>> bobANDjohn = Arrays.asList(instanceBob, instanceJohn);
        List<Map<String, Object>> johnANDnotAPerson = Arrays.asList(instanceJohn, instanceNOTaPerson);

        assertTrue(dmnPersonList.isAssignableValue(onlyBob));
        assertTrue(dmnPersonList.isAssignableValue(bobANDjohn));
        assertFalse(dmnPersonList.isAssignableValue(johnANDnotAPerson));
        assertTrue(dmnPersonList.isAssignableValue(instanceBob)); // because accordingly to FEEL spec, bob=[bob]
        
        List<List<Map<String, Object>>> the2ListsThatContainBob = Arrays.asList(onlyBob, bobANDjohn);
        assertTrue(dmnListOfPersonsGrouped.isAssignableValue(the2ListsThatContainBob));

        List<List<Map<String, Object>>> the3Lists = Arrays.asList(onlyBob, bobANDjohn, johnANDnotAPerson);
        assertFalse(dmnListOfPersonsGrouped.isAssignableValue(the3Lists));

        List<Object> groupsOfBobAndBobHimself = Arrays.asList(instanceBob, onlyBob, bobANDjohn);
        assertTrue(dmnListOfPersonsGrouped.isAssignableValue(groupsOfBobAndBobHimself)); // [bob, [bob], [bob, john]] because for the property of FEEL spec a=[a] is equivalent to [[bob], [bob], [bob, john]]

        DMNType listOfGroups = typeRegistry.registerType(new CompositeTypeImpl(testNS, "listOfGroups", null, true, null, dmnListOfPersonsGrouped, null));
        List<Object> groupsContainingBobPartitionedBySize = Arrays.asList(the2ListsThatContainBob, Arrays.asList(bobANDjohn));
        assertTrue(listOfGroups.isAssignableValue(groupsContainingBobPartitionedBySize)); // [ [[B], [B, J]], [[B, J]] ]
    }
}

