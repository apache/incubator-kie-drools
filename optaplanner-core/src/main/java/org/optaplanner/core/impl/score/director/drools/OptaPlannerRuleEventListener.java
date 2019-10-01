package org.optaplanner.core.impl.score.director.drools;

import org.drools.core.common.AgendaItem;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

public final class OptaPlannerRuleEventListener implements RuleEventListener {

    @Override
    public void onUpdateMatch(Match match) {
        undoPreviousMatch((AgendaItem) match);
    }

    @Override
    public void onDeleteMatch(Match match) {
        undoPreviousMatch((AgendaItem) match);
    }

    public void undoPreviousMatch(AgendaItem agendaItem) {
        Object callback = agendaItem.getCallback();
        // Some rules don't have a callback because their RHS doesn't do addConstraintMatch()
        if (callback instanceof AbstractScoreHolder.ConstraintActivationUnMatchListener) {
            ((AbstractScoreHolder.ConstraintActivationUnMatchListener) callback).run();
            agendaItem.setCallback(null);
        }
    }

}
