package org.drools.core.event.rule;

import org.drools.core.runtime.rule.Activation;
import org.drools.core.runtime.rule.WorkingMemory;

public interface ActivationUnMatchListener {
    public void unMatch(WorkingMemory wm, Activation activation);
}
