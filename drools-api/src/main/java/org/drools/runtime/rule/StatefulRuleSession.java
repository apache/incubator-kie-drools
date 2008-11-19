package org.drools.runtime.rule;

public interface StatefulRuleSession
    extends
    WorkingMemory {

    /**
     * Fire all Activations on the Agenda
     * @return
     *     returns the number of rules fired
     */
    int fireAllRules();

    /**
     * Fire all Activations on the Agenda
     * 
     * @param agendaFilter
     *      filters the activations that may fire
     * @return
     *      returns the number of rules fired
     */
    int fireAllRules(AgendaFilter agendaFilter);

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group.
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public void fireUntilHalt();

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group.
     * 
     * @param agendaFilter
     *            filters the activations that may fire
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public void fireUntilHalt(final AgendaFilter agendaFilter);
}
