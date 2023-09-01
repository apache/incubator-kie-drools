package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.base.definitions.rule.impl.RuleImpl;

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
