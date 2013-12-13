package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSet;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.rule.Rule;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Arrays;
import java.util.List;

public class DefeasibleBeliefSet implements JTMSBeliefSet {
    private BeliefSystem beliefSystem;

    public static final String DEFEATS = Defeats.class.getSimpleName();

    private static final FastIterator iterator = new IteratorImpl();

    private InternalFactHandle rootHandle;
    private InternalFactHandle positiveFactHandle;
    private InternalFactHandle negativeFactHandle;

    private LinkedListEntry<DefeasibleLogicalDependency> rootUndefeated;
    private LinkedListEntry<DefeasibleLogicalDependency> tailUndefeated;

    private int definitelyPosCount;
    private int definitelyNegCount;
    private int defeasiblyPosCount;
    private int defeasiblyNegCount;
    private int defeatedlyPosCount;
    private int defeatedlyNegCount;

    private static final int DEFINITELY_POS_BIT = 1;
    private static final int DEFINITELY_NEG_BIT = 2;
    private static final int DEFEASIBLY_POS_BIT = 4;
    private static final int DEFEASIBLY_NEG_BIT = 8;
    private static final int DEFEATEDLY_POS_BIT = 16;
    private static final int DEFEATEDLY_NEG_BIT = 32;

    private static final int POS_MASK = DEFINITELY_POS_BIT | DEFEASIBLY_POS_BIT | DEFEATEDLY_POS_BIT;
    private static final int NEG_MASK = DEFINITELY_NEG_BIT | DEFEASIBLY_NEG_BIT | DEFEATEDLY_NEG_BIT;

    private static final int WEAK_POS_MASK = DEFEASIBLY_POS_BIT | DEFEATEDLY_POS_BIT;
    private static final int WEAK_NEG_MASK = DEFEASIBLY_NEG_BIT | DEFEATEDLY_NEG_BIT;

    private int statusMask = 0;

    private DefeasibilityStatus status;

    public DefeasibleBeliefSet(BeliefSystem beliefSystem, InternalFactHandle rootHandle) {
        this.beliefSystem = beliefSystem;
        this.rootHandle = rootHandle;
    }

    public BeliefSystem getBeliefSystem() {
        return beliefSystem;
    }

    public InternalFactHandle getFactHandle() {
        return rootHandle;
    }

    public LinkedListEntry<DefeasibleLogicalDependency> getFirst() {
        return rootUndefeated;
    }

    public LinkedListEntry<DefeasibleLogicalDependency> getLast() {
        return tailUndefeated;
    }

    public InternalFactHandle getPositiveFactHandle() {
        return positiveFactHandle;
    }

    public void setPositiveFactHandle(InternalFactHandle positiveFactHandle) {
        this.positiveFactHandle = positiveFactHandle;
    }

    public InternalFactHandle getNegativeFactHandle() {
        return negativeFactHandle;
    }

    public void setNegativeFactHandle(InternalFactHandle negativeFactHandle) {
        this.negativeFactHandle = negativeFactHandle;
    }

    public void add(LinkedListNode node) {
        DefeasibleLogicalDependency newDep = (DefeasibleLogicalDependency) ((LinkedListEntry) node).getObject();
        newDep.setStatus(resolveStatus(newDep));

        Rule rule = newDep.getJustifier().getRule();

        // first iterate to see if this new dep is defeated. If it's defeated, it can no longer impacts any deps
        // if we checked what it defeats, and later this was defeated, we would have undo action. So we do the cheaper work first.
        boolean wasDefeated = false;
        for (LinkedListEntry<DefeasibleLogicalDependency> existingNode = rootUndefeated; existingNode != null; existingNode = existingNode.getNext()) {
            DefeasibleLogicalDependency existingDep = existingNode.getObject();
            wasDefeated = checkAndApplyIsDefeated(newDep, rule, existingDep);
            if (wasDefeated) {
                break;
            }
        }

        if (!wasDefeated) {
            LinkedListEntry<DefeasibleLogicalDependency> stagedDeps = null;
            //for (DefeasibleLogicalDependency existingDep = rootUndefeated; existingDep != null; ) {
            for (LinkedListEntry<DefeasibleLogicalDependency> existingNode = rootUndefeated; existingNode != null;) {
                LinkedListEntry<DefeasibleLogicalDependency> next = existingNode.getNext();
                DefeasibleLogicalDependency existingDep = existingNode.getObject();

                if (checkAndApplyIsDefeated(existingDep, existingDep.getJustifier().getRule(), newDep)) {
                    // fist remove it from the undefeated list
                    removeUndefeated(existingDep, existingNode);
                    if (existingDep.getRootDefeated() != null) {
                        // build up the list of staged deps, that will need to be reprocessed
                        if (stagedDeps == null) {
                            stagedDeps = existingDep.getRootDefeated();
                        } else {
                            stagedDeps.setPrevious( existingDep.getTailDefeated() );
                            stagedDeps = existingDep.getRootDefeated();
                        }
                    }
                    existingDep.clearDefeated();
                }
                existingNode = next;
            }
            addUndefeated(newDep, (LinkedListEntry<DefeasibleLogicalDependency>) node);
            // now process the staged
            reprocessDefeated(stagedDeps);
        }
        updateStatus();
    }

    private void reprocessDefeated( LinkedListEntry<DefeasibleLogicalDependency> deps) {
        for ( LinkedListEntry<DefeasibleLogicalDependency> dep = deps; dep != null; ) {
            LinkedListEntry<DefeasibleLogicalDependency> next = dep.getNext();
            dep.nullPrevNext(); // it needs to be removed, before it can be processed
            add( dep ); // adding back in, effectively reprocesses the dep
            dep = next;
        }
    }

    public void remove(LinkedListNode node) {
        DefeasibleLogicalDependency dep =  ((LinkedListEntry<DefeasibleLogicalDependency>)node).getObject();

        if (dep.getDefeatedBy() != null) {
            // Defeated deps do not have defeated defeated lists of their own, so just remove.
            DefeasibleLogicalDependency defeater = dep.getDefeatedBy();
            defeater.removeDefeated(dep);
        } else {
            // ins undefeated, process it's defeated list if they exist
            removeUndefeated(dep, (LinkedListEntry<DefeasibleLogicalDependency>) node);
            if (dep.getRootDefeated() != null) {
                reprocessDefeated(dep.getRootDefeated());
            }
        }
        updateStatus();
    }

    private boolean checkAndApplyIsDefeated(DefeasibleLogicalDependency potentialInferior, Rule rule, DefeasibleLogicalDependency potentialSuperior) {
        if ( potentialSuperior.getDefeats() == null ) {
            return false;
        }
        if ( potentialSuperior.getStatus() == DefeasibilityStatus.DEFINITELY && potentialInferior.getStatus() != DefeasibilityStatus.DEFINITELY ) {
            potentialSuperior.addDefeated(potentialInferior);
            return true;
        }
        // adds the references that defeat the current node
        if (Arrays.binarySearch(potentialSuperior.getDefeats(), rule.getName()) >= 0 ||
            Arrays.binarySearch(potentialSuperior.getDefeats(), rule.getPackage() + "." + rule.getName()) >= 0) {
            potentialSuperior.addDefeated(potentialInferior);
            return true;
        }
        return false;
    }

    public void addUndefeated(DefeasibleLogicalDependency dep, LinkedListEntry<DefeasibleLogicalDependency> node) {
        boolean pos = ! ( dep.getValue() != null && MODE.NEGATIVE.getId().equals( dep.getValue().toString() ) );
        switch( dep.getStatus() ) {
            case DEFINITELY:
                if ( pos ) {
                    definitelyPosCount++;
                    statusMask = statusMask | DEFINITELY_POS_BIT;
                } else {
                    definitelyNegCount++;
                    statusMask = statusMask | DEFINITELY_NEG_BIT;
                }
                break;
            case DEFEASIBLY:
                if ( pos ) {
                    defeasiblyPosCount++;
                    statusMask = statusMask | DEFEASIBLY_POS_BIT;
                } else {
                    defeasiblyNegCount++;
                    statusMask = statusMask | DEFEASIBLY_NEG_BIT;
                }
                break;
            case DEFEATEDLY:
                if ( pos ) {
                    defeatedlyPosCount++;
                    statusMask = statusMask | DEFEATEDLY_POS_BIT;
                } else {
                    defeatedlyNegCount++;
                    statusMask = statusMask | DEFEATEDLY_NEG_BIT;
                }
                break;
            case UNDECIDABLY:
                throw new IllegalStateException("Individual logical dependencies cannot be undecidably");
        }

        if (rootUndefeated == null) {
            rootUndefeated = node;
            tailUndefeated = node;
        } else {
            if ( dep.getStatus() == DefeasibilityStatus.DEFINITELY ) {
                // Strict dependencies at to the front
                rootUndefeated.setPrevious(node);
                node.setNext(rootUndefeated);
                rootUndefeated = node;
            } else {
                // add to end
                tailUndefeated.setNext(node);
                node.setPrevious(tailUndefeated);
                tailUndefeated = node;
            }
        }
    }

    public void removeUndefeated(DefeasibleLogicalDependency dep, LinkedListEntry<DefeasibleLogicalDependency> node) {
        boolean pos = ! ( dep.getValue() != null && MODE.NEGATIVE.getId().equals( dep.getValue().toString() ) );
        switch( dep.getStatus() ) {
            case DEFINITELY:
                if ( pos ) {
                    definitelyPosCount--;
                    if ( definitelyPosCount == 0 ) {
                        statusMask = statusMask ^ DEFINITELY_POS_BIT;
                    }
                } else {
                    definitelyNegCount--;
                    if ( definitelyNegCount == 0 ) {
                        statusMask = statusMask ^ DEFINITELY_NEG_BIT;
                    }
                }
                break;
            case DEFEASIBLY:
                if ( pos ) {
                    defeasiblyPosCount--;
                    if ( defeasiblyPosCount == 0 ) {
                        statusMask = statusMask ^ DEFEASIBLY_POS_BIT;
                    }
                } else {
                    defeasiblyNegCount--;
                    if ( defeasiblyNegCount == 0 ) {
                        statusMask = statusMask ^ DEFEASIBLY_NEG_BIT;
                    }
                }
                break;
            case DEFEATEDLY:
                if ( pos ) {
                    defeatedlyPosCount--;
                    if ( defeatedlyPosCount == 0 ) {
                        statusMask = statusMask ^ DEFEATEDLY_POS_BIT;
                    }
                } else {
                    defeatedlyNegCount--;
                    if ( defeatedlyNegCount == 0 ) {
                        statusMask = statusMask ^ DEFEATEDLY_NEG_BIT;
                    }
                }
                break;
            case UNDECIDABLY:
                throw new IllegalStateException("Individual logical dependencies cannot be undecidably");
        }

        if (this.rootUndefeated == node) {
            removeFirst();
        } else if (this.tailUndefeated == node) {
            removeLast();
        } else {
            node.getPrevious().setNext(node.getNext());
            ((LinkedListNode)node.getNext()).setPrevious(node.getPrevious());
            node.nullPrevNext();
        }
    }

    public LinkedListNode removeFirst() {
        if (this.rootUndefeated == null) {
            return null;
        }
        final LinkedListEntry<DefeasibleLogicalDependency> node = this.rootUndefeated;
        this.rootUndefeated = node.getNext();
        node.setNext(null);
        if (this.rootUndefeated != null) {
            this.rootUndefeated.setPrevious(null);
        } else {
            this.tailUndefeated = null;
        }
        return node;
    }

    public LinkedListNode removeLast() {
        if (this.tailUndefeated == null) {
            return null;
        }
        final LinkedListEntry<DefeasibleLogicalDependency> node = this.tailUndefeated;
        this.tailUndefeated = node.getPrevious();
        node.setPrevious(null);
        if (this.tailUndefeated != null) {
            this.tailUndefeated.setNext(null);
        } else {
            this.rootUndefeated = this.tailUndefeated;
        }
        return node;
    }

    public LinkedListNode getRootUndefeated() {
        return this.rootUndefeated;
    }

    public LinkedListNode getTailUnDefeated() {
        return this.tailUndefeated;
    }

    public boolean isEmpty() {
        return rootUndefeated == null;
    }

    public int size() {
        return definitelyPosCount + definitelyNegCount + defeasiblyPosCount + defeasiblyNegCount + defeatedlyPosCount + defeatedlyNegCount;
    }

    public int undefeatdSize() {
        int i = 0;
        for (LinkedListEntry<DefeasibleLogicalDependency> existingNode = rootUndefeated; existingNode != null; existingNode = existingNode.getNext()) {
            i++;
        }
        return i;
    }

    public void cancel(PropagationContext propagationContext) {
        // get all but last, as that we'll do via the BeliefSystem, for cleanup
        // note we don't update negative, conflict counters. It's needed for the last cleanup operation
        FastIterator it = iterator();
        for ( LinkedListEntry<DefeasibleLogicalDependency>  node =  getFirst(); node != tailUndefeated;  ) {
            LinkedListEntry<DefeasibleLogicalDependency>  temp = (LinkedListEntry<DefeasibleLogicalDependency>) it.next(node); // get next, as we are about to remove it
            final DefeasibleLogicalDependency dep =  node.getObject();
            dep.getJustifier().getLogicalDependencies().remove( dep );
            remove( dep );
            node = temp;
        }

        LinkedListEntry last = (LinkedListEntry) getFirst();
        final LogicalDependency node = (LogicalDependency) last.getObject();
        node.getJustifier().getLogicalDependencies().remove( node );
        //beliefSystem.delete( node, this, context );
        positiveFactHandle = null;
        negativeFactHandle = null;
    }

    public void clear(PropagationContext propagationContext) {
    }

    public void setWorkingMemoryAction(WorkingMemoryAction wmAction) {
    }

    public boolean isDefinitelyPosProveable() {
        return  (statusMask &  DEFINITELY_POS_BIT) != 0;
    }

    public boolean isDefinitelyNegProveable() {
        return  (statusMask &  DEFINITELY_NEG_BIT) != 0;
    }

    public boolean isDefeasiblyPosProveable() {
        return  (statusMask &  DEFEASIBLY_POS_BIT) != 0;
    }

    public boolean isDefeasiblyNegProveable() {
        return  (statusMask &  DEFEASIBLY_NEG_BIT) != 0;
    }

    public boolean isDefeatedlyPosProveable() {
        return  (statusMask &  DEFEATEDLY_POS_BIT) != 0;
    }

    public boolean isDefeatedlyNegProveable() {
        return  (statusMask &  DEFEATEDLY_NEG_BIT) != 0;
    }
    public DefeasibilityStatus getStatus() {
        return status;
    }

    public void updateStatus() {
        if ( isDefinitelyPosProveable() ^ isDefinitelyNegProveable() ) {
            status = DefeasibilityStatus.DEFINITELY;
            return;
        }

        if ( isConflicting() ) {
            status = DefeasibilityStatus.UNDECIDABLY;
            return;
        }

        if ( isDefeasiblyPosProveable() ^ isDefeasiblyNegProveable() ) {
            status = DefeasibilityStatus.DEFEASIBLY;
            return;
        }

        if ( isDefeatedlyPosProveable() ^ isDefeatedlyNegProveable() ) {
            status = DefeasibilityStatus.DEFEATEDLY;
            return;
        }

        status = DefeasibilityStatus.UNDECIDABLY;
    }

//    public boolean isHeld() {
//        //isUndecided() ||  isDefinitelyPosProveable() ||isDefinitelyNegProveable()
//    }

    public boolean isNegated() {
        return ((statusMask & POS_MASK ) == 0 ) &&  ((statusMask & NEG_MASK ) != 0 );
    }

    public boolean isPositive() {
        return ((statusMask & POS_MASK ) != 0 ) &&  ((statusMask & NEG_MASK ) == 0 );
    }

    public boolean isConflicting() {
        return ((statusMask & POS_MASK ) != 0 ) &&  ((statusMask & NEG_MASK ) != 0 );
    }

    public boolean isUndecided() {
        return getStatus() == DefeasibilityStatus.UNDECIDABLY || getStatus() == DefeasibilityStatus.DEFEATEDLY;
    }

    public FastIterator iterator() {
        return iterator;
    }

    private static class IteratorImpl implements FastIterator {

        public Entry next(Entry object) {
            DefeasibleLogicalDependency dep = ( DefeasibleLogicalDependency ) object;
            if ( dep.getRootDefeated() != null ) {
                // try going down the list of defeated first
                return dep.getRootDefeated();
            }

            if ( dep.getNext() != null ) {
                return dep.getNext();
            }

            if ( dep.getDefeatedBy() != null ) {
                // go back up to the parent undefeated, and try the next undefeated
                return dep.getDefeatedBy().getNext();
            }

            return null; // nothing more to iterate too.

        }

        public boolean isFullIterator() {
            return true;
        }
    }

    private DefeasibilityStatus resolveStatus( LogicalDependency node ) {
        List<? extends FactHandle> premise = node.getJustifier().getFactHandles();

        DefeasibilityStatus status = DefeasibilityStatus.resolve( node.getValue() );

        if ( status == null ) {
            DefeasibleRuleNature defeasibleType = DefeasibleRuleNature.STRICT;
            if ( node.getJustifier().getRule().getMetaData().containsKey( DefeasibleRuleNature.DEFEASIBLE.getLabel() ) ) {
                defeasibleType = DefeasibleRuleNature.DEFEASIBLE;
            } else if ( node.getJustifier().getRule().getMetaData().containsKey( DefeasibleRuleNature.DEFEATER.getLabel() ) ) {
                defeasibleType = DefeasibleRuleNature.DEFEATER;
            }

            switch ( defeasibleType ) {
                case DEFEASIBLE :
                    status = checkDefeasible(premise);
                    break;
                case DEFEATER   :
                    status = checkDefeater(premise);
                    break;
                case STRICT     :
                default         :
                    status = checkStrict(premise );
                    break;
            }
        }
        return status;
    }

    private DefeasibilityStatus checkDefeasible(  List<? extends FactHandle> premise ) {
        return DefeasibilityStatus.DEFEASIBLY;
    }

    private DefeasibilityStatus checkDefeater(  List<? extends FactHandle> premise ) {
        return DefeasibilityStatus.DEFEATEDLY;
    }

    private DefeasibilityStatus checkStrict(  List<? extends FactHandle> premise ) {
        // The rule is strict. To prove that the derivation is strict we have to check that all the premises are
        // either facts or strictly proved facts
        for ( FactHandle h : premise ) {
            if ( h instanceof QueryElementFactHandle ) {
                return DefeasibilityStatus.DEFINITELY;
            }
            EqualityKey key = ((InternalFactHandle) h).getEqualityKey();
            if ( key != null && key.getStatus() == EqualityKey.JUSTIFIED ) {
                //DefeasibleBeliefSet bs = (DefeasibleBeliefSet) getTruthMaintenanceSystem().getJustifiedMap().get(((DefaultFactHandle) h).getId());

                DefeasibleBeliefSet bs = (DefeasibleBeliefSet) key.getBeliefSet();


                if ( bs.getStatus() != DefeasibilityStatus.DEFINITELY ) {
                    // to make a fact "definitely provable", all the supporting non-factual premises must be definitely provable.
                    return DefeasibilityStatus.DEFEASIBLY;
                }
            }
            // else it's a fact, so it's a good candidate for definite entailment
        }
        return DefeasibilityStatus.DEFINITELY;
    }


}
