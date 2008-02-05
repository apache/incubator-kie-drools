package org.drools.rule.builder.dialect.clips;

import java.util.Iterator;
import java.util.List;

import org.drools.clips.Appendable;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispForm;
import org.drools.clips.StringBuilderAppendable;
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
        Appendable builder = new StringBuilderAppendable();
        
        List list = (List) context.getRuleDescr().getConsequence();
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            FunctionHandlers.dump( (LispForm) it.next(),
                                   builder );
        }
        
        returnValueRestrictionDescr.setContent( builder.toString() );
        context.getRuleDescr().setConsequence( builder.toString() );
        
        super.build(context, usedIdentifiers, previousDeclarations, localDeclarations, returnValueRestriction, returnValueRestrictionDescr );
    }
}
