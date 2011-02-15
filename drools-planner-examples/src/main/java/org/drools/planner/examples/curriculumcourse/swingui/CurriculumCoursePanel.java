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

package org.drools.planner.examples.curriculumcourse.swingui;

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
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.examples.curriculumcourse.domain.Period;
import org.drools.planner.examples.curriculumcourse.domain.Room;
import org.drools.planner.examples.curriculumcourse.solver.move.PeriodChangeMove;
import org.drools.planner.examples.curriculumcourse.solver.move.RoomChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class CurriculumCoursePanel extends SolutionPanel {

    private static final Color HEADER_COLOR = Color.YELLOW;

    private GridLayout gridLayout;

    public CurriculumCoursePanel() {
        gridLayout = new GridLayout(0, 1);
        setLayout(gridLayout);
    }

    private CurriculumCourseSchedule getCurriculumCourseSchedule() {
        return (CurriculumCourseSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        removeAll();
        CurriculumCourseSchedule schedule = getCurriculumCourseSchedule();
        gridLayout.setColumns(schedule.getRoomList().size() + 1);
        JLabel headerCornerLabel = new JLabel("Period         \\         Room");
        headerCornerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        headerCornerLabel.setBackground(HEADER_COLOR);
        headerCornerLabel.setOpaque(true);
        add(headerCornerLabel);
        for (Room room : schedule.getRoomList()) {
            JLabel roomLabel = new JLabel(room.toString());
            roomLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            roomLabel.setBackground(HEADER_COLOR);
            roomLabel.setOpaque(true);
            add(roomLabel);
        }
        Map<Period, Map<Room, PeriodRoomPanel>> periodRoomPanelMap = new HashMap<Period, Map<Room, PeriodRoomPanel>>();
        for (Period period : schedule.getPeriodList()) {
            JLabel periodLabel = new JLabel(period.toString());
            periodLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            periodLabel.setBackground(HEADER_COLOR);
            periodLabel.setOpaque(true);
            add(periodLabel);
            Map<Room, PeriodRoomPanel> roomPanelMap = new HashMap<Room, PeriodRoomPanel>();
            periodRoomPanelMap.put(period, roomPanelMap);
            for (Room room : schedule.getRoomList()) {
                PeriodRoomPanel periodRoomPanel = new PeriodRoomPanel();
                add(periodRoomPanel);
                roomPanelMap.put(room, periodRoomPanel);
            }
        }
        if (schedule.isInitialized()) {
            for (Lecture lecture : schedule.getLectureList()) {
                PeriodRoomPanel periodRoomPanel = periodRoomPanelMap.get(lecture.getPeriod()).get(lecture.getRoom());
                periodRoomPanel.addLecture(lecture);
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

        public void addLecture(Lecture lecture) {
            JButton button = new JButton(new ExamAction(lecture));
            add(button);
        }

    }

    private class ExamAction extends AbstractAction {

        private Lecture lecture;

        public ExamAction(Lecture lecture) {
            super(lecture.getLabel());
            this.lecture = lecture;
        }

        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<Period> periodList = getCurriculumCourseSchedule().getPeriodList();
            JComboBox periodListField = new JComboBox(periodList.toArray());
            periodListField.setSelectedItem(lecture.getPeriod());
            listFieldsPanel.add(periodListField);
            List<Room> roomList = getCurriculumCourseSchedule().getRoomList();
            JComboBox roomListField = new JComboBox(roomList.toArray());
            roomListField.setSelectedItem(lecture.getRoom());
            listFieldsPanel.add(roomListField);
            int result = JOptionPane.showConfirmDialog(CurriculumCoursePanel.this.getRootPane(), listFieldsPanel,
                    "Select period and room", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Period toPeriod = (Period) periodListField.getSelectedItem();
                solutionBusiness.doMove(new PeriodChangeMove(lecture, toPeriod));
                Room toRoom = (Room) roomListField.getSelectedItem();
                solutionBusiness.doMove(new RoomChangeMove(lecture, toRoom));
                workflowFrame.updateScreen();
            }
        }

    }

}
