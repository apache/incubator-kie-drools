/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.tsp.swingui;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.Location;

public class TspPanel extends SolutionPanel<TspSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/tsp/swingui/tspLogo.png";

    private TspWorldPanel tspWorldPanel;
    private TspListPanel tspListPanel;

    private Long nextLocationId = null;

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
    public void resetPanel(TspSolution tspSolution) {
        tspWorldPanel.resetPanel(tspSolution);
        tspListPanel.resetPanel(tspSolution);
        resetNextLocationId();
    }

    private void resetNextLocationId() {
        long highestLocationId = 0L;
        for (Location location : getSolution().getLocationList()) {
            if (highestLocationId < location.getId().longValue()) {
                highestLocationId = location.getId();
            }
        }
        nextLocationId = highestLocationId + 1L;
    }

    @Override
    public void updatePanel(TspSolution tspSolution) {
        tspWorldPanel.updatePanel(tspSolution);
        tspListPanel.updatePanel(tspSolution);
    }

    public SolverAndPersistenceFrame getWorkflowFrame() {
        return solverAndPersistenceFrame;
    }

    public void insertLocationAndVisit(double longitude, double latitude) {
        final Location newLocation;
        switch (getSolution().getDistanceType()) {
            case AIR_DISTANCE:
                newLocation = new AirLocation();
                break;
            case ROAD_DISTANCE:
                logger.warn("Adding locations for a road distance dataset is not supported.");
                return;
            default:
                throw new IllegalStateException("The distanceType (" + getSolution().getDistanceType()
                        + ") is not implemented.");
        }
        newLocation.setId(nextLocationId);
        nextLocationId++;
        newLocation.setLongitude(longitude);
        newLocation.setLatitude(latitude);
        logger.info("Scheduling insertion of newLocation ({}).", newLocation);
        doProblemFactChange(scoreDirector -> {
            TspSolution tspSolution = scoreDirector.getWorkingSolution();
            scoreDirector.beforeProblemFactAdded(newLocation);
            tspSolution.getLocationList().add(newLocation);
            scoreDirector.afterProblemFactAdded(newLocation);
            Visit newVisit = new Visit();
            newVisit.setId(newLocation.getId());
            newVisit.setLocation(newLocation);
            scoreDirector.beforeEntityAdded(newVisit);
            tspSolution.getVisitList().add(newVisit);
            scoreDirector.afterEntityAdded(newVisit);
            scoreDirector.triggerVariableListeners();
        });
    }

    public void connectStandstills(Standstill sourceStandstill, Standstill targetStandstill) {
        if (targetStandstill instanceof Domicile) {
            TspSolution tspSolution = getSolution();
            Standstill lastStandstill = tspSolution.getDomicile();
            for (Visit nextVisit = findNextVisit(tspSolution, lastStandstill); nextVisit != null; nextVisit = findNextVisit(
                    tspSolution, lastStandstill)) {
                lastStandstill = nextVisit;
            }
            targetStandstill = sourceStandstill;
            sourceStandstill = lastStandstill;
        }
        if (targetStandstill instanceof Visit
                && (sourceStandstill instanceof Domicile || ((Visit) sourceStandstill).getPreviousStandstill() != null)) {
            solutionBusiness.doChangeMove((Visit) targetStandstill, "previousStandstill", sourceStandstill);
        }
        solverAndPersistenceFrame.resetScreen();
    }

    public Standstill findNearestStandstill(AirLocation clickLocation) {
        TspSolution tspSolution = getSolution();
        Standstill standstill = tspSolution.getDomicile();
        double minimumAirDistance = standstill.getLocation().getAirDistanceDoubleTo(clickLocation);
        for (Visit selectedVisit : tspSolution.getVisitList()) {
            double airDistance = selectedVisit.getLocation().getAirDistanceDoubleTo(clickLocation);
            if (airDistance < minimumAirDistance) {
                standstill = selectedVisit;
                minimumAirDistance = airDistance;
            }
        }
        return standstill;
    }

    private Visit findNextVisit(TspSolution tspSolution, Standstill standstill) {
        // Using an @InverseRelationShadowVariable on the model like in vehicle routing is far more efficient
        for (Visit visit : tspSolution.getVisitList()) {
            if (visit.getPreviousStandstill() == standstill) {
                return visit;
            }
        }
        return null;
    }

    public void doMove(Visit visit, Standstill toStandstill) {
        solutionBusiness.doChangeMove(visit, "previousStandstill", toStandstill);
    }

}
