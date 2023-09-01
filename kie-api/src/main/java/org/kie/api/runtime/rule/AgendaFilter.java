package org.kie.api.runtime.rule;

public interface AgendaFilter {

    /**
     * Determine if a given match should be fired.
     *
     * @param match
     *     The match that is requested to be fired
     * @return
     *     boolean value of "true" accepts the match for firing.
     */
    boolean accept(Match match);
}
