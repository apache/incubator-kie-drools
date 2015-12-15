/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.swing.impl.TangoColorFactory;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;

public class TspListPanel extends JPanel implements Scrollable {

    public static final Dimension PREFERRED_SCROLLABLE_VIEWPORT_SIZE = new Dimension(800, 600);

    private static final Color HEADER_COLOR = TangoColorFactory.BUTTER_1;

    private final TspPanel tspPanel;

    public TspListPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
        setLayout(new GridLayout(0, 1));
    }

    public void resetPanel(TravelingSalesmanTour tour) {
        removeAll();
        if (tour.getVisitList().size() > 1000) {
            JLabel tooBigLabel = new JLabel("The dataset is too big to show.");
            add(tooBigLabel);
            return;
        }
        Domicile domicile = tour.getDomicile();
        add(new JLabel(domicile.getLocation().toString()));
        // TODO If the model contains the nextVisit like in vehicle routing, use that instead
        Map<Standstill, Visit> nextVisitMap = new LinkedHashMap<Standstill, Visit>();
        List<Visit> unassignedVisitList = new ArrayList<Visit>();
        for (Visit visit : tour.getVisitList()) {
            if (visit.getPreviousStandstill() != null) {
                nextVisitMap.put(visit.getPreviousStandstill(), visit);
            } else {
                unassignedVisitList.add(visit);
            }
        }
        Visit lastVisit = null;
        for (Visit visit = nextVisitMap.get(domicile); visit != null; visit = nextVisitMap.get(visit)) {
            addVisitButton(tour, visit);
            lastVisit = visit;
        }
        if (lastVisit != null) {
            JPanel backToDomicilePanel = new JPanel(new GridLayout(1, 2));
            backToDomicilePanel.add(new JLabel("Back to " + domicile.getLocation()));
            JLabel distanceLabel = new JLabel(
                    lastVisit.getDistanceTo(domicile) + " " + tour.getDistanceUnitOfMeasurement());
            distanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            backToDomicilePanel.add(distanceLabel);
            add(backToDomicilePanel);
        }
        add(new JLabel("Unassigned"));
        for (Visit visit : unassignedVisitList) {
            addVisitButton(tour, visit);
        }
    }

    protected void addVisitButton(TravelingSalesmanTour tour, Visit visit) {
        JPanel visitPanel = new JPanel(new GridLayout(1, 2));
        JButton button = new JButton(new VisitAction(visit));
        visitPanel.add(button);
        String distanceLabelString;
        if (visit.getPreviousStandstill() == null) {
            distanceLabelString = "Unassigned";
        } else {
            distanceLabelString = visit.getDistanceFromPreviousStandstill() + " " + tour.getDistanceUnitOfMeasurement();
        }
        JLabel distanceLabel = new JLabel(distanceLabelString);
        distanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        visitPanel.add(distanceLabel);
        add(visitPanel);
    }

    public void updatePanel(TravelingSalesmanTour travelingSalesmanTour) {
        resetPanel(travelingSalesmanTour);
    }

    private class VisitAction extends AbstractAction {

        private Visit visit;

        public VisitAction(Visit visit) {
            super(visit.getLocation().toString());
            this.visit = visit;
        }

        public void actionPerformed(ActionEvent e) {
            TravelingSalesmanTour travelingSalesmanTour = tspPanel.getTravelingSalesmanTour();
            JComboBox previousStandstillListField = new JComboBox();
            for (Standstill previousStandstill : travelingSalesmanTour.getVisitList()) {
                previousStandstillListField.addItem(previousStandstill);
            }
            previousStandstillListField.addItem(travelingSalesmanTour.getDomicile());
            previousStandstillListField.setSelectedItem(visit.getPreviousStandstill());
            int result = JOptionPane.showConfirmDialog(TspListPanel.this.getRootPane(), previousStandstillListField,
                    "Visit " + visit.getLocation() + " after", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Standstill toStandstill = (Standstill) previousStandstillListField.getSelectedItem();
                tspPanel.doMove(visit, toStandstill);
                tspPanel.getWorkflowFrame().resetScreen();
            }
        }

    }

    public Dimension getPreferredScrollableViewportSize() {
        return PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
        }
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return false;
    }

}
