package org.drools.compiler;

import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Rule;

public class RuleBuildError extends DescrBuildError {
    private final Rule rule;

    public RuleBuildError(final Rule rule,
                          final BaseDescr descr,
                          final Object object,
                          final String message) {
        super( descr,
               descr,
               object,
               message );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }
}
