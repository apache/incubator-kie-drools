/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.util.Pair;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
final class KOptListMove<Solution_> extends AbstractMove<Solution_> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final KOptDescriptor<?> descriptor;
    private final List<FlipSublistAction> equivalent2Opts;
    private final KOptAffectedElements affectedElementsInfo;
    private final MultipleDelegateList<?> combinedList;
    private final int postShiftAmount;
    private final int[] newEndIndices;
    private final Object[] originalEntities;

    public KOptListMove(ListVariableDescriptor<Solution_> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            KOptDescriptor<?> descriptor,
            List<FlipSublistAction> equivalent2Opts,
            int postShiftAmount,
            int[] newEndIndices) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.descriptor = descriptor;
        this.equivalent2Opts = equivalent2Opts;
        this.postShiftAmount = postShiftAmount;
        this.newEndIndices = newEndIndices;
        if (equivalent2Opts.isEmpty()) {
            affectedElementsInfo = KOptAffectedElements.forMiddleRange(0, 0);
            combinedList = new MultipleDelegateList<>();
        } else if (postShiftAmount != 0) {
            affectedElementsInfo = KOptAffectedElements.forMiddleRange(0, equivalent2Opts.get(0).getCombinedList().size());
            combinedList = equivalent2Opts.get(0).getCombinedList();
        } else {
            KOptAffectedElements currentAffectedElements = equivalent2Opts.get(0).getAffectedElements();
            combinedList = equivalent2Opts.get(0).getCombinedList();
            for (int i = 1; i < equivalent2Opts.size(); i++) {
                currentAffectedElements = currentAffectedElements.merge(equivalent2Opts.get(i).getAffectedElements());
            }
            affectedElementsInfo = currentAffectedElements;
        }

        originalEntities = new Object[combinedList.delegates.length];
        for (int i = 0; i < originalEntities.length; i++) {
            originalEntities[i] = inverseVariableSupply.getInverseSingleton(combinedList.delegates[i].get(0));
        }
    }

    KOptDescriptor<?> getDescriptor() {
        return descriptor;
    }

    @Override
    protected AbstractMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        if (equivalent2Opts.isEmpty()) {
            return this;
        } else {
            List<FlipSublistAction> inverse2Opts = new ArrayList<>(equivalent2Opts.size());
            for (int i = equivalent2Opts.size() - 1; i >= 0; i--) {
                inverse2Opts.add(equivalent2Opts.get(i).createUndoMove());
            }

            int[] originalEndIndices = new int[newEndIndices.length];
            for (int i = 0; i < originalEndIndices.length - 1; i++) {
                originalEndIndices[i] = combinedList.offsets[i + 1] - 1;
            }
            originalEndIndices[originalEndIndices.length - 1] = combinedList.size() - 1;

            return new UndoKOptListMove<>(listVariableDescriptor, descriptor, inverse2Opts, -postShiftAmount,
                    originalEndIndices, originalEntities);
        }
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;

        combinedList.actOnAffectedElements(originalEntities,
                (entity, start, end) -> innerScoreDirector.beforeListVariableChanged(listVariableDescriptor, entity,
                        start,
                        end));

        for (FlipSublistAction move : equivalent2Opts) {
            move.doMoveOnGenuineVariables();
        }

        combinedList.moveElementsOfDelegates(newEndIndices);

        Collections.rotate(combinedList, postShiftAmount);

        combinedList.actOnAffectedElements(originalEntities,
                (entity, start, end) -> innerScoreDirector.afterListVariableChanged(listVariableDescriptor, entity,
                        start,
                        end));
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return !equivalent2Opts.isEmpty();
    }

    @Override
    public Move<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        List<FlipSublistAction> rebasedEquivalent2Opts = new ArrayList<>(equivalent2Opts.size());
        InnerScoreDirector<?, ?> innerScoreDirector = (InnerScoreDirector<?, ?>) destinationScoreDirector;

        for (FlipSublistAction twoOpt : equivalent2Opts) {
            rebasedEquivalent2Opts.add(twoOpt.rebase(innerScoreDirector));
        }

        return new KOptListMove<>(listVariableDescriptor,
                innerScoreDirector.getSupplyManager().demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor)),
                descriptor, rebasedEquivalent2Opts, postShiftAmount, newEndIndices);
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return descriptor.getK() + "-opt(" + listVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return List.of(originalEntities);
    }

    @Override
    public Collection<?> getPlanningValues() {
        List<Object> out = new ArrayList<>();

        if (affectedElementsInfo.getWrappedStartIndex() != -1) {
            out.addAll(combinedList.subList(affectedElementsInfo.getWrappedStartIndex(), combinedList.size()));
            out.addAll(combinedList.subList(0, affectedElementsInfo.getWrappedEndIndex()));
        }
        for (Pair<Integer, Integer> affectedInterval : affectedElementsInfo.getAffectedMiddleRangeList()) {
            out.addAll(combinedList.subList(affectedInterval.getKey(), affectedInterval.getValue()));
        }

        return out;
    }

    public String toString() {
        return descriptor.toString();
    }

    /**
     * A K-Opt move that does the list rotation before performing the flips instead of after, allowing
     * it to act as the undo move of a K-Opt move that does the rotation after the flips.
     *
     * @param <Solution_>
     */
    private static final class UndoKOptListMove<Solution_, Node_> extends AbstractMove<Solution_> {
        private final ListVariableDescriptor<Solution_> listVariableDescriptor;
        private final KOptDescriptor<Node_> descriptor;
        private final List<FlipSublistAction> equivalent2Opts;
        private final MultipleDelegateList<?> combinedList;
        private final int preShiftAmount;
        private final int[] newEndIndices;

        private final Object[] originalEntities;

        public UndoKOptListMove(ListVariableDescriptor<Solution_> listVariableDescriptor,
                KOptDescriptor<Node_> descriptor,
                List<FlipSublistAction> equivalent2Opts,
                int preShiftAmount,
                int[] newEndIndices,
                Object[] originalEntities) {
            this.listVariableDescriptor = listVariableDescriptor;
            this.descriptor = descriptor;
            this.equivalent2Opts = equivalent2Opts;
            this.preShiftAmount = preShiftAmount;
            this.combinedList = equivalent2Opts.get(0).getCombinedList();
            this.newEndIndices = newEndIndices;
            this.originalEntities = originalEntities;
        }

        @Override
        public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
            return true;
        }

        @Override
        protected AbstractMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
            InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;

            combinedList.actOnAffectedElements(originalEntities,
                    (entity, start, end) -> innerScoreDirector.beforeListVariableChanged(listVariableDescriptor, entity,
                            start,
                            end));

            Collections.rotate(combinedList, preShiftAmount);
            combinedList.moveElementsOfDelegates(newEndIndices);

            for (FlipSublistAction move : equivalent2Opts) {
                move.doMoveOnGenuineVariables();
            }
            combinedList.actOnAffectedElements(originalEntities,
                    (entity, start, end) -> innerScoreDirector.afterListVariableChanged(listVariableDescriptor, entity,
                            start,
                            end));
        }

        public String toString() {
            return "Undo" + descriptor.toString();
        }
    }

}
