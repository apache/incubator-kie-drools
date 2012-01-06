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
import org.drools.planner.core.phase.custom.CustomSolverPhaseCommand;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.director.SolutionDirector;
import org.drools.planner.examples.common.domain.PersistableIdComparator;
import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.Journey;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TspSolutionInitializer implements CustomSolverPhaseCommand {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public void changeWorkingSolution(SolutionDirector solutionDirector) {
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) solutionDirector.getWorkingSolution();
        initializeJourneyList(solutionDirector, travelingSalesmanTour);
    }

    private void initializeJourneyList(SolutionDirector solutionDirector,
            TravelingSalesmanTour travelingSalesmanTour) {
        City startCity = travelingSalesmanTour.getStartCity();
        WorkingMemory workingMemory = solutionDirector.getWorkingMemory();

        // TODO the planning entity list from the solution should be used and might already contain initialized entities
        List<Journey> journeyList = createJourneyList(travelingSalesmanTour);
        List<Journey> assignedJourneyList = null;
        for (Journey journey : journeyList) {
            FactHandle journeyHandle = null;
            if (assignedJourneyList == null) {
                assignedJourneyList = new ArrayList<Journey>(journeyList.size());
                journey.setNextJourney(journey);
                journey.setPreviousJourney(journey);
                journeyHandle = workingMemory.insert(journey);
            } else {
                Score bestScore = DefaultSimpleScore.valueOf(Integer.MIN_VALUE);
                Journey bestAfterJourney = null;
                FactHandle bestAfterJourneyFactHandle = null;
                Journey bestBeforeJourney = null;
                FactHandle bestBeforeJourneyFactHandle = null;
                for (Journey afterJourney : assignedJourneyList) {
                    Journey beforeJourney = afterJourney.getNextJourney();
                    FactHandle afterJourneyFactHandle = workingMemory.getFactHandle(afterJourney);
                    FactHandle beforeJourneyFactHandle = workingMemory.getFactHandle(beforeJourney);
                    // Do changes
                    afterJourney.setNextJourney(journey);
                    journey.setPreviousJourney(afterJourney);
                    journey.setNextJourney(beforeJourney);
                    beforeJourney.setPreviousJourney(journey);
                    if (journeyHandle == null) {
                        journeyHandle = workingMemory.insert(journey);
                    } else {
                        workingMemory.update(journeyHandle, journey);
                    }
                    workingMemory.update(afterJourneyFactHandle, afterJourney);
                    workingMemory.update(beforeJourneyFactHandle, beforeJourney);
                    // Calculate score
                    Score score = solutionDirector.calculateScoreFromWorkingMemory();
                    if (score.compareTo(bestScore) > 0) {
                        bestScore = score;
                        bestAfterJourney = afterJourney;
                        bestAfterJourneyFactHandle = afterJourneyFactHandle;
                        bestBeforeJourney = beforeJourney;
                        bestBeforeJourneyFactHandle = beforeJourneyFactHandle;
                    }
                    // Undo changes
                    afterJourney.setNextJourney(beforeJourney);
                    beforeJourney.setPreviousJourney(afterJourney);
                    workingMemory.update(afterJourneyFactHandle, afterJourney);
                    workingMemory.update(beforeJourneyFactHandle, beforeJourney);
                }
                if (bestAfterJourney == null) {
                    throw new IllegalStateException("The bestAfterJourney (" + bestAfterJourney
                            + ") cannot be null.");
                }
                bestAfterJourney.setNextJourney(journey);
                journey.setPreviousJourney(bestAfterJourney);
                journey.setNextJourney(bestBeforeJourney);
                bestBeforeJourney.setPreviousJourney(journey);
                workingMemory.update(journeyHandle, journey);
                workingMemory.update(bestAfterJourneyFactHandle, bestAfterJourney);
                workingMemory.update(bestBeforeJourneyFactHandle, bestBeforeJourney);
            }
            assignedJourneyList.add(journey);
            if (journey.getCity() == startCity) {
                travelingSalesmanTour.setStartJourney(journey);
            }
            logger.debug("    Journey ({}) initialized.", journey);
        }
        Collections.sort(journeyList, new PersistableIdComparator());
        travelingSalesmanTour.setJourneyList(journeyList);
    }

    public List<Journey> createJourneyList(TravelingSalesmanTour travelingSalesmanTour) {
        List<City> cityList = travelingSalesmanTour.getCityList();
        // TODO weight: create by city on distance from the center ascending
        List<Journey> journeyList = new ArrayList<Journey>(cityList.size());
        int journeyId = 0;
        for (City city : cityList) {
            Journey journey = new Journey();
            journey.setId((long) journeyId);
            journey.setCity(city);
            journeyList.add(journey);
            journeyId++;
        }
        return journeyList;
    }

}
