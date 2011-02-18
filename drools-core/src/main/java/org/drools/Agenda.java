/**
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import org.drools.WorkingMemory;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;
import org.drools.spi.RuleFlowGroup;

/**
 * Agenda interface for the WorkingMemory
 *
 */
public interface Agenda extends org.drools.runtime.rule.Agenda {

    /**
     * Returns the WorkignMemory for this Agenda
     * @return
     *      The WorkingMemory
     */
    public WorkingMemory getWorkingMemory();

    /**
     * Sets the Agenda's focus to the specified AgendaGroup
     * @param agendaGroup
     * @return
     */    
    public void setFocus(String name);

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

    public AgendaGroup[] getAgendaGroups();

    public AgendaGroup[] getStack();

    /**
     * Iterates all the <code>AgendGroup<code>s in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of <code>Activation</code>s on the focus stack
     */
    public int focusStackSize();

    /**
     * Iterates all the modules in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of activations on the focus stack
     */
    public int agendaSize();

    public Activation[] getActivations();

    public Activation[] getScheduledActivations();

    /**
     * Clears all Activations from the Agenda
     * 
     */
    public void clearAndCancel();

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     * 
     * @param agendaGroup
     */
    public void clearAndCancelAgendaGroup(String name);

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     * 
     * @param agendaGroup
     */
    public void clearAndCancelAgendaGroup(AgendaGroup agendaGroup);

    /**
     * Clears all Activations from an Activation-Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     * 
     * @param activationGroup
     */
    public void clearAndCancelActivationGroup(String name);

    /**
     * Clears all Activations from an Activation Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     * 
     * @param activationGroup
     */
    public void clearAndCancelActivationGroup(ActivationGroup activationGroup);
    
    public void clearAndCancelRuleFlowGroup(final String name);
    
    public void clearAndCancelAndCancel(final RuleFlowGroup ruleFlowGroup);

    /**
     * Returns the name of the agenda group that currently
     * has the focus
     * 
     * @return
     */
    public String getFocusName();
    
    AgendaGroup getAgendaGroup(String name);
    
    ActivationGroup getActivationGroup(String name);
    
    RuleFlowGroup getRuleFlowGroup(String name);    

}
