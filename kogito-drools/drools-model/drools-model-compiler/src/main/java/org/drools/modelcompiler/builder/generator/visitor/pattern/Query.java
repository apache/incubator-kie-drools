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

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.QueryParameter;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.QUERY_INVOCATION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.VALUE_OF_CALL;
import static org.drools.modelcompiler.builder.generator.QueryGenerator.toQueryDef;

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
            Expression[] queryArgs = new Expression[queryParams.size()];

            for (int i = 0; i < constraintDescrs.size(); i++) {
                String itemText = constraintDescrs.get( i ).getText();
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
        if ( QueryGenerator.isLiteral( itemText ) ) {
            MethodCallExpr valueOfMethod = new MethodCallExpr( null, VALUE_OF_CALL );
            valueOfMethod.addArgument( new NameExpr( itemText ) );
            queryArgs[i] = valueOfMethod;
        } else {
            context.addDeclaration( itemText, queryParams.get( i ).getType() );
            queryArgs[i] = context.getVarExpr( itemText );
        }
    }
}
