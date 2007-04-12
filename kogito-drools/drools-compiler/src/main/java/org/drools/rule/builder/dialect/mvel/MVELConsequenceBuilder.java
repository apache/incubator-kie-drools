package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELConsequence;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.dialect.java.BuildUtils;
import org.mvel.MVEL;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleDescr ruleDescr) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        final DroolsMVELFactory factory = new DroolsMVELFactory();
        factory.setPreviousDeclarationMap( context.getDeclarationResolver().getDeclarations() );
        factory.setGlobalsMap( context.getPkg().getGlobals() );

        final Serializable expr = MVEL.compileExpression( (String) ruleDescr.getConsequence() );

        context.getRule().setConsequence( new MVELConsequence( expr,
                                                               factory ) );
    }

}
