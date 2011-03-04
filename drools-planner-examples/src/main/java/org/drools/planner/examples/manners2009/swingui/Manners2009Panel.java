/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.manners2009.swingui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.manners2009.domain.HobbyPractician;
import org.drools.planner.examples.manners2009.domain.Manners2009;
import org.drools.planner.examples.manners2009.domain.Seat;
import org.drools.planner.examples.manners2009.domain.SeatDesignation;
import org.drools.planner.examples.manners2009.domain.Table;
import org.drools.planner.examples.manners2009.solver.move.SeatDesignationSwitchMove;

/**
 * TODO this code is highly unoptimized
 */
public class Manners2009Panel extends SolutionPanel {

    private GridLayout gridLayout;

    public Manners2009Panel() {
        gridLayout = new GridLayout(0, 1);
        setLayout(gridLayout);
    }

    private Manners2009 getManners2009() {
        return (Manners2009) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        removeAll();
        Manners2009 manners2009 = getManners2009();
        gridLayout.setColumns((int) Math.ceil(Math.sqrt(manners2009.getTableList().size())));
        Map<Table, JPanel> tablePanelMap = new HashMap<Table, JPanel>(manners2009.getTableList().size());
        Map<Seat, SeatPanel> seatPanelMap = new HashMap<Seat, SeatPanel>(manners2009.getSeatList().size());
        for (Table table : manners2009.getTableList()) {
            // Formula: 4(columns - 1) = tableSize
            int edgeLength = (int) Math.ceil(((double) (table.getSeatList().size() + 4)) / 4.0);
            JPanel tablePanel = new JPanel(new GridLayout(0, edgeLength));
            tablePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createTitledBorder("Table " + table.getTableIndex())
            ));
            add(tablePanel);
            tablePanelMap.put(table, tablePanel);
            for (int y = 0; y < edgeLength; y++) {
                for (int x = 0; x < edgeLength; x++) {
                    int index;
                    if (y == 0) {
                        index = x;
                    } else if (x == (edgeLength - 1)) {
                        index = (edgeLength - 1) + y;
                    } else if (y == (edgeLength - 1)) {
                        index = 2 * (edgeLength - 1) + (edgeLength - 1 - x);
                    } else if (x == 0) {
                        index = 3 * (edgeLength - 1) + (edgeLength - 1 - y);
                    } else {
                        index = Integer.MAX_VALUE;
                    }
                    if (index < table.getSeatList().size()) {
                        Seat seat = table.getSeatList().get(index);
                        SeatPanel seatPanel = new SeatPanel(seat);
                        tablePanel.add(seatPanel);
                        seatPanelMap.put(seat, seatPanel);
                    } else {
                        tablePanel.add(new JPanel());
                    }
                }
            }
        }
        if (manners2009.isInitialized()) {
            for (SeatDesignation seatDesignation : manners2009.getSeatDesignationList()) {
                SeatPanel seatPanel = seatPanelMap.get(seatDesignation.getSeat());
                seatPanel.addSeatDesignation(seatDesignation);
            }
        }
    }

    private class SeatPanel extends JPanel {

        public SeatPanel(Seat seat) {
            super(new GridLayout(0, 1));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            JLabel seatLabel = new JLabel("Seat " + seat.getSeatIndexInTable(), SwingConstants.CENTER);
            add(seatLabel);
        }

        public void addSeatDesignation(SeatDesignation seatDesignation) {
            JButton button = new JButton(new SeatDesignationAction(seatDesignation));
            add(button);
            add(new JLabel(seatDesignation.getGuest().getJob().getJobType().getCode() + ": " + seatDesignation.getGuest().getJob().getName(), SwingConstants.CENTER));
            StringBuilder hobbyString = new StringBuilder();
            for (HobbyPractician hobbyPractician : seatDesignation.getGuest().getHobbyPracticianList()) {
                if (hobbyString.length() > 0) {
                    hobbyString.append(", ");
                }
                hobbyString.append(hobbyPractician.getHobby().toString());
            }
            add(new JLabel(hobbyString.toString(), SwingConstants.CENTER));
        }

    }

    private class SeatDesignationAction extends AbstractAction {

        private SeatDesignation seatDesignation;

        public SeatDesignationAction(SeatDesignation seatDesignation) {
            super(seatDesignation.getGuest().getCode() + "(" + seatDesignation.getGuest().getGender().getCode() + ")");
            this.seatDesignation = seatDesignation;
        }

        public void actionPerformed(ActionEvent e) {
            List<SeatDesignation> seatDesignationList = getManners2009().getSeatDesignationList();
            JComboBox seatDesignationListField = new JComboBox(seatDesignationList.toArray());
            seatDesignationListField.setSelectedItem(seatDesignation);
            int result = JOptionPane.showConfirmDialog(Manners2009Panel.this.getRootPane(), seatDesignationListField,
                    "Select seat designation to switch with", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                SeatDesignation switchSeatDesignation = (SeatDesignation) seatDesignationListField.getSelectedItem();
                solutionBusiness.doMove(new SeatDesignationSwitchMove(seatDesignation, switchSeatDesignation));
                workflowFrame.updateScreen();
            }
        }

    }

}
