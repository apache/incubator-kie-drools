package org.drools.runtime.rule.impl;

import org.drools.runtime.rule.Agenda;

public interface InternalAgenda extends Agenda {

    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName, long processInstanceId);

    /**
     * Activates the <code>RuleFlowGroup</code> with the given name.
     * All activations in the given <code>RuleFlowGroup</code> are added to the agenda.
     * As long as the <code>RuleFlowGroup</code> remains active,
     * its activations are automatically added to the agenda. 
     */
    public void activateRuleFlowGroup(String name);
    
    /**
     * Activates the <code>RuleFlowGroup</code> with the given name.
     * All activations in the given <code>RuleFlowGroup</code> are added to the agenda.
     * As long as the <code>RuleFlowGroup</code> remains active,
     * its activations are automatically added to the agenda.
     * The given processInstanceId and nodeInstanceId define the process context
     * in which this <code>RuleFlowGroup</code> is used.
     */
    public void activateRuleFlowGroup(String name, long processInstanceId, String nodeInstanceId);

    /**
     * Deactivates the <code>RuleFlowGroup</code> with the given name.
     * All activations in the given <code>RuleFlowGroup</code> are removed from the agenda.
     * As long as the <code>RuleFlowGroup</code> remains deactive,
     * its activations are not added to the agenda
     */
    public void deactivateRuleFlowGroup(String name);

}
