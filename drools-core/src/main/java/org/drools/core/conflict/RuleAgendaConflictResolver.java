package org.drools.core.conflict;

import java.io.Serializable;

import org.drools.core.phreak.RuleAgendaItem;
import org.drools.base.rule.consequence.ConflictResolver;
import org.kie.api.definition.rule.Rule;

public class RuleAgendaConflictResolver implements ConflictResolver<RuleAgendaItem>, Serializable {

    public static final RuleAgendaConflictResolver INSTANCE = new RuleAgendaConflictResolver();

    public final int compare(RuleAgendaItem existing, RuleAgendaItem adding) {
        return doCompare( existing, adding );
    }

    public final static int doCompare(final RuleAgendaItem existing, final RuleAgendaItem adding) {
        if (existing == adding) {
            return 0;
        }

        final int s1 = existing.getSalience();
        final int s2 = adding.getSalience();

        if (s1 != s2) {
            return s1 > s2 ? 1 : -1;
        }
        Rule r1 = existing.getRule();
        Rule r2 = adding.getRule();

        if (r1.getLoadOrder() == r2.getLoadOrder()) {
            return adding.getTerminalNode().getId() - existing.getTerminalNode().getId();
        }

        return r2.getLoadOrder() - r1.getLoadOrder(); // lowest order goes first
    }
}
