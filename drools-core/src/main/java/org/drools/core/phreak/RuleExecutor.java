/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import org.drools.base.base.SalienceInteger;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.consequence.Consequence;
import org.drools.base.rule.consequence.ConsequenceException;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalActivationGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.conflict.MatchConflictResolver;
import org.drools.core.conflict.RuleAgendaConflictResolver;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.core.util.Queue;
import org.drools.core.util.QueueFactory;
import org.drools.core.util.index.TupleList;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.rule.AgendaFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleExecutor {

    protected static final Logger log = LoggerFactory.getLogger(RuleExecutor.class);

    private final PathMemory pmem;
    private final RuleAgendaItem ruleAgendaItem;
    private final TupleList tupleList;
    private Queue<InternalMatch> queue;
    private volatile boolean dirty;
    private final boolean declarativeAgendaEnabled;
    private boolean fireExitedEarly;

    public RuleExecutor(final PathMemory pmem,
            RuleAgendaItem ruleAgendaItem,
            boolean declarativeAgendaEnabled) {
        this.pmem = pmem;
        this.ruleAgendaItem = ruleAgendaItem;
        this.tupleList = new TupleList();
        this.declarativeAgendaEnabled = declarativeAgendaEnabled;
        if (ruleAgendaItem.getRule().getSalience().isDynamic()) {
            queue = QueueFactory.createQueue(MatchConflictResolver.INSTANCE);
        }
    }

    public void evaluateNetwork(ActivationsManager activationsManager) {
        RuleNetworkEvaluator.INSTANCE.evaluateNetwork( pmem, this, activationsManager );
        setDirty( false );
    }

    public int evaluateNetworkAndFire( ReteEvaluator reteEvaluator,
                                       AgendaFilter filter,
                                       int fireCount,
                                       int fireLimit ) {
        reEvaluateNetwork( reteEvaluator );
        return fire(reteEvaluator, pmem.getActualActivationsManager( reteEvaluator ), filter, fireCount, fireLimit);
    }

    public int evaluateNetworkAndFire( ActivationsManager activationsManager,
                                       AgendaFilter filter,
                                       int fireCount,
                                       int fireLimit ) {
        ReteEvaluator reteEvaluator = activationsManager.getReteEvaluator();

        reEvaluateNetwork( activationsManager );

        if ( reteEvaluator.getRuleSessionConfiguration().isDirectFiring() ) {
            RuleTerminalNode rtn = (RuleTerminalNode) pmem.getPathEndNode();
            RuleImpl rule = rtn.getRule();
            int directFirings = tupleList.size();

            for (Tuple tuple = tupleList.getFirst(); tuple != null; tuple = tupleList.getFirst()) {
                if (cancelAndContinue(reteEvaluator, rtn, rule, tuple, filter)) {
                    directFirings--;
                } else {
                    fireActivationEvent(reteEvaluator, activationsManager, (InternalMatch) tuple, ((InternalMatch) tuple).getConsequence());
                }
                removeLeftTuple( tuple );
            }
            ruleAgendaItem.remove();
            return directFirings;
        }

        return fire( reteEvaluator, activationsManager, filter, fireCount, fireLimit );
    }

    public void fire(ActivationsManager activationsManager) {
        fire(activationsManager.getReteEvaluator(), activationsManager, null, 0, Integer.MAX_VALUE);
    }

    private int fire( ReteEvaluator reteEvaluator,
                      ActivationsManager activationsManager,
                      AgendaFilter filter,
                      int fireCount,
                      int fireLimit) {
        int localFireCount = 0;

        if (!tupleList.isEmpty()) {
            if (!fireExitedEarly && isDeclarativeAgendaEnabled()) {
                // Network Evaluation can notify meta rules, which should be given a chance to fire first
                RuleAgendaItem nextRule = activationsManager.peekNextRule();
                if (!isHigherSalience( nextRule )) {
                    fireExitedEarly = true;
                    return localFireCount;
                }
            }

            RuleTerminalNode rtn = (RuleTerminalNode) pmem.getPathEndNode();
            RuleImpl rule = rtn.getRule();
            boolean ruleIsAllMatches = rule.isAllMatches();
            Tuple tuple = getNextTuple();
            
            if (ruleIsAllMatches) {
                fireConsequenceEvent(reteEvaluator, activationsManager, (InternalMatch) tuple, ActivationsManager.ON_BEFORE_ALL_FIRES_CONSEQUENCE_NAME);
            }

            Tuple lastTuple = null;
            for (; tuple != null; lastTuple = tuple, tuple = getNextTuple()) {

                //check if the rule is not effective or
                // if the current Rule is no-loop and the origin rule is the same then return
                if (cancelAndContinue(reteEvaluator, rtn, rule, tuple, filter)) {
                    continue;
                }

                InternalMatch item = (InternalMatch) tuple;
                if (activationsManager.getActivationsFilter() != null && !activationsManager.getActivationsFilter().accept(item)) {
                    // only relevant for serialization, to not refire Matches already fired
                    continue;
                }

                fireActivation( reteEvaluator, activationsManager, item );
                localFireCount++;

                if (rtn.getLeftTupleSource() == null) {
                    break; // The activation firing removed this rule from the rule base
                }

                activationsManager.flushPropagations();

                int salience = ruleAgendaItem.getSalience(); // dynamic salience may have updated it, so get again.
                if (queue != null && !queue.isEmpty() && salience != queue.peek().getSalience()) {
                    ruleAgendaItem.dequeue();
                    ruleAgendaItem.setSalience(queue.peek().getSalience());
                    ruleAgendaItem.getAgendaGroup().add( ruleAgendaItem );
                }

                if (!ruleIsAllMatches) { // if firing rule is @All don't give way to other rules
                    if ( haltRuleFiring( fireCount, fireLimit, localFireCount, activationsManager ) ) {
                        break; // another rule has high priority and is on the agenda, so evaluate it first
                    }
                    if (!reteEvaluator.isSequential()) {
                        reEvaluateNetwork( activationsManager );
                    }
                }
            }

            if (ruleIsAllMatches) {
                fireConsequenceEvent(reteEvaluator, activationsManager, (InternalMatch) lastTuple, ActivationsManager.ON_AFTER_ALL_FIRES_CONSEQUENCE_NAME);
            }
        }

        removeRuleAgendaItemWhenEmpty(reteEvaluator);

        fireExitedEarly = false;
        return localFireCount;
    }

    private Tuple getNextTuple() {
        if (tupleList.isEmpty()) {
            return null;
        }
        Tuple leftTuple;
        if (queue != null) {
            leftTuple = (Tuple) queue.dequeue();
            tupleList.remove(leftTuple);
        } else {
            leftTuple = tupleList.removeFirst();
            ((InternalMatch) leftTuple).setQueued(false);
        }
        return leftTuple;
    }

    public PathMemory getPathMemory() {
        return pmem;
    }

    public void removeRuleAgendaItemWhenEmpty(ReteEvaluator reteEvaluator) {
        if (!dirty && tupleList.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("Removing RuleAgendaItem " + ruleAgendaItem);
            }
            ruleAgendaItem.remove();
            if ( ruleAgendaItem.getRule().isQuery() ) {
                pmem.getActualActivationsManager( reteEvaluator ).removeQueryAgendaItem( ruleAgendaItem );
            } else if ( ruleAgendaItem.getRule().isEager() ) {
                pmem.getActualActivationsManager( reteEvaluator ).removeEagerRuleAgendaItem(ruleAgendaItem);
            }
        }
    }

    public void reEvaluateNetwork(ReteEvaluator reteEvaluator) {
        reEvaluateNetwork(pmem.getActualActivationsManager( reteEvaluator ));
    }

    public void reEvaluateNetwork(ActivationsManager activationsManager) {
        if ( isDirty() ) {
            setDirty(false);
            RuleNetworkEvaluator.INSTANCE.evaluateNetwork(pmem, this, activationsManager);
        }
    }

    public RuleAgendaItem getRuleAgendaItem() {
        return ruleAgendaItem;
    }

    private boolean cancelAndContinue(ReteEvaluator reteEvaluator,
                                      RuleTerminalNode rtn,
                                      RuleImpl rule,
                                      Tuple leftTuple,
                                      AgendaFilter filter) {
        // NB. stopped setting the LT.object to Boolean.TRUE, that Reteoo did.
        if ( !rule.isEffective(leftTuple, rtn.getEnabledDeclarations(), reteEvaluator) ) {
            return true;
        }

        if (rule.hasCalendars()) {
            long timestamp = reteEvaluator.getSessionClock().getCurrentTime();
            for (String cal : rule.getCalendars()) {
                if (!reteEvaluator.getCalendars().get(cal).isTimeIncluded(timestamp)) {
                    return true;
                }
            }
        }

        return filter != null && !filter.accept((InternalMatch) leftTuple);
    }

    private boolean haltRuleFiring(int fireCount,
                                   int fireLimit,
                                   int localFireCount,
                                   ActivationsManager activationsManager) {
        if (!activationsManager.isFiring() || (fireLimit >= 0 && (localFireCount + fireCount >= fireLimit))) {
            return true;
        }

        // The eager list must be evaluated first, as dynamic salience rules will impact the results of peekNextRule
        activationsManager.evaluateEagerList();

        RuleAgendaItem nextRule = activationsManager.peekNextRule();
        return nextRule != null && (!ruleAgendaItem.getAgendaGroup().equals( nextRule.getAgendaGroup() ) || !isHigherSalience(nextRule));
    }

    private boolean isHigherSalience(RuleAgendaItem nextRule) {
        return RuleAgendaConflictResolver.doCompare(ruleAgendaItem, nextRule) >= 0;
    }

    public TupleList getLeftTupleList() {
        return tupleList;
    }

    public void addLeftTuple(Tuple tuple) {
        ((InternalMatch) tuple).setQueued(true);
        this.tupleList.add(tuple);
        if (queue != null) {
            addQueuedLeftTuple(tuple);
        }
    }

    public void addQueuedLeftTuple(Tuple tuple) {
        int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();
        queue.enqueue((InternalMatch) tuple);
        updateSalience(currentSalience);
    }

    public void removeLeftTuple(Tuple tuple) {
        ((InternalMatch) tuple).setQueued(false);
        this.tupleList.remove(tuple);
        if (queue != null) {
            removeQueuedLeftTuple(tuple);
        }
    }

    private void removeQueuedLeftTuple(Tuple tuple) {
        int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();
        queue.dequeue(((InternalMatch) tuple));
        updateSalience(currentSalience);
    }

    private void updateSalience(int currentSalience) {
        // the queue may be emtpy if no more matches are left, so reset it to default salience 0
        int newSalience = queue.isEmpty() ? SalienceInteger.DEFAULT_SALIENCE.getValue() : queue.peek().getSalience();
        if (currentSalience != newSalience) {
            // salience changed, so the RuleAgendaItem needs to be removed and re-added, for sorting
            ruleAgendaItem.remove();
        }
        if (!ruleAgendaItem.isQueued()) {
            ruleAgendaItem.setSalience(newSalience);
            ruleAgendaItem.getAgendaGroup().add(ruleAgendaItem);
        }
    }

    public void cancel(ReteEvaluator reteEvaluator, EventSupport es) {
        while (!tupleList.isEmpty()) {
            RuleTerminalNodeLeftTuple rtnLt = (RuleTerminalNodeLeftTuple) tupleList.removeFirst();
            if (queue != null) {
                queue.dequeue(rtnLt);
            }

            es.getAgendaEventSupport().fireActivationCancelled(rtnLt, reteEvaluator, MatchCancelledCause.CLEAR);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDeclarativeAgendaEnabled() {
        return this.declarativeAgendaEnabled;
    }

    public void fireActivation(ReteEvaluator reteEvaluator, ActivationsManager activationsManager, InternalMatch internalMatch) throws ConsequenceException {
        // We do this first as if a node modifies a fact that causes a recursion
        // on an empty pattern
        // we need to make sure it re-activates
        BeforeMatchFiredEvent beforeMatchFiredEvent = activationsManager.getAgendaEventSupport().fireBeforeActivationFired(internalMatch, reteEvaluator);

        if (internalMatch.getActivationGroupNode() != null ) {
            // We know that this rule will cancel all other activations in the group
            // so lets remove the information now, before the consequence fires
            final InternalActivationGroup activationGroup = internalMatch.getActivationGroupNode().getActivationGroup();
            activationGroup.removeActivation(internalMatch);
            activationsManager.clearAndCancelActivationGroup( activationGroup );
        }
        internalMatch.setQueued(false);

        fireActivationEvent(reteEvaluator, activationsManager, internalMatch, internalMatch.getConsequence());
        activationsManager.getAgendaEventSupport().fireAfterActivationFired(internalMatch, reteEvaluator, beforeMatchFiredEvent);
    }

    public void fireConsequenceEvent(ReteEvaluator reteEvaluator, ActivationsManager activationsManager, InternalMatch internalMatch, String consequenceName) {
        Consequence consequence = internalMatch.getRule().getNamedConsequence(consequenceName);
        if (consequence != null) {
            fireActivationEvent(reteEvaluator, activationsManager, internalMatch, consequence);
        }
    }

    private void fireActivationEvent(ReteEvaluator reteEvaluator, ActivationsManager activationsManager, InternalMatch internalMatch, Consequence consequence) {
        KnowledgeHelper knowledgeHelper = activationsManager.getKnowledgeHelper();
        try {
            knowledgeHelper.setActivation(internalMatch);

            if ( log.isTraceEnabled() ) {
                log.trace("Fire event {} for rule \"{}\" \n{}", consequence.getName(), internalMatch.getRule().getName(), internalMatch.getTuple());
            }

            RuleEventListenerSupport ruleEventSupport = reteEvaluator.getRuleEventSupport();
            ruleEventSupport.onBeforeMatchFire(internalMatch);
            consequence.evaluate(knowledgeHelper, reteEvaluator);
            ruleEventSupport.onAfterMatchFire(internalMatch);

            internalMatch.setActive(false);
            knowledgeHelper.reset();
        } catch ( final Exception e ) {
            e.printStackTrace();
            knowledgeHelper.restoreActivationOnConsequenceFailure(internalMatch);
            activationsManager.handleException(internalMatch, e);
        } finally {
            if (internalMatch.getActivationFactHandle() != null ) {
                // update the Activation in the WM
                InternalFactHandle factHandle = internalMatch.getActivationFactHandle();
                reteEvaluator.getDefaultEntryPoint().getEntryPointNode().modifyActivation(factHandle, internalMatch.getPropagationContext(), reteEvaluator);
            }
        }
    }
}
