package org.drools.rule.builder.dialect.java;

import java.util.List;
import java.util.Map;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.Declaration;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.RuleBuildContext;

public class JavaPredicateBuilder extends AbstractJavaRuleBuilder
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
                                               usedIdentifiers.getGlobals(),
                                               null );

        generatTemplates( "predicateMethod",
                          "predicateInvoker",
                          context,
                          className,
                          map,
                          predicateConstraint,
                          predicateDescr );
    }

}
