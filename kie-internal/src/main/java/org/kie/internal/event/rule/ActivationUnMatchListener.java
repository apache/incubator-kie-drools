package org.kie.internal.event.rule;

import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.Session;

public interface ActivationUnMatchListener {
    public void unMatch(Session wm, Match activation);
}
