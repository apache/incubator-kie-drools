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

class OpenAPIOperationIdTest {

    @Test
    void testOperationId() {
        OpenAPIOperationId id = OpenAPIOperationId.fromOperation("http://myserver.com/spec/PePE1.yaml#doSomething");
        assertEquals("doSomething", id.getOperationId());
        assertEquals("PePE1.yaml", id.getFileName());
        assertEquals("Pepe1_doSomething", id.geClassName());
        assertEquals("pepe", id.getServiceName());
        assertEquals("http://myserver.com/spec/PePE1.yaml", id.getUri().toString());
        assertEquals(id.geClassName(), OpenAPIOperationId.getClassName(id.getFileName(), id.getOperationId()));
    }
}
