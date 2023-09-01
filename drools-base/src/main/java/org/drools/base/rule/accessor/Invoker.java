package org.drools.base.rule.accessor;

import org.drools.base.rule.RuleComponent;

public interface Invoker extends RuleComponent {
    default boolean wrapsCompiledInvoker() {
        return false;
    }
}
