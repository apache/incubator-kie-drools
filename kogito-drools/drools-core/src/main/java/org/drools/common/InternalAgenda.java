package org.drools.common;

import org.drools.Agenda;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConsequenceException;

public interface InternalAgenda
    extends
    Agenda {

    public void fireActivation(final Activation activation) throws ConsequenceException;

    public void removeScheduleItem(final ScheduledAgendaItem item);
    
    public org.drools.util.LinkedList getScheduledActivationsLinkedList();

    public boolean fireNextItem(AgendaFilter filter) throws ConsequenceException;

    public void scheduleItem(final ScheduledAgendaItem item);
    
    /**
     * Adds the activation to the agenda. Depending on the mode the agenda is running,
     * the activation may be added to the agenda priority queue (synchronously or 
     * asynchronously) or be executed immediately.
     * 
     * @param activation
     */
    public void addActivation(final AgendaItem activation);

    public void addAgendaGroup(final AgendaGroup agendaGroup);

    public void increaseActiveActivations();

    public void decreaseActiveActivations();

    public void increaseDormantActivations();

    public void decreaseDormantActivations();

    public int getActiveActivations();

    public int getDormantActivations();

    /**
     * Returns true if there is at least one activation of the given rule name
     * in the given ruleflow group name
     * 
     * @param ruleflowGroupName
     * @param ruleName
     * 
     * @return 
     */
	public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName);

	/**
	 * Adds a RuleFlowGroupListerner to the named RuleFlowGroup
	 * 
	 * @param ruleFlowGroup
	 * @param listener
	 */
    public void addRuleFlowGroupListener(String ruleFlowGroup,
                                         RuleFlowGroupListener listener);

    /**
     * Removes the given RuleFlowGroupListener from the list of listeners of the named RuleFlowGroup
     * 
     * @param ruleFlowGroup
     * @param listener
     */
    public void removeRuleFlowGroupListener(String ruleFlowGroup,
                                            RuleFlowGroupListener listener);

}
