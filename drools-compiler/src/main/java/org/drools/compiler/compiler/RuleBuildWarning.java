package org.drools.compiler.compiler;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;

public class RuleBuildWarning extends DescrBuildWarning {
    private final RuleImpl rule;

    public RuleBuildWarning( final RuleImpl rule,
                             final BaseDescr descr,
                             final Object object,
                             final String message ) {
        super( descr, descr, object, message );
        this.rule = rule;
    }

    public RuleImpl getRule() {
        return this.rule;
    }

}
