package org.drools.core.event;

import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;

public class RuleEventListenerSupport extends AbstractEventSupport<RuleEventListener> {

    public void onBeforeMatchFire(Match match) {
        if ( hasListeners() ) {
            notifyAllListeners( match, (l, m) -> l.onBeforeMatchFire( m ) );
        }
    }

    public void onAfterMatchFire(Match match) {
        if ( hasListeners() ) {
            notifyAllListeners( match, (l, m) -> l.onAfterMatchFire( m ) );
        }
    }

    public void onDeleteMatch(Match match) {
        if ( hasListeners() ) {
            notifyAllListeners( match, (l, m) -> l.onDeleteMatch( m ) );
        }
    }

    public void onUpdateMatch(Match match) {
        if ( hasListeners() ) {
            notifyAllListeners( match, (l, m) ->l.onUpdateMatch( m ) );
        }
    }
}
