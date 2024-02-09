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
package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import java.util.List;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.QueryParameter;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;
import org.drools.model.codegen.execmodel.generator.expressiontyper.TypedExpressionResult;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.FROM_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.QUERY_INVOCATION_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.VALUE_OF_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;
import static org.drools.model.codegen.execmodel.generator.QueryGenerator.toQueryDef;

class Query implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private PatternDescr pattern;
    private List<? extends BaseDescr> constraintDescrs;
    private String queryName;

    public Query(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, String queryName) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.constraintDescrs = constraintDescrs;
        this.queryName = queryName;
    }

    @Override
    public void buildPattern() {
        NameExpr queryCall = new NameExpr(toQueryDef(pattern.getObjectType()));
        MethodCallExpr callCall = new MethodCallExpr(queryCall, QUERY_INVOCATION_CALL);
        callCall.addArgument("" + !pattern.isQuery());

        if (!constraintDescrs.isEmpty()) {
            List<QueryParameter> queryParams = packageModel.queryVariables( queryName );
            if (queryParams.size() != constraintDescrs.size()) {
                context.addCompilationError(new InvalidExpressionErrorResult("Wrong number of argument invoking query '" + queryName + "'"));
                return;
            }

            Expression[] queryArgs = new Expression[queryParams.size()];
            for (int i = 0; i < constraintDescrs.size(); i++) {
                BaseDescr baseDescr = constraintDescrs.get( i );
                String itemText = baseDescr.getText();

                boolean isPositional = baseDescr instanceof ExprConstraintDescr && ((ExprConstraintDescr) baseDescr).getType() == ExprConstraintDescr.Type.POSITIONAL;
                boolean isBinding = baseDescr instanceof BindingDescr || itemText.contains(":");

                if ( ( !isPositional ) && ( !isBinding ) ) {
                    // error, can't have non binding slots.
                    context.addCompilationError( new InvalidExpressionErrorResult( "Query's must use positional or bindings, not field constraints: " + itemText ) );
                } else if ( isPositional && isBinding ) {
                    // error, can't have positional binding slots.
                    context.addCompilationError( new InvalidExpressionErrorResult( "Query's can't use positional bindings: " + itemText ) );
                }

                int colonPos = itemText.indexOf( ':' );

                if ( colonPos > 0 ) {
                    String bindingId = itemText.substring( 0, colonPos ).trim();
                    String paramName = itemText.substring( colonPos + 1 ).trim();

                    for (int j = 0; j < queryParams.size(); j++) {
                        if ( queryParams.get( j ).getName().equals( paramName ) ) {
                            addQueryArg( queryParams, queryArgs, bindingId, j );
                            break;
                        } else if ( queryParams.get( j ).getName().equals( bindingId ) ) {
                            addQueryArg( queryParams, queryArgs, paramName, j );
                            break;
                        }
                    }

                } else {
                    addQueryArg( queryParams, queryArgs, itemText, i );
                }
            }

            for (Expression queryArg : queryArgs) {
                callCall.addArgument( queryArg );
            }
        }

        context.addExpression(callCall);
    }

    private void addQueryArg( List<QueryParameter> queryParams, Expression[] queryArgs, String itemText, int i ) {
        if ( isLiteral( itemText ) ) {
            MethodCallExpr valueOfMethod = createDslTopLevelMethod( VALUE_OF_CALL );
            valueOfMethod.addArgument( new NameExpr( itemText ) );
            queryArgs[i] = valueOfMethod;
        } else {
            Expression expr = parseExpression(itemText);
            if (expr.isNameExpr()) {
                context.addDeclaration(itemText, queryParams.get(i).getType());
                queryArgs[i] = context.getVarExpr(itemText);
            } else {
                String variableName = context.getExprId(queryParams.get(i).getType(), itemText);
                context.addDeclaration(variableName, queryParams.get(i).getType(), createFromExpr(variableName, expr));
                queryArgs[i] = context.getVarExpr(variableName);
            }
        }
    }

    private MethodCallExpr createFromExpr(String variableName, Expression expr) {
        MethodCallExpr dslExpr = createDslTopLevelMethod(PATTERN_CALL);
        dslExpr.addArgument( context.getVarExpr( variableName ) );
        context.addExpression(dslExpr);

        MethodCallExpr fromExpr = createDslTopLevelMethod(FROM_CALL);

        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters(true);

        TypedExpressionResult result = new ExpressionTyper(context).toTypedExpression(expr);
        for (String usedDeclration : result.getExpressionTyperContext().getUsedDeclarations()) {
            fromExpr.addArgument(context.getVarExpr(usedDeclration));
            lambdaExpr.addParameter(new Parameter(context.getDelarationType(usedDeclration), usedDeclration));
        }

        fromExpr.addArgument(lambdaExpr);
        if (result.getTypedExpression().isPresent()) {
            lambdaExpr.setBody(new ExpressionStmt(result.getTypedExpression().get().getExpression()));
        }
        return fromExpr;
    }

    private static boolean isLiteral(String value) {
        if ( value != null && value.length() > 0 &&
                ( value.charAt(0) == '"' || "true".equals(value) || "false".equals(value) || "null".equals(value) || value.endsWith( ".class" ) ) ) {
            return true;
        }

        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
