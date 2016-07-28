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
        assertEquals(repoResults.size(), 3);

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

        WorkDefinitionImpl testServiceTwo = repoResults.get("TestServiceTwo");
        assertNotNull(testServiceTwo);
        assertEquals(2, testServiceTwo.getResults().size());
        assertTrue(testServiceTwo.getResult("result1").getType() instanceof StringDataType);
        assertTrue(testServiceTwo.getResult("result2").getType() instanceof StringDataType);

        WorkDefinitionImpl testServiceThree = repoResults.get("TestServiceThree");
        assertNotNull(testServiceThree);
        assertEquals("1.0", testServiceThree.getVersion());
        assertEquals("org.drools.eclipse.flow.common.editor.editpart.work.SampleCustomEditor", testServiceThree.getCustomEditor());
        assertEquals("org.jbpm.process.workitem.MyHandler", testServiceThree.getDefaultHandler());
        assertEquals(2, testServiceThree.getDependencies().length);
        assertEquals(2, testServiceThree.getMavenDependencies().length);

    }

}
