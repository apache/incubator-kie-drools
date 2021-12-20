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
package org.jbpm.compiler.canonical.descriptors;

import java.util.Arrays;
import java.util.Collections;

import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.process.workitems.impl.expr.ExpressionWorkItemResolver;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiTaskDescriptorTest {

    private static class DummyWorkItemHandlerResolver extends ExpressionWorkItemResolver {

        protected DummyWorkItemHandlerResolver(String lang, String expression, String paramName) {
            super(lang, expression, paramName);
        }

        @Override
        protected Object evalExpression(Object inputModel) {
            return null;
        }
    }

    @Test
    void addParametersToServiceCall() {
        final BlockStmt execWorkItem = new BlockStmt();
        final MethodCallExpr serviceCallMethod = new MethodCallExpr("doCall");

        final WorkItemNode workItemNode =
                OpenApiTaskDescriptor.builderFor("http://myspec.com", "add")
                        .withArgs(Collections.singletonMap("body", "jeje"), DummyWorkItemHandlerResolver.class, Object.class)
                        .build();
        final OpenApiTaskDescriptor taskDescriptor = new OpenApiTaskDescriptor(workItemNode);
        taskDescriptor.handleParametersForServiceCall(execWorkItem, serviceCallMethod);
        assertNotNull(serviceCallMethod);
        assertEquals(1, serviceCallMethod.getArguments().size());
    }

    @Test
    void verifyModifierWithSingleParameter() {
        final WorkItemNode workItemNode =
                OpenApiTaskDescriptor.builderFor("http://myspec.com", "add")
                        .withArgs(Collections.singletonMap("bodyRequest", "jeje"), DummyWorkItemHandlerResolver.class, Object.class)
                        .build();
        final OpenApiTaskDescriptor.WorkItemModifier modifier = OpenApiTaskDescriptor.modifierFor(workItemNode);
        modifier.modify(this.getClass().getCanonicalName(), "add", Collections.singletonList("body"));
        assertNotNull(workItemNode.getWork().getParameter("bodyRequest"));
        assertNull(workItemNode.getWork().getParameter("body"));
    }

    @Test
    void verifyModifierWithSingleParameterSpecNone() {
        final WorkItemNode workItemNode =
                OpenApiTaskDescriptor.builderFor("http://myspec.com", "add")
                        .withArgs(Collections.singletonMap("body", "jeje"), DummyWorkItemHandlerResolver.class, Object.class)
                        .build();
        final OpenApiTaskDescriptor.WorkItemModifier modifier = OpenApiTaskDescriptor.modifierFor(workItemNode);
        assertThrows(IllegalArgumentException.class, () -> {
            // spec doesn't have a parameter, but one was defined in the WorkItem
            modifier.modify(this.getClass().getCanonicalName(), "add", Collections.emptyList());
        });
    }

    @Test
    void verifyModifierWithoutParameters() {
        // spec has none, process has none
        final WorkItemNode workItemNode =
                OpenApiTaskDescriptor.builderFor("http://myspec.com", "add")
                        .build();
        final OpenApiTaskDescriptor.WorkItemModifier modifier = OpenApiTaskDescriptor.modifierFor(workItemNode);
        modifier.modify(this.getClass().getCanonicalName(), "add", Collections.emptyList());
        assertTrue(workItemNode.getWork().getParameterDefinitions().isEmpty());
    }

    @Test
    void verifyModifierWithManyParametersDiffNames() {
        final WorkItemNode workItemNode =
                OpenApiTaskDescriptor.builderFor("http://myspec.com", "add")
                        .withArgs(Collections.singletonMap("body", "jeje"), DummyWorkItemHandlerResolver.class, Object.class)
                        .build();
        final OpenApiTaskDescriptor.WorkItemModifier modifier = OpenApiTaskDescriptor.modifierFor(workItemNode);
        assertThrows(IllegalArgumentException.class, () -> {
            modifier.modify(this.getClass().getCanonicalName(), "add", Arrays.asList("email", "tag"));
        });
    }

}
