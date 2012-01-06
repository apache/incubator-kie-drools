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
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.tsp.domain.Journey;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;
import org.drools.planner.examples.tsp.solver.move.SubTourChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class TspListPanel extends JPanel {

    private static final Color HEADER_COLOR = Color.YELLOW;

    private final TspPanel tspPanel;

    public TspListPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
        setLayout(new GridLayout(0, 1));
    }

    public void resetPanel(Solution solution) {
        removeAll();
        TravelingSalesmanTour travelingSalesmanTour = (TravelingSalesmanTour) solution;
        JLabel headerLabel = new JLabel("Tour of " + travelingSalesmanTour.getName());
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        headerLabel.setBackground(HEADER_COLOR);
        headerLabel.setOpaque(true);
        add(headerLabel);
        for (Journey journey : travelingSalesmanTour.getJourneyList()) {
            JPanel journeyPanel = new JPanel(new GridLayout(1, 2));
            JButton button = new JButton(new JourneyAction(journey));
            journeyPanel.add(button);
            String distanceLabelString;
            if (journey.getNextJourney() == null) {
                distanceLabelString = "Unassigned";
            } else {
                distanceLabelString = "Distance to next: "
                        + journey.getDistanceToNextJourney();
            }
            journeyPanel.add(new JLabel(distanceLabelString));
            add(journeyPanel);
        }
    }

    private class JourneyAction extends AbstractAction {

        private Journey journey;

        public JourneyAction(Journey journey) {
            super(journey.getCity().toString());
            this.journey = journey;
        }

        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<Journey> afterJourneyList = tspPanel.getTravelingSalesmanTour().getJourneyList();
            JComboBox afterJourneyListField = new JComboBox(afterJourneyList.toArray());
            afterJourneyListField.setSelectedItem(journey.getNextJourney());
            listFieldsPanel.add(afterJourneyListField);
            int result = JOptionPane.showConfirmDialog(TspListPanel.this.getRootPane(), listFieldsPanel,
                    "Select to move after city", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Journey toAfterJourney = (Journey) afterJourneyListField.getSelectedItem();
                tspPanel.doMove(new SubTourChangeMove(journey, journey, toAfterJourney));
                tspPanel.getWorkflowFrame().resetScreen();
            }
        }

    }

}
