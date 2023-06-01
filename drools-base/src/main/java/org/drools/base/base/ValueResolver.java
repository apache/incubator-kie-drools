package org.drools.base.base;

import org.kie.api.definition.rule.RuleBase;
import org.drools.core.rule.accessor.GlobalResolver;

public interface ValueResolver {

    default Object getGlobal(String identifier) {
        return getGlobalResolver().resolveGlobal( identifier );
    }

    long getCurrentTime();

    GlobalResolver getGlobalResolver();

    RuleBase getRuleBase();
}
