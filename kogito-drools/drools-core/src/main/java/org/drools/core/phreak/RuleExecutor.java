package org.drools.core.phreak;

import java.util.Comparator;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.LeftTupleList;
import org.kie.api.event.rule.MatchCancelledCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleExecutor {
    protected static transient Logger               log              = LoggerFactory.getLogger(RuleExecutor.class);
    private static             RuleNetworkEvaluator networkEvaluator = new RuleNetworkEvaluator();
    private final    PathMemory      pmem;
    private          RuleAgendaItem  ruleAgendaItem;
    private          LeftTupleList   tupleList;
    private          BinaryHeapQueue queue;
    private volatile boolean         dirty;
    private          boolean         declarativeAgendaEnabled;

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

    public void evaluateNetwork(InternalWorkingMemory wm) {
        this.networkEvaluator.evaluateNetwork(pmem, null, this, wm);
        setDirty(false);
        wm.executeQueuedActions();
    }

    public int evaluateNetworkAndFire(InternalWorkingMemory wm,
                                      final AgendaFilter filter,
                                      int fireCount,
                                      int fireLimit) {
        LinkedList<StackEntry> outerStack = new LinkedList<StackEntry>();

        InternalAgenda agenda = (InternalAgenda) wm.getAgenda();
        boolean fireUntilHalt = agenda.isFireUntilHalt();

        reEvaluateNetwork(wm, outerStack, fireUntilHalt);
        wm.executeQueuedActions();

        //int fireCount = 0;
        int localFireCount = 0;
        if (!tupleList.isEmpty()) {
            RuleTerminalNode rtn = (RuleTerminalNode) pmem.getNetworkNode();


            int salience = ruleAgendaItem.getSalience();

            if (isDeclarativeAgendaEnabled()) {
                // Network Evaluation can notify meta rules, which should be given a chance to fire first
                RuleAgendaItem nextRule = agenda.peekNextRule();
                if (!isHighestSalience(nextRule, salience)) {
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
                    ((Activation)leftTuple).setQueued( false );
                }

                rtn = (RuleTerminalNode) leftTuple.getSink(); // branches result in multiple RTN's for a given rule, so unwrap per LeftTuple
                Rule rule = rtn.getRule();

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

                salience = ruleAgendaItem.getSalience(); // dyanmic salience may have updated it, so get again.
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
                    this.networkEvaluator.evalStackEntry(entry, outerStack, outerStack, this, wm);
                }
            }
        }

        removeRuleAgendaItemWhenEmpty( wm );

        return localFireCount;
    }

    public void removeRuleAgendaItemWhenEmpty(InternalWorkingMemory wm) {
        if (!dirty && tupleList.isEmpty()) {
            // dirty check, before doing the synced check and removal
            synchronized (ruleAgendaItem) {
                if (!dirty && tupleList.isEmpty()) {
                    if ( log.isTraceEnabled() ) {
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

    public void reEvaluateNetwork(InternalWorkingMemory wm, LinkedList<StackEntry> outerStack, boolean fireUntilHalt) {
        if (isDirty() || (pmem.getQueue() != null && !pmem.getQueue().isEmpty())) {
            setDirty(false);

            boolean evaled = false;
            if (pmem.getQueue() != null ) {
                if ( !fireUntilHalt) {
                    while ( !pmem.getQueue().isEmpty() ) {
                        removeQueuedTupleEntry();
                        this.networkEvaluator.evaluateNetwork(pmem, outerStack, this, wm);
                        evaled = true;
                    }
                } else {
                    removeQueuedTupleEntry();
                    this.networkEvaluator.evaluateNetwork(pmem, outerStack, this, wm);
                    evaled = true;
                }
            }

            if ( !evaled) {
                this.networkEvaluator.evaluateNetwork(pmem, outerStack, this, wm);
            }
        }
    }

    private void removeQueuedTupleEntry() {
        TupleEntry tupleEntry = pmem.getQueue().remove();
        PropagationContext originalPctx = tupleEntry.getPropagationContext();

        boolean repeat = true;
        while ( repeat ) {
            if ( log.isTraceEnabled() ) {
                log.trace( "Stream removed entry {} {} size {}",  System.identityHashCode(  pmem.getQueue() ), tupleEntry, pmem.getQueue().size() );
            }
            if (tupleEntry.getLeftTuple() != null) {
                SegmentMemory sm = tupleEntry.getNodeMemory().getSegmentMemory();
                LeftTupleSets tuples = sm.getStagedLeftTuples();
                tupleEntry.getLeftTuple().setPropagationContext(tupleEntry.getPropagationContext());
                switch (tupleEntry.getPropagationContext().getType()) {
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
                        tuples.addDelete(tupleEntry.getLeftTuple());
                        break;
                }
            } else {
                BetaMemory bm = (BetaMemory) tupleEntry.getNodeMemory();
                tupleEntry.getRightTuple().setPropagationContext(tupleEntry.getPropagationContext());
                switch (tupleEntry.getPropagationContext().getType()) {
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
                        bm.getStagedRightTuples().addDelete(tupleEntry.getRightTuple());
                        break;
                }
            }
            if ( !pmem.getQueue().isEmpty() ) {
                tupleEntry = pmem.getQueue().peek();
                PropagationContext pctx = tupleEntry.getPropagationContext();

                // repeat if either the pctx number is the same, or the event time is the same or before
                if ( pctx.getPropagationNumber() == originalPctx.getPropagationNumber() ) {
                    repeat = true;
                } else {
                    repeat = false;
                }

//                if ( tupleEntry.getLeftTuple() != null ) {
//                    evFh2 = ( EventFactHandle ) tupleEntry.getLeftTuple().getLastHandle();
//                }  else {
//                    evFh2 = ( EventFactHandle ) tupleEntry.getRightTuple().getFactHandle();
//                }
//
//                if ( !repeat && evFh2.getStartTimestamp() <= evFh1.getStartTimestamp() ) {
//                    repeat = true;
//                } else {
//                    repeat = false;
//                }
            } else {
                repeat = false;
            }
            if ( repeat ) {
                tupleEntry = pmem.getQueue().remove();
            }
        }
    }

//    private void removeQueuedTupleEntry(boolean fireUntilHalt) {
//        TupleEntry tupleEntry = pmem.getQueue().remove();
//        TupleEntry originalTupleEntry = tupleEntry;
//        PropagationContext originalPctx = tupleEntry.getPropagationContext();
//        PropagationContext pctx = originalPctx;
//
//        EventFactHandle  evFh1 = null;
//        EventFactHandle  evFh2 = null;
//        if ( tupleEntry.getLeftTuple() != null ) {
//            evFh1 = ( EventFactHandle ) tupleEntry.getLeftTuple().getLastHandle();
//        }  else {
//            evFh1 = ( EventFactHandle ) tupleEntry.getRightTuple().getFactHandle();
//        }
//
//        boolean repeat = true;
//        while ( repeat ) {
//            if ( log.isTraceEnabled() ) {
//                log.trace( "Stream removed entry {} {} size {}",  System.identityHashCode(  pmem.getQueue() ), tupleEntry, pmem.getQueue().size() );
//            }
//            if (tupleEntry.getLeftTuple() != null) {
//                SegmentMemory sm = (SegmentMemory) tupleEntry.getNodeMemory().getSegmentMemory();
//                LeftTupleSets tuples = sm.getStagedLeftTuples();
//                tupleEntry.getLeftTuple().setPropagationContext(tupleEntry.getPropagationContext());
//                switch (tupleEntry.getPropagationContext().getType()) {
//                    case PropagationContext.INSERTION:
//                    case PropagationContext.RULE_ADDITION:
//                        tuples.addInsert(tupleEntry.getLeftTuple());
//                        break;
//                    case PropagationContext.MODIFICATION:
//                        tuples.addUpdate(tupleEntry.getLeftTuple());
//                        break;
//                    case PropagationContext.DELETION:
//                    case PropagationContext.EXPIRATION:
//                    case PropagationContext.RULE_REMOVAL:
//                        tuples.addDelete(tupleEntry.getLeftTuple());
//                        break;
//                }
//            } else {
//                BetaMemory bm = (BetaMemory) tupleEntry.getNodeMemory();
//                switch (tupleEntry.getPropagationContext().getType()) {
//                    case PropagationContext.INSERTION:
//                    case PropagationContext.RULE_ADDITION:
//                        bm.getStagedRightTuples().addInsert(tupleEntry.getRightTuple());
//                        break;
//                    case PropagationContext.MODIFICATION:
//                        bm.getStagedRightTuples().addUpdate(tupleEntry.getRightTuple());
//                        break;
//                    case PropagationContext.DELETION:
//                    case PropagationContext.EXPIRATION:
//                    case PropagationContext.RULE_REMOVAL:
//                        bm.getStagedRightTuples().addDelete(tupleEntry.getRightTuple());
//                        break;
//                }
//            }
//            if ( !pmem.getQueue().isEmpty() ) {
//                tupleEntry = pmem.getQueue().peek();
//                pctx = tupleEntry.getPropagationContext();
//
//                if ( fireUntilHalt ) {
//                    // repeat if either the pctx number is the same, or the event time is the same or before
//                    if ( pctx.getPropagationNumber() != originalPctx.getPropagationNumber() ) {
//                        repeat = false;
//                    }
//
//                    if ( tupleEntry.getLeftTuple() != null ) {
//                        evFh2 = ( EventFactHandle ) tupleEntry.getLeftTuple().getLastHandle();
//                    }  else {
//                        evFh2 = ( EventFactHandle ) tupleEntry.getRightTuple().getFactHandle();
//                    }
//
//                    if ( evFh2.getStartTimestamp() <= evFh1.getStartTimestamp() ) {
//                        repeat = true;
//                    }
//                } else {
//                    // fireAllRules must drain all events, unless it's already staged
//                    // it may be staged, if it tries to expire something not yet propagated
//                    if ( tupleEntry.getLeftTuple() != null && tupleEntry.getLeftTuple().getStagedType() == LeftTuple.NONE) {
//                        repeat = true;
//                    } else if ( tupleEntry.getRightTuple() != null && tupleEntry.getRightTuple().getStagedType() == LeftTuple.NONE) {
//                        repeat = true;
//                    } else {
//                        repeat = false;
//                    }
//                }
//            } else {
//                repeat = false;
//            }
//            if ( repeat ) {
//                tupleEntry = pmem.getQueue().remove();
//            }
//        }
//    }

    public RuleAgendaItem getRuleAgendaItem() {
        return ruleAgendaItem;
    }

    private boolean cancelAndContinue(InternalWorkingMemory wm,
                                      RuleTerminalNode rtn,
                                      Rule rule,
                                      LeftTuple leftTuple,
                                      PropagationContext pctx, AgendaFilter filter) {
        // NB. stopped setting the LT.object to Boolean.TRUE, that Reteoo did.
        if ((!rule.isEffective(leftTuple,
                               rtn,
                               wm)) ||
            (rule.isNoLoop() && rule.equals(pctx.getRuleOrigin()))) {
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
        return !agenda.continueFiring(0) || !isHighestSalience(nextRule, salience) || (fireLimit >= 0 && (localFireCount + fireCount >= fireLimit));
    }

    public boolean isHighestSalience(RuleAgendaItem nextRule,
                                     int currentSalience) {
        return (nextRule == null) || nextRule.getSalience() <= currentSalience;
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

    public void removeQueuedLeftTuple(LeftTuple leftTuple) {
        int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();
        queue.dequeue(((Activation) leftTuple).getQueueIndex());
        updateSalience(currentSalience);
    }

    public void updateLeftTuple(RuleTerminalNodeLeftTuple leftTuple, int salience, PropagationContext pctx) {
        // this method is only call when dynamic salience is on for the current rule and the salience for the LeftTuple has changed
        if (salience != leftTuple.getSalience()) {
            // saliences are different, so it must be dynamic and thus the queue is not null
            int currentSalience = queue.isEmpty() ? 0 : queue.peek().getSalience();

            leftTuple.dequeue();
            queue.enqueue(leftTuple);
            updateSalience(currentSalience);
        }
        leftTuple.update(salience, pctx);
    }

    private void updateSalience(int currentSalience) {
        int newSalience = queue.peek().getSalience();
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
                queue.dequeue(rtnLt.getQueueIndex());
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

            if (s1 > s2) {
                return 1;
            } else if (s1 < s2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
