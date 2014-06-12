package org.drools.core.common;

import org.drools.core.phreak.RuleAgendaItem;

public interface GarbageCollector {

    void increaseDeleteCounter();
    int getDeleteCounter();

    void gcUnlinkedRules();
    void forceGcUnlinkedRules();

    void remove(RuleAgendaItem item);
    void add(RuleAgendaItem item);
}
