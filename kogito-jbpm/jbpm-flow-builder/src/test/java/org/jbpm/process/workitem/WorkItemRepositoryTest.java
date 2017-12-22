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

import java.util.Map;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkItemRepositoryTest extends AbstractBaseTest {

    @Test
    public void testGetWorkDefinitions() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 9);
    }

    @Test
    public void testGetWorkDefinitionsFromInvalidRepo() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions("invlidrepo");
        assertNotNull(repoResults);
        assertTrue(repoResults.isEmpty());
    }

    @Test
    public void testGetWorkDefinitionsForNames() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(),
                new String[]{"TestServiceOne", "TestServiceTwo"});

        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 2);
    }

    @Test
    public void testWorkDefinitionsPathAndFile() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(),
                                                                                            new String[]{"TestServiceOne"});

        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 1);

        assertNotNull(repoResults.get("TestServiceOne"));
        assertNotNull(repoResults.get("TestServiceOne").getPath());
        assertTrue(repoResults.get("TestServiceOne").getPath().endsWith("/repository/TestServiceOne"));
        assertNotNull(repoResults.get("TestServiceOne").getFile());
        assertTrue(repoResults.get("TestServiceOne").getFile().equals("TestServiceOne.wid"));

    }

    @Test
    public void testGetWorkDefinitionsForInvalidNames() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(),
                new String[]{"TestServiceOne", "INVALID_NAME"});

        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 1);

        Map<String, WorkDefinitionImpl> repoResults2 = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(),
                new String[]{"INVALID_NAME1", "INVALID_NAME2"});

        assertNotNull(repoResults2);
        assertTrue(repoResults2.isEmpty());

        Map<String, WorkDefinitionImpl> repoResults3 = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(),
                new String[]{});

        assertNotNull(repoResults3);
        assertTrue(repoResults3.isEmpty());
    }

    @Test
    public void testGetWorkDefinitionForSingleDirRepo() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repositorysingledir").toURI().toString(),
                                                                                            null, "repowid");

        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 1);
    }

    @Test
    public void testGetInvalidWorkDefinitionForSingleDirRepo() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repositorysingledir").toURI().toString(),
                                                                                            null, "invalidrepowid");

        assertNotNull(repoResults);
        assertTrue(repoResults.isEmpty());
    }
}
