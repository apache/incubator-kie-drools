package org.optaplanner.examples.tsp.swingui;

import java.awt.BorderLayout;
import java.util.ArrayList;

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
            if (highestLocationId < location.getId()) {
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
                newLocation = new AirLocation(nextLocationId, latitude, longitude);
                break;
            case ROAD_DISTANCE:
                logger.warn("Adding locations for a road distance dataset is not supported.");
                return;
            default:
                throw new IllegalStateException("The distanceType (" + getSolution().getDistanceType()
                        + ") is not implemented.");
        }
        nextLocationId++;
        logger.info("Scheduling insertion of newLocation ({}).", newLocation);
        doProblemChange((tspSolution, problemChangeDirector) -> {
            // A SolutionCloner does not clone problem fact lists (such as locationList)
            // Shallow clone the locationList so only workingSolution is affected, not bestSolution or guiSolution
            tspSolution.setLocationList(new ArrayList<>(tspSolution.getLocationList()));
            // Add the problem fact itself
            problemChangeDirector.addProblemFact(newLocation, tspSolution.getLocationList()::add);

            Visit newVisit = new Visit(newLocation.getId(), newLocation);
            // A SolutionCloner clones planning entity lists (such as visitList), so no need to clone the visitList here
            // Add the planning entity itself
            problemChangeDirector.addEntity(newVisit, tspSolution.getVisitList()::add);
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
            changePreviousStandstill((Visit) targetStandstill, sourceStandstill);
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

    private static Visit findNextVisit(TspSolution tspSolution, Standstill standstill) {
        // Using an @InverseRelationShadowVariable on the model like in vehicle routing is far more efficient
        for (Visit visit : tspSolution.getVisitList()) {
            if (visit.getPreviousStandstill() == standstill) {
                return visit;
            }
        }
        return null;
    }

    public void changePreviousStandstill(Visit visit, Standstill toStandstill) {
        doProblemChange((workingSolution, problemChangeDirector) -> problemChangeDirector.lookUpWorkingObject(visit)
                .ifPresentOrElse(workingVisit -> {
                    Standstill workingToStandstill = problemChangeDirector.lookUpWorkingObjectOrFail(toStandstill);
                    Visit oldNextVisit = findNextVisit(workingSolution, workingVisit);
                    Visit newNextVisit = findNextVisit(workingSolution, workingToStandstill);
                    Standstill oldPreviousStandstill = workingVisit.getPreviousStandstill();

                    // Close the old chain
                    if (oldNextVisit != null) {
                        problemChangeDirector.changeVariable(
                                oldNextVisit, "previousStandstill", v -> v.setPreviousStandstill(oldPreviousStandstill));
                    }

                    // Change the entity
                    problemChangeDirector.changeVariable(
                            workingVisit, "previousStandstill", v -> v.setPreviousStandstill(workingToStandstill));

                    // Reroute the new chain
                    if (newNextVisit != null) {
                        problemChangeDirector.changeVariable(
                                newNextVisit, "previousStandstill", v -> v.setPreviousStandstill(workingVisit));
                    }
                }, () -> logger.info("Skipping problem change due to visit ({}) deleted.", visit)));
    }

}
