package org.drools.rule.builder.dialect.asm;

import org.drools.compiler.*;
import org.drools.lang.descr.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;

import java.util.*;

import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;

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

        final Map vars = createVariableContext( className,
                                               (String) predicateDescr.getContent(),
                                               context,
                                               previousDeclarations,
                                               localDeclarations,
                                               usedIdentifiers.getGlobals() );

        generateMethodTemplate("predicateMethod", context, vars);

        byte[] bytecode = createPredicateBytecode(context, vars);
        registerInvokerBytecode(context, vars, bytecode, predicateConstraint);
    }

    protected abstract byte[] createPredicateBytecode(RuleBuildContext context, Map vars);
}
