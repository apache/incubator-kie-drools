package org.drools.rule.builder.dialect.asm;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.drools.rule.builder.PatternBuilder.buildAnalysis;
import static org.drools.rule.builder.PatternBuilder.createImplicitBindings;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.createVariableContext;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.generateMethodTemplate;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.registerInvokerBytecode;

public abstract class AbstractASMEvalBuilder implements RuleConditionBuilder {
    public RuleConditionElement build(RuleBuildContext context, BaseDescr descr) {
        // it must be an EvalDescr
        final EvalDescr evalDescr = (EvalDescr) descr;

        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );

        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                          evalDescr,
                                                                          evalDescr.getContent(),
                                                                          new BoundIdentifiers( context.getDeclarationResolver().getDeclarationClasses(decls),
                                                                                                context.getPackageBuilder().getGlobals() ) );

        final Declaration[] declarations = decls.values().toArray( new Declaration[decls.size()]);
        return buildEval(context, evalDescr, analysis, declarations);
     }

    public RuleConditionElement build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
        if (prefixPattern == null) {
            return build(context, descr);
        }

        EvalDescr evalDescr = (EvalDescr) descr;

        PredicateDescr predicateDescr = new PredicateDescr( context.getRuleDescr().getResource(), evalDescr.getContent() );
        AnalysisResult analysis = buildAnalysis(context, prefixPattern, predicateDescr, null );

        Declaration[] declarations = getUsedDeclarations(context, prefixPattern, analysis);
        return buildEval(context, evalDescr, analysis, declarations);
    }

    private RuleConditionElement buildEval(RuleBuildContext context, EvalDescr evalDescr, AnalysisResult analysis, Declaration[] declarations) {
        String className = "eval" + context.getNextId();
        evalDescr.setClassMethodName( className );

        Arrays.sort(declarations, RuleTerminalNode.SortDeclarations.instance);

        final EvalCondition eval = new EvalCondition( declarations );

        final Map vars = createVariableContext( className,
                (String)evalDescr.getContent(),
                context,
                declarations,
                null,
                analysis.getBoundIdentifiers().getGlobals() );

        generateMethodTemplate("evalMethod", context, vars);

        byte[] bytecode = createEvalBytecode(context, vars);
        registerInvokerBytecode(context, vars, bytecode, eval);
        return eval;
    }

    private Declaration[] getUsedDeclarations(RuleBuildContext context, Pattern pattern, AnalysisResult analysis) {
        BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        final List<Declaration> declarations = new ArrayList<Declaration>();

        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
            declarations.add( context.getDeclarationResolver().getDeclaration( context.getRule(), id ) );
        }

        createImplicitBindings( context,
                                pattern,
                                analysis.getNotBoundedIdentifiers(),
                                analysis.getBoundIdentifiers(),
                                declarations );

        return declarations.toArray( new Declaration[declarations.size()] );
    }

    protected abstract byte[] createEvalBytecode(RuleBuildContext context, Map vars);
}
