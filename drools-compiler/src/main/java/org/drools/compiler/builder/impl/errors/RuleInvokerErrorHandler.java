package org.drools.compiler.builder.impl.errors;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.base.definitions.rule.impl.RuleImpl;

public class RuleInvokerErrorHandler extends RuleErrorHandler {

    public RuleInvokerErrorHandler(final BaseDescr ruleDescr,
                                   final RuleImpl rule,
                                   final String message) {
        super(ruleDescr,
              rule,
              message);
    }
}
