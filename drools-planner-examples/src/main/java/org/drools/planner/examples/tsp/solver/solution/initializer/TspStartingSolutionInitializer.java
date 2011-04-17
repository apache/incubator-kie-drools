/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.tsp.solver.solution.initializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.score.DefaultSimpleDoubleScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.initializer.AbstractStartingSolutionInitializer;
import org.drools.planner.core.solver.AbstractSolverScope;
import org.drools.planner.examples.common.domain.PersistableIdComparator;
import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.CityAssignment;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;

public class TspStartingSolutionInitializer extends AbstractStartingSolutionInitializer {

    @Override
    public boolean isSolutionInitialized(AbstractSolverScope abstractSolverScope) {
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) abstractSolverScope.getWorkingSolution();
        return travelingSalesmanTour.isInitialized();
    }

    public void initializeSolution(AbstractSolverScope abstractSolverScope) {
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) abstractSolverScope.getWorkingSolution();
        initializeCityAssignmentList(abstractSolverScope, travelingSalesmanTour);
    }

    private void initializeCityAssignmentList(AbstractSolverScope abstractSolverScope,
            TravelingSalesmanTour travelingSalesmanTour) {
        City startCity = travelingSalesmanTour.getStartCity();
        WorkingMemory workingMemory = abstractSolverScope.getWorkingMemory();

        List<CityAssignment> cityAssignmentList = createCityAssignmentList(travelingSalesmanTour);
        List<CityAssignment> assignedCityAssignmentList = null;
        for (CityAssignment cityAssignment : cityAssignmentList) {
            FactHandle cityAssignmentHandle = null;
            if (assignedCityAssignmentList == null) {
                assignedCityAssignmentList = new ArrayList<CityAssignment>(cityAssignmentList.size());
                cityAssignment.setNextCityAssignment(cityAssignment);
                cityAssignment.setPreviousCityAssignment(cityAssignment);
                cityAssignmentHandle = workingMemory.insert(cityAssignment);
            } else {
                Score bestScore = DefaultSimpleDoubleScore.valueOf(-Double.MAX_VALUE);
                CityAssignment bestAfterCityAssignment = null;
                FactHandle bestAfterCityAssignmentFactHandle = null;
                CityAssignment bestBeforeCityAssignment = null;
                FactHandle bestBeforeCityAssignmentFactHandle = null;
                for (CityAssignment afterCityAssignment : assignedCityAssignmentList) {
                    CityAssignment beforeCityAssignment = afterCityAssignment.getNextCityAssignment();
                    FactHandle afterCityAssignmentFactHandle = workingMemory.getFactHandle(afterCityAssignment);
                    FactHandle beforeCityAssignmentFactHandle = workingMemory.getFactHandle(beforeCityAssignment);
                    // Do changes
                    afterCityAssignment.setNextCityAssignment(cityAssignment);
                    cityAssignment.setPreviousCityAssignment(afterCityAssignment);
                    cityAssignment.setNextCityAssignment(beforeCityAssignment);
                    beforeCityAssignment.setPreviousCityAssignment(cityAssignment);
                    if (cityAssignmentHandle == null) {
                        cityAssignmentHandle = workingMemory.insert(cityAssignment);
                    } else {
                        workingMemory.update(cityAssignmentHandle, cityAssignment);
                    }
                    workingMemory.update(afterCityAssignmentFactHandle, afterCityAssignment);
                    workingMemory.update(beforeCityAssignmentFactHandle, beforeCityAssignment);
                    // Calculate score
                    Score score = abstractSolverScope.calculateScoreFromWorkingMemory();
                    if (score.compareTo(bestScore) > 0) {
                        bestScore = score;
                        bestAfterCityAssignment = afterCityAssignment;
                        bestAfterCityAssignmentFactHandle = afterCityAssignmentFactHandle;
                        bestBeforeCityAssignment = beforeCityAssignment;
                        bestBeforeCityAssignmentFactHandle = beforeCityAssignmentFactHandle;
                    }
                    // Undo changes
                    afterCityAssignment.setNextCityAssignment(beforeCityAssignment);
                    beforeCityAssignment.setPreviousCityAssignment(afterCityAssignment);
                    workingMemory.update(afterCityAssignmentFactHandle, afterCityAssignment);
                    workingMemory.update(beforeCityAssignmentFactHandle, beforeCityAssignment);
                }
                if (bestAfterCityAssignment == null) {
                    throw new IllegalStateException("The bestAfterCityAssignment (" + bestAfterCityAssignment
                            + ") cannot be null.");
                }
                bestAfterCityAssignment.setNextCityAssignment(cityAssignment);
                cityAssignment.setPreviousCityAssignment(bestAfterCityAssignment);
                cityAssignment.setNextCityAssignment(bestBeforeCityAssignment);
                bestBeforeCityAssignment.setPreviousCityAssignment(cityAssignment);
                workingMemory.update(cityAssignmentHandle, cityAssignment);
                workingMemory.update(bestAfterCityAssignmentFactHandle, bestAfterCityAssignment);
                workingMemory.update(bestBeforeCityAssignmentFactHandle, bestBeforeCityAssignment);
            }
            assignedCityAssignmentList.add(cityAssignment);
            if (cityAssignment.getCity() == startCity) {
                travelingSalesmanTour.setStartCityAssignment(cityAssignment);
            }
            logger.debug("    CityAssignment ({}) initialized for starting solution.", cityAssignment);
        }
        Collections.sort(cityAssignmentList, new PersistableIdComparator());
        travelingSalesmanTour.setCityAssignmentList(cityAssignmentList);
    }

    public List<CityAssignment> createCityAssignmentList(TravelingSalesmanTour travelingSalesmanTour) {
        List<City> cityList = travelingSalesmanTour.getCityList();
        // TODO weight: create by city on distance from the center ascending
        List<CityAssignment> cityAssignmentList = new ArrayList<CityAssignment>(cityList.size());
        int cityAssignmentId = 0;
        for (City city : cityList) {
            CityAssignment cityAssignment = new CityAssignment();
            cityAssignment.setId((long) cityAssignmentId);
            cityAssignment.setCity(city);
            cityAssignmentList.add(cityAssignment);
            cityAssignmentId++;
        }
        return cityAssignmentList;
    }

}
