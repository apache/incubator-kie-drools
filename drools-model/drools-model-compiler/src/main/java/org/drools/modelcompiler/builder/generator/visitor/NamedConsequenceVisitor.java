package org.drools.modelcompiler.builder.generator.visitor;

import java.util.Collections;
import java.util.Objects;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.Consequence;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
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
        PatternDescr patternRelated = Objects.requireNonNull((PatternDescr) getReferringPatternDescr(desc, (AndDescr) context.parentDesc),
                                                             "Related pattern cannot be found!");
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
            if (identifier == null) { // The accumulate pattern doesn't have an identifier. Let's take the identifier from the first function
                AccumulateDescr source = (AccumulateDescr) patternRelated.getSource();
                String identifierDeclaration = ofNullable(source.getFunctions().iterator().next())
                        .map(AccumulateDescr.AccumulateFunctionCallDescr::getBind)
                        .orElseThrow(() -> new InvalidNamedConsequenceException("Cannot find function identifier"));


                DeclarationSpec functionIdentifierType =
                        context.getDeclarationById(identifierDeclaration)
                                .orElseThrow(() -> new InvalidNamedConsequenceException("Function identifier is not a declaration"));

                when.addArgument(context.getVarExpr(identifierDeclaration));

                parseResult = new ConstraintParser(context, packageModel).drlxParse(functionIdentifierType.getDeclarationClass(), identifierDeclaration, condition);
                parseResult.accept((DrlxParseSuccess parseSuccess) -> {
                    SingleDrlxParseSuccess parseSuccess1 = (SingleDrlxParseSuccess) parseSuccess;
                    when.addArgument(generateLambdaWithoutParameters(parseSuccess1.getUsedDeclarations(), parseSuccess.getExpr(), true));
                });

            } else {

                when.addArgument(context.getVarExpr(identifier));
                parseResult = new ConstraintParser(context, packageModel).drlxParse(patternType, identifier, condition);
                parseResult.accept(parseSuccess -> when.addArgument(generateLambdaWithoutParameters(Collections.emptySortedSet(), parseSuccess.getExpr())));

            }


        }

        MethodCallExpr then = new MethodCallExpr(when, THEN_CALL);
        MethodCallExpr rhs = onDSL(desc.getConsequence());
        then.addArgument(rhs);
        return then;
    }

    private BaseDescr getReferringPatternDescr(ConditionalBranchDescr desc, AndDescr parent) {
        BaseDescr patternRelated = null;
        for (BaseDescr b : parent.getDescrs()) {
            if (b.equals(desc)) {
                break;
            }
            patternRelated = b;
        }
        return patternRelated;
    }

    private MethodCallExpr onDSL(NamedConsequenceDescr namedConsequence) {
        String namedConsequenceString = context.getNamedConsequences().get(namedConsequence.getName());
        BlockStmt ruleVariablesBlock = new BlockStmt();
        createVariables(context.getKbuilder(), ruleVariablesBlock, packageModel, context);
        return new Consequence(context).createCall(null, namedConsequenceString, ruleVariablesBlock, namedConsequence.isBreaking() );
    }

    static class InvalidNamedConsequenceException extends RuntimeException {

        public InvalidNamedConsequenceException(String message) {
            super(message);
        }
    }

}
