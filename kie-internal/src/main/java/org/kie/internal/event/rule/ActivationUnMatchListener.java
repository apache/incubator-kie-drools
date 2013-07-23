package org.kie.internal.event.rule;

import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleRuntime;

public interface ActivationUnMatchListener {
    public void unMatch(RuleRuntime wm, Match activation);
}
