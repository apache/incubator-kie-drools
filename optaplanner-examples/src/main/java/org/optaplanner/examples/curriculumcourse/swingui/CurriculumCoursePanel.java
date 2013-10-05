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

package org.optaplanner.examples.curriculumcourse.swingui;

import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.common.swingui.timetable.TimeTableLayout;
import org.optaplanner.examples.common.swingui.timetable.TimeTableLayoutConstraints;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.Period;
import org.optaplanner.examples.curriculumcourse.domain.Room;
import org.optaplanner.examples.curriculumcourse.domain.Teacher;
import org.optaplanner.examples.curriculumcourse.solver.move.PeriodChangeMove;
import org.optaplanner.examples.curriculumcourse.solver.move.RoomChangeMove;

public class CurriculumCoursePanel extends SolutionPanel {

    private static final Color HEADER_COLOR = TangoColorFactory.ALUMINIUM_2;

    private final JPanel roomsPanel;
    private TimeTableLayout roomsTimeTableLayout;
    private final JPanel teachersPanel;
    private TimeTableLayout teachersTimeTableLayout;

    private Map<Period, Integer> periodXMap;
    private Map<Room, Integer> roomYMap;
    private Map<Teacher, Integer> teacherYMap;

    public CurriculumCoursePanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        roomsTimeTableLayout = new TimeTableLayout();
        roomsPanel = new JPanel(roomsTimeTableLayout);
        tabbedPane.add("Rooms", new JScrollPane(roomsPanel));
        teachersTimeTableLayout = new TimeTableLayout();
        teachersPanel = new JPanel(teachersTimeTableLayout);
        tabbedPane.add("Teachers", new JScrollPane(teachersPanel));
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

    private CourseSchedule getCourseSchedule() {
        return (CourseSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        roomsPanel.removeAll();
        teachersPanel.removeAll();
        CourseSchedule courseSchedule = (CourseSchedule) solution;
        defineGrid(courseSchedule);
        fillCells(courseSchedule);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(CourseSchedule courseSchedule) {
        roomsTimeTableLayout.reset();
        teachersTimeTableLayout.reset();
        JButton footprint = new JButton("1234567");
        int footprintWidth = footprint.getPreferredSize().width;
        int footprintHeight = footprint.getPreferredSize().height;
        roomsTimeTableLayout.addColumn(150); // Header
        teachersTimeTableLayout.addColumn(150); // Header
        periodXMap = new HashMap<Period, Integer>(courseSchedule.getPeriodList().size());
        int unassignedX = roomsTimeTableLayout.addColumn(footprintWidth); // Unassigned period
        teachersTimeTableLayout.addColumn(footprintWidth); // Unassigned period
        periodXMap.put(null, unassignedX);
        for (Period period : courseSchedule.getPeriodList()) {
            int x = roomsTimeTableLayout.addColumn(footprintWidth);
            int otherX = teachersTimeTableLayout.addColumn(footprintWidth);
            if (x != otherX) {
                throw new IllegalStateException("Impossible");
            }
            periodXMap.put(period, x);
        }
        roomsTimeTableLayout.addRow(footprintHeight); // Header
        roomYMap = new HashMap<Room, Integer>(courseSchedule.getRoomList().size());
        roomsTimeTableLayout.addRow(footprintHeight); // Unassigned
        roomYMap.put(null, 1);
        for (Room room : courseSchedule.getRoomList()) {
            int y = roomsTimeTableLayout.addRow(footprintHeight);
            roomYMap.put(room, y);
        }
        teachersTimeTableLayout.addRow(footprintHeight); // Header
        teacherYMap = new HashMap<Teacher, Integer>(courseSchedule.getTeacherList().size());
        for (Teacher teacher : courseSchedule.getTeacherList()) {
            int y = teachersTimeTableLayout.addRow(footprintHeight);
            teacherYMap.put(teacher, y);
        }
    }

    private void fillCells(CourseSchedule courseSchedule) {
        JPanel unassignedPeriodRoomLabel = createHeaderPanel(new JLabel("Unassigned", SwingConstants.CENTER));
        roomsPanel.add(unassignedPeriodRoomLabel, new TimeTableLayoutConstraints(periodXMap.get(null), 0, true));
        JPanel unassignedPeriodTeacherLabel = createHeaderPanel(new JLabel("Unassigned", SwingConstants.CENTER));
        teachersPanel.add(unassignedPeriodTeacherLabel, new TimeTableLayoutConstraints(periodXMap.get(null), 0, true));
        for (Period period : courseSchedule.getPeriodList()) {
            JPanel periodRoomLabel = createHeaderPanel(new JLabel(period.getLabel(), SwingConstants.CENTER));
            roomsPanel.add(periodRoomLabel, new TimeTableLayoutConstraints(periodXMap.get(period), 0, true));
            JPanel periodTeacherLabel = createHeaderPanel(new JLabel(period.getLabel(), SwingConstants.CENTER));
            teachersPanel.add(periodTeacherLabel, new TimeTableLayoutConstraints(periodXMap.get(period), 0, true));
        }

        JPanel unassignedRoomLabel = createHeaderPanel(new JLabel("Unassigned"));
        roomsPanel.add(unassignedRoomLabel, new TimeTableLayoutConstraints(0, roomYMap.get(null), true));
        for (Room room : courseSchedule.getRoomList()) {
            JPanel roomLabel = createHeaderPanel(new JLabel(room.getLabel()));
            roomsPanel.add(roomLabel, new TimeTableLayoutConstraints(0, roomYMap.get(room), true));
        }

        for (Teacher teacher : courseSchedule.getTeacherList()) {
            JPanel teacherLabel = createHeaderPanel(new JLabel(teacher.getLabel()));
            teachersPanel.add(teacherLabel, new TimeTableLayoutConstraints(0, teacherYMap.get(teacher), true));
        }

        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (Lecture lecture : courseSchedule.getLectureList()) {
            Color lectureColor = tangoColorFactory.pickColor(lecture);
            int x = periodXMap.get(lecture.getPeriod());
            JButton roomButton = new JButton(new LectureAction(lecture));
            roomButton.setBackground(lectureColor);
            roomsPanel.add(roomButton, new TimeTableLayoutConstraints(x, roomYMap.get(lecture.getRoom())));
            JButton teacherButton = new JButton(new LectureAction(lecture));
            teacherButton.setBackground(lectureColor);
            teachersPanel.add(teacherButton, new TimeTableLayoutConstraints(x, teacherYMap.get(lecture.getTeacher())));
        }
    }

    private JPanel createHeaderPanel(JLabel label) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private class LectureAction extends AbstractAction {

        private Lecture lecture;

        public LectureAction(Lecture lecture) {
            super(lecture.getLabel());
            this.lecture = lecture;
        }

        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<Period> periodList = getCourseSchedule().getPeriodList();
            JComboBox periodListField = new JComboBox(periodList.toArray());
            periodListField.setSelectedItem(lecture.getPeriod());
            listFieldsPanel.add(periodListField);
            List<Room> roomList = getCourseSchedule().getRoomList();
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
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
