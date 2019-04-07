/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.workitem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.datatype.impl.type.EnumDataType;
import org.jbpm.process.core.datatype.impl.type.ListDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.workitem.enums.AnimalsEnum;
import org.jbpm.process.workitem.enums.CarsEnum;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkDefinitionImplTest extends AbstractBaseTest {

    @Test
    public void testServices() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 9);

        WorkDefinitionImpl testServiceOne = repoResults.get("TestServiceOne");
        assertNotNull(testServiceOne);
        assertEquals("TestServiceOne", testServiceOne.getName());
        assertEquals("TestServiceOne", testServiceOne.getDisplayName());
        assertEquals("Test Service One", testServiceOne.getDescription());
        assertEquals(3, testServiceOne.getParameters().size());
        assertEquals("testserviceone.png", testServiceOne.getIcon());
        assertEquals("MyTestServices", testServiceOne.getCategory());
        assertEquals(0, testServiceOne.getDependencies().length);
        assertEquals("MyTestServices", testServiceOne.getCategory());
        assertEquals("mvel", testServiceOne.getWidType());

        WorkDefinitionImpl testServiceTwo = repoResults.get("TestServiceTwo");
        assertNotNull(testServiceTwo);
        assertEquals(2, testServiceTwo.getResults().size());
        assertTrue(testServiceTwo.getResult("result1").getType() instanceof StringDataType);
        assertTrue(testServiceTwo.getResult("result2").getType() instanceof StringDataType);
        assertEquals("mvel", testServiceTwo.getWidType());

        WorkDefinitionImpl testServiceThree = repoResults.get("TestServiceThree");
        assertNotNull(testServiceThree);
        assertEquals("1.0", testServiceThree.getVersion());
        assertEquals("org.drools.eclipse.flow.common.editor.editpart.work.SampleCustomEditor", testServiceThree.getCustomEditor());
        assertEquals("org.jbpm.process.workitem.MyHandler", testServiceThree.getDefaultHandler());
        assertEquals(2, testServiceThree.getDependencies().length);
        assertEquals(2, testServiceThree.getMavenDependencies().length);
        assertEquals("mvel", testServiceThree.getWidType());

        // service defined as json wids
        WorkDefinitionImpl testServiceFour = repoResults.get("TestServiceFour");
        assertNotNull(testServiceFour);
        assertEquals("TestServiceFour", testServiceFour.getName());
        assertEquals("TestServiceFour", testServiceFour.getDisplayName());
        assertEquals("Test Service Four", testServiceFour.getDescription());
        assertEquals(3, testServiceFour.getParameters().size());
        assertEquals(0, testServiceFour.getResults().size());
        assertEquals("1.0", testServiceFour.getVersion());
        assertEquals(2, testServiceFour.getDependencies().length);
        assertEquals("json", testServiceFour.getWidType());


        WorkDefinitionImpl testServiceFour2 = repoResults.get("TestServiceFour2");
        assertNotNull(testServiceFour2);
        assertEquals("TestServiceFour2", testServiceFour2.getName());
        assertEquals("TestServiceFour2", testServiceFour2.getDisplayName());
        assertEquals("Test Service Four2", testServiceFour2.getDescription());
        assertEquals(2, testServiceFour2.getParameters().size());
        assertEquals(2, testServiceFour2.getResults().size());
        assertTrue(testServiceFour2.getResult("c").getType() instanceof ListDataType);
        assertTrue(testServiceFour2.getResult("d").getType() instanceof StringDataType);
        assertEquals("2.0", testServiceFour2.getVersion());
        assertEquals(3, testServiceFour2.getDependencies().length);
        assertEquals(2, testServiceFour2.getMavenDependencies().length);
        assertEquals("json", testServiceFour2.getWidType());

        // workitem with no dependency defined
        WorkDefinitionImpl testServiceFive = repoResults.get("TestServiceFive");
        assertNotNull(testServiceFive);
        assertEquals("TestServiceFive", testServiceFive.getName());
        assertEquals("TestServiceFive", testServiceFive.getDisplayName());
        assertNull(testServiceFive.getDependencies());

    }

    @Test
    public void testParameterValuesWithEnumsOnly() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 9);

        WorkDefinitionImpl testServiceWithParamValues = repoResults.get("TestServiceWithParamValues");
        assertNotNull(testServiceWithParamValues);
        assertNotNull(testServiceWithParamValues.getParameterValues());

        Map<String, Object> parameterValues = testServiceWithParamValues.getParameterValues();
        assertNotNull(parameterValues);
        assertEquals(parameterValues.size(), 2);
        for( Map.Entry<String, Object> entry : parameterValues.entrySet() ) {
            assertTrue( entry.getValue() instanceof  EnumDataType );

            if (entry.getKey().equals("param1")) {
                EnumDataType paramEnum = (EnumDataType) entry.getValue();
                assertEquals("org.jbpm.process.workitem.enums.AnimalsEnum", paramEnum.getClassName());
                Map<String, Object> paramValuesMap = paramEnum.getValueMap(null);
                assertNotNull(paramValuesMap);
                assertEquals(5, paramValuesMap.size());

                assertTrue(paramValuesMap.containsKey("DOGS"));
                assertTrue(paramValuesMap.containsKey("CATS"));
                assertTrue(paramValuesMap.containsKey("ELEPHANTS"));
                assertTrue(paramValuesMap.containsKey("GIRAFFES"));
                assertTrue(paramValuesMap.containsKey("BIRDS"));

                assertEquals(paramValuesMap.get("DOGS"), AnimalsEnum.DOGS);
                assertEquals(paramValuesMap.get("CATS"), AnimalsEnum.CATS);
                assertEquals(paramValuesMap.get("ELEPHANTS"), AnimalsEnum.ELEPHANTS);
                assertEquals(paramValuesMap.get("GIRAFFES"), AnimalsEnum.GIRAFFES);
                assertEquals(paramValuesMap.get("BIRDS"), AnimalsEnum.BIRDS);

            } else if(entry.getKey().equals("param3")) {
                EnumDataType paramEnum = (EnumDataType) entry.getValue();
                assertEquals("org.jbpm.process.workitem.enums.CarsEnum", paramEnum.getClassName());
                Map<String, Object> paramValuesMap = paramEnum.getValueMap(null);
                assertNotNull(paramValuesMap);
                assertEquals(5, paramValuesMap.size());

                assertTrue(paramValuesMap.containsKey("HONDA"));
                assertTrue(paramValuesMap.containsKey("MAZDA"));
                assertTrue(paramValuesMap.containsKey("NISSAN"));
                assertTrue(paramValuesMap.containsKey("TOYOTA"));
                assertTrue(paramValuesMap.containsKey("FORD"));

                assertEquals(paramValuesMap.get("HONDA"), CarsEnum.HONDA);
                assertEquals(paramValuesMap.get("MAZDA"), CarsEnum.MAZDA);
                assertEquals(paramValuesMap.get("NISSAN"), CarsEnum.NISSAN);
                assertEquals(paramValuesMap.get("TOYOTA"), CarsEnum.TOYOTA);
                assertEquals(paramValuesMap.get("FORD"), CarsEnum.FORD);
            } else {
                fail("invalid parameter name");
            }
        }

    }

    @Test
    public void testParameterValuesWithStringsOnly() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 9);

        WorkDefinitionImpl testServiceWithParamValuesTwo = repoResults.get("TestServiceWithParamValuesTwo");
        assertNotNull(testServiceWithParamValuesTwo);
        assertNotNull(testServiceWithParamValuesTwo.getParameterValues());

        Map<String, Object> parameterValues = testServiceWithParamValuesTwo.getParameterValues();
        assertNotNull(parameterValues);
        assertEquals(parameterValues.size(), 2);

        for( Map.Entry<String, Object> entry : parameterValues.entrySet() ) {
            assertTrue( entry.getValue() instanceof  String );
            assertNotNull( entry.getValue());

            if (entry.getKey().equals("param1")) {
                String paramValue = (String) entry.getValue();
                List<String> paramValueList = Arrays.asList(paramValue.split(","));
                assertNotNull(paramValueList);
                assertEquals(3, paramValueList.size());
                assertTrue(paramValueList.contains("one"));
                assertTrue(paramValueList.contains("two"));
                assertTrue(paramValueList.contains("three"));
            } else if(entry.getKey().equals("param3")) {
                String paramValue = (String) entry.getValue();
                List<String> paramValueList = Arrays.asList(paramValue.split(","));
                assertNotNull(paramValueList);
                assertEquals(3, paramValueList.size());
                assertTrue(paramValueList.contains("four"));
                assertTrue(paramValueList.contains("five"));
                assertTrue(paramValueList.contains("six"));
            } else {
                fail("invalid parameter name");
            }
        }
    }

    @Test
    public void testParameterValuesWithStringsAndEnums() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 9);

        WorkDefinitionImpl testServiceWithParamValuesThree = repoResults.get("TestServiceWithParamValuesThree");
        assertNotNull(testServiceWithParamValuesThree);
        assertNotNull(testServiceWithParamValuesThree.getParameterValues());

        Map<String, Object> parameterValues = testServiceWithParamValuesThree.getParameterValues();
        assertNotNull(parameterValues);
        assertEquals(parameterValues.size(), 2);

        /**
         *  "parameterValues" : [
         "param1" : new EnumDataType("org.jbpm.process.workitem.enums.AnimalsEnum"),
         "param3" : "one, two, three"
         ],
         */
        for( Map.Entry<String, Object> entry : parameterValues.entrySet() ) {
            assertNotNull( entry.getValue());

            if (entry.getKey().equals("param1")) {
                assertTrue( entry.getValue() instanceof  EnumDataType );
                EnumDataType paramEnum = (EnumDataType) entry.getValue();
                assertEquals("org.jbpm.process.workitem.enums.AnimalsEnum", paramEnum.getClassName());
                Map<String, Object> paramValuesMap = paramEnum.getValueMap(null);
                assertNotNull(paramValuesMap);
                assertEquals(5, paramValuesMap.size());

                assertTrue(paramValuesMap.containsKey("DOGS"));
                assertTrue(paramValuesMap.containsKey("CATS"));
                assertTrue(paramValuesMap.containsKey("ELEPHANTS"));
                assertTrue(paramValuesMap.containsKey("GIRAFFES"));
                assertTrue(paramValuesMap.containsKey("BIRDS"));

                assertEquals(paramValuesMap.get("DOGS"), AnimalsEnum.DOGS);
                assertEquals(paramValuesMap.get("CATS"), AnimalsEnum.CATS);
                assertEquals(paramValuesMap.get("ELEPHANTS"), AnimalsEnum.ELEPHANTS);
                assertEquals(paramValuesMap.get("GIRAFFES"), AnimalsEnum.GIRAFFES);
                assertEquals(paramValuesMap.get("BIRDS"), AnimalsEnum.BIRDS);
            } else if(entry.getKey().equals("param3")) {
                assertTrue( entry.getValue() instanceof  String );
                String paramValue = (String) entry.getValue();
                List<String> paramValueList = Arrays.asList(paramValue.split(","));
                assertNotNull(paramValueList);
                assertEquals(3, paramValueList.size());
                assertTrue(paramValueList.contains("one"));
                assertTrue(paramValueList.contains("two"));
                assertTrue(paramValueList.contains("three"));
            } else {
                fail("invalid parameter name");
            }

        }
    }

}
