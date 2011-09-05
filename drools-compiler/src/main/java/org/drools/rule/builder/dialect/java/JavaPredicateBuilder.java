package org.drools.rule.builder.dialect.java;

import java.util.Map;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.Declaration;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.RuleBuildContext;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;

public class JavaPredicateBuilder
    implements
    PredicateBuilder {

    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final PredicateConstraint predicateConstraint,
                      final PredicateDescr predicateDescr,
                      final AnalysisResult analysis) {
        final String className = "predicate" + context.getNextId();
        predicateDescr.setClassMethodName( className );

        final Map map = createVariableContext( className,
                                               (String) predicateDescr.getContent(),
                                               context,
                                               previousDeclarations,
                                               localDeclarations,
                                               usedIdentifiers.getGlobals()
        );

        generateTemplates("predicateMethod",
                "predicateInvoker",
                context,
                className,
                map,
                predicateConstraint,
                predicateDescr);
    }

}
