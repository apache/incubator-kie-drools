package org.drools.base.base;

import org.drools.base.RuleBase;
import org.drools.base.rule.accessor.GlobalResolver;

public interface ValueResolver {

    default Object getGlobal(String identifier) {
        return getGlobalResolver().resolveGlobal( identifier );
    }

    long getCurrentTime();

    GlobalResolver getGlobalResolver();

    RuleBase getRuleBase();
}
