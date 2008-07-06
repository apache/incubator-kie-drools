package org.drools.common;

import org.drools.StatelessSession;

public interface InternalStatelessSession extends StatelessSession {
    InternalRuleBase getRuleBase();
}
