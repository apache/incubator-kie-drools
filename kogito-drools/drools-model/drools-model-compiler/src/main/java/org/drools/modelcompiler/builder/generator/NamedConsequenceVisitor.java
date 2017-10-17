package org.drools.modelcompiler.builder.generator;

import org.drools.compiler.lang.descr.*;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.modelcompiler.builder.PackageModel;

import java.util.HashSet;
import java.util.List;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.*;

public class NamedConsequenceVisitor {

    public static final String WHEN_CALL = "when";
    public static final String ELSE_WHEN_CALL = "elseWhen";
    public static final String THEN_CALL = "then";
    public static final String BREAKING_CALL = "breaking";


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
        PatternDescr patternRelated = (PatternDescr) getReferringPatternDescr(desc, (AndDescr) context.parentDesc);
        Class<?> patternRelatedClass = getClassFromContext(context.getPkg(),patternRelated.getObjectType());
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
            when.addArgument(new NameExpr(toVar(patternRelated.getIdentifier())));
            ModelGenerator.DrlxParseResult parseResult = drlxParse(context, packageModel, patternType, patternRelated.getIdentifier(), condition);
            when.addArgument(generateLambdaWithoutParameters(new HashSet<>(), parseResult.expr));
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
        String namedConsequenceString = context.namedConsequences.get(namedConsequence.getName());
        BlockStmt ruleVariablesBlock = createRuleVariables(packageModel, context);
        BlockStmt ruleConsequence = rewriteConsequence(context, namedConsequenceString);
        List<String> verifiedDeclUsedInRHS = extractUsedDeclarations(packageModel, context, ruleConsequence);

        MethodCallExpr onCall = onCall(verifiedDeclUsedInRHS);
        if (namedConsequence.isBreaking()) {
            onCall = new MethodCallExpr( onCall, BREAKING_CALL );
        }
        return executeCall(context, ruleVariablesBlock, ruleConsequence, verifiedDeclUsedInRHS, onCall);
    }
}
