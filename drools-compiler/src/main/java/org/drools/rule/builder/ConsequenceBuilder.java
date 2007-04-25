package org.drools.rule.builder;

import org.drools.lang.descr.RuleDescr;

public interface ConsequenceBuilder {

    public void build(final RuleBuildContext context,
                      final RuleDescr ruleDescr);

}