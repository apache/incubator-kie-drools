package org.drools.event.rule;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.WorkingMemory;

public interface ActivationUnMatchListener {
    public void unMatch(WorkingMemory wm, Activation activation);
}
