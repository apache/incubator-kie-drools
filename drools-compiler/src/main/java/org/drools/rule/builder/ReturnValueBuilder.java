package org.drools.rule.builder;

import java.util.List;

import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;

public interface ReturnValueBuilder {
    public void build(final RuleBuildContext context,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr);
}
