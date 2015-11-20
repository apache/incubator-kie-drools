/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.meetingscheduling.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.domain.Person;
import org.optaplanner.examples.meetingscheduling.domain.RequiredAttendance;
import org.optaplanner.examples.meetingscheduling.domain.Room;
import org.optaplanner.examples.meetingscheduling.domain.TimeGrain;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class MeetingSchedulingPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/meetingscheduling/swingui/meetingschedulingLogo.png";

    private final TimeTablePanel<Room, TimeGrain> roomsPanel;
    private final TimeTablePanel<Person, TimeGrain> personsPanel;

    public MeetingSchedulingPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        roomsPanel = new TimeTablePanel<Room, TimeGrain>();
        tabbedPane.add("Rooms", new JScrollPane(roomsPanel));
        personsPanel = new TimeTablePanel<Person, TimeGrain>();
        tabbedPane.add("Persons", new JScrollPane(personsPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private MeetingSchedule getMeetingSchedule() {
        return (MeetingSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        roomsPanel.reset();
        personsPanel.reset();
        MeetingSchedule meetingSchedule = (MeetingSchedule) solution;
        defineGrid(meetingSchedule);
        fillCells(meetingSchedule);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(MeetingSchedule meetingSchedule) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("AAAAA BB CC DDDDD"));
        int footprintWidth = footprint.getPreferredSize().width;

        roomsPanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP1); // Day header
        roomsPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Period header
        for (Room room : meetingSchedule.getRoomList()) {
            roomsPanel.defineColumnHeader(room, footprintWidth);
        }
        roomsPanel.defineColumnHeader(null, footprintWidth); // Unassigned

        personsPanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP1); // Day header
        personsPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Period header
        for (Person person : meetingSchedule.getPersonList()) {
            personsPanel.defineColumnHeader(person, footprintWidth);
        }

        roomsPanel.defineRowHeaderByKey(HEADER_ROW); // Room header
        personsPanel.defineRowHeaderByKey(HEADER_ROW); // Teacher header
        for (TimeGrain timeGrain : meetingSchedule.getTimeGrainList()) {
            roomsPanel.defineRowHeader(timeGrain);
            personsPanel.defineRowHeader(timeGrain);
        }
        roomsPanel.defineRowHeader(null); // Unassigned timeGrain
        personsPanel.defineRowHeader(null); // Unassigned timeGrain
    }

    private void fillCells(MeetingSchedule meetingSchedule) {
        roomsPanel.addCornerHeader(HEADER_COLUMN_GROUP1, HEADER_ROW, createTableHeader(new JLabel("Day")));
        roomsPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Time")));
        fillRoomCells(meetingSchedule);
        personsPanel.addCornerHeader(HEADER_COLUMN_GROUP1, HEADER_ROW, createTableHeader(new JLabel("Day")));
        personsPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Time")));
        fillTeacherCells(meetingSchedule);
        fillTimeUnitCells(meetingSchedule);
        fillMeetingAssignmentCells(meetingSchedule);
    }

    private void fillRoomCells(MeetingSchedule meetingSchedule) {
        for (Room room : meetingSchedule.getRoomList()) {
            roomsPanel.addColumnHeader(room, HEADER_ROW,
                    createTableHeader(new JLabel(room.getLabel(), SwingConstants.CENTER)));
        }
        roomsPanel.addColumnHeader(null, HEADER_ROW,
                createTableHeader(new JLabel("Unassigned", SwingConstants.CENTER)));
    }

    private void fillTeacherCells(MeetingSchedule meetingSchedule) {
        for (Person person : meetingSchedule.getPersonList()) {
            personsPanel.addColumnHeader(person, HEADER_ROW,
                    createTableHeader(new JLabel(person.getLabel(), SwingConstants.CENTER)));
        }
    }

    private void fillTimeUnitCells(MeetingSchedule meetingSchedule) {
        for (TimeGrain timeGrain : meetingSchedule.getTimeGrainList()) {
            roomsPanel.addRowHeader(HEADER_COLUMN, timeGrain,
                    createTableHeader(new JLabel(timeGrain.getLabel())));
            personsPanel.addRowHeader(HEADER_COLUMN, timeGrain,
                    createTableHeader(new JLabel(timeGrain.getLabel())));
        }
        roomsPanel.addRowHeader(HEADER_COLUMN_GROUP1, null, HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
        personsPanel.addRowHeader(HEADER_COLUMN_GROUP1, null, HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
    }

    private void fillMeetingAssignmentCells(MeetingSchedule meetingSchedule) {
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (MeetingAssignment meetingAssignment : meetingSchedule.getMeetingAssignmentList()) {
            Color color = tangoColorFactory.pickColor(meetingAssignment.getMeeting());
            TimeGrain startingTimeGrain = meetingAssignment.getStartingTimeGrain();
            // TODO consider adding lastTimeGrain on Meeting
            TimeGrain lastTimeGrain = startingTimeGrain == null ? null :
                    meetingSchedule.getTimeGrainList().get(
                    startingTimeGrain.getGrainIndex() + meetingAssignment.getMeeting().getDurationInGrains());
            roomsPanel.addCell(meetingAssignment.getRoom(), startingTimeGrain,
                    meetingAssignment.getRoom(), lastTimeGrain,
                    createButton(meetingAssignment, color));
            for (RequiredAttendance requiredAttendance : meetingAssignment.getMeeting().getRequiredAttendanceList()) {
                personsPanel.addCell(requiredAttendance.getPerson(), startingTimeGrain,
                        requiredAttendance.getPerson(), lastTimeGrain,
                        createButton(meetingAssignment, color));
            }
        }
    }

    private JPanel createTableHeader(JLabel label) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private JButton createButton(MeetingAssignment meetingAssignment, Color color) {
        JButton button = SwingUtils.makeSmallButton(new JButton(new MeetingAssignmentAction(meetingAssignment)));
        button.setBackground(color);
        return button;
    }

    private class MeetingAssignmentAction extends AbstractAction {

        private MeetingAssignment meetingAssignment;

        public MeetingAssignmentAction(MeetingAssignment meetingAssignment) {
            super(meetingAssignment.getLabel());
            this.meetingAssignment = meetingAssignment;
        }

        public void actionPerformed(ActionEvent e) {
//            JPanel listFieldsPanel = new JPanel(new GridLayout(3, 2));
//            listFieldsPanel.add(new JLabel("Period:"));
//            List<Period> periodList = getMeetingSchedule().getPeriodList();
//            // Add 1 to array size to add null, which makes the entity unassigned
//            JComboBox periodListField = new JComboBox(
//                    periodList.toArray(new Object[periodList.size() + 1]));
//            LabeledComboBoxRenderer.applyToComboBox(periodListField);
//            periodListField.setSelectedItem(meetingAssignment.getPeriod());
//            listFieldsPanel.add(periodListField);
//            listFieldsPanel.add(new JLabel("Room:"));
//            List<Room> roomList = getMeetingSchedule().getRoomList();
//            // Add 1 to array size to add null, which makes the entity unassigned
//            JComboBox roomListField = new JComboBox(
//                    roomList.toArray(new Object[roomList.size() + 1]));
//            LabeledComboBoxRenderer.applyToComboBox(roomListField);
//            roomListField.setSelectedItem(meetingAssignment.getRoom());
//            listFieldsPanel.add(roomListField);
//            listFieldsPanel.add(new JLabel("Locked:"));
//            JCheckBox lockedField = new JCheckBox("immovable during planning");
//            lockedField.setSelected(meetingAssignment.isLocked());
//            listFieldsPanel.add(lockedField);
//            int result = JOptionPane.showConfirmDialog(MeetingSchedulingPanel.this.getRootPane(), listFieldsPanel,
//                    "Select period and room", JOptionPane.OK_CANCEL_OPTION);
//            if (result == JOptionPane.OK_OPTION) {
//                Period toPeriod = (Period) periodListField.getSelectedItem();
//                if (meetingAssignment.getPeriod() != toPeriod) {
//                    solutionBusiness.doChangeMove(meetingAssignment, "period", toPeriod);
//                }
//                Room toRoom = (Room) roomListField.getSelectedItem();
//                if (meetingAssignment.getRoom() != toRoom) {
//                    solutionBusiness.doChangeMove(meetingAssignment, "room", toRoom);
//                }
//                boolean toLocked = lockedField.isSelected();
//                if (meetingAssignment.isLocked() != toLocked) {
//                    if (solutionBusiness.isSolving()) {
//                        logger.error("Not doing user change because the solver is solving.");
//                        return;
//                    }
//                    meetingAssignment.setLocked(toLocked);
//                }
//                solverAndPersistenceFrame.resetScreen();
//            }
        }

    }

}
