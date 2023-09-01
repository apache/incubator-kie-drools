package org.drools.mvel.expr;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.mvel.MVELDialectRuntimeData;

public interface MVELCompileable {
    void compile( MVELDialectRuntimeData runtimeData);
    void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule);
}
