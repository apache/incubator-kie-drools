/*
 * Copyright 2010 JBoss Inc
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

package org.drools.common;

import org.drools.Agenda;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConsequenceException;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;

public interface InternalAgenda
    extends
    Agenda {

    public void fireActivation(final Activation activation) throws ConsequenceException;

    public void removeScheduleItem(final ScheduledAgendaItem item);
    
    public org.drools.core.util.LinkedList getScheduledActivationsLinkedList();

    public boolean fireNextItem(AgendaFilter filter) throws ConsequenceException;

    public void scheduleItem(final ScheduledAgendaItem item, InternalWorkingMemory workingMemory);

    public AgendaItem createAgendaItem(final LeftTuple tuple,
                                       final int salience,
                                       final PropagationContext context,
                                       final RuleTerminalNode rtn);

    public ScheduledAgendaItem createScheduledAgendaItem(final LeftTuple tuple,
                                                         final PropagationContext context,
                                                         final RuleTerminalNode rtn);
    
    public boolean createActivation(final LeftTuple tuple,
                                    final PropagationContext context,
                                    final InternalWorkingMemory workingMemory,
                                    final RuleTerminalNode rtn,
                                    final boolean reuseActivation );        

    public void cancelActivation(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory,
                                 final Activation activation,
                                 final RuleTerminalNode rtn );

    /**
     * Adds the activation to the agenda. Depending on the mode the agenda is running,
     * the activation may be added to the agenda priority queue (synchronously or 
     * asynchronously) or be executed immediately.
     * 
     * @param activation
     * 
     * @return true if the activation was really added, and not ignored in cases of lock-on-active or no-loop
     */
    public boolean addActivation(final AgendaItem activation);
    
    public void removeActivation(final AgendaItem activation);
    
    public void modifyActivation(final AgendaItem activation, boolean previouslyActive);    

    public void addAgendaGroup(final AgendaGroup agendaGroup);
    
    public boolean isDeclarativeAgenda();

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
    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName,
                                               String ruleName,
                                               long processInstanceId);

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

    public void clear();

    public void setWorkingMemory(final InternalWorkingMemory workingMemory);

    /**
     * Fires all activations currently in agenda that match the given agendaFilter
     * until the fireLimit is reached or no more activations exist.
     * 
     * @param agendaFilter the filter on which activations may fire.
     * @param fireLimit the maximum number of activations that may fire. If -1, then it will
     *                  fire until no more activations exist.
     *                  
     * @return the number of rules that were actually fired                 
     */
    public int fireAllRules(AgendaFilter agendaFilter,
                             int fireLimit);

    /**
     * Stop agenda from firing any other rule. It will finish the current rule
     * execution though.
     */
    public void halt();

    public boolean isHalted();
    
    public void notifyHalt();

    /**
     * Keeps firing activations until a halt is called. If in a given moment, there is 
     * no activation to fire, it will wait for an activation to be added to an active 
     * agenda group or rule flow group.
     */
    public void fireUntilHalt();
    
    /**
     * Keeps firing activations until a halt is called. If in a given moment, there is 
     * no activation to fire, it will wait for an activation to be added to an active 
     * agenda group or rule flow group.
     * 
     * @param agendaFilter filters the activations that may fire
     */
    public void fireUntilHalt(AgendaFilter agendaFilter);

    public AgendaGroup getAgendaGroup(String name);

    public ActivationGroup getActivationGroup(String name);

    public RuleFlowGroup getRuleFlowGroup(String name);
    
    /**
     * Sets a filter that prevents activations from being added to 
     * the agenda.
     * 
     * @param filter
     */
    public void setActivationsFilter( ActivationsFilter filter );
    
    /**
     * Returns the current activations filter or null if none is set
     * 
     * @return
     */
    public ActivationsFilter getActivationsFilter();
    
    
}
