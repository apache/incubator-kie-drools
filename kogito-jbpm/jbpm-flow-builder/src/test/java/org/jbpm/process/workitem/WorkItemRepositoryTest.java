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
import java.util.Collection;
import java.util.Map;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class WorkItemRepositoryTest extends AbstractBaseTest {

    @Parameters(name = "Repository Name : {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"repository"},
                {"repositorynoindexconf"}
        });
    }

    private String repoName;

    public WorkItemRepositoryTest(String repoName) {
        this.repoName = repoName;
    }

    @Test
    public void testGetWorkDefinitions() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource(repoName).toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 9);
    }

    @Test
    public void testGetWorkDefinitionsForNames() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource(repoName).toURI().toString(),
                new String[]{"TestServiceOne", "TestServiceTwo"});

        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 2);
    }

    @Test
    public void testGetWorkDefinitionsForInvalidNames() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource(repoName).toURI().toString(),
                new String[]{"TestServiceOne", "INVALID_NAME"});

        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 1);

        Map<String, WorkDefinitionImpl> repoResults2 = WorkItemRepository.getWorkDefinitions(getClass().getResource(repoName).toURI().toString(),
                new String[]{"INVALID_NAME1", "INVALID_NAME2"});

        assertNotNull(repoResults2);
        assertTrue(repoResults2.isEmpty());

        Map<String, WorkDefinitionImpl> repoResults3 = WorkItemRepository.getWorkDefinitions(getClass().getResource(repoName).toURI().toString(),
                new String[]{});

        assertNotNull(repoResults3);
        assertTrue(repoResults3.isEmpty());
    }
}
