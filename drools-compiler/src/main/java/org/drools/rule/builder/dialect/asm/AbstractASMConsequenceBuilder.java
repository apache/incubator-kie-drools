package org.drools.rule.builder.dialect.asm;

import org.drools.rule.Declaration;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.rule.builder.dialect.java.KnowledgeHelperFixer;

import java.util.Map;

import static org.drools.rule.builder.dialect.DialectUtil.fixBlockDescr;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.createConsequenceContext;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.createJavaAnalysisResult;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.generateMethodTemplate;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.registerInvokerBytecode;

public abstract class AbstractASMConsequenceBuilder implements ConsequenceBuilder {
    public void build(RuleBuildContext context, String consequenceName) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        Map<String, Object> vars = consequenceContext(context, consequenceName);
        if (vars == null) return;
        generateMethodTemplate("consequenceMethod", context, vars);

        byte[] bytecode = createConsequenceBytecode(context, vars);
        registerInvokerBytecode(context, vars, bytecode, context.getRule());

        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
    }

    private Map<String, Object> consequenceContext(RuleBuildContext context, String consequenceName) {
        String className = consequenceName + "Consequence";
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());
        JavaAnalysisResult analysis = createJavaAnalysisResult(context, consequenceName, decls);

        if ( analysis == null ) {
            // not possible to get the analysis results
            return null;
        }

        // this will fix modify, retract, insert, update, entrypoints and channels
        String fixedConsequence = fixBlockDescr(context, analysis, decls);

        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return null;
        }
        fixedConsequence = KnowledgeHelperFixer.fix( fixedConsequence );

        return createConsequenceContext(context, consequenceName, className, fixedConsequence, decls, analysis.getBoundIdentifiers());
    }

    protected abstract byte[] createConsequenceBytecode(RuleBuildContext ruleContext, final Map<String, Object> consequenceContext);
}
