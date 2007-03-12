package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELConsequence;
import org.drools.base.mvel.MVELEvalExpression;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.EvalCondition;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.dialect.java.BuildUtils;
import org.drools.spi.Consequence;
import org.mvel.MVEL;

public class MVELConsequenceBuilder
    implements
    ConsequenceBuilder {

    public void build(BuildContext context,
                      BuildUtils utils,
                      RuleDescr ruleDescr) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        DroolsMVELFactory factory = new DroolsMVELFactory();
        factory.setPreviousDeclarationMap( context.getDeclarationResolver().getDeclarations() );
        factory.setGlobalsMap( context.getPkg().getGlobals() );

        Serializable expr = MVEL.compileExpression( (String) ruleDescr.getConsequence() );

        context.getRule().setConsequence( new MVELConsequence( expr,
                                                               factory ) );
    }

}
