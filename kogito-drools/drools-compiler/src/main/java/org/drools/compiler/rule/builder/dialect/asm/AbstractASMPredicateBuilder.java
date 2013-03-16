package org.drools.compiler.rule.builder.dialect.asm;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.rule.builder.PredicateBuilder;
import org.drools.compiler.rule.builder.dialect.java.JavaRuleBuilderHelper;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.PredicateConstraint;
import org.drools.compiler.rule.builder.RuleBuildContext;

import java.util.Map;

public abstract class AbstractASMPredicateBuilder implements PredicateBuilder {
    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final PredicateConstraint predicateConstraint,
                      final PredicateDescr predicateDescr,
                      final AnalysisResult analysis) {

        final String className = "predicate" + context.getNextId();
        predicateDescr.setClassMethodName( className );

        final Map vars = JavaRuleBuilderHelper.createVariableContext(className,
                (String) predicateDescr.getContent(),
                context,
                previousDeclarations,
                localDeclarations,
                usedIdentifiers.getGlobals());

        JavaRuleBuilderHelper.generateMethodTemplate("predicateMethod", context, vars);

        byte[] bytecode = createPredicateBytecode(context, vars);
        JavaRuleBuilderHelper.registerInvokerBytecode(context, vars, bytecode, predicateConstraint);
    }

    protected abstract byte[] createPredicateBytecode(RuleBuildContext context, Map<String, Object> vars);
}
