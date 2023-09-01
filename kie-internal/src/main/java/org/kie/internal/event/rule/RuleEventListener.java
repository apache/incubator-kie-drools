package org.kie.internal.event.rule;

import java.util.EventListener;

import org.kie.api.runtime.rule.Match;

public interface RuleEventListener extends EventListener {

    default void onBeforeMatchFire(Match match) {}
    default void onAfterMatchFire(Match match) {}

    default void onDeleteMatch(Match match) {}
    default void onUpdateMatch(Match match) {}

//    to add later
//    void onAllFiring(Rule rule);
//    void onAllFired(Rule rule);
}
