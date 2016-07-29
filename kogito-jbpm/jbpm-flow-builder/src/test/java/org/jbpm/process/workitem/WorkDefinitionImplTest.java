/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.process.workitem;

import org.drools.core.process.core.datatype.impl.type.ListDataType;
import org.drools.core.process.core.datatype.impl.type.StringDataType;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;

public class WorkDefinitionImplTest extends AbstractBaseTest {

    @Test
    public void testServices() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 5);

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
    }

}
