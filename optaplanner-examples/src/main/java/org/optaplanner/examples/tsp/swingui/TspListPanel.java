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

package org.optaplanner.examples.tsp.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import org.optaplanner.examples.common.swingui.TangoColorFactory;
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

    public void resetPanel(TravelingSalesmanTour travelingSalesmanTour) {
        removeAll();
        JLabel headerLabel = new JLabel("Tour of " + travelingSalesmanTour.getName());
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        headerLabel.setBackground(HEADER_COLOR);
        headerLabel.setOpaque(true);
        add(headerLabel);
        Domicile domicile = travelingSalesmanTour.getDomicile();
        add(new JLabel(domicile.getLocation().toString()));
        if (travelingSalesmanTour.getVisitList().size() > 1000) {
            JLabel tooBigLabel = new JLabel("The dataset is too big to show.");
            add(tooBigLabel);
            return;
        }
        for (Visit visit : travelingSalesmanTour.getVisitList()) {
            JPanel visitPanel = new JPanel(new GridLayout(1, 2));
            JButton button = new JButton(new VisitAction(visit));
            visitPanel.add(button);
            String distanceLabelString;
            if (visit.getPreviousStandstill() == null) {
                distanceLabelString = "Unassigned";
            } else {
                distanceLabelString = "After " + visit.getPreviousStandstill().getLocation()
                        + " with distance " + visit.getDistanceFromPreviousStandstill();
            }
            visitPanel.add(new JLabel(distanceLabelString));
            add(visitPanel);
        }
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
