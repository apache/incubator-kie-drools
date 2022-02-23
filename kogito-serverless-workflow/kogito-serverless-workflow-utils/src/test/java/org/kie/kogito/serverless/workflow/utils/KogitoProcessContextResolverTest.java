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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Process;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KogitoProcessContextResolverTest {

    KogitoProcessContext context;

    @BeforeEach
    void setup() {
        context = mock(KogitoProcessContext.class);
        KogitoProcessInstance pi = mock(KogitoProcessInstance.class);
        when(context.getProcessInstance()).thenReturn(pi);
        when(pi.getId()).thenReturn("pepe");
        Process process = mock(Process.class);
        when(pi.getProcess()).thenReturn(process);

    }

    @Test
    void testGetSimpleKey() {
        assertEquals("pepe", KogitoProcessContextResolver.get().readKey(context, "instanceId"));
    }

}
