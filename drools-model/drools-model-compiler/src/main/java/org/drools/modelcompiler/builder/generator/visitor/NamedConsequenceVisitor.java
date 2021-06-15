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

package org.drools.modelcompiler.builder.generator.visitor;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.Consequence;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;

import static java.util.Optional.ofNullable;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ELSE_WHEN_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.THEN_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WHEN_CALL;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.createVariables;

public class NamedConsequenceVisitor {

    final RuleContext context;
    final PackageModel packageModel;

    public NamedConsequenceVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(NamedConsequenceDescr descr) {
        MethodCallExpr executeCallDSL = onDSL(descr);
        context.addExpression(executeCallDSL);
    }

    public void visit(ConditionalBranchDescr desc) {
        PatternDescr patternRelated = desc.getReferringPatternDescr(context.getParentDescr());
        if (patternRelated == null) {
            context.addCompilationError(new InvalidExpressionErrorResult("Related pattern cannot be found for " + desc));
            return;
        }
        Class<?> patternRelatedClass = getClassFromContext(context.getTypeResolver(), patternRelated.getObjectType());
        MethodCallExpr whenBlock = whenThenDSL(desc, patternRelated, patternRelatedClass, WHEN_CALL, null);
        recurseAmongElseBranch(patternRelatedClass, patternRelated, whenBlock, desc.getElseBranch());
    }

    private void recurseAmongElseBranch(Class<?> patternType, PatternDescr patternRelated, MethodCallExpr parentMethodExpr, ConditionalBranchDescr branch) {
        if (branch != null) {
            MethodCallExpr elseWhenBlock = whenThenDSL(branch, patternRelated, patternType, ELSE_WHEN_CALL, parentMethodExpr);
            recurseAmongElseBranch(patternType, patternRelated, elseWhenBlock, branch.getElseBranch());
        } else {
            context.addExpression(parentMethodExpr);
        }
    }

    private MethodCallExpr whenThenDSL(ConditionalBranchDescr desc, PatternDescr patternRelated, Class<?> patternType, String callMethod, MethodCallExpr parentExpression) {
        MethodCallExpr when = new MethodCallExpr(parentExpression, callMethod);
        final String condition = desc.getCondition().toString();
        if (!condition.equals("true")) { // Default case
            when.addArgument(new StringLiteralExpr(context.getConditionId(patternType, condition)));

            String identifier = patternRelated.getIdentifier();
            DrlxParseResult parseResult;
            if (identifier == null) { // The accumulate pattern doesn't have an identifier. Let's parse the consequence and use the acc functions

                parseResult = new ConstraintParser(context, packageModel).drlxParse(Object.class, "", condition);
                parseResult.accept((DrlxParseSuccess parseSuccess) -> {

                    SingleDrlxParseSuccess parseSuccess1 = (SingleDrlxParseSuccess) parseSuccess;

                    AccumulateDescr source = (AccumulateDescr) patternRelated.getSource();

                    for(String usedDeclaration : parseSuccess1.getUsedDeclarations()) {
                        for(AccumulateDescr.AccumulateFunctionCallDescr functionCallDescr :source.getFunctions()) {
                            if(functionCallDescr.getBind().equals(usedDeclaration)) {
                                addVariable(patternRelated, when, functionCallDescr);
                            }
                        }
                    }

                    when.addArgument(generateLambdaWithoutParameters(parseSuccess1.getUsedDeclarations(), parseSuccess.getExpr(), true, Optional.empty()));
                });

            } else {

                when.addArgument(context.getVarExpr(identifier));
                parseResult = new ConstraintParser(context, packageModel).drlxParse(patternType, identifier, condition);
                Collection<String> usedDeclarations = ((SingleDrlxParseSuccess)parseResult).getUsedDeclarations();
                if (usedDeclarations.isEmpty()) { // _this
                    parseResult.accept(parseSuccess -> when.addArgument(generateLambdaWithoutParameters(Collections.emptySortedSet(), parseSuccess.getExpr())));
                } else {
                    parseResult.accept(parseSuccess -> when.addArgument(generateLambdaWithoutParameters(usedDeclarations, parseSuccess.getExpr(), true, Optional.empty())));
                }
            }
        }

        MethodCallExpr then = new MethodCallExpr(when, THEN_CALL);
        MethodCallExpr rhs = onDSL(desc.getConsequence());
        then.addArgument(rhs);
        return then;
    }

    private void addVariable(PatternDescr patternRelated, MethodCallExpr when, AccumulateDescr.AccumulateFunctionCallDescr accFuncCallDescr) {
        String identifierDeclaration = ofNullable(accFuncCallDescr)
                .map(AccumulateDescr.AccumulateFunctionCallDescr::getBind)
                .orElseThrow(() -> new InvalidNamedConsequenceException("Cannot find function identifier"));

        when.addArgument(context.getVarExpr(identifierDeclaration));
    }

    private MethodCallExpr onDSL(NamedConsequenceDescr namedConsequence) {
        String namedConsequenceString = context.getNamedConsequences().get(namedConsequence.getName());
        if (namedConsequenceString == null) {
            context.addCompilationError(new InvalidExpressionErrorResult("Unknown consequence name: " + namedConsequence.getName()));
            return null;
        }
        BlockStmt ruleVariablesBlock = context.getRuleVariablesBlock();
        createVariables(ruleVariablesBlock, packageModel, context);
        return new Consequence(context).createCall(null, namedConsequenceString, ruleVariablesBlock, namedConsequence.isBreaking() );
    }

    static class InvalidNamedConsequenceException extends RuntimeException {

        public InvalidNamedConsequenceException(String message) {
            super(message);
        }
    }

}
