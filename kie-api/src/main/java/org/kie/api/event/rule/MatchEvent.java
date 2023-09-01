package org.kie.api.event.rule;

import org.kie.api.event.KieRuntimeEvent;
import org.kie.api.runtime.rule.Match;

public interface MatchEvent
    extends
    KieRuntimeEvent {

    public Match getMatch();

}
