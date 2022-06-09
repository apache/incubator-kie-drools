package org.optaplanner.constraint.drl;

import org.drools.core.common.AgendaItem;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;

public final class OptaPlannerRuleEventListener implements RuleEventListener {

    @Override
    public void onUpdateMatch(Match match) {
        undoPreviousMatch(match);
    }

    @Override
    public void onDeleteMatch(Match match) {
        undoPreviousMatch(match);
    }

    public void undoPreviousMatch(Match match) {
        AgendaItem agendaItem = (AgendaItem) match;
        Runnable callback = agendaItem.getCallback();
        /*
         * In DRL, it is possible that RHS would not call addConstraintMatch() and do some insertLogical() instead,
         * and therefore the callback would be null.
         *
         * Also, if we insert a fact and then immediately delete it without firing any rules inbetween,
         * a dummy match will be created by Drools and that match will not have our callback in it.
         * Although this is inefficient, it was decided that the cure would have been worse than the disease.
         *
         * In both of these situations, it is safe to ignore the null callback.
         */
        if (callback != null) {
            callback.run();
            agendaItem.setCallback(null);
        }
    }

}
