package org.kie.api.event.rule;


public interface MatchCancelledEvent
    extends
    MatchEvent {
    MatchCancelledCause getCause();
}
