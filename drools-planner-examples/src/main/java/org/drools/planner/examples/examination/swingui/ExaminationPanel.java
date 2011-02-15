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

package org.drools.planner.examples.examination.swingui;

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

import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.examples.examination.domain.Period;
import org.drools.planner.examples.examination.domain.Room;
import org.drools.planner.examples.examination.solver.move.PeriodChangeMove;
import org.drools.planner.examples.examination.solver.move.RoomChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class ExaminationPanel extends SolutionPanel {

    private static final Color HEADER_COLOR = Color.YELLOW;

    private GridLayout gridLayout;

    public ExaminationPanel() {
        gridLayout = new GridLayout(0, 1);
        setLayout(gridLayout);
    }

    private Examination getExamination() {
        return (Examination) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        removeAll();
        Examination examination = getExamination();
        gridLayout.setColumns(examination.getRoomList().size() + 1);
        JLabel headerCornerLabel = new JLabel("Period         \\         Room");
        headerCornerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        headerCornerLabel.setBackground(HEADER_COLOR);
        headerCornerLabel.setOpaque(true);
        add(headerCornerLabel);
        for (Room room : examination.getRoomList()) {
            JLabel roomLabel = new JLabel(room.toString());
            roomLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            roomLabel.setBackground(HEADER_COLOR);
            roomLabel.setOpaque(true);
            add(roomLabel);
        }
        Map<Period, Map<Room, PeriodRoomPanel>> periodRoomPanelMap = new HashMap<Period, Map<Room, PeriodRoomPanel>>();
        for (Period period : examination.getPeriodList()) {
            JLabel periodLabel = new JLabel(period.toString() + " " + period.getStartDateTimeString());
            periodLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            periodLabel.setBackground(HEADER_COLOR);
            periodLabel.setOpaque(true);
            add(periodLabel);
            Map<Room, PeriodRoomPanel> roomPanelMap = new HashMap<Room, PeriodRoomPanel>();
            periodRoomPanelMap.put(period, roomPanelMap);
            for (Room room : examination.getRoomList()) {
                PeriodRoomPanel periodRoomPanel = new PeriodRoomPanel();
                add(periodRoomPanel);
                roomPanelMap.put(room, periodRoomPanel);
            }
        }
        if (examination.isInitialized()) {
            for (Exam exam : examination.getExamList()) {
                PeriodRoomPanel periodRoomPanel = periodRoomPanelMap.get(exam.getPeriod()).get(exam.getRoom());
                periodRoomPanel.addExam(exam);
            }
        }
    }

    private class PeriodRoomPanel extends JPanel {

        public PeriodRoomPanel() {
            super(new GridLayout(0, 1));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        }

        public void addExam(Exam exam) {
            JButton button = new JButton(new ExamAction(exam));
            add(button);
        }

    }

    private class ExamAction extends AbstractAction {

        private Exam exam;

        public ExamAction(Exam exam) {
            super(exam.getTopic().toString());
            this.exam = exam;
        }

        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<Period> periodList = getExamination().getPeriodList();
            JComboBox periodListField = new JComboBox(periodList.toArray());
            periodListField.setSelectedItem(exam.getPeriod());
            listFieldsPanel.add(periodListField);
            List<Room> roomList = getExamination().getRoomList();
            JComboBox roomListField = new JComboBox(roomList.toArray());
            roomListField.setSelectedItem(exam.getRoom());
            listFieldsPanel.add(roomListField);
            int result = JOptionPane.showConfirmDialog(ExaminationPanel.this.getRootPane(), listFieldsPanel,
                    "Select period and room", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Period toPeriod = (Period) periodListField.getSelectedItem();
                solutionBusiness.doMove(new PeriodChangeMove(exam, toPeriod));
                Room toRoom = (Room) roomListField.getSelectedItem();
                solutionBusiness.doMove(new RoomChangeMove(exam, toRoom));
                workflowFrame.updateScreen();
            }
        }

    }

}
