package org.drools.compiler.rule.builder;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.drl.ast.descr.PredicateDescr;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.PredicateConstraint;

public interface PredicateBuilder {
    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final PredicateConstraint predicateConstraint,
                      final PredicateDescr predicateDescr, 
                      AnalysisResult analysis);
}
