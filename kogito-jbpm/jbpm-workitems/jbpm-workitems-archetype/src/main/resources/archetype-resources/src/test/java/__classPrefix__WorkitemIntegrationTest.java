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
package ${package};

import java.util.Map;

import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;

import org.junit.Test;

import static org.junit.Assert.*;

public class ${classPrefix}WorkitemIntegrationTest {
    @Test
    public void testWorkitemValidity() {

        String repoPath = "file://" + System.getProperty("builddir") +
                "/" + System.getProperty("artifactId") + "-" +
                System.getProperty("version") + "/";

        Map<String, WorkDefinitionImpl> repoResults = new WorkItemRepository().getWorkDefinitions(repoPath,
                                                                                                  null,
                                                                                                  System.getProperty("artifactId"));
        assertNotNull(repoResults);
        assertEquals(1,
                     repoResults.size());
    }
}
