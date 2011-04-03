/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.pas.solver.solution.initializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.score.DefaultHardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.initializer.AbstractStartingSolutionInitializer;
import org.drools.planner.core.solver.AbstractSolverScope;
import org.drools.planner.examples.common.domain.PersistableIdComparator;
import org.drools.planner.examples.pas.domain.AdmissionPart;
import org.drools.planner.examples.pas.domain.Bed;
import org.drools.planner.examples.pas.domain.BedDesignation;
import org.drools.planner.examples.pas.domain.PatientAdmissionSchedule;
import org.drools.planner.examples.pas.domain.Room;

public class PatientAdmissionScheduleStartingSolutionInitializer extends AbstractStartingSolutionInitializer {

    private boolean checkSameBedInSameNight = true;

    @Override
    public boolean isSolutionInitialized(AbstractSolverScope abstractSolverScope) {
        PatientAdmissionSchedule patientAdmissionSchedule = (PatientAdmissionSchedule) abstractSolverScope.getWorkingSolution();
        return patientAdmissionSchedule.isInitialized();
    }

    public void initializeSolution(AbstractSolverScope abstractSolverScope) {
        PatientAdmissionSchedule patientAdmissionSchedule = (PatientAdmissionSchedule)
                abstractSolverScope.getWorkingSolution();
        initializeBedDesignationList(abstractSolverScope, patientAdmissionSchedule);
    }

    private void initializeBedDesignationList(AbstractSolverScope abstractSolverScope,
            PatientAdmissionSchedule patientAdmissionSchedule) {
        WorkingMemory workingMemory = abstractSolverScope.getWorkingMemory();
        List<BedDesignation> bedDesignationList = createBedDesignationList(patientAdmissionSchedule);
        Map<Bed, Set<Integer>> bedToTakenNightIndexSetMap = null;
        if (checkSameBedInSameNight) {
            bedToTakenNightIndexSetMap = new HashMap<Bed, Set<Integer>>(
                    patientAdmissionSchedule.getBedList().size());
        }
        // Assign one admissionPart at a time
        List<Bed> bedListInPriority = new ArrayList(patientAdmissionSchedule.getBedList());
        for (BedDesignation bedDesignation : bedDesignationList) {
            Score unscheduledScore = abstractSolverScope.calculateScoreFromWorkingMemory();
            int firstNightIndex = bedDesignation.getAdmissionPart().getFirstNight().getIndex();
            int lastNightIndex = bedDesignation.getAdmissionPart().getLastNight().getIndex();
            boolean perfectMatch = false;
            Score bestScore = DefaultHardAndSoftScore.valueOf(Integer.MIN_VALUE);
            Bed bestBed = null;

            FactHandle bedDesignationHandle = null;
            // Try every bed for that admissionPart
            // TODO by reordening the beds so index 0 has a different table then index 1 and so on,
            // this will probably be faster because perfectMatch will be true sooner
            for (Bed bed : bedListInPriority) {
                if (checkSameBedInSameNight) {
                    boolean taken = false;
                    Set<Integer> takenNightIndexSet = bedToTakenNightIndexSetMap.get(bed);
                    if (takenNightIndexSet != null) {
                        for (int i = firstNightIndex; i <= lastNightIndex; i++) {
                            if (takenNightIndexSet.contains(i)) {
                                taken = true;
                                break;
                            }
                        }
                    }
                    if (taken) {
                        continue;
                    }
                }
                bedDesignation.setBed(bed);
                if (bedDesignationHandle == null) {
                    bedDesignationHandle = workingMemory.insert(bedDesignation);
                } else {
                    workingMemory.update(bedDesignationHandle, bedDesignation);
                }
                Score score = abstractSolverScope.calculateScoreFromWorkingMemory();
                if (score.compareTo(unscheduledScore) < 0) {
                    if (score.compareTo(bestScore) > 0) {
                        bestScore = score;
                        bestBed = bed;
                    }
                } else if (score.equals(unscheduledScore)) {
                    perfectMatch = true;
                    bestScore = score;
                    bestBed = bed;
                    break;
                } else {
                    throw new IllegalStateException("The score (" + score
                            + ") cannot be higher than unscheduledScore (" + unscheduledScore + ").");
                }
                if (perfectMatch) {
                    break;
                }
            }
            if (bestBed == null) {
                if (checkSameBedInSameNight) {
                    throw new IllegalArgumentException(
                            "The initializer could not locate an allowed and empty bed for admissionPart ("
                                    + bedDesignation.getAdmissionPart() + ").");
                } else {
                    throw new IllegalArgumentException(
                            "The initializer could not locate an allowed bed for admissionPart ("
                                    + bedDesignation.getAdmissionPart() + ").");
                }
            }
            if (checkSameBedInSameNight) {
                Set<Integer> takenNightIndexSet = bedToTakenNightIndexSetMap.get(bestBed);
                if (takenNightIndexSet == null) {
                    takenNightIndexSet = new HashSet<Integer>(patientAdmissionSchedule.getNightList().size());
                    bedToTakenNightIndexSetMap.put(bestBed, takenNightIndexSet);
                }
                if (takenNightIndexSet != null) {
                    for (int i = firstNightIndex; i <= lastNightIndex; i++) {
                        boolean unique = takenNightIndexSet.add(i);
                        if (!unique) {
                            throw new IllegalStateException(
                                    "The takenNightIndexSet cannot possibly already have nightIndex (" + i + ").");
                        }
                    }
                }
            }
            if (!perfectMatch) {
                bedDesignation.setBed(bestBed);
                workingMemory.update(bedDesignationHandle, bedDesignation);
            }
            // put the occupied bed at the end of the list
            bedListInPriority.remove(bestBed);
            bedListInPriority.add(bestBed);
        }
        // For the GUI's combobox list mainly, not really needed
        Collections.sort(bedDesignationList, new PersistableIdComparator());
        patientAdmissionSchedule.setBedDesignationList(bedDesignationList);
    }

    private List<BedDesignation> createBedDesignationList(PatientAdmissionSchedule patientAdmissionSchedule) {
        List<BedDesignationInitializationWeight> initializationWeightList
                = new ArrayList<BedDesignationInitializationWeight>(
                patientAdmissionSchedule.getAdmissionPartList().size());
        for (AdmissionPart admissionPart : patientAdmissionSchedule.getAdmissionPartList()) {
            BedDesignation bedDesignation = new BedDesignation();
            bedDesignation.setId(admissionPart.getId());
            bedDesignation.setAdmissionPart(admissionPart);
            int disallowedCount = 0;
            for (Room room : patientAdmissionSchedule.getRoomList()) {
                disallowedCount += (room.getCapacity() * room.countDisallowedAdmissionPart(admissionPart));
            }
            initializationWeightList.add(new BedDesignationInitializationWeight(bedDesignation,
                    bedDesignation.getAdmissionPart().getNightCount(), disallowedCount));
        }
        Collections.sort(initializationWeightList);
        List<BedDesignation> bedDesignationList = new ArrayList<BedDesignation>(
                patientAdmissionSchedule.getAdmissionPartList().size());
        for (BedDesignationInitializationWeight bedDesignationInitializationWeight : initializationWeightList) {
            bedDesignationList.add(bedDesignationInitializationWeight.getBedDesignation());
        }
        return bedDesignationList;
    }

    private class BedDesignationInitializationWeight implements Comparable<BedDesignationInitializationWeight> {

        private BedDesignation bedDesignation;
        private int nightCount;
        private int disallowedCount;

        private BedDesignationInitializationWeight(BedDesignation bedDesignation, int nightCount, int disallowedCount) {
            this.bedDesignation = bedDesignation;
            this.nightCount = nightCount;
            this.disallowedCount = disallowedCount;
        }

        public BedDesignation getBedDesignation() {
            return bedDesignation;
        }

        public int compareTo(BedDesignationInitializationWeight other) {
            if (nightCount < other.nightCount) {
                return 1;
            } else if (nightCount > other.nightCount) {
                return -1;
            } else if (disallowedCount < other.disallowedCount) {
                return 1;
            } else if (disallowedCount > other.disallowedCount) {
                return -1;
            } else {
                return 0;
            }
        }

    }

}
