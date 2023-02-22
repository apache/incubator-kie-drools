package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.util.CollectionUtils;

/**
 * A 2-opt move for list variables, which takes two edges assigned to the same entity and swap their endpoints.
 * For instance, let [A, B, E, D, C, F, G, H] be the route assigned to an entity.
 * Select (B, E) and (C, F) as the edges to swap. Then the resulting route after this operation would be
 * [A, B, C, D, E, F, G, H]. The edge (B, E) became (B, C), and the edge (C, F) became (E, F)
 * (the first edge end point became the second edge start point and vice-versa). It is used to fix crossings;
 * for instance, it can change:
 * ... -> A B <- ...
 * x
 * ... <- C D -> ...
 *
 * to
 *
 * ... -> A -> B -> ...
 *
 * ... <- C <- D <- ...
 *
 * Note the sub-path D...B was reversed. The 2-opt works be reversing the path between the two edges being removed.
 *
 * @param <Solution_>
 */
final class TwoOptListMove<Solution_> extends AbstractMove<Solution_> {
    private final ListVariableDescriptor<Solution_> variableDescriptor;
    private final IndexVariableSupply indexVariableSupply;
    private final Object entity;
    private final Object firstEdgeStartpoint;
    private final Object firstEdgeEndpoint;
    private final Object secondEdgeStartpoint;
    private final Object secondEdgeEndpoint;

    public TwoOptListMove(ListVariableDescriptor<Solution_> variableDescriptor,
            IndexVariableSupply indexVariableSupply,
            Object entity,
            Object firstEdgeEndpoint, Object secondEdgeEndpoint) {
        this(variableDescriptor, indexVariableSupply, entity,
                getStartPoint(variableDescriptor, indexVariableSupply, entity, firstEdgeEndpoint), firstEdgeEndpoint,
                getStartPoint(variableDescriptor, indexVariableSupply, entity, secondEdgeEndpoint), secondEdgeEndpoint);
    }

    public TwoOptListMove(ListVariableDescriptor<Solution_> variableDescriptor,
            IndexVariableSupply indexVariableSupply,
            Object entity,
            Object firstEdgeStartpoint, Object firstEdgeEndpoint,
            Object secondEdgeStartpoint, Object secondEdgeEndpoint) {
        this.variableDescriptor = variableDescriptor;
        this.indexVariableSupply = indexVariableSupply;
        this.entity = entity;
        this.firstEdgeStartpoint = firstEdgeStartpoint;
        this.firstEdgeEndpoint = firstEdgeEndpoint;
        this.secondEdgeStartpoint = secondEdgeStartpoint;
        this.secondEdgeEndpoint = secondEdgeEndpoint;
    }

    @Override
    protected TwoOptListMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new TwoOptListMove<>(variableDescriptor,
                indexVariableSupply,
                entity,
                firstEdgeStartpoint,
                secondEdgeStartpoint,
                firstEdgeEndpoint,
                secondEdgeEndpoint);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        List<Object> listVariable = variableDescriptor.getListVariable(entity);
        int firstEdgeEndpointIndex = indexVariableSupply.getIndex(firstEdgeEndpoint);
        int secondEdgeEndpointIndex = indexVariableSupply.getIndex(secondEdgeEndpoint);

