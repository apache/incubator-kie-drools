package org.drools.core.common;

import org.drools.core.phreak.RuleAgendaItem;
import org.kie.api.runtime.rule.AgendaFilter;

/**
 * A filter interface for agenda activations
 */
public interface ActivationsFilter extends AgendaFilter {

    boolean accept(RuleAgendaItem activation);

    void fireRNEAs(InternalWorkingMemory wm);
}
