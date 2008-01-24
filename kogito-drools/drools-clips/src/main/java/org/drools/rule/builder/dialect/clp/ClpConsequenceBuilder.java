package org.drools.rule.builder.dialect.clp;

import java.util.HashMap;
import java.util.Map;

import org.drools.clp.BlockExecutionEngine;
import org.drools.clp.ExecutionEngine;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;

public class ClpConsequenceBuilder implements ConsequenceBuilder {

    public void build(RuleBuildContext context) {
        Rule rule = context.getRule();
        BlockExecutionEngine rhs = ( BlockExecutionEngine ) context.getRuleDescr().getConsequence();
        Map vars = new HashMap();
        
        for(Declaration declaration : rule.getDeclarations() ) {
            vars.put( declaration.getIdentifier(), declaration );
        }
        
        rhs.replaceTempTokens( vars );
        rule.setConsequence( rhs );        
    }

}
