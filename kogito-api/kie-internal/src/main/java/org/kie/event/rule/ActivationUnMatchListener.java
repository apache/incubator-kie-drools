package org.kie.event.rule;

import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.WorkingMemory;

public interface ActivationUnMatchListener {
    public void unMatch(WorkingMemory wm, Match activation);
}
