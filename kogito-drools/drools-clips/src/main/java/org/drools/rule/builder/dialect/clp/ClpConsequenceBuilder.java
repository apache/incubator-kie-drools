package org.drools.rule.builder.dialect.clp;

import org.drools.clp.BlockExecutionEngine;
import org.drools.clp.ExecutionEngine;
import org.drools.rule.Rule;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;

public class ClpConsequenceBuilder implements ConsequenceBuilder {

    public void build(RuleBuildContext context) {
        Rule rule = context.getRule();
        BlockExecutionEngine rhs = ( BlockExecutionEngine ) context.getRuleDescr().getConsequence();
        rule.setConsequence( rhs );        
    }

}
