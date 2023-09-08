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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.DeclarationSpec;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;
import org.kie.api.definition.rule.Watch;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.findLastMethodInChain;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.findRootNodeViaScope;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.NO_OP_EXPR;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PASSIVE_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.WATCH_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

class PatternDSLPattern extends PatternDSL {

    protected PatternDSLPattern(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        super(context, packageModel, pattern, constraintDescrs, patternType);
    }

    @Override
    protected void buildPattern(DeclarationSpec declarationSpec, List<PatternConstraintParseResult> patternConstraintParseResults) {
        MethodCallExpr patternExpression = createPatternExpression(pattern, declarationSpec);

        List<Expression> exprs = new ArrayList<>();
        context.pushExprPointer(exprs::add);
        buildConstraints(pattern, patternType, patternConstraintParseResults);
        context.popExprPointer();

        List<Expression> additionalPatterns = new ArrayList<>();
        for (Expression expr : exprs) {
            Optional<Expression> rootScope = findRootNodeViaScope(expr );
            if ( rootScope.isPresent() && (( MethodCallExpr ) rootScope.get()).getNameAsString().equals( PATTERN_CALL ) ) {
                additionalPatterns.add( expr );
            } else {
                MethodCallExpr currentExpr = ( MethodCallExpr ) expr;
                MethodCallExpr lastMethodInChain = findLastMethodInChain(currentExpr);
                if (!NO_OP_EXPR.equals(lastMethodInChain.getNameAsString())) {
                    lastMethodInChain.setScope(patternExpression);
                    patternExpression = currentExpr;
                }
            }
        }

        if (pattern.isQuery()) {
            patternExpression = new MethodCallExpr( patternExpression, PASSIVE_CALL );
        }

        context.addExpression( addWatchToPattern( patternExpression ) );
        additionalPatterns.forEach( context::addExpression );
    }

    @Override
    public MethodCallExpr input(DeclarationSpec declarationSpec) {
        return addWatchToPattern( createPatternExpression(pattern, declarationSpec) );
    }

    private MethodCallExpr addWatchToPattern( MethodCallExpr patternExpression ) {
        Set<String> settableWatchedProps = getSettableWatchedProps();
        if ( !settableWatchedProps.isEmpty() ) {
            if (context.isPropertyReactive(patternType)) {
                patternExpression = new MethodCallExpr(patternExpression, WATCH_CALL);
                settableWatchedProps.stream().map(StringLiteralExpr::new).forEach(patternExpression::addArgument);
            } else {
                context.addCompilationError(new InvalidExpressionErrorResult("Wrong usage of @" + Watch.class.getSimpleName() + " annotation on class " + patternType.getName() + " that is not annotated as @PropertyReactive"));
            }
        }
        return patternExpression;
    }

    private MethodCallExpr createPatternExpression(PatternDescr pattern, DeclarationSpec declarationSpec) {
        MethodCallExpr dslExpr = createDslTopLevelMethod(PATTERN_CALL);
        dslExpr.addArgument( context.getVarExpr( pattern.getIdentifier()) );
        if (context.isQuery()) {
            Optional<Expression> declarationSource = declarationSpec.getDeclarationSource();
            declarationSource.ifPresent(dslExpr::addArgument);
        }
        return dslExpr;
    }

    private void buildConstraints(PatternDescr pattern, Class<?> patternType, List<PatternConstraintParseResult> patternConstraintParseResults) {
        boolean hasOOPath = false;
        for (PatternConstraintParseResult patternConstraintParseResult : patternConstraintParseResults) {
            // only one oopath per pattern is allowed
            if (patternConstraintParseResult.getDrlxParseResult().isOOPath()) {
                if (hasOOPath) {
                    context.addCompilationError(new InvalidExpressionErrorResult("Only one oopath per pattern is allowed"));
                    break;
                } else {
                    hasOOPath = true;
                }
            }
            buildConstraint(pattern, patternType, patternConstraintParseResult);
        }
        if (hasOOPath) {
            context.clearOOPathPatternExpr();
        }
    }

    @Override
    protected DSLNode createSimpleConstraint( DrlxParseSuccess drlxParseResult, PatternDescr pattern ) {
        return new PatternDSLSimpleConstraint( context, pattern, drlxParseResult );
    }
}
