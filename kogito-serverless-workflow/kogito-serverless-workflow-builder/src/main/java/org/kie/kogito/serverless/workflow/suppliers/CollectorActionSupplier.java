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

import java.util.function.Supplier;

import org.jbpm.compiler.canonical.descriptors.SupplierUtils;
import org.kie.kogito.serverless.workflow.actions.CollectorAction;

import com.github.javaparser.ast.expr.Expression;

public class CollectorActionSupplier extends CollectorAction implements Supplier<Expression> {

    private Expression expression;

    public CollectorActionSupplier(String lang, String expr, String inputVar, String outputVar) {
        super(lang, expr, inputVar, outputVar);
        expression = SupplierUtils.getExpression(CollectorAction.class, lang, expr, inputVar, outputVar);
    }

    @Override
    public Expression get() {
        return expression;
    }

}
