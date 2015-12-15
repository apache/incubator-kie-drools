/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.dinnerparty.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.swing.impl.TangoColorFactory;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.dinnerparty.domain.DinnerParty;
import org.optaplanner.examples.dinnerparty.domain.Gender;
import org.optaplanner.examples.dinnerparty.domain.Guest;
import org.optaplanner.examples.dinnerparty.domain.Hobby;
import org.optaplanner.examples.dinnerparty.domain.HobbyPractician;
import org.optaplanner.examples.dinnerparty.domain.Seat;
import org.optaplanner.examples.dinnerparty.domain.SeatDesignation;
import org.optaplanner.examples.dinnerparty.domain.Table;

public class DinnerPartyPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/dinnerparty/swingui/dinnerPartyLogo.png";
    public static final int MALE_FEMALE_ICON_VARIATION = 5;

    private GridLayout gridLayout;
    private Map<Hobby, ImageIcon> hobbyImageIconMap;
    private List<ImageIcon> maleImageIconList;
    private List<ImageIcon> femaleImageIconList;

    public DinnerPartyPanel() {
        gridLayout = new GridLayout(0, 1);
        setLayout(gridLayout);
        Hobby[] hobbies = Hobby.values();
        hobbyImageIconMap = new HashMap<Hobby, ImageIcon>(hobbies.length);
        for (Hobby hobby : hobbies) {
            String imageIconFilename;
            switch (hobby) {
                case TENNIS:
                    imageIconFilename = "hobbyTennis.png";
                    break;
                case GOLF:
                    imageIconFilename = "hobbyGolf.png";
                    break;
                case MOTORCYCLES:
                    imageIconFilename = "hobbyMotorcycles.png";
                    break;
                case CHESS:
                    imageIconFilename = "hobbyChess.png";
                    break;
                case POKER:
                    imageIconFilename = "hobbyPoker.png";
                    break;
                default:
                    throw new IllegalArgumentException("The hobby (" + hobby + ") is not supported.");
            }
            hobbyImageIconMap.put(hobby, new ImageIcon(getClass().getResource(imageIconFilename)));
        }
        maleImageIconList = new ArrayList<ImageIcon>(MALE_FEMALE_ICON_VARIATION);
        femaleImageIconList = new ArrayList<ImageIcon>(MALE_FEMALE_ICON_VARIATION);
        for (int i = 0; i < MALE_FEMALE_ICON_VARIATION; i++) {
             maleImageIconList.add(new ImageIcon(getClass().getResource("guestMale" + i + ".png")));
             femaleImageIconList.add(new ImageIcon(getClass().getResource("guestFemale" + i + ".png")));
        }
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private DinnerParty getDinnerParty() {
        return (DinnerParty) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        removeAll();
        DinnerParty dinnerParty = (DinnerParty) solution;
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        gridLayout.setColumns((int) Math.ceil(Math.sqrt(dinnerParty.getTableList().size())));
        Map<Seat, SeatPanel> seatPanelMap = new HashMap<Seat, SeatPanel>(dinnerParty.getSeatList().size());
        SeatPanel unassignedPanel = new SeatPanel(null);
        seatPanelMap.put(null, unassignedPanel);
        for (Table table : dinnerParty.getTableList()) {
            // Formula: 4(columns - 1) = tableSize
            int edgeLength = (int) Math.ceil(((double) (table.getSeatList().size() + 4)) / 4.0);
            JPanel tablePanel = new JPanel(new GridLayout(0, edgeLength));
            tablePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createTitledBorder("Table " + table.getTableIndex())
            ));
            add(tablePanel);
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
        for (SeatDesignation seatDesignation : dinnerParty.getSeatDesignationList()) {
            SeatPanel seatPanel = seatPanelMap.get(seatDesignation.getSeat());
            seatPanel.setBackground(tangoColorFactory.pickColor(seatDesignation.getGuestJobType()));
            seatPanel.setSeatDesignation(seatDesignation);
        }
    }

    private class SeatPanel extends JPanel {

        public SeatPanel(Seat seat) {
            setLayout(new BorderLayout(5, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            SeatDesignation dummySeatDesignation = new SeatDesignation();
            dummySeatDesignation.setGuest(null);
            dummySeatDesignation.setSeat(seat);
            setSeatDesignation(dummySeatDesignation);
        }

        public void setSeatDesignation(SeatDesignation seatDesignation) {
            removeAll();
            Guest guest = seatDesignation.getGuest();
            if (guest == null) {
                add(new JLabel("Empty seat"), BorderLayout.CENTER);
                return;
            }
            JButton button = SwingUtils.makeSmallButton(new JButton(new SeatDesignationAction(seatDesignation)));
            add(button, BorderLayout.WEST);
            JPanel infoPanel = new JPanel(new GridLayout(0, 1));
            infoPanel.setOpaque(false);
            infoPanel.add(new JLabel(guest.getName()));
            JPanel jobPanel = new JPanel();
            jobPanel.setLayout(new BoxLayout(jobPanel, BoxLayout.Y_AXIS));
            jobPanel.setOpaque(false);
            jobPanel.add(new JLabel(guest.getJob().getJobType().getCode()));
            JLabel jobLabel = new JLabel("  " + guest.getJob().getName());
            jobLabel.setFont(jobLabel.getFont().deriveFont(jobLabel.getFont().getSize() - 2.0F));
            jobPanel.add(jobLabel);
            infoPanel.add(jobPanel);
            add(infoPanel, BorderLayout.CENTER);
            JPanel hobbyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            hobbyPanel.setOpaque(false);
            hobbyPanel.setAlignmentX(CENTER_ALIGNMENT);
            for (HobbyPractician hobbyPractician : guest.getHobbyPracticianList()) {
                Hobby hobby = hobbyPractician.getHobby();
                JLabel hobbyLabel = new JLabel(hobbyImageIconMap.get(hobby));
                hobbyLabel.setToolTipText(hobby.getLabel());
                hobbyPanel.add(hobbyLabel);
            }
            add(hobbyPanel, BorderLayout.SOUTH);
        }

    }

    private ImageIcon determineGuestIcon(SeatDesignation seatDesignation) {
        Guest guest = seatDesignation.getGuest();
        if (guest == null) {
            return null;
        }
        List<ImageIcon> imageIconList = guest.getGender() == Gender.MALE ? maleImageIconList : femaleImageIconList;
        return imageIconList.get(guest.getId().intValue() % imageIconList.size());
    }

    private class SeatDesignationAction extends AbstractAction {

        private SeatDesignation seatDesignation;

        public SeatDesignationAction(SeatDesignation seatDesignation) {
            super(null, determineGuestIcon(seatDesignation));
            this.seatDesignation = seatDesignation;
            Seat seat = seatDesignation.getSeat();
            if (seat != null) {
                // Tooltip
                putValue(SHORT_DESCRIPTION, "<html>Guest: " + seatDesignation.getGuest().getName() + "<br/>"
                        + "Table: " + seat.getTable().getTableIndex() + "<br/>"
                        + "Seat: " + seat.getSeatIndexInTable()
                        + "</html>");
            }
        }

        public void actionPerformed(ActionEvent e) {
            List<SeatDesignation> seatDesignationList = getDinnerParty().getSeatDesignationList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox seatDesignationListField = new JComboBox(
                    seatDesignationList.toArray(new Object[seatDesignationList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(seatDesignationListField);
            seatDesignationListField.setSelectedItem(seatDesignation);
            int result = JOptionPane.showConfirmDialog(DinnerPartyPanel.this.getRootPane(), seatDesignationListField,
                    "Select seat designation to switch with", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                SeatDesignation switchSeatDesignation = (SeatDesignation) seatDesignationListField.getSelectedItem();
                if (seatDesignation != switchSeatDesignation) {
                    solutionBusiness.doSwapMove(seatDesignation, switchSeatDesignation);
                }
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
