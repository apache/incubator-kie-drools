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
import java.awt.Insets;
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

    private final JPanel roomsPanel;
    private TimeTableLayout roomsTimeTableLayout;
    private final JPanel teachersPanel;
    private TimeTableLayout teachersTimeTableLayout;

    private Map<Period, Integer> periodYMap;
    private Map<Room, Integer> roomXMap;
    private Map<Teacher, Integer> teacherXMap;

    public CurriculumCoursePanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        roomsTimeTableLayout = new TimeTableLayout();
        roomsPanel = new JPanel(roomsTimeTableLayout);
        tabbedPane.add("Rooms", createScrollPane(roomsPanel));
        teachersTimeTableLayout = new TimeTableLayout();
        teachersPanel = new JPanel(teachersTimeTableLayout);
        tabbedPane.add("Teachers", createScrollPane(teachersPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    private JScrollPane createScrollPane(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        return scrollPane;
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
        JButton footprint = new JButton("LinLetGre1-0");
        footprint.setMargin(new Insets(0, 0, 0, 0));
        int footprintWidth = footprint.getPreferredSize().width;
        int footprintHeight = footprint.getPreferredSize().height;
        roomsTimeTableLayout.addColumn(150); // Header
        roomXMap = new HashMap<Room, Integer>(courseSchedule.getRoomList().size());
        for (Room room : courseSchedule.getRoomList()) {
            int x = roomsTimeTableLayout.addColumn(footprintWidth);
            roomXMap.put(room, x);
        }
        int unassignedRoomX = roomsTimeTableLayout.addColumn(footprintWidth); // Unassigned
        roomXMap.put(null, unassignedRoomX);
        teachersTimeTableLayout.addColumn(150); // Header
        teacherXMap = new HashMap<Teacher, Integer>(courseSchedule.getTeacherList().size());
        for (Teacher teacher : courseSchedule.getTeacherList()) {
            int x = teachersTimeTableLayout.addColumn(footprintWidth);
            teacherXMap.put(teacher, x);
        }
        roomsTimeTableLayout.addRow(footprintHeight); // Header
        teachersTimeTableLayout.addRow(footprintHeight); // Header
        periodYMap = new HashMap<Period, Integer>(courseSchedule.getPeriodList().size());
        for (Period period : courseSchedule.getPeriodList()) {
            int y = roomsTimeTableLayout.addRow(footprintHeight);
            int otherY = teachersTimeTableLayout.addRow(footprintHeight);
            if (y != otherY) {
                throw new IllegalStateException("Impossible");
            }
            periodYMap.put(period, y);
        }
        int unassignedPeriodY = roomsTimeTableLayout.addRow(footprintHeight); // Unassigned period
        int otherUnassignedPeriodY = teachersTimeTableLayout.addRow(footprintHeight); // Unassigned period
        if (unassignedPeriodY != otherUnassignedPeriodY) {
            throw new IllegalStateException("Impossible");
        }
        periodYMap.put(null, unassignedPeriodY);
    }

    private void fillCells(CourseSchedule courseSchedule) {
        for (Room room : courseSchedule.getRoomList()) {
            JPanel roomLabel = createHeaderPanel(new JLabel(room.getLabel(), SwingConstants.CENTER));
            roomsPanel.add(roomLabel, new TimeTableLayoutConstraints(roomXMap.get(room), 0, true));
        }
        JPanel unassignedRoomLabel = createHeaderPanel(new JLabel("Unassigned", SwingConstants.CENTER));
        roomsPanel.add(unassignedRoomLabel, new TimeTableLayoutConstraints(roomXMap.get(null), 0, true));

        for (Teacher teacher : courseSchedule.getTeacherList()) {
            JPanel teacherLabel = createHeaderPanel(new JLabel(teacher.getLabel(), SwingConstants.CENTER));
            teachersPanel.add(teacherLabel, new TimeTableLayoutConstraints(teacherXMap.get(teacher), 0, true));
        }

        for (Period period : courseSchedule.getPeriodList()) {
            JPanel periodRoomLabel = createHeaderPanel(new JLabel(period.getLabel()));
            roomsPanel.add(periodRoomLabel, new TimeTableLayoutConstraints(0, periodYMap.get(period), true));
            JPanel periodTeacherLabel = createHeaderPanel(new JLabel(period.getLabel()));
            teachersPanel.add(periodTeacherLabel, new TimeTableLayoutConstraints(0, periodYMap.get(period), true));
        }
        JPanel unassignedPeriodRoomLabel = createHeaderPanel(new JLabel("Unassigned"));
        roomsPanel.add(unassignedPeriodRoomLabel, new TimeTableLayoutConstraints(0, periodYMap.get(null), true));
        JPanel unassignedPeriodTeacherLabel = createHeaderPanel(new JLabel("Unassigned"));
        teachersPanel.add(unassignedPeriodTeacherLabel, new TimeTableLayoutConstraints(0, periodYMap.get(null), true));

        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (Lecture lecture : courseSchedule.getLectureList()) {
            Color lectureColor = tangoColorFactory.pickColor(lecture.getCourse());
            int y = periodYMap.get(lecture.getPeriod());
            JButton roomButton = createButton(lecture, lectureColor);
            roomsPanel.add(roomButton, new TimeTableLayoutConstraints(roomXMap.get(lecture.getRoom()), y));
            JButton teacherButton = createButton(lecture, lectureColor);
            teachersPanel.add(teacherButton, new TimeTableLayoutConstraints(teacherXMap.get(lecture.getTeacher()), y));
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

    private JButton createButton(Lecture lecture, Color color) {
        JButton button = new JButton(new LectureAction(lecture));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBackground(color);
        return button;
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
