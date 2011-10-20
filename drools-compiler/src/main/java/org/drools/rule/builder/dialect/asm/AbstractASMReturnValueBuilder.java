package org.drools.rule.builder.dialect.asm;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;

import java.util.Map;

import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.createVariableContext;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.generateMethodTemplate;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.registerInvokerBytecode;

public abstract class AbstractASMReturnValueBuilder implements ReturnValueBuilder {

    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr,
                      final AnalysisResult analysis) {
        final String className = "returnValue" + context.getNextId();
        returnValueRestrictionDescr.setClassMethodName( className );

        final Map vars = createVariableContext(className,
                                               (String) returnValueRestrictionDescr.getContent(),
                                               context,
                                               previousDeclarations,
                                               localDeclarations,
                                               usedIdentifiers.getGlobals());

        generateMethodTemplate("returnValueMethod", context, vars);

        byte[] bytecode = createReturnValueBytecode(context, vars, false);
        registerInvokerBytecode(context, vars, bytecode, returnValueRestriction);
    }

    protected abstract byte[] createReturnValueBytecode(RuleBuildContext context, Map vars, boolean readLocalsFromTuple);
}
