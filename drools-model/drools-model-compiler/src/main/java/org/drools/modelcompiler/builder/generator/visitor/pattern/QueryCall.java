/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.QueryParameter;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.QUERY_INVOCATION_CALL;

class QueryCall implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private PatternDescr pattern;
    private final String queryDef;

    QueryCall(RuleContext context, PackageModel packageModel, PatternDescr pattern, String queryDef) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.queryDef = queryDef;
    }

    @Override
    public void buildPattern() {
        MethodCallExpr callMethod = new MethodCallExpr(new NameExpr(queryDef), QUERY_INVOCATION_CALL);
        callMethod.addArgument("" + !pattern.isQuery());

        String queryName = context.getQueryName().orElseThrow(RuntimeException::new);
        List<QueryParameter> parameters = packageModel.getQueryDefWithType().get(queryDef).getContext().getQueryParameters();
        for (int i = 0; i < parameters.size(); i++) {
            String variableName = getVariableName(i);
            Expression parameter = context.getQueryParameterByName(variableName)
                    .map(qp -> (Expression) new MethodCallExpr(new NameExpr(queryName), QueryGenerator.toQueryArg(qp.getIndex())))
                    .orElseGet(() -> context.getUnificationId(variableName)
                            .map(name -> context.getVarExpr(name))
                            .orElseGet(() -> context.getVarExpr(variableName)));
            callMethod.addArgument(parameter);
        }

        context.addExpression(callMethod);
    }

    private String getVariableName(int i) {
        ExprConstraintDescr variableExpr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(i);
        String variableName = variableExpr.toString();
        int unifPos = variableName.indexOf( ":=" );
        if (unifPos > 0) {
            variableName = variableName.substring( 0, unifPos ).trim();
        }
        return variableName;
    }
}
