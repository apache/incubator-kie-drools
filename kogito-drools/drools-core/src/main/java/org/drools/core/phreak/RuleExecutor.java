/*
 * Copyright 2015 JBoss Inc
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

import org.drools.core.base.SalienceInteger;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.DefaultAgenda;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.conflict.PhreakConflictResolver;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Tuple;
import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.index.TupleList;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.rule.AgendaFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class RuleExecutor {

    protected static final transient Logger   log               = LoggerFactory.getLogger(RuleExecutor.class);
    private static final RuleNetworkEvaluator NETWORK_EVALUATOR = new RuleNetworkEvaluator();
    private final PathMemory                  pmem;
    private final RuleAgendaItem              ruleAgendaItem;
    private final TupleList                   tupleList;
    private BinaryHeapQueue                   queue;
    private volatile boolean                  dirty;
    private final boolean                     declarativeAgendaEnabled;
    private boolean                           fireExitedEarly;

    public RuleExecutor(final PathMemory pmem,
            RuleAgendaItem ruleAgendaItem,
            boolean declarativeAgendaEnabled) {
        this.pmem = pmem;
        this.ruleAgendaItem = ruleAgendaItem;
        this.tupleList = new TupleList();
        this.declarativeAgendaEnabled = declarativeAgendaEnabled;
        if (ruleAgendaItem.getRule().getSalience().isDynamic()) {
            queue = new BinaryHeapQueue(SalienceComparator.INSTANCE);
        }
    }

    public void evaluateNetwork(InternalWorkingMemory wm) {
        NETWORK_EVALUATOR.evaluateNetwork( pmem, this, wm );
        setDirty( false );
    }

    public int evaluateNetworkAndFire( InternalWorkingMemory wm,
                                                    final AgendaFilter filter,
                                                    int fireCount,
                                                    int fireLimit ) {
        reEvaluateNetwork( wm );
        return fire(wm, filter, fireCount, fireLimit, wm.getAgenda());
    }

    public void fire(InternalWorkingMemory wm) {
        fire(wm, null, 0, Integer.MAX_VALUE, wm.getAgenda());
    }

    private int fire( InternalWorkingMemory wm,
                      AgendaFilter filter,
                      int fireCount,
                      int fireLimit,
                      InternalAgenda agenda) {
        int localFireCount = 0;

        if (!tupleList.isEmpty()) {
            if (!fireExitedEarly && isDeclarativeAgendaEnabled()) {
                // Network Evaluation can notify meta rules, which should be given a chance to fire first
                RuleAgendaItem nextRule = agenda.peekNextRule();
                if (!isHigherSalience( nextRule )) {
                    fireExitedEarly = true;
                    return localFireCount;
                }
            }

            RuleTerminalNode rtn = (RuleTerminalNode) pmem.getNetworkNode();
            RuleImpl rule = rtn.getRule();
            Tuple tuple = getNextTuple();
            
            if (rule.isAllMatches()) {
                agenda.fireConsequenceEvent((AgendaItem) tuple, DefaultAgenda.ON_BEFORE_ALL_FIRES_CONSEQUENCE_NAME);
            }

            Tuple lastTuple = null;
            for (; tuple != null; lastTuple = tuple, tuple = getNextTuple()) {

                //check if the rule is not effective or
                // if the current Rule is no-loop and the origin rule is the same then return
                if (cancelAndContinue(wm, rtn, rule, tuple, filter)) {
                    continue;
                }

                AgendaItem item = (AgendaItem) tuple;
                if (agenda.getActivationsFilter() != null && !agenda.getActivationsFilter().accept(item, wm, rtn)) {
                    // only relevant for seralization, to not refire Matches already fired
                    continue;
                }

                agenda.fireActivation( item );
                localFireCount++;

                if (rtn.getLeftTupleSource() == null) {
                    break; // The activation firing removed this rule from the rule base
                }

                wm.flushPropagations();

                int salience = ruleAgendaItem.getSalience(); // dyanmic salience may have updated it, so get again.
                if (queue != null && !queue.isEmpty() && salience != queue.peek().getSalience()) {
                    ruleAgendaItem.dequeue();
                    ruleAgendaItem.setSalience(queue.peek().getSalience());
                    ruleAgendaItem.getAgendaGroup().add( ruleAgendaItem );
                }

                if (!rule.isAllMatches()) { // if firing rule is @All don't give way to other rules
                    if ( haltRuleFiring( fireCount, fireLimit, localFireCount, agenda ) ) {
                        break; // another rule has high priority and is on the agenda, so evaluate it first
                    }
                    if (!wm.isSequential()) {
                        reEvaluateNetwork( wm );
                    }
                }
            }

            if (rule.isAllMatches()) {
                agenda.fireConsequenceEvent((AgendaItem) lastTuple, DefaultAgenda.ON_AFTER_ALL_FIRES_CONSEQUENCE_NAME);
            }
        }

        removeRuleAgendaItemWhenEmpty(wm);

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
            ((Activation) leftTuple).setQueued(false);
        }
        return leftTuple;
    }

    public PathMemory getPathMemory() {
        return pmem;
    }

    public void removeRuleAgendaItemWhenEmpty(InternalWorkingMemory wm) {
        if (!dirty && tupleList.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("Removing RuleAgendaItem " + ruleAgendaItem);
            }
            ruleAgendaItem.remove();
            if ( ruleAgendaItem.getRule().isQuery() ) {
                wm.getAgenda().removeQueryAgendaItem( ruleAgendaItem );
            } else if ( ruleAgendaItem.getRule().isEager() ) {
                wm.getAgenda().removeEagerRuleAgendaItem(ruleAgendaItem);
            }
        }
    }

    public void reEvaluateNetwork(InternalWorkingMemory wm) {
        if ( isDirty() ) {
            setDirty(false);
            NETWORK_EVALUATOR.evaluateNetwork(pmem, this, wm);
        }
    }

    public RuleAgendaItem getRuleAgendaItem() {
        return ruleAgendaItem;
    }

    private boolean cancelAndContinue(InternalWorkingMemory wm,
            RuleTerminalNode rtn,
            RuleImpl rule,
            Tuple leftTuple,
            AgendaFilter filter) {
        // NB. stopped setting the LT.object to Boolean.TRUE, that Reteoo did.
        if ( !rule.isEffective(leftTuple, rtn, wm) ) {
            return true;
        }

        if (rule.getCalendars() != null) {
            long timestamp = wm.getSessionClock().getCurrentTime();
            for (String cal : rule.getCalendars()) {
                if (!wm.getCalendars().get(cal).isTimeIncluded(timestamp)) {
                    return true;
                }
            }
        }

        return filter != null && !filter.accept((Activation) leftTuple);
    }

    private boolean haltRuleFiring(int fireCount,
                                   int fireLimit,
                                   int localFireCount,
                                   InternalAgenda agenda) {
        if (!agenda.isFiring() || (fireLimit >= 0 && (localFireCount + fireCount >= fireLimit))) {
            return true;
        }

        // The eager list must be evaluated first, as dynamic salience rules will impact the results of peekNextRule
        agenda.evaluateEagerList();

        RuleAgendaItem nextRule = agenda.peekNextRule();
        return nextRule != null && (!ruleAgendaItem.getAgendaGroup().equals( nextRule.getAgendaGroup() ) || !isHigherSalience(nextRule));
    }

    private boolean isHigherSalience(RuleAgendaItem nextRule) {
        return PhreakConflictResolver.doCompare(ruleAgendaItem,nextRule) >= 0;
    }

    public TupleList getLeftTupleList() {
        return tupleList;
    }

    public void addLeftTuple(Tuple tuple) {
        ((AgendaItem) tuple).setQueued(true);
        this.tupleList.add(tuple);
        if (queue != null) {
            addQueuedLeftTuple(tuple);
        }
    }

    public void addQueuedLeftTuple(Tuple tuple) {
        int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();
        queue.enqueue((Activation) tuple);
        updateSalience(currentSalience);
    }

    public void removeLeftTuple(Tuple tuple) {
        ((AgendaItem) tuple).setQueued(false);
        this.tupleList.remove(tuple);
        if (queue != null) {
            removeQueuedLeftTuple(tuple);
        }
    }

    private void removeQueuedLeftTuple(Tuple tuple) {
        int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();
        queue.dequeue(((Activation) tuple));
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

    public void cancel(InternalWorkingMemory wm, EventSupport es) {
        while (!tupleList.isEmpty()) {
            RuleTerminalNodeLeftTuple rtnLt = (RuleTerminalNodeLeftTuple) tupleList.removeFirst();
            if (queue != null) {
                queue.dequeue(rtnLt);
            }

            es.getAgendaEventSupport().fireActivationCancelled(rtnLt, wm, MatchCancelledCause.CLEAR);
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

    public static class SalienceComparator implements Comparator {

        public static final SalienceComparator INSTANCE = new SalienceComparator();

        public int compare(Object existing, Object adding) {
            RuleTerminalNodeLeftTuple rtnLt1 = (RuleTerminalNodeLeftTuple) existing;
            RuleTerminalNodeLeftTuple rtnLt2 = (RuleTerminalNodeLeftTuple) adding;

            final int s1 = rtnLt1.getSalience();
            final int s2 = rtnLt2.getSalience();

            // highest goes first
            if (s1 > s2) {
                return 1;
            } else if (s1 < s2) {
                return -1;
            }

            final int l1 = rtnLt1.getRule().getLoadOrder();
            final int l2 = rtnLt2.getRule().getLoadOrder();

            // lowest goes first
            if (l1 < l2) {
                return 1;
            } else if (l1 > l2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
