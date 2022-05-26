/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkflowOperationIdTest {

    @Test
    void testOperationId() {
        WorkflowOperationId id = WorkflowOperationId.fromOperation("http://myserver.com/spec/PePE1.yaml#doSomething");
        assertEquals("doSomething", id.getOperation());
        assertEquals("PePE1.yaml", id.getFileName());
        assertEquals("Pepe1_doSomething", id.geClassName());
        assertEquals("pepe", id.getPackageName());
        assertEquals("http://myserver.com/spec/PePE1.yaml", id.getUri().toString());
        assertNull(id.getService());
        assertEquals(id.geClassName(), WorkflowOperationId.getClassName(id.getFileName(), id.getService(), id.getOperation()));
    }

    @Test
    void testOperationIdWithService() {
        WorkflowOperationId id = WorkflowOperationId.fromOperation("http://myserver.com/spec/PePE1.yaml#service#doSomething");
        assertEquals("doSomething", id.getOperation());
        assertEquals("PePE1.yaml", id.getFileName());
        assertEquals("Pepe1_service_doSomething", id.geClassName());
        assertEquals("pepe", id.getPackageName());
        assertEquals("http://myserver.com/spec/PePE1.yaml", id.getUri().toString());
        assertEquals("service", id.getService());
        assertEquals(id.geClassName(), WorkflowOperationId.getClassName(id.getFileName(), id.getService(), id.getOperation()));
    }
}
