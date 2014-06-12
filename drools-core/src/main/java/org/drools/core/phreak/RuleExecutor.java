package org.drools.core.phreak;

import org.drools.core.base.SalienceInteger;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.StreamTupleEntryQueue;
import org.drools.core.common.TupleEntryQueue;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.LeftTupleList;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.rule.AgendaFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RuleExecutor {

    protected static transient Logger         log               = LoggerFactory.getLogger(RuleExecutor.class);
    private static final RuleNetworkEvaluator NETWORK_EVALUATOR = new RuleNetworkEvaluator();
    private final PathMemory                  pmem;
    private RuleAgendaItem                    ruleAgendaItem;
    private LeftTupleList                     tupleList;
    private BinaryHeapQueue                   queue;
    private volatile boolean                  dirty;
    private boolean                           declarativeAgendaEnabled;
    private boolean                           fireExitedEarly;

    public RuleExecutor(final PathMemory pmem,
            RuleAgendaItem ruleAgendaItem,
            boolean declarativeAgendaEnabled) {
        this.pmem = pmem;
        this.ruleAgendaItem = ruleAgendaItem;
        this.tupleList = new LeftTupleList();
        this.declarativeAgendaEnabled = declarativeAgendaEnabled;
        if (ruleAgendaItem.getRule().getSalience().isDynamic()) {
            queue = new BinaryHeapQueue(SalienceComparator.INSTANCE);
        }
    }

    public synchronized void gcStreamQueue() {
        List<TupleEntry> nonNormalizedDeletes = flushStreamQueue();
        for (TupleEntry tupleEntry : nonNormalizedDeletes) {
            if (tupleEntry.getLeftTuple() != null) {
                LeftTuple leftTuple = tupleEntry.getLeftTuple();
                if (leftTuple.getMemory() != null) {
                    leftTuple.getMemory().remove(leftTuple);
                }
            } else {
                RightTuple rightTuple = tupleEntry.getRightTuple();
                if (rightTuple.getMemory() != null) {
                    rightTuple.getMemory().remove(rightTuple);
                }
            }
        }
    }

    public synchronized void evaluateNetwork(InternalWorkingMemory wm) {
        NETWORK_EVALUATOR.evaluateNetwork(pmem, null, this, wm);
        setDirty(false);
        wm.executeQueuedActions();
    }

    public synchronized int evaluateNetworkAndFire(InternalWorkingMemory wm,
            final AgendaFilter filter,
            int fireCount,
            int fireLimit) {
        LinkedList<StackEntry> outerStack = new LinkedList<StackEntry>();

        InternalAgenda agenda = (InternalAgenda) wm.getAgenda();
        boolean fireUntilHalt = agenda.isFireUntilHalt();

        reEvaluateNetwork(wm, outerStack, fireUntilHalt);
        wm.executeQueuedActions();

        return fire(wm, filter, fireCount, fireLimit, outerStack, agenda, fireUntilHalt);
    }

    public synchronized void fire(InternalWorkingMemory wm, LinkedList<StackEntry> outerStack) {
        InternalAgenda agenda = (InternalAgenda) wm.getAgenda();
        boolean fireUntilHalt = agenda.isFireUntilHalt();
        fire(wm, null, 0, Integer.MAX_VALUE, outerStack, agenda, fireUntilHalt);
    }

    private int fire(InternalWorkingMemory wm,
            AgendaFilter filter,
            int fireCount,
            int fireLimit,
            LinkedList<StackEntry> outerStack,
            InternalAgenda agenda,
            boolean fireUntilHalt) {
        int localFireCount = 0;
        if (!tupleList.isEmpty()) {
            RuleTerminalNode rtn = (RuleTerminalNode) pmem.getNetworkNode();

            if (!fireExitedEarly && isDeclarativeAgendaEnabled()) {
                // Network Evaluation can notify meta rules, which should be given a chance to fire first
                RuleAgendaItem nextRule = agenda.peekNextRule();
                if (!isHighestSalience(nextRule, ruleAgendaItem.getSalience())) {
                    fireExitedEarly = true;
                    return localFireCount;
                }
            }

            while (!tupleList.isEmpty()) {
                LeftTuple leftTuple;
                if (queue != null) {
                    leftTuple = (LeftTuple) queue.dequeue();
                    tupleList.remove(leftTuple);
                } else {
                    leftTuple = tupleList.removeFirst();
                    ((Activation) leftTuple).setQueued(false);
                }

                rtn = (RuleTerminalNode) leftTuple.getSink(); // branches result in multiple RTN's for a given rule, so unwrap per LeftTuple
                RuleImpl rule = rtn.getRule();

                PropagationContext pctx = leftTuple.getPropagationContext();
                pctx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                        pctx);

                //check if the rule is not effective or
                // if the current Rule is no-loop and the origin rule is the same then return
                if (cancelAndContinue(wm, rtn, rule, leftTuple, pctx, filter)) {
                    continue;
                }

                AgendaItem item = (AgendaItem) leftTuple;
                if (agenda.getActivationsFilter() != null && !agenda.getActivationsFilter().accept(item, wm, rtn)) {
                    // only relevant for seralization, to not refire Matches already fired
                    continue;
                }

                agenda.fireActivation(item);
                localFireCount++;

                if (rtn.getLeftTupleSource() == null) {
                    break; // The activation firing removed this rule from the rule base
                }

                int salience = ruleAgendaItem.getSalience(); // dyanmic salience may have updated it, so get again.
                if (queue != null && !queue.isEmpty() && salience != queue.peek().getSalience()) {
                    ruleAgendaItem.dequeue();
                    ruleAgendaItem.setSalience(queue.peek().getSalience());
                    ruleAgendaItem.getAgendaGroup().add(ruleAgendaItem);
                    salience = ruleAgendaItem.getSalience();
                }

                RuleAgendaItem nextRule = agenda.peekNextRule();
                if (haltRuleFiring(nextRule, fireCount, fireLimit, localFireCount, agenda, salience)) {
                    break; // another rule has high priority and is on the agenda, so evaluate it first
                }
                reEvaluateNetwork(wm, outerStack, fireUntilHalt);
                wm.executeQueuedActions();

                if (tupleList.isEmpty() && !outerStack.isEmpty()) {
                    // the outer stack is nodes needing evaluation, once all rule firing is done
                    // such as window expiration, which must be done serially
                    StackEntry entry = outerStack.removeFirst();
                    NETWORK_EVALUATOR.evalStackEntry(entry, outerStack, outerStack, this, wm);
                }
            }
        }

        removeRuleAgendaItemWhenEmpty(wm);

        fireExitedEarly = false;
        return localFireCount;
    }

    public PathMemory getPathMemory() {
        return pmem;
    }

    public void removeRuleAgendaItemWhenEmpty(InternalWorkingMemory wm) {
        if (!dirty && tupleList.isEmpty()) {
            // dirty check, before doing the synced check and removal
            synchronized (ruleAgendaItem) {
                if (!dirty && tupleList.isEmpty()) {
                    if (log.isTraceEnabled()) {
                        log.trace("Removing RuleAgendaItem " + ruleAgendaItem);
                    }
                    ruleAgendaItem.remove();
                    if (ruleAgendaItem.getRule().isEager()) {
                        ((InternalAgenda) wm.getAgenda()).removeEagerRuleAgendaItem(ruleAgendaItem);
                    }
                }
            }
        }
    }

    public synchronized void reEvaluateNetwork(InternalWorkingMemory wm, LinkedList<StackEntry> outerStack, boolean fireUntilHalt) {
        if (isDirty() || (pmem.getStreamQueue() != null && !pmem.getStreamQueue().isEmpty())) {
            setDirty(false);
            TupleEntryQueue queue = pmem.getStreamQueue() != null ? pmem.getStreamQueue().takeAllForFlushing() : null;

            if ( queue == null || queue.isEmpty() ) {
                NETWORK_EVALUATOR.evaluateNetwork(pmem, outerStack, this, wm);
            } else {
                while (!queue.isEmpty()) {
                    removeQueuedTupleEntry( queue );
                    NETWORK_EVALUATOR.evaluateNetwork(pmem, outerStack, this, wm);
                }
            }
        }
    }

    private static void removeQueuedTupleEntry( TupleEntryQueue tupleQueue ) {
        TupleEntry tupleEntry = tupleQueue.remove();
        PropagationContext originalPctx = tupleEntry.getPropagationContext();

        while (true) {
            processStreamTupleEntry(tupleQueue, tupleEntry);
            if (tupleQueue.isEmpty()) {
                return;
            }
            tupleEntry = tupleQueue.peek();
            PropagationContext pctx = tupleEntry.getPropagationContext();

            // repeat if either the pctx number is the same, or the event time is the same or before
            if (pctx.getPropagationNumber() != originalPctx.getPropagationNumber()) {
                break;
            }
            tupleEntry = tupleQueue.remove();
        }
    }

    private List<TupleEntry> flushStreamQueue() {
        TupleEntryQueue tupleQueue = pmem.getStreamQueue().takeAllForFlushing();
        if (tupleQueue == null || tupleQueue.isEmpty()) {
            return Collections.emptyList();
        }
        List<TupleEntry> nonNormalizedDeletes = new ArrayList<TupleEntry>();
        while (!tupleQueue.isEmpty()) {
            TupleEntry tupleEntry = tupleQueue.remove();
            if ( processStreamTupleEntry( tupleQueue, tupleEntry ) ) {
                nonNormalizedDeletes.add(tupleEntry);
            }
        }
        return nonNormalizedDeletes;
    }

    public static void flushTupleQueue( StreamTupleEntryQueue streamQueue ) {
        if ( streamQueue != null ) {
            TupleEntryQueue tupleQueue = streamQueue.takeAllForFlushing();
            while (!tupleQueue.isEmpty()) {
                processStreamTupleEntry( tupleQueue, tupleQueue.remove() );
            }
        }
    }

    private static boolean processStreamTupleEntry(TupleEntryQueue tupleQueue, TupleEntry tupleEntry) {
        boolean isNonNormalizedDelete = false;
        if (log.isTraceEnabled()) {
            log.trace("Stream removed entry {} {} size {}", System.identityHashCode(tupleQueue), tupleEntry, tupleQueue.size());
        }
        if (tupleEntry.getLeftTuple() != null) {
            SegmentMemory sm = tupleEntry.getNodeMemory().getSegmentMemory();
            LeftTupleSets tuples = sm.getStagedLeftTuples();
            tupleEntry.getLeftTuple().setPropagationContext(tupleEntry.getPropagationContext());
            switch (tupleEntry.getPropagationType()) {
                case PropagationContext.INSERTION:
                case PropagationContext.RULE_ADDITION:
                    tuples.addInsert(tupleEntry.getLeftTuple());
                    break;
                case PropagationContext.MODIFICATION:
                    tuples.addUpdate(tupleEntry.getLeftTuple());
                    break;
                case PropagationContext.DELETION:
                case PropagationContext.EXPIRATION:
                case PropagationContext.RULE_REMOVAL:
                    isNonNormalizedDelete = tupleEntry.getLeftTuple().getStagedType() == LeftTuple.NONE;
                    tuples.addDelete(tupleEntry.getLeftTuple());
                    break;
            }
        } else {
            BetaMemory bm = (BetaMemory) tupleEntry.getNodeMemory();
            tupleEntry.getRightTuple().setPropagationContext(tupleEntry.getPropagationContext());
            switch (tupleEntry.getPropagationType()) {
                case PropagationContext.INSERTION:
                case PropagationContext.RULE_ADDITION:
                    bm.getStagedRightTuples().addInsert(tupleEntry.getRightTuple());
                    break;
                case PropagationContext.MODIFICATION:
                    bm.getStagedRightTuples().addUpdate(tupleEntry.getRightTuple());
                    break;
                case PropagationContext.DELETION:
                case PropagationContext.EXPIRATION:
                case PropagationContext.RULE_REMOVAL:
                    isNonNormalizedDelete = tupleEntry.getRightTuple().getStagedType() == LeftTuple.NONE;
                    bm.getStagedRightTuples().addDelete(tupleEntry.getRightTuple());
                    break;
            }
        }
        return isNonNormalizedDelete;
    }

    public RuleAgendaItem getRuleAgendaItem() {
        return ruleAgendaItem;
    }

    private boolean cancelAndContinue(InternalWorkingMemory wm,
            RuleTerminalNode rtn,
            RuleImpl rule,
            LeftTuple leftTuple,
            PropagationContext pctx, AgendaFilter filter) {
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

    private boolean haltRuleFiring(RuleAgendaItem nextRule,
                                   int fireCount,
                                   int fireLimit,
                                   int localFireCount,
                                   InternalAgenda agenda,
                                   int salience) {


        return !agenda.continueFiring(0) ||
               ( (nextRule != null) && (!ruleAgendaItem.getRule().getAgendaGroup().equals( nextRule.getAgendaGroup() ) || !isHighestSalience(nextRule, salience)) )
               || (fireLimit >= 0 && (localFireCount + fireCount >= fireLimit));
    }

    public boolean isHighestSalience(RuleAgendaItem nextRule,
                                     int currentSalience) {
        return nextRule.getSalience() <= currentSalience;
    }

    public LeftTupleList getLeftTupleList() {
        return tupleList;
    }

    public void addLeftTuple(LeftTuple leftTuple) {
        ((AgendaItem) leftTuple).setQueued(true);
        this.tupleList.add(leftTuple);
        if (queue != null) {
            addQueuedLeftTuple(leftTuple);
        }
    }

    public void addQueuedLeftTuple(LeftTuple leftTuple) {
        int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();
        queue.enqueue((Activation) leftTuple);
        updateSalience(currentSalience);
    }

    public void removeLeftTuple(LeftTuple leftTuple) {
        ((AgendaItem) leftTuple).setQueued(false);
        this.tupleList.remove(leftTuple);
        if (queue != null) {
            removeQueuedLeftTuple(leftTuple);
        }
    }

    private void removeQueuedLeftTuple(LeftTuple leftTuple) {
        int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();
        queue.dequeue(((Activation) leftTuple));
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
