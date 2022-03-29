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
package org.kie.kogito.serverless.workflow.suppliers;

import org.jbpm.compiler.canonical.ExpressionSupplier;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.descriptors.ExpressionUtils;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.actions.InjectAction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.javaparser.ast.expr.Expression;

public class InjectActionSupplier extends InjectAction implements ExpressionSupplier {

    public InjectActionSupplier(JsonNode node) {
        super(node);
    }

    @Override
    public Expression get(KogitoNode kogitoNode, ProcessMetaData metadata) {
        try {
            return ExpressionUtils.getObjectCreationExpr(InjectAction.class, JsonObjectUtils.toString(this.node));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
