package org.drools.compiler.builder.impl.errors;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;

public class RuleInvokerErrorHandler extends RuleErrorHandler {

    public RuleInvokerErrorHandler(final BaseDescr ruleDescr,
                                   final RuleImpl rule,
                                   final String message) {
        super(ruleDescr,
              rule,
              message);
    }
}
