/**
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
package org.drools.model.codegen.execmodel.generator.visitor.accumulate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;

import static java.util.stream.Collectors.toList;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PATTERN_CALL;

public class PatternToReplace {

    final RuleContext context;
    final List<Expression> bindingExprsVars;

    public PatternToReplace(RuleContext context, Collection<String> patternBindings) {
        this.context = context;
        this.bindingExprsVars = patternBindings.stream().map(context::getVarExpr).collect(toList());
    }

    public Optional<MethodCallExpr> findFromPattern() {
        return context.getExpressions().stream()
                .flatMap(e -> e.findAll(MethodCallExpr.class).stream())
                .filter(expr -> expr.getName().asString().equals(PATTERN_CALL))
                .filter(this::hasBindingExprVar)
                .map(Expression::asMethodCallExpr)
                .findFirst();
    }

    private boolean hasBindingExprVar(MethodCallExpr expr) {
        return !Collections.disjoint(bindingExprsVars, expr.getArguments());
    }
}
