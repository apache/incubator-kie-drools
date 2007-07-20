package org.drools.rule.builder.dialect.mvel;

import org.drools.lang.descr.RuleDescr;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;

public class MVELRuleClassBuilder
    implements
    RuleClassBuilder {

    /**
     * No real building for now.
     * Simply to update ruleDescr.setConsequenceOffset
     */
    public void buildRule(RuleBuildContext context) {
        final MVELDialect dialect = (MVELDialect) context.getDialect();

        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuffer buffer = new StringBuffer();

        final RuleDescr ruleDescr = context.getRuleDescr();

        ruleDescr.setConsequenceOffset( 0);
    }
}
