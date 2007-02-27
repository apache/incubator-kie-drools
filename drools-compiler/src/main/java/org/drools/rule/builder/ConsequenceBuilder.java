package org.drools.rule.builder;

import org.drools.lang.descr.RuleDescr;

public interface ConsequenceBuilder {

    public void buildConsequence(final BuildContext context,
                                 final BuildUtils utils,
                                 final RuleDescr ruleDescr);

}