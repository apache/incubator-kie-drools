package org.drools.rule.builder.dialect.java;

import java.util.List;
import java.util.Map;

import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.Declaration;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.RuleBuildContext;

public class JavaPredicateBuilder extends AbstractJavaBuilder
    implements
    PredicateBuilder {

    public void build(final RuleBuildContext context,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final PredicateConstraint predicateConstraint,
                      final PredicateDescr predicateDescr) {
        final String className = "returnValue" + context.getNextId();
        predicateDescr.setClassMethodName( className );

        final Map map = createVariableContext( className,
                                         (String) predicateDescr.getContent(),
                                         context,
                                         previousDeclarations,
                                         localDeclarations,
                                         (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        generatTemplates( "predicateMethod",
                          "predicateInvoker",
                          context,
                          className,
                          map,
                          predicateConstraint,
                          predicateDescr );
    }

}
