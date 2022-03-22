/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.jbpm.ruleflow.core.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Process;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.prepareExpr;
import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.trimExpr;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpressionHandlerUtilsTest {

    private KogitoProcessContext context;

    @BeforeEach
    void setup() {
        context = mock(KogitoProcessContext.class);
        KogitoProcessInstance pi = mock(KogitoProcessInstance.class);
        when(context.getProcessInstance()).thenReturn(pi);
        Process process = mock(Process.class);
        when(pi.getProcess()).thenReturn(process);
        when(process.getMetaData()).thenReturn(
                Collections.singletonMap(Metadata.CONSTANTS, ObjectMapperFactory.get().createObjectNode().set("name", ObjectMapperFactory.get().createObjectNode().put("surname", "carapito"))));
    }

    @Test
    void testTrimExpression() {
        assertEquals(".pepe", trimExpr("${ .pepe }"));
        assertEquals("{name:.pepe}", trimExpr("${ {name:.pepe} }"));
    }

    @Test
    void testPrepareString() {
        Map<String, String> map = Collections.singletonMap("name", "javierito");
        ConfigResolverHolder.setConfigResolver(new ConfigResolver() {

            @Override
            public <T> T getConfigProperty(String name, Class<T> clazz, T defaultValue) {
                return (T) map.get(name);
            }
        });
        assertEquals("My name is javierito carapito", prepareExpr(trimExpr("${ My name is $SECRET.name $CONST.name.surname }"), Optional.of(context)));
    }
}
