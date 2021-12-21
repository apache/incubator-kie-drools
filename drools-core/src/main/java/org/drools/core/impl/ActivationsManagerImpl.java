/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.common.ActivationGroupImpl;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationsFilter;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.AgendaGroupsManager;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.concurrent.RuleEvaluator;
import org.drools.core.concurrent.SequentialRuleEvaluator;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.phreak.ExecutableEntry;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.phreak.SynchronizedPropagationList;
import org.drools.core.reteoo.AgendaComponentFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.QueryImpl;
import org.drools.core.spi.Activation;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.StringUtils;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.rule.AgendaFilter;

public class ActivationsManagerImpl implements ActivationsManager {

    private final ReteEvaluator reteEvaluator;
    private final AgendaGroupsManager agendaGroupsManager;

    private int activationCounter = 0;

    private final AgendaEventSupport agendaEventSupport = new AgendaEventSupport();

    private final Map<String, InternalActivationGroup> activationGroups = new HashMap<>();

    private final PropagationList propagationList;

    private final RuleEvaluator ruleEvaluator;

    private boolean firing = false;

    private final org.drools.core.util.LinkedList<RuleAgendaItem> eager = new org.drools.core.util.LinkedList<>();

    private final Map<QueryImpl, RuleAgendaItem> queries = new ConcurrentHashMap<>();

    private List<PropagationContext> expirationContexts;

    public ActivationsManagerImpl(ReteEvaluator reteEvaluator) {
        this.reteEvaluator = reteEvaluator;
        this.agendaGroupsManager = new AgendaGroupsManager.SimpleAgendaGroupsManager(reteEvaluator);
        this.propagationList = new SynchronizedPropagationList(reteEvaluator);
        this.ruleEvaluator = new SequentialRuleEvaluator( this );
        if (reteEvaluator.getKnowledgeBase().getConfiguration().getEventProcessingMode() == EventProcessingOption.STREAM) {
            expirationContexts = new ArrayList<>();
        }
    }

    @Override
    public ReteEvaluator getReteEvaluator() {
        return reteEvaluator;
    }

    @Override
    public AgendaGroupsManager getAgendaGroupsManager() {
        return agendaGroupsManager;
    }

    @Override
    public AgendaEventSupport getAgendaEventSupport() {
        return agendaEventSupport;
    }

    @Override
    public ActivationsFilter getActivationsFilter() {
        // TODO this is only used by protobuf serialization which is not implemented here for now
        return null;
    }

    @Override
    public void addEagerRuleAgendaItem(RuleAgendaItem item) {
        if ( item.isInList(eager) ) {
            return;
        }
        eager.add( item );
    }

    @Override
    public void removeEagerRuleAgendaItem(RuleAgendaItem item) {
        if ( !item.isInList(eager) ) {
            return;
        }
        eager.remove( item );
    }

    @Override
    public void addQueryAgendaItem(RuleAgendaItem item) {
        queries.put( (QueryImpl) item.getRule(), item );
    }

    @Override
    public void removeQueryAgendaItem(RuleAgendaItem item) {
        queries.remove( (QueryImpl) item.getRule() );
    }

    @Override
    public void registerExpiration(PropagationContext ectx) {
        // it is safe to add into the expirationContexts list without any synchronization because
        // the state machine already guarantees that only one thread at time can access it
        expirationContexts.add(ectx);
    }

    @Override
    public void clearAndCancelActivationGroup(final String name) {
        final InternalActivationGroup activationGroup = this.activationGroups.get( name );
        if ( activationGroup != null ) {
            clearAndCancelActivationGroup( activationGroup );
        }
    }

    @Override
    public void clearAndCancelActivationGroup(final InternalActivationGroup activationGroup) {
        activationGroup.setTriggeredForRecency( this.reteEvaluator.getFactHandleFactory().getRecency() );

        for (final Iterator it = activationGroup.iterator(); it.hasNext(); ) {
            final ActivationGroupNode node = (ActivationGroupNode) it.next();
            final Activation activation = node.getActivation();
            activation.setActivationGroupNode( null );

            if ( activation.isQueued() ) {
                activation.setQueued(false);
                activation.remove();

                RuleExecutor ruleExec = ((RuleTerminalNodeLeftTuple)activation).getRuleAgendaItem().getRuleExecutor();
                ruleExec.removeLeftTuple((LeftTuple) activation);
                getAgendaEventSupport().fireActivationCancelled( activation, this.reteEvaluator, MatchCancelledCause.CLEAR );
            }
        }
        activationGroup.reset();
    }

