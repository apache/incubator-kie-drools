package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.HashMap;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELConsequence;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.ConsequenceBuilder;
import org.mvel.MVEL;
import org.mvel.integration.impl.MapVariableResolverFactory;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    public void build(final RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        final DroolsMVELFactory factory = new DroolsMVELFactory(context.getDeclarationResolver().getDeclarations(), null,  context.getPkg().getGlobals() );

        final Serializable expr = MVEL.compileExpression( (String) context.getRuleDescr().getConsequence() );

        context.getRule().setConsequence( new MVELConsequence( expr,
                                                               factory ) );
    }

}
