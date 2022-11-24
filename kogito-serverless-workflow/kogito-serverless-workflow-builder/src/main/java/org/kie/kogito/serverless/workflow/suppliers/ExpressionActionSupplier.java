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
import org.kie.kogito.serverless.workflow.actions.ExpressionAction;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import io.serverlessworkflow.api.Workflow;

public class ExpressionActionSupplier extends ExpressionAction implements ExpressionSupplier {

    public static Builder of(Workflow workflow, String expr) {
        return new Builder(workflow.getExpressionLang(), ExpressionHandlerUtils.replaceExpr(workflow, expr));
    }

    public static class Builder {

        private final String lang;
        private final String expr;
        private String inputVar = ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR;
        private String outputVar = ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR;

        private Builder(String lang, String expr) {
            this.lang = lang;
            this.expr = expr;
        }

        public Builder withVarNames(String varName) {
            this.inputVar = varName;
            this.outputVar = varName;
            return this;
        }

        public Builder withVarNames(String inputVar, String outputVar) {
            this.inputVar = inputVar;
            this.outputVar = outputVar;
            return this;
        }

        public ExpressionActionSupplier build() {
            return new ExpressionActionSupplier(lang, expr, inputVar, outputVar);
        }
    }

    private final ObjectCreationExpr expression;

    private ExpressionActionSupplier(String lang, String expr, String inputVar, String outputVar) {
        super(lang, expr, inputVar, outputVar);
        ExpressionUtils.checkValid(lang, expr);
        expression = ExpressionUtils.getObjectCreationExpr(ExpressionAction.class, lang, expr, inputVar, outputVar);
    }

    @Override
    public Expression get(KogitoNode node, ProcessMetaData metadata) {
        return expression;
    }
}
