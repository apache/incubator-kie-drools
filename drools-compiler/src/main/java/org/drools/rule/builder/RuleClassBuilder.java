package org.drools.rule.builder;

import org.drools.lang.descr.RuleDescr;
import org.drools.rule.builder.dialect.java.BuildUtils;

public interface RuleClassBuilder {

    public void buildRule(final BuildContext context,
                          final BuildUtils utils,
                          final RuleDescr ruleDescr);

}