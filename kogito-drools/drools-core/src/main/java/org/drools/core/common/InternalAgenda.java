/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.ExecutableEntry;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.spi.Tuple;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaFilter;

import java.util.LinkedList;
import java.util.Map;

public interface InternalAgenda
    extends
    Agenda {

    /**
     * Returns the WorkignMemory for this Agenda
     * @return
     *      The WorkingMemory
     */
    InternalWorkingMemory getWorkingMemory();

    /**
     * Sets the Agenda's focus to the specified AgendaGroup
     */
    void setFocus(String name);

    /**
     * Activates the <code>RuleFlowGroup</code> with the given name.
     * All activations in the given <code>RuleFlowGroup</code> are added to the agenda.
     * As long as the <code>RuleFlowGroup</code> remains active,
     * its activations are automatically added to the agenda.
     */
    void activateRuleFlowGroup(String name);

    /**
     * Activates the <code>RuleFlowGroup</code> with the given name.
     * All activations in the given <code>RuleFlowGroup</code> are added to the agenda.
     * As long as the <code>RuleFlowGroup</code> remains active,
     * its activations are automatically added to the agenda.
     * The given processInstanceId and nodeInstanceId define the process context
     * in which this <code>RuleFlowGroup</code> is used.
     */
    void activateRuleFlowGroup(String name, long processInstanceId, String nodeInstanceId);

    /**
     * Deactivates the <code>RuleFlowGroup</code> with the given name.
     * All activations in the given <code>RuleFlowGroup</code> are removed from the agenda.
     * As long as the <code>RuleFlowGroup</code> remains deactive,
     * its activations are not added to the agenda
     */
    void deactivateRuleFlowGroup(String name);

    AgendaGroup[] getAgendaGroups();

    AgendaGroup[] getStack();

    int unstageActivations();

    /**
     * Iterates all the <code>AgendGroup<code>s in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of <code>Activation</code>s on the focus stack
     */
    int focusStackSize();

    /**
     * Iterates all the modules in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of activations on the focus stack
     */
    int agendaSize();

    Activation[] getActivations();

    Activation[] getScheduledActivations();

    /**
     * Clears all Activations from the Agenda
     */
    void clearAndCancel();

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     */
    void clearAndCancelAgendaGroup(String name);

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     */
    void clearAndCancelAgendaGroup(InternalAgendaGroup agendaGroup);

    /**
     * Clears all Activations from an Activation-Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     */
    void clearAndCancelActivationGroup(String name);

    /**
     * Clears all Activations from an Activation Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     *
     * @param activationGroup
     *
     */
    void clearAndCancelActivationGroup(InternalActivationGroup activationGroup);

    void clearAndCancelRuleFlowGroup(final String name);

    void clearAndCancelAndCancel(final RuleFlowGroup ruleFlowGroup);

    /**
     * Returns the name of the agenda group that currently
     * has the focus
     *
     * @return
     */
    String getFocusName();

    void fireActivation(final Activation activation) throws ConsequenceException;

    void fireConsequenceEvent(Activation activation, String consequenceName) throws ConsequenceException;

    boolean fireTimedActivation(final Activation activation) throws ConsequenceException;

    void removeScheduleItem(final ScheduledAgendaItem item);

    <T extends ModedAssertion<T>> org.drools.core.util.LinkedList<ScheduledAgendaItem<T>> getScheduledActivationsLinkedList();

    int fireNextItem(AgendaFilter filter, int fireCount, int fireLimit) throws ConsequenceException;

    void scheduleItem(final ScheduledAgendaItem item, InternalWorkingMemory workingMemory);

    AgendaItem createAgendaItem(RuleTerminalNodeLeftTuple rtnLeftTuple,
                                int salience,
                                PropagationContext context,
                                RuleAgendaItem ruleAgendaItem,
                                InternalAgendaGroup agendaGroup);

    boolean createActivation(final Tuple tuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory,
                             final TerminalNode rtn );

    void cancelActivation(final Tuple leftTuple,
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
    boolean addActivation(final AgendaItem activation);

    void removeActivation(final AgendaItem activation);

    void modifyActivation(final AgendaItem activation, boolean previouslyActive);

    void addAgendaGroup(final AgendaGroup agendaGroup);

    boolean isDeclarativeAgenda();

    /**
     * Returns true if there is at least one activation of the given rule name
     * in the given ruleflow group name
     *
     * @param ruleflowGroupName
     * @param ruleName
     *
     * @return
     */
    boolean isRuleInstanceAgendaItem(String ruleflowGroupName,
                                            String ruleName,
                                            long processInstanceId);

    void setWorkingMemory(final InternalWorkingMemory workingMemory);

    /**
     * Fires all activations currently in agenda that match the given agendaFilter
     * until the fireLimit is reached or no more activations exist.
     *
     *
     * @param agendaFilter the filter on which activations may fire.
     * @param fireLimit the maximum number of activations that may fire. If -1, then it will
     *                  fire until no more activations exist.
     *
     * @param fireLimit
     * @return the number of rules that were actually fired
     */
    int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit);

    /**
     * Stop agenda from firing any other rule. It will finish the current rule
     * execution though.
     */
    void halt();

    /**
     * Keeps firing activations until a halt is called. If in a given moment, there is
     * no activation to fire, it will wait for an activation to be added to an active
     * agenda group or rule flow group.
     */
    void fireUntilHalt();

    /**
     * Keeps firing activations until a halt is called. If in a given moment, there is
     * no activation to fire, it will wait for an activation to be added to an active
     * agenda group or rule flow group.
     *
     * @param agendaFilter filters the activations that may fire
     */
    void fireUntilHalt(AgendaFilter agendaFilter);

    AgendaGroup getAgendaGroup(String name);

    AgendaGroup getAgendaGroup(final String name,
                                      InternalKnowledgeBase kBase);

    InternalActivationGroup getActivationGroup(String name);

    RuleFlowGroup getRuleFlowGroup(String name);

    /**
     * Sets a filter that prevents activations from being added to
     * the agenda.
     *
     * @param filter
     */
    void setActivationsFilter( ActivationsFilter filter );

    /**
     * Returns the current activations filter or null if none is set
     *
     * @return
     */
    ActivationsFilter getActivationsFilter();

    RuleAgendaItem createRuleAgendaItem(final int salience,
                                               final PathMemory rs,
                                               final TerminalNode rtn );

    RuleAgendaItem peekNextRule();

    boolean isFiring();
    void executeTask( ExecutableEntry executable );

    void insertAndStageActivation(AgendaItem activation);

    void addAgendaItemToGroup(AgendaItem item);

    void addEagerRuleAgendaItem(RuleAgendaItem item);
    void removeEagerRuleAgendaItem(RuleAgendaItem item);

    void addQueryAgendaItem(final RuleAgendaItem item);
    void removeQueryAgendaItem(final RuleAgendaItem item);

    /*
         * (non-Javadoc)
         *
         * @see org.kie.common.AgendaI#setFocus(org.kie.spi.AgendaGroup)
         */
    boolean setFocus(AgendaGroup agendaGroup);

    void stageLeftTuple(RuleAgendaItem ruleAgendaItem, AgendaItem justified);

    Map<String, InternalAgendaGroup> getAgendaGroupsMap();

    void addAgendaGroupOnStack(AgendaGroup agendaGroup);

    void evaluateEagerList();

    Map<String,InternalActivationGroup> getActivationGroupsMap();

    InternalAgendaGroup getNextFocus();

    LinkedList<AgendaGroup> getStackList();

    AgendaGroup getFocus();

    int sizeOfRuleFlowGroup(String s);

    void addItemToActivationGroup(AgendaItem item);

    boolean createPostponedActivation(LeftTuple postponedTuple, PropagationContext propagationContext, InternalWorkingMemory workingMemory, TerminalNode terminalNode);

    boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName, long processInstanceId);
}
