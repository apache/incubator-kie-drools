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
import org.drools.planner.examples.tsp.domain.Depot;
import org.drools.planner.examples.tsp.domain.Journey;
import org.drools.planner.examples.tsp.domain.Terminal;
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
        WorkingMemory workingMemory = solutionDirector.getWorkingMemory();

        // TODO the planning entity list from the solution should be used and might already contain initialized entities
        List<Journey> journeyList = createJourneyList(travelingSalesmanTour);
        List<Depot> depotList = travelingSalesmanTour.getDepotList();
        List<Journey> initializedJourneyList = new ArrayList<Journey>(journeyList.size());
        List<Terminal> initializedTerminalList = new ArrayList<Terminal>(depotList.size() + journeyList.size());
        initializedTerminalList.addAll(depotList);
        for (Journey journey : journeyList) {
            FactHandle journeyHandle = workingMemory.insert(journey);
            Score bestScore = DefaultSimpleScore.valueOf(Integer.MIN_VALUE);
            Terminal bestPreviousTerminal = null;
            FactHandle bestPreviousTerminalFactHandle = null;
            for (Terminal previousTerminal : initializedTerminalList) {
                FactHandle afterJourneyFactHandle = workingMemory.getFactHandle(previousTerminal);
                // Do changes
                journey.setPreviousTerminal(previousTerminal);
                workingMemory.update(journeyHandle, journey);
                Journey chainedJourney = getChainedJourney(initializedJourneyList, previousTerminal);
                if (chainedJourney != null) {
                    chainedJourney.setPreviousTerminal(journey);
                    workingMemory.update(journeyHandle, chainedJourney);
                }
                // Calculate score
                Score score = solutionDirector.calculateScoreFromWorkingMemory();
                if (score.compareTo(bestScore) > 0) {
                    bestScore = score;
                    bestPreviousTerminal = previousTerminal;
                    bestPreviousTerminalFactHandle = afterJourneyFactHandle;
                }
                // Undo changes
                if (chainedJourney != null) {
                    chainedJourney.setPreviousTerminal(previousTerminal);
                    workingMemory.update(journeyHandle, chainedJourney);
                }
                journey.setPreviousTerminal(null);
                workingMemory.update(journeyHandle, journey);
            }
            if (bestPreviousTerminal == null) {
                throw new IllegalStateException("The bestPreviousJourney (" + bestPreviousTerminal
                        + ") cannot be null.");
            }
            journey.setPreviousTerminal(bestPreviousTerminal);
            workingMemory.update(journeyHandle, journey);
            Journey chainedJourney = getChainedJourney(initializedJourneyList, bestPreviousTerminal);
            if (chainedJourney != null) {
                chainedJourney.setPreviousTerminal(journey);
                workingMemory.update(journeyHandle, chainedJourney);
            }
            initializedJourneyList.add(journey);
            initializedTerminalList.add(journey);
            logger.debug("    Journey ({}) initialized.", journey);
        }
        Collections.sort(journeyList, new PersistableIdComparator());
        travelingSalesmanTour.setJourneyList(journeyList);
    }

    private Journey getChainedJourney(List<Journey> initializedJourneyList, Terminal previousTerminal) {
        for (Journey journey : initializedJourneyList) {
            if (journey.getPreviousTerminal() == previousTerminal) {
                return journey;
            }
        }
        return null;
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
