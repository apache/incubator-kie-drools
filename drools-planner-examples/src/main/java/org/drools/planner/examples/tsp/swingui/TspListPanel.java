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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.tsp.domain.Appearance;
import org.drools.planner.examples.tsp.domain.Domicile;
import org.drools.planner.examples.tsp.domain.Visit;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;

/**
 * TODO this code is highly unoptimized
 */
public class TspListPanel extends JPanel {

    private static final Color HEADER_COLOR = TangoColors.BUTTER_1;

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
        for (Domicile domicile : travelingSalesmanTour.getDomicileList()) {
            JLabel domicileLabel = new JLabel(domicile.getCity().getSafeName());
            add(domicileLabel);
        }
        for (Visit visit : travelingSalesmanTour.getVisitList()) {
            JPanel visitPanel = new JPanel(new GridLayout(1, 2));
            JButton button = new JButton(new VisitAction(visit));
            visitPanel.add(button);
            String distanceLabelString;
            if (visit.getPreviousAppearance() == null) {
                distanceLabelString = "Unassigned";
            } else {
                distanceLabelString = "After " + visit.getPreviousAppearance().getCity().getSafeName()
                        + " with distance " + visit.getDistanceToPreviousAppearance();
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
            super(visit.getCity().getSafeName());
            this.visit = visit;
        }

        public void actionPerformed(ActionEvent e) {
            TravelingSalesmanTour travelingSalesmanTour = tspPanel.getTravelingSalesmanTour();
            JComboBox previousAppearanceListField = new JComboBox();
            for (Appearance previousAppearance : travelingSalesmanTour.getVisitList()) {
                previousAppearanceListField.addItem(previousAppearance);
            }
            for (Appearance previousAppearance : travelingSalesmanTour.getDomicileList()) {
                previousAppearanceListField.addItem(previousAppearance);
            }
            previousAppearanceListField.setSelectedItem(visit.getPreviousAppearance());
            int result = JOptionPane.showConfirmDialog(TspListPanel.this.getRootPane(), previousAppearanceListField,
                    "Visit " + visit.getCity().getSafeName() + " after", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Appearance toAppearance = (Appearance) previousAppearanceListField.getSelectedItem();
//                tspPanel.doMove(visit, toAppearance);
                JOptionPane.showMessageDialog(TspListPanel.this, "Unsupported operation."); // TODO FIXME
                tspPanel.getWorkflowFrame().resetScreen();
            }
        }

    }

}
