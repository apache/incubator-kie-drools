package org.drools.rule.builder;

import org.drools.lang.descr.RuleDescr;

public interface RuleClassBuilder {

    public void buildRule(final RuleBuildContext context,
                          final RuleDescr ruleDescr);

}