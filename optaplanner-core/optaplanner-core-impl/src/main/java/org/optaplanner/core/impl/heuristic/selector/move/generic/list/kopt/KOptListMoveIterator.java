package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.move.NoChangeMove;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

final class KOptListMoveIterator<Solution_, Node_, Entity_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final Random workingRandom;
    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final SingletonInverseVariableSupply inverseVariableSupply;
    private final IndexVariableSupply indexVariableSupply;
    private final EntitySelector<Solution_> entitySelector;
    private final Function<Node_, Node_> successorFunction;
    private final Function<Node_, Node_> predecessorFunction;
    private final TriPredicate<Node_, Node_, Node_> betweenFunction;
    private final int minK;
    private final int maxK;
    private final int maxCyclesPatchedInInfeasibleMove;

    private Iterator<Entity_> entityIterator;

    public KOptListMoveIterator(Random workingRandom,
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply,
            EntitySelector<Solution_> entitySelector,
            int minK,
            int maxK) {
        this.workingRandom = workingRandom;
        this.listVariableDescriptor = listVariableDescriptor;
        this.inverseVariableSupply = inverseVariableSupply;
        this.indexVariableSupply = indexVariableSupply;
        this.entitySelector = entitySelector;
        this.minK = minK;
        this.maxK = maxK;
        this.maxCyclesPatchedInInfeasibleMove = maxK;
        this.successorFunction =
                KOptUtils.getSuccessorFunction(listVariableDescriptor, inverseVariableSupply, indexVariableSupply);
        this.predecessorFunction =
                KOptUtils.getPredecessorFunction(listVariableDescriptor, inverseVariableSupply, indexVariableSupply);
        this.betweenFunction = KOptUtils.getBetweenPredicate(indexVariableSupply);
    }

    @SuppressWarnings("unchecked")
    private Iterator<Node_> getValueIteratorForEntity(Entity_ entity) {
        // valueSelector.iterator(entity) can select values on different entities,
        // so to only pick values on the selected entity, pick random elements from
        // its list variable
        List<Node_> entityListVariable = (List<Node_>) listVariableDescriptor.getListVariable(entity);
        return workingRandom.ints(0, entityListVariable.size())
                .mapToObj(entityListVariable::get).iterator();
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        int k = workingRandom.nextInt(maxK - minK + 1) + minK;
        Entity_ entity = pickEntityWithMinimumRouteLength(2 * k);
        while (entity == null) {
            k--;
            if (k <= 1) {
                // Was unable to find an entity with more than 1 value in its route
                // (rare, but possible)
                return new NoChangeMove<>();
            }
            entity = pickEntityWithMinimumRouteLength(2 * k);
        }
        if (k == 2) {
            Iterator<Node_> valueIterator = getValueIteratorForEntity(entity);
            Node_ firstEndpoint = valueIterator.next();
            Node_ secondEndpoint = valueIterator.next();
            while (secondEndpoint == firstEndpoint) {
                secondEndpoint = valueIterator.next();
            }
            return new TwoOptListMove<>(listVariableDescriptor, indexVariableSupply, entity,
                    firstEndpoint, secondEndpoint);
        }
        KOptDescriptor<Node_> descriptor = pickKOptMove(entity, k);
        if (descriptor == null) {
            // Was unable to find a K-Opt move
            return new NoChangeMove<>();
        }
        return descriptor.getKOptListMove(listVariableDescriptor, indexVariableSupply, entity);
    }

    @SuppressWarnings("unchecked")
    private Entity_ pickEntityWithMinimumRouteLength(int minimumLength) {
        if (entityIterator == null) {
            entityIterator = (Iterator<Entity_>) entitySelector.endingIterator();
        }
        for (int i = 0; i < 2; i++) {
            while (entityIterator.hasNext()) {
                Entity_ entity = entityIterator.next();
                if (listVariableDescriptor.getListSize(entity) >= minimumLength) {
                    return entity;
                }
            }
            entityIterator = (Iterator<Entity_>) entitySelector.endingIterator();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private KOptDescriptor<Node_> pickKOptMove(Entity_ entity, int k) {
        // The code in the paper used 1-index arrays
        Node_[] pickedValues = (Node_[]) new Object[2 * k + 1];
        Iterator<Node_> valueIterator = getValueIteratorForEntity(entity);
        pickedValues[1] = valueIterator.next();
        pickedValues[2] = workingRandom.nextBoolean() ? getNodePredecessor(pickedValues[1]) : getNodeSuccessor(pickedValues[1]);
        return pickKOptMoveRec(valueIterator, pickedValues, 2, k);
    }

    private KOptDescriptor<Node_> pickKOptMoveRec(Iterator<Node_> valueIterator, Node_[] pickedValues,
            int pickedSoFar,
            int k) {
        Node_ previousRemovedEdgeEndpoint = pickedValues[2 * pickedSoFar - 2];
        Node_ nextRemovedEdgePoint, nextRemovedEdgeOppositePoint;

        int remainingAttempts = (k - pickedSoFar + 3) * 2;
        while (remainingAttempts > 0) {
            nextRemovedEdgePoint = valueIterator.next();
            while (nextRemovedEdgePoint == getNodePredecessor(previousRemovedEdgeEndpoint) ||
                    nextRemovedEdgePoint == getNodeSuccessor(previousRemovedEdgeEndpoint) ||
                    isEdgeAlreadyAdded(pickedValues, previousRemovedEdgeEndpoint, nextRemovedEdgePoint, pickedSoFar - 2) ||
                    (isEdgeAlreadyDeleted(pickedValues, nextRemovedEdgePoint, getNodePredecessor(nextRemovedEdgePoint),
                            pickedSoFar - 2)
                            && isEdgeAlreadyDeleted(pickedValues, nextRemovedEdgePoint, getNodeSuccessor(nextRemovedEdgePoint),
                                    pickedSoFar - 2))) {
                if (remainingAttempts == 0) {
                    return null;
                }
                nextRemovedEdgePoint = valueIterator.next();
                remainingAttempts--;
            }
            remainingAttempts--;

            pickedValues[2 * pickedSoFar - 1] = nextRemovedEdgePoint;
            if (isEdgeAlreadyDeleted(pickedValues, nextRemovedEdgePoint, getNodePredecessor(nextRemovedEdgePoint),
                    pickedSoFar - 2)) {
                nextRemovedEdgeOppositePoint = getNodeSuccessor(nextRemovedEdgePoint);
            } else if (isEdgeAlreadyDeleted(pickedValues, nextRemovedEdgePoint, getNodeSuccessor(nextRemovedEdgePoint),
                    pickedSoFar - 2)) {
                nextRemovedEdgeOppositePoint = getNodePredecessor(nextRemovedEdgePoint);
            } else {
                nextRemovedEdgeOppositePoint = workingRandom.nextBoolean() ? getNodeSuccessor(nextRemovedEdgePoint)
                        : getNodePredecessor(nextRemovedEdgePoint);
            }
            pickedValues[2 * pickedSoFar] = nextRemovedEdgeOppositePoint;

            if (pickedSoFar < k) {
                KOptDescriptor<Node_> descriptor = pickKOptMoveRec(valueIterator, pickedValues, pickedSoFar + 1, k);
                if (descriptor != null && descriptor.isFeasible()) {
                    return descriptor;
                }
            } else {
                KOptDescriptor<Node_> descriptor = new KOptDescriptor<>(pickedValues, successorFunction, betweenFunction);
                if (descriptor.isFeasible()) {
                    return descriptor;
                } else {
                    descriptor = patchCycles(valueIterator, descriptor, pickedValues, pickedSoFar);
                    if (descriptor.isFeasible()) {
                        return descriptor;
                    }
                }
            }
        }
        return null;
    }

    KOptDescriptor<Node_> patchCycles(Iterator<Node_> valueIterator,
            KOptDescriptor<Node_> descriptor, Node_[] oldRemovedEdges, int k) {
        Node_ s1, s2;
        int[] removedEdgeIndexToTourOrder = descriptor.getRemovedEdgeIndexToTourOrder();
        KOptCycle cycleInfo = KOptUtils.getCyclesForPermutation(descriptor);
        int cycleCount = cycleInfo.cycleCount;
        int[] cycle = cycleInfo.indexToCycleIdentifier;

        if (cycleCount == 1 || cycleCount > maxCyclesPatchedInInfeasibleMove) {
            return descriptor;
        }
        int currentCycle = getShortestCycleIdentifier(oldRemovedEdges, cycle, removedEdgeIndexToTourOrder, cycleCount, k);
        for (int i = 0; i < k; i++) {
            if (cycle[removedEdgeIndexToTourOrder[2 * i]] == currentCycle) {
                Node_ sStart = oldRemovedEdges[removedEdgeIndexToTourOrder[2 * i]];
                Node_ sStop = oldRemovedEdges[removedEdgeIndexToTourOrder[2 * i + 1]];
                for (s1 = sStart; s1 != sStop; s1 = s2) {
                    Node_[] removedEdges = Arrays.copyOf(oldRemovedEdges, oldRemovedEdges.length + 2);

                    removedEdges[2 * k + 1] = s1;
                    s2 = getNodeSuccessor(s1);
                    removedEdges[2 * k + 2] = s2;
                    int[] addedEdgeToOtherEndpoint = new int[removedEdges.length];
                    KOptDescriptor<Node_> newMove = patchCyclesRec(valueIterator, descriptor, removedEdges,
                            addedEdgeToOtherEndpoint, cycle, currentCycle,
                            k, 2, cycleCount);
                    if (newMove.isFeasible()) {
                        return newMove;
                    }
                }
            }
        }
        return descriptor;
    }

    KOptDescriptor<Node_> patchCyclesRec(Iterator<Node_> valueIterator,
            KOptDescriptor<Node_> originalMove,
            Node_[] oldRemovedEdges, int[] addedEdgeToOtherEndpoint, int[] cycle, int currentCycle,
            int k, int patchedCycleCount, int cycleCount) {
        Node_ s1, s2, s3, s4;
        int NewCycle, i;
        Integer[] cycleSaved = new Integer[1 + 2 * k];
        Node_[] removedEdges = Arrays.copyOf(oldRemovedEdges, oldRemovedEdges.length + 2);

        s1 = removedEdges[2 * k + 1];
        s2 = removedEdges[i = 2 * (k + patchedCycleCount) - 2];
        addedEdgeToOtherEndpoint[addedEdgeToOtherEndpoint[i] = i + 1] = i;
        for (i = 1; i <= 2 * k; i++) {
            cycleSaved[i] = cycle[i];
        }

        s3 = valueIterator.next();
        int remainingAttempts = (cycleCount - patchedCycleCount) * 2;
        while (s3 != getNodePredecessor(s2) || s3 != getNodeSuccessor(s2)
                || ((NewCycle = findCycleIdentifierForNode(s3, removedEdges, originalMove.getRemovedEdgeIndexToTourOrder(),
                        cycle)) == currentCycle)
                ||
                (isEdgeAlreadyDeleted(removedEdges, s3, getNodePredecessor(s3), k)
                        && isEdgeAlreadyDeleted(removedEdges, s3, getNodeSuccessor(s3), k))) {
            if (remainingAttempts == 0) {
                return originalMove;
            }
            s3 = valueIterator.next();
            remainingAttempts--;
        }
        removedEdges[2 * (k + patchedCycleCount) - 1] = s3;
        if (isEdgeAlreadyDeleted(removedEdges, s3, getNodePredecessor(s3), k)) {
            s4 = getNodeSuccessor(s3);
        } else if (isEdgeAlreadyDeleted(removedEdges, s3, getNodeSuccessor(s3), k)) {
            s4 = getNodePredecessor(s3);
        } else {
            s4 = workingRandom.nextBoolean() ? getNodeSuccessor(s3) : getNodePredecessor(s3);
        }
        removedEdges[2 * (k + patchedCycleCount)] = s4;
        if (cycleCount > 2) {
            for (i = 1; i <= 2 * k; i++) {
                if (cycle[i] == NewCycle) {
                    cycle[i] = currentCycle;
                }
            }
            KOptDescriptor<Node_> recursiveCall =
                    patchCyclesRec(valueIterator, originalMove, removedEdges, addedEdgeToOtherEndpoint, cycle, currentCycle,
                            k, patchedCycleCount + 1, cycleCount - 1);
            if (recursiveCall.isFeasible()) {
                return recursiveCall;
            }
            for (i = 1; i <= 2 * k; i++) {
                cycle[i] = cycleSaved[i];
            }
        } else if (s4 != s1) {
            addedEdgeToOtherEndpoint[addedEdgeToOtherEndpoint[2 * k + 1] = 2 * (k + patchedCycleCount)] =
                    2 * k + 1;
            return new KOptDescriptor<>(removedEdges, addedEdgeToOtherEndpoint, successorFunction, betweenFunction);
        }
        return originalMove;
    }

    int findCycleIdentifierForNode(Node_ value, Node_[] pickedValues, int[] permutation, int[] indexToCycle) {
        for (int i = 1; i < pickedValues.length; i++) {
            if (isMiddleNodeBetween(pickedValues[permutation[i]], value, pickedValues[permutation[i + 1]])) {
                return indexToCycle[permutation[i]];
            }
        }
        throw new IllegalStateException("Cannot find cycle the " + value + " belongs to");
    }

    int getShortestCycleIdentifier(Object[] removeEdgeEndpoints, int[] endpointIndexToCycle,
            int[] removeEdgeEndpointIndexToTourOrder, int cycleCount, int k) {
        int i;
        int minCycleIdentifier = 0;
        int minSize = Integer.MAX_VALUE;
        int[] size = new int[cycleCount + 1];

        for (i = 1; i <= cycleCount; i++) {
            size[i] = 0;
        }
        removeEdgeEndpointIndexToTourOrder[0] = removeEdgeEndpointIndexToTourOrder[2 * k];
        for (i = 0; i < 2 * k; i += 2) {
            size[endpointIndexToCycle[removeEdgeEndpointIndexToTourOrder[i]]] +=
                    getSegmentSize(removeEdgeEndpoints[removeEdgeEndpointIndexToTourOrder[i]],
                            removeEdgeEndpoints[removeEdgeEndpointIndexToTourOrder[i + 1]]);
        }
        for (i = 1; i <= cycleCount; i++) {
            if (size[i] < minSize) {
                minSize = size[i];
                minCycleIdentifier = i;
            }
        }
        return minCycleIdentifier;
    }

    private int getSegmentSize(Object from, Object to) {
        int startIndex = indexVariableSupply.getIndex(from);
        int endIndex = indexVariableSupply.getIndex(to);

        if (startIndex <= endIndex) {
            return endIndex - startIndex;
        } else {
            return listVariableDescriptor.getListSize(inverseVariableSupply.getInverseSingleton(from)) - startIndex + endIndex;
        }
    }

    private boolean isEdgeAlreadyAdded(Object[] pickedValues, Object ta, Object tb, int k) {
        int i = 2 * k;
        while ((i -= 2) > 0) {
            if ((ta == pickedValues[i] && tb == pickedValues[i + 1]) ||
                    (ta == pickedValues[i + 1] && tb == pickedValues[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isEdgeAlreadyDeleted(Object[] pickedValues, Object ta, Object tb, int k) {
        int i = 2 * k + 2;
        while ((i -= 2) > 0) {
            if ((ta == pickedValues[i - 1] && tb == pickedValues[i]) ||
                    (ta == pickedValues[i] && tb == pickedValues[i - 1])) {
                return true;
            }
        }
        return false;
    }

    private Node_ getNodeSuccessor(Node_ node) {
        return successorFunction.apply(node);
    }

    private Node_ getNodePredecessor(Node_ node) {
        return predecessorFunction.apply(node);
    }

    private boolean isMiddleNodeBetween(Node_ start, Node_ middle, Node_ end) {
        return betweenFunction.test(start, middle, end);
    }
}
