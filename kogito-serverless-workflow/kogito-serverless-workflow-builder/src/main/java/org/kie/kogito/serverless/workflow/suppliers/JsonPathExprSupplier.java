/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.serverless.workflow.functions.JsonPathResolver;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class JsonPathExprSupplier implements Supplier<Expression> {

    private String jsonPathExpr;
    private String paramName;

    public JsonPathExprSupplier(String jsonPathExpr, String paramName) {
        this.jsonPathExpr = jsonPathExpr;
        this.paramName = paramName;
    }

    @Override
    public Expression get() {
        return new ObjectCreationExpr()
                .setType(JsonPathResolver.class.getCanonicalName())
                .addArgument(new StringLiteralExpr(jsonPathExpr)).addArgument(new StringLiteralExpr(paramName));
    }

}
