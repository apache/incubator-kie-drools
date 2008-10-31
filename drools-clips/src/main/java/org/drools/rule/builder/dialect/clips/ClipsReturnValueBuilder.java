package org.drools.rule.builder.dialect.clips;

import java.util.List;

import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.mvel.MVELReturnValueBuilder;

public class ClipsReturnValueBuilder extends MVELReturnValueBuilder {
    public void build(final RuleBuildContext context,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr) {        
        super.build(context, usedIdentifiers, previousDeclarations, localDeclarations, returnValueRestriction, returnValueRestrictionDescr );
    }
}