        if (firstEdgeEndpointIndex < secondEdgeEndpointIndex) {
            if (firstEdgeEndpointIndex > 0) {
                innerScoreDirector.beforeListVariableChanged(variableDescriptor, entity,
                        firstEdgeEndpointIndex,
                        secondEdgeEndpointIndex);
            } else {
                innerScoreDirector.beforeListVariableChanged(variableDescriptor, entity,
                        0,
                        listVariable.size());
            }

            Collections.reverse(listVariable.subList(firstEdgeEndpointIndex, secondEdgeEndpointIndex));

            if (firstEdgeEndpointIndex == 0) {
                Collections.rotate(listVariable, -(secondEdgeEndpointIndex - 1));
            }

            if (firstEdgeEndpointIndex > 0) {
                innerScoreDirector.afterListVariableChanged(variableDescriptor, entity,
                        firstEdgeEndpointIndex,
                        secondEdgeEndpointIndex);
            } else {
                innerScoreDirector.afterListVariableChanged(variableDescriptor, entity,
                        0,
                        listVariable.size());
            }
        } else {
            List<Object> firstHalfReversedPath = listVariable.subList(firstEdgeEndpointIndex, listVariable.size());
            List<Object> secondHalfReversedPath = listVariable.subList(0, secondEdgeEndpointIndex);

            innerScoreDirector.beforeListVariableChanged(variableDescriptor, entity,
                    0,
                    listVariable.size());

            // Reverse the combined list firstHalfReversedPath + secondHalfReversedPath
            // For instance, (1, 2, 3)(4, 5, 6, 7, 8, 9) becomes
            // (9, 8, 7)(6, 5, 4, 3, 2, 1)
            int totalLength = firstHalfReversedPath.size() + secondHalfReversedPath.size();

            // Used to rotate the list to put the first element back in its original position
            int firstElementShift = 0;
            for (int i = 0; (i < totalLength >> 1); i++) {
                if (i < firstHalfReversedPath.size()) {
                    if (i < secondHalfReversedPath.size()) {
                        // firstHalfIndex = i
                        int secondHalfIndex = secondHalfReversedPath.size() - i - 1;
                        if (secondHalfIndex == 0) {
                            firstElementShift = firstEdgeEndpointIndex + i;
                        }
                        Object savedFirstItem = firstHalfReversedPath.get(i);
                        firstHalfReversedPath.set(i, secondHalfReversedPath.get(secondHalfIndex));
                        secondHalfReversedPath.set(secondHalfIndex, savedFirstItem);
                    } else {
                        // firstIndex = i
                        int secondIndex = firstHalfReversedPath.size() - i + secondHalfReversedPath.size() - 1;
                        Object savedFirstItem = firstHalfReversedPath.get(i);
                        firstHalfReversedPath.set(i, firstHalfReversedPath.get(secondIndex));
                        firstHalfReversedPath.set(secondIndex, savedFirstItem);
                    }
                } else {
                    int firstIndex = i - firstHalfReversedPath.size();
                    int secondIndex = secondHalfReversedPath.size() - i - 1;
                    if (firstIndex == 0) {
                        firstElementShift = secondIndex;
                    } else if (secondIndex == 0) {
                        firstElementShift = firstIndex;
                    }
                    Object savedFirstItem = secondHalfReversedPath.get(firstIndex);
                    secondHalfReversedPath.set(firstIndex, secondHalfReversedPath.get(secondIndex));
                    secondHalfReversedPath.set(secondIndex, savedFirstItem);
                }
            }

            // Rotate the list so the first element back in its original position
            Collections.rotate(listVariable, -firstElementShift);
            innerScoreDirector.afterListVariableChanged(variableDescriptor, entity,
                    0,
                    listVariable.size());
        }
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return true;
    }

    @Override
    public TwoOptListMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new TwoOptListMove<>(variableDescriptor,
                indexVariableSupply,
                destinationScoreDirector.lookUpWorkingObject(entity),
                destinationScoreDirector.lookUpWorkingObject(firstEdgeStartpoint),
                destinationScoreDirector.lookUpWorkingObject(firstEdgeEndpoint),
                destinationScoreDirector.lookUpWorkingObject(secondEdgeStartpoint),
                destinationScoreDirector.lookUpWorkingObject(secondEdgeEndpoint));
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return "2-Opt(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return List.of(entity);
    }

    @Override
    public Collection<?> getPlanningValues() {
        List<Object> listVariable = variableDescriptor.getListVariable(entity);
        int firstEdgeEndpointIndex = indexVariableSupply.getIndex(firstEdgeEndpoint);
        int secondEdgeEndpointIndex = indexVariableSupply.getIndex(secondEdgeEndpoint);

        if (firstEdgeEndpointIndex < secondEdgeEndpointIndex) {
            return new ArrayList<>(listVariable.subList(firstEdgeEndpointIndex, secondEdgeEndpointIndex));
        } else {
            List<Object> firstHalfReversedPath = listVariable.subList(firstEdgeEndpointIndex, listVariable.size());
            List<Object> secondHalfReversedPath = listVariable.subList(0, secondEdgeEndpointIndex);
            return CollectionUtils.concat(firstHalfReversedPath, secondHalfReversedPath);
        }
    }

    public Object getEntity() {
        return entity;
    }

    public Object getFirstEdgeEndpoint() {
        return firstEdgeEndpoint;
    }

    public Object getSecondEdgeEndpoint() {
        return secondEdgeEndpoint;
    }

    public Object getFirstEdgeStartpoint() {
        return firstEdgeStartpoint;
    }

    public Object getSecondEdgeStartpoint() {
        return secondEdgeStartpoint;
    }

    private static <Solution_> Object getStartPoint(ListVariableDescriptor<Solution_> variableDescriptor,
            IndexVariableSupply indexVariableSupply,
            Object entity,
            Object endPoint) {
        List<Object> listVariable = variableDescriptor.getListVariable(entity);
        int endPointIndex = indexVariableSupply.getIndex(endPoint);
        if (endPointIndex == 0) {
            return listVariable.get(listVariable.size() - 1);
        } else {
            return listVariable.get(endPointIndex - 1);
        }
    }

    @Override
    public String toString() {
        return "2-Opt(entity=" +
                entity +
                ", removed=[(" +
                firstEdgeStartpoint + " -> " + firstEdgeEndpoint + "), (" +
                secondEdgeStartpoint + "-> " + secondEdgeEndpoint + ")]" +
                ", added=[(" +
                firstEdgeStartpoint + " -> " + secondEdgeStartpoint + "), (" +
                firstEdgeEndpoint + " -> " + secondEdgeEndpoint + ")]" +
                ")";
    }
}
