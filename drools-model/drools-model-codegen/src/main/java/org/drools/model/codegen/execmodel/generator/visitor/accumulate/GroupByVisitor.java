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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.TypedDeclarationSpec;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;
import org.drools.model.codegen.execmodel.generator.expressiontyper.TypedExpressionResult;
import org.drools.model.codegen.execmodel.generator.visitor.ModelGeneratorVisitor;

import java.util.Optional;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toVar;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.GROUP_BY_CALL;
import static org.drools.util.StringUtils.generateUUID;

public class GroupByVisitor extends AccumulateVisitor {
    public GroupByVisitor(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(modelGeneratorVisitor, context, packageModel);
    }

    protected void processAccumulateFunctions(AccumulateDescr descr, PatternDescr basePattern, BaseDescr input, MethodCallExpr accumulateDSL) {
        accumulateDSL.setName(GROUP_BY_CALL);

        GroupByDescr groupByDescr = (GroupByDescr) descr;

        Expression expr = parseExpression(groupByDescr.getGroupingFunction());
        TypedExpressionResult result = new ExpressionTyper(context).toTypedExpression(expr);
        Optional<TypedExpression> optResult = result.getTypedExpression();
        if (optResult.isEmpty()) {
            context.addCompilationError( new InvalidExpressionErrorResult( "Unable to parse grouping expression: " + groupByDescr.getGroupingFunction() ) );
            return;
        }

        for (String used : result.getUsedDeclarations()) {
            accumulateDSL.addArgument( context.getVarExpr(used) );
        }

        TypedExpression typedExpression = optResult.get();

        String groupingKey = groupByDescr.getGroupingKey() != null ? groupByDescr.getGroupingKey() : generateUUID();
        context.addDeclarationReplacing(new TypedDeclarationSpec(groupingKey, typedExpression.getRawClass()));

        accumulateDSL.addArgument(toVar(groupingKey));

        accumulateDSL.addArgument( buildConstraintExpression(typedExpression.getExpression(), result.getUsedDeclarations()) );

        super.processAccumulateFunctions(descr, basePattern, input, accumulateDSL);
    }
}