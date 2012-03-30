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

package org.drools.planner.examples.tsp.swingui;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.common.swingui.SolverAndPersistenceFrame;
import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.Visit;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;

/**
 * TODO this code is highly unoptimized
 */
public class TspPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/drools/planner/examples/tsp/swingui/tspLogo.png";

    private TspWorldPanel tspWorldPanel;
    private TspListPanel tspListPanel;

    private Long nextCityId = null;

    public TspPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tspWorldPanel = new TspWorldPanel(this);
        tspWorldPanel.setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
        tabbedPane.add("World", tspWorldPanel);
        tspListPanel = new TspListPanel(this);
        JScrollPane tspListScrollPane = new JScrollPane(tspListPanel);
        tabbedPane.add("List", tspListScrollPane);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public TravelingSalesmanTour getTravelingSalesmanTour() {
        return (TravelingSalesmanTour) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) solution;
        tspWorldPanel.resetPanel(travelingSalesmanTour);
        tspListPanel.resetPanel(travelingSalesmanTour);
        resetNextCityId();
    }

    private void resetNextCityId() {
        long highestCityId = 0L;
        for (City city : getTravelingSalesmanTour().getCityList()) {
            if (highestCityId < city.getId().longValue()) {
                highestCityId = city.getId();
            }
        }
        nextCityId = highestCityId + 1L;
    }

    @Override
    public void updatePanel(Solution solution) {
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) solution;
        tspWorldPanel.updatePanel(travelingSalesmanTour);
        tspListPanel.updatePanel(travelingSalesmanTour);
    }

    public void doMove(Move move) {
        solutionBusiness.doMove(move);
    }

    public SolverAndPersistenceFrame getWorkflowFrame() {
        return solverAndPersistenceFrame;
    }

    public void insertCityAndVisit(double longitude, double latitude) {
        final City newCity = new City();
        newCity.setId(nextCityId);
        nextCityId++;
        newCity.setLongitude(longitude);
        newCity.setLatitude(latitude);
        logger.info("Scheduling insertion of newCity ({}).", newCity);
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                TravelingSalesmanTour solution = (TravelingSalesmanTour) scoreDirector.getWorkingSolution();
                scoreDirector.beforeProblemFactAdded(newCity);
                solution.getCityList().add(newCity);
                scoreDirector.afterProblemFactAdded(newCity);
                Visit newVisit = new Visit();
                newVisit.setId(newCity.getId());
                newVisit.setCity(newCity);
                scoreDirector.beforeEntityAdded(newVisit);
                solution.getVisitList().add(newVisit);
                scoreDirector.afterEntityAdded(newVisit);
            }
        });
        updatePanel(solutionBusiness.getSolution());
    }

}
