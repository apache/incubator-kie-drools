/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.serverless.workflow.suppliers;

import org.jbpm.compiler.canonical.ExpressionSupplier;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.descriptors.ExpressionUtils;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.serverless.workflow.actions.SetExpressionAction;

import com.github.javaparser.ast.expr.Expression;

public class SetExpressionActionSupplier extends SetExpressionAction implements ExpressionSupplier {

    private Expression expression;

    public SetExpressionActionSupplier(String lang, String expr, String inputVar, String outputVar) {
        super(lang, expr, inputVar, outputVar);
        ExpressionUtils.checkValid(lang, expr);
        expression = ExpressionUtils.getObjectCreationExpr(SetExpressionAction.class, lang, expr, inputVar, outputVar);
    }

    @Override
    public Expression get(KogitoNode node, ProcessMetaData metadata) {
        return expression;
    }

}