    @Override
    public RuleAgendaItem createRuleAgendaItem(int salience, PathMemory pathMemory, TerminalNode rtn) {
        return AgendaComponentFactory.get().createAgendaItem( activationCounter++, null, salience, null, pathMemory, rtn, false, agendaGroupsManager.getMainAgendaGroup());
    }

    @Override
    public AgendaItem createAgendaItem(RuleTerminalNodeLeftTuple rtnLeftTuple, int salience, PropagationContext context, RuleAgendaItem ruleAgendaItem, InternalAgendaGroup agendaGroup) {
        rtnLeftTuple.init(activationCounter++, salience, context, ruleAgendaItem, agendaGroup);
        return rtnLeftTuple;
    }

    @Override
    public void cancelActivation(Activation activation) {
        AgendaItem item = (AgendaItem) activation;

        if ( activation.isQueued() ) {
            if ( activation.getActivationGroupNode() != null ) {
                activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
            }
            ((Tuple) activation).decreaseActivationCountForEvents();

            getAgendaEventSupport().fireActivationCancelled( activation, reteEvaluator, MatchCancelledCause.WME_MODIFY );
        }

        if (item.getRuleAgendaItem() != null) {
            item.getRuleAgendaItem().getRuleExecutor().fireConsequenceEvent( this.reteEvaluator, this, item, ON_DELETE_MATCH_CONSEQUENCE_NAME );
        }

        reteEvaluator.getRuleEventSupport().onDeleteMatch( item );
    }

    @Override
    public void addItemToActivationGroup(AgendaItem activation) {
        if ( activation.isRuleAgendaItem() ) {
            throw new UnsupportedOperationException("defensive programming, making sure this isn't called, before removing");
        }
        String group = activation.getRule().getActivationGroup();
        if ( !StringUtils.isEmpty(group) ) {
            InternalActivationGroup actgroup = this.activationGroups.computeIfAbsent(group, k -> new ActivationGroupImpl( this, k ));

            // Don't allow lazy activations to activate, from before it's last trigger point
            if ( actgroup.getTriggeredForRecency() != 0 &&
                    actgroup.getTriggeredForRecency() >= activation.getPropagationContext().getFactHandle().getRecency() ) {
                return;
            }

            actgroup.addActivation( activation );
        }
    }

    @Override
    public RuleAgendaItem peekNextRule() {
        return getAgendaGroupsManager().peekNextRule();
    }

    @Override
    public void flushPropagations() {
        propagationList.flush();
    }

    @Override
    public boolean isFiring() {
        return firing;
    }

    @Override
    public void evaluateEagerList() {
        while ( !eager.isEmpty() ) {
            RuleAgendaItem item = eager.removeFirst();
            if (item.isRuleInUse()) { // this rule could have been removed by an incremental compilation
                evaluateQueriesForRule( item );
                RuleExecutor ruleExecutor = item.getRuleExecutor();
                ruleExecutor.evaluateNetwork( this );
            }
        }
    }

    @Override
    public void evaluateQueriesForRule(RuleAgendaItem item) {
        RuleImpl rule = item.getRule();
        if (!rule.isQuery()) {
            for (QueryImpl query : rule.getDependingQueries()) {
                RuleAgendaItem queryAgendaItem = queries.remove(query);
                if (queryAgendaItem != null) {
                    queryAgendaItem.getRuleExecutor().evaluateNetwork(this);
                }
            }
        }
    }

    @Override
    public KnowledgeHelper getKnowledgeHelper() {
        return ruleEvaluator.getKnowledgeHelper();
    }

    @Override
    public void executeTask(ExecutableEntry executableEntry) {
        executableEntry.execute();
    }

    @Override
    public void addPropagation(PropagationEntry propagationEntry) {
        propagationList.addEntry( propagationEntry );
    }

