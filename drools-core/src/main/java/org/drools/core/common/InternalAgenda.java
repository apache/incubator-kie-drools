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

package org.drools.core.common;

import org.drools.core.Agenda;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.ActivationGroup;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;

public interface InternalAgenda
    extends
    Agenda {

    public void fireActivation(final Activation activation) throws ConsequenceException;

    public boolean fireTimedActivation(final Activation activation, boolean saveForLater ) throws ConsequenceException;

    public void removeScheduleItem(final ScheduledAgendaItem item);
    
    public org.drools.core.util.LinkedList<ScheduledAgendaItem> getScheduledActivationsLinkedList();

    public int fireNextItem(AgendaFilter filter, int fireCount, int fireLimit) throws ConsequenceException;

    public void scheduleItem(final ScheduledAgendaItem item, InternalWorkingMemory workingMemory);

    public AgendaItem createAgendaItem(final LeftTuple tuple,
                                       final int salience,
                                       final PropagationContext context,
                                       final TerminalNode rtn,
                                       RuleAgendaItem ruleAgendaItem,
                                       InternalAgendaGroup agendaGroup);

    public ScheduledAgendaItem createScheduledAgendaItem(final LeftTuple tuple,
                                                         final PropagationContext context,
                                                         final TerminalNode rtn,
                                                         InternalAgendaGroup agendaGroup);
    
    public boolean createActivation(final LeftTuple tuple,
                                    final PropagationContext context,
                                    final InternalWorkingMemory workingMemory,
                                    final TerminalNode rtn );

    public void cancelActivation(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory,
                                 final Activation activation,
                                 final TerminalNode rtn );

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

    /**
     * Returns true if there is at least one activation of the given rule name
     * in the given ruleflow group name
     * 
     * @param ruleflowGroupName
     * @param ruleName
     * 
     * @return 
     */
    public boolean isRuleInstanceAgendaItem(String ruleflowGroupName,
                                            String ruleName,
                                            long processInstanceId);

    public void clear();

    public void setWorkingMemory(final InternalWorkingMemory workingMemory);

    /**
     * Fires all activations currently in agenda that match the given agendaFilter
     * until the fireLimit is reached or no more activations exist.
     * 
     *
     * @param agendaFilter the filter on which activations may fire.
     * @param fireLimit the maximum number of activations that may fire. If -1, then it will
     *                  fire until no more activations exist.
     *
     * @param limit
     * @return the number of rules that were actually fired
     */
    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit);

    /**
     * Stop agenda from firing any other rule. It will finish the current rule
     * execution though.
     */
    public void halt();

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
        
    public RuleAgendaItem createRuleAgendaItem(final int salience,
                                               final PathMemory rs,
                                               final TerminalNode rtn );

    public RuleAgendaItem peekNextRule();

    boolean continueFiring(int fireLimit);

    void insertAndStageActivation(AgendaItem activation);

    void addAgendaItemToGroup(AgendaItem item);

    void addEagerRuleAgendaItem(RuleAgendaItem item);

    void removeEagerRuleAgendaItem(RuleAgendaItem item);

    long getNextActivationCounter();

    /*
         * (non-Javadoc)
         *
         * @see org.kie.common.AgendaI#setFocus(org.kie.spi.AgendaGroup)
         */
    boolean setFocus(AgendaGroup agendaGroup);

    boolean isFireUntilHalt();
}
