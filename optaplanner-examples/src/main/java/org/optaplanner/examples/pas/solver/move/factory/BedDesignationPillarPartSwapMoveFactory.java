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

package org.optaplanner.examples.pas.solver.move.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.pas.domain.AdmissionPart;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Night;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.solver.move.BedChangeMove;

import static java.util.Comparator.*;

public class BedDesignationPillarPartSwapMoveFactory implements MoveListFactory<PatientAdmissionSchedule> {

    private static final Comparator<Night> NIGHT_COMPARATOR = comparingLong(Night::getId);
    // This comparison is sameBedInSameNight safe.
    private static final Comparator<BedDesignation> COMPARATOR =
            comparing((BedDesignation bedDesignation) -> bedDesignation.getAdmissionPart().getFirstNight(),
                    NIGHT_COMPARATOR)
                    .thenComparing(bedDesignation -> bedDesignation.getAdmissionPart().getLastNight(), NIGHT_COMPARATOR)
                    .thenComparing(BedDesignation::getAdmissionPart, comparingLong(AdmissionPart::getId));

    @Override
    public List<Move<PatientAdmissionSchedule>> createMoveList(PatientAdmissionSchedule patientAdmissionSchedule) {
        Map<Bed, List<BedDesignation>> bedToBedDesignationList = new HashMap<>(
                patientAdmissionSchedule.getBedList().size());
        for (BedDesignation bedDesignation : patientAdmissionSchedule.getBedDesignationList()) {
            List<BedDesignation> bedDesignationListPerBed = bedToBedDesignationList.computeIfAbsent(bedDesignation.getBed(),
                    // Note: the initialCapacity is probably too high,
                    // which is bad for memory, but the opposite is bad for performance (which is worse)
                    k -> new ArrayList<>(patientAdmissionSchedule.getNightList().size()));
            bedDesignationListPerBed.add(bedDesignation);
        }
        for (List<BedDesignation> bedDesignationListPerBed : bedToBedDesignationList.values()) {
            Collections.sort(bedDesignationListPerBed, COMPARATOR);
        }

        List<Bed> bedList = patientAdmissionSchedule.getBedList();
        List<Move<PatientAdmissionSchedule>> moveList = new ArrayList<>();

        // For every 2 distinct beds
        for (ListIterator<Bed> leftBedIt = bedList.listIterator(); leftBedIt.hasNext(); ) {
            Bed leftBed = leftBedIt.next();
            for (ListIterator<Bed> rightBedIt = bedList.listIterator(leftBedIt.nextIndex());
                    rightBedIt.hasNext(); ) {
                Bed rightBed = rightBedIt.next();
                List<BedDesignation> leftBedDesignationList = bedToBedDesignationList.get(leftBed);
                if (leftBedDesignationList == null) {
                    leftBedDesignationList = Collections.emptyList();
                }
                List<BedDesignation> rightBedDesignationList = bedToBedDesignationList.get(rightBed);
                if (rightBedDesignationList == null) {
                    rightBedDesignationList = Collections.emptyList();
                }
                LowestFirstNightBedDesignationIterator lowestIt = new LowestFirstNightBedDesignationIterator(
                        leftBedDesignationList, rightBedDesignationList);
                // For every pillar part duo
                while (lowestIt.hasNext()) {
                    BedDesignation pillarPartBedDesignation = lowestIt.next();
                    // Note: the initialCapacity is probably too high,
                    // which is bad for memory, but the opposite is bad for performance (which is worse)
                    List<BedChangeMove> moveListByPillarPartDuo = new ArrayList<>(
                            leftBedDesignationList.size() + rightBedDesignationList.size());
                    int lastNightIndex = pillarPartBedDesignation.getAdmissionPart().getLastNight().getIndex();
                    Bed otherBed;
                    int leftMinimumFirstNightIndex = Integer.MIN_VALUE;
                    int rightMinimumFirstNightIndex = Integer.MIN_VALUE;
                    if (lowestIt.isLastNextWasLeft()) {
                        otherBed = rightBed;
                        leftMinimumFirstNightIndex = lastNightIndex;
                    } else {
                        otherBed = leftBed;
                        rightMinimumFirstNightIndex = lastNightIndex;
                    }
                    moveListByPillarPartDuo.add(new BedChangeMove(pillarPartBedDesignation, otherBed));
                    // For every BedDesignation in that pillar part duo
                    while (lowestIt.hasNextWithMaximumFirstNightIndexes(
                            leftMinimumFirstNightIndex, rightMinimumFirstNightIndex)) {
                        pillarPartBedDesignation = lowestIt.next();
                        lastNightIndex = pillarPartBedDesignation.getAdmissionPart().getLastNight().getIndex();
                        if (lowestIt.isLastNextWasLeft()) {
                            otherBed = rightBed;
                            leftMinimumFirstNightIndex = Math.max(leftMinimumFirstNightIndex, lastNightIndex);
                        } else {
                            otherBed = leftBed;
                            rightMinimumFirstNightIndex = Math.max(rightMinimumFirstNightIndex, lastNightIndex);
                        }
                        moveListByPillarPartDuo.add(new BedChangeMove(pillarPartBedDesignation, otherBed));
                    }
                    moveList.add(CompositeMove.buildMove(moveListByPillarPartDuo));
                }
            }
        }
        return moveList;
    }

    private static class LowestFirstNightBedDesignationIterator implements Iterator<BedDesignation> {

        private Iterator<BedDesignation> leftIterator;
        private Iterator<BedDesignation> rightIterator;

        private boolean leftHasNext = true;
        private boolean rightHasNext = true;

        private BedDesignation nextLeft;
        private BedDesignation nextRight;

        private boolean lastNextWasLeft;

        public LowestFirstNightBedDesignationIterator(
                List<BedDesignation> leftBedDesignationList, List<BedDesignation> rightBedDesignationList) {
            // Buffer the nextLeft and nextRight
            leftIterator = leftBedDesignationList.iterator();
            if (leftIterator.hasNext()) {
                nextLeft = leftIterator.next();
            } else {
                leftHasNext = false;
                nextLeft = null;
            }
            rightIterator = rightBedDesignationList.iterator();
            if (rightIterator.hasNext()) {
                nextRight = rightIterator.next();
            } else {
                rightHasNext = false;
                nextRight = null;
            }
        }

        @Override
        public boolean hasNext() {
            return leftHasNext || rightHasNext;
        }

        public boolean hasNextWithMaximumFirstNightIndexes(
                int leftMinimumFirstNightIndex, int rightMinimumFirstNightIndex) {
            if (!hasNext()) {
                return false;
            }
            boolean nextIsLeft = nextIsLeft();
            if (nextIsLeft) {
                int firstNightIndex = nextLeft.getAdmissionPart().getFirstNight().getIndex();
                // It should not be conflict in the same pillar and it should be in conflict with the other pillar
                return firstNightIndex > leftMinimumFirstNightIndex && firstNightIndex <= rightMinimumFirstNightIndex;
            } else {
                int firstNightIndex = nextRight.getAdmissionPart().getFirstNight().getIndex();
                // It should not be conflict in the same pillar and it should be in conflict with the other pillar
                return firstNightIndex > rightMinimumFirstNightIndex && firstNightIndex <= leftMinimumFirstNightIndex;
            }
        }

        @Override
        public BedDesignation next() {
            lastNextWasLeft = nextIsLeft();
            // Buffer the nextLeft or nextRight
            BedDesignation lowest;
            if (lastNextWasLeft) {
                lowest = nextLeft;
                if (leftIterator.hasNext()) {
                    nextLeft = leftIterator.next();
                } else {
                    leftHasNext = false;
                    nextLeft = null;
                }
            } else {
                lowest = nextRight;
                if (rightIterator.hasNext()) {
                    nextRight = rightIterator.next();
                } else {
                    rightHasNext = false;
                    nextRight = null;
                }
            }
            return lowest;
        }

        private boolean nextIsLeft() {
            boolean returnLeft;
            if (leftHasNext) {
                if (rightHasNext) {
                    int leftFirstNightIndex = nextLeft.getAdmissionPart().getFirstNight().getIndex();
                    int rightFirstNightIndex = nextRight.getAdmissionPart().getFirstNight().getIndex();
                    returnLeft = leftFirstNightIndex <= rightFirstNightIndex;
                } else {
                    returnLeft = true;
                }
            } else {
                if (rightHasNext) {
                    returnLeft = false;
                } else {
                    throw new NoSuchElementException();
                }
            }
            return returnLeft;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("The optional operation remove() is not supported.");
        }

        public boolean isLastNextWasLeft() {
            return lastNextWasLeft;
        }
    }
}
