package org.drools.compiler.rule.builder;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.lang.descr.ReturnValueRestrictionDescr;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.ReturnValueRestriction;

public interface ReturnValueBuilder {
    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr, 
                      final AnalysisResult analysis);
}