    @Override
    public int fireAllRules(AgendaFilter agendaFilter, int fireLimit) {
        return fireLoop( agendaFilter, fireLimit, RestHandler.FIRE_ALL_RULES );
    }

    private int fireLoop(AgendaFilter agendaFilter, int fireLimit, RestHandler restHandler) {
        firing = true;
        int fireCount = 0;
        PropagationEntry head = propagationList.takeAll();
        int returnedFireCount;

        boolean limitReached = fireLimit == 0; // -1 or > 0 will return false. No reason for user to give 0, just handled for completeness.

        while ( isFiring()  )  {
            if ( head != null ) {
                // it is possible that there are no action propagations, but there are rules to fire.
                propagationList.flush(head);
                head = null;
            }

            evaluateEagerList();
            InternalAgendaGroup group = getAgendaGroupsManager().getNextFocus();
            if ( group != null && !limitReached ) {
                // only fire rules while the limit has not reached.
                // if halt is called, then isFiring will be false.
                // The while loop may continue to loop, to keep flushing the action propagation queue
                returnedFireCount = ruleEvaluator.evaluateAndFire( agendaFilter, fireCount, fireLimit, group );
                fireCount += returnedFireCount;

                limitReached = ( fireLimit > 0 && fireCount >= fireLimit );
                head = propagationList.takeAll();
            } else {
                returnedFireCount = 0; // no rules fired this iteration, so we know this is 0
                group = null; // set the group to null in case the fire limit has been reached
            }

            if ( returnedFireCount == 0 && head == null && ( group == null || ( group.isEmpty() && !group.isAutoDeactivate() ) ) && !flushExpirations() ) {
                // if true, the engine is now considered potentially at rest
                head = restHandler.handleRest( this );
            }
        }

        agendaGroupsManager.deactivateMainGroupWhenEmpty();
        return fireCount;
    }

    private boolean flushExpirations() {
        if (expirationContexts == null || expirationContexts.isEmpty() || propagationList.hasEntriesDeferringExpiration()) {
            return false;
        }
        for (PropagationContext ectx : expirationContexts) {
            doRetract( ectx );
        }
        expirationContexts.clear();
        return true;
    }

    private void doRetract( PropagationContext ectx ) {
        InternalFactHandle factHandle = ectx.getFactHandle();
        ObjectTypeNode.retractLeftTuples( factHandle, ectx, reteEvaluator );
        ObjectTypeNode.retractRightTuples( factHandle, ectx, reteEvaluator );
        if ( factHandle.isPendingRemoveFromStore() ) {
            String epId = factHandle.getEntryPointName();
            ( (InternalWorkingMemoryEntryPoint) reteEvaluator.getEntryPoint( epId ) ).removeFromObjectStore( factHandle );
        }
    }

    interface RestHandler {
        RestHandler FIRE_ALL_RULES = new RestHandler.FireAllRulesRestHandler();
        RestHandler FIRE_UNTIL_HALT = new RestHandler.FireUntilHaltRestHandler();

        PropagationEntry handleRest(ActivationsManagerImpl agenda);

        class FireAllRulesRestHandler implements RestHandler {
            @Override
            public PropagationEntry handleRest(ActivationsManagerImpl agenda) {
                PropagationEntry head = agenda.propagationList.takeAll();
                if (head == null) {
                    agenda.firing = false;
                }
                return head;
            }
        }

        class FireUntilHaltRestHandler implements RestHandler {
            @Override
            public PropagationEntry handleRest(ActivationsManagerImpl agenda) {
                PropagationEntry head;
                // this must use the same sync target as takeAllPropagations, to ensure this entire block is atomic, up to the point of wait
                synchronized (agenda.propagationList) {
                    head = agenda.propagationList.takeAll();

                    // if halt() has called, the thread should not be put into a wait state
                    // instead this is just a safe way to make sure the queue is flushed before exiting the loop
                    if (head == null) {
                        agenda.propagationList.waitOnRest();
                        head = agenda.propagationList.takeAll();
                        if (head == null) {
                            agenda.firing = false;
                        }
                    }
                }

                return head;
            }
        }
    }
}
