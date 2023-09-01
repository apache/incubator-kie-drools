package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.base.definitions.rule.impl.RuleImpl;

public class RuleBuildError extends DescrBuildError {
    private final RuleImpl rule;

    public RuleBuildError(final RuleImpl rule,
                          final BaseDescr descr,
                          final Object object,
                          final String message) {
        super( descr,
               descr,
               object,
               message );
        this.rule = rule;
    }

    public RuleImpl getRule() {
        return this.rule;
    }
}
