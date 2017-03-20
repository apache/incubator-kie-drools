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

package org.optaplanner.examples.curriculumcourse.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.CommonIcons;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Curriculum;
import org.optaplanner.examples.curriculumcourse.domain.Day;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.Period;
import org.optaplanner.examples.curriculumcourse.domain.Room;
import org.optaplanner.examples.curriculumcourse.domain.Teacher;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class CurriculumCoursePanel extends SolutionPanel<CourseSchedule> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/curriculumcourse/swingui/curriculumCourseLogo.png";

    private final TimeTablePanel<Room, Period> roomsPanel;
    private final TimeTablePanel<Teacher, Period> teachersPanel;
    private final TimeTablePanel<Curriculum, Period> curriculaPanel;

    public CurriculumCoursePanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        roomsPanel = new TimeTablePanel<>();
        tabbedPane.add("Rooms", new JScrollPane(roomsPanel));
        teachersPanel = new TimeTablePanel<>();
        tabbedPane.add("Teachers", new JScrollPane(teachersPanel));
        curriculaPanel = new TimeTablePanel<>();
        tabbedPane.add("Curricula", new JScrollPane(curriculaPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(CourseSchedule courseSchedule) {
        roomsPanel.reset();
        teachersPanel.reset();
        curriculaPanel.reset();
        defineGrid(courseSchedule);
        fillCells(courseSchedule);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(CourseSchedule courseSchedule) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("LinLetGre1-0"));
        int footprintWidth = footprint.getPreferredSize().width;

        roomsPanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP1); // Day header
        roomsPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Period header
        for (Room room : courseSchedule.getRoomList()) {
            roomsPanel.defineColumnHeader(room, footprintWidth);
        }
        roomsPanel.defineColumnHeader(null, footprintWidth); // Unassigned

        teachersPanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP1); // Day header
        teachersPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Period header
        for (Teacher teacher : courseSchedule.getTeacherList()) {
            teachersPanel.defineColumnHeader(teacher, footprintWidth);
        }

        curriculaPanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP1); // Day header
        curriculaPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Period header
        for (Curriculum curriculum : courseSchedule.getCurriculumList()) {
            curriculaPanel.defineColumnHeader(curriculum, footprintWidth);
        }

        roomsPanel.defineRowHeaderByKey(HEADER_ROW); // Room header
        teachersPanel.defineRowHeaderByKey(HEADER_ROW); // Teacher header
        curriculaPanel.defineRowHeaderByKey(HEADER_ROW); // Curriculum header
        for (Period period : courseSchedule.getPeriodList()) {
            roomsPanel.defineRowHeader(period);
            teachersPanel.defineRowHeader(period);
            curriculaPanel.defineRowHeader(period);
        }
        roomsPanel.defineRowHeader(null); // Unassigned period
        teachersPanel.defineRowHeader(null); // Unassigned period
        curriculaPanel.defineRowHeader(null); // Unassigned period
    }

    private void fillCells(CourseSchedule courseSchedule) {
        roomsPanel.addCornerHeader(HEADER_COLUMN_GROUP1, HEADER_ROW, createTableHeader(new JLabel("Day")));
        roomsPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Time")));
        fillRoomCells(courseSchedule);
        teachersPanel.addCornerHeader(HEADER_COLUMN_GROUP1, HEADER_ROW, createTableHeader(new JLabel("Day")));
        teachersPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Time")));
        fillTeacherCells(courseSchedule);
        curriculaPanel.addCornerHeader(HEADER_COLUMN_GROUP1, HEADER_ROW, createTableHeader(new JLabel("Day")));
        curriculaPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Time")));
        fillCurriculumCells(courseSchedule);
        fillDayCells(courseSchedule);
        fillLectureCells(courseSchedule);
    }

    private void fillRoomCells(CourseSchedule courseSchedule) {
        for (Room room : courseSchedule.getRoomList()) {
            roomsPanel.addColumnHeader(room, HEADER_ROW,
                    createTableHeader(new JLabel(room.getLabel(), SwingConstants.CENTER)));
        }
        roomsPanel.addColumnHeader(null, HEADER_ROW,
                createTableHeader(new JLabel("Unassigned", SwingConstants.CENTER)));
    }

    private void fillTeacherCells(CourseSchedule courseSchedule) {
        for (Teacher teacher : courseSchedule.getTeacherList()) {
            teachersPanel.addColumnHeader(teacher, HEADER_ROW,
                    createTableHeader(new JLabel(teacher.getLabel(), SwingConstants.CENTER)));
        }
    }

    private void fillCurriculumCells(CourseSchedule courseSchedule) {
        for (Curriculum curriculum : courseSchedule.getCurriculumList()) {
            curriculaPanel.addColumnHeader(curriculum, HEADER_ROW,
                    createTableHeader(new JLabel(curriculum.getLabel(), SwingConstants.CENTER)));
        }
    }

    private void fillDayCells(CourseSchedule courseSchedule) {
        for (Day day : courseSchedule.getDayList()) {
            Period dayStartPeriod = day.getPeriodList().get(0);
            Period dayEndPeriod = day.getPeriodList().get(day.getPeriodList().size() - 1);
            roomsPanel.addRowHeader(HEADER_COLUMN_GROUP1, dayStartPeriod, HEADER_COLUMN_GROUP1, dayEndPeriod,
                    createTableHeader(new JLabel(day.getLabel())));
            teachersPanel.addRowHeader(HEADER_COLUMN_GROUP1, dayStartPeriod, HEADER_COLUMN_GROUP1, dayEndPeriod,
                    createTableHeader(new JLabel(day.getLabel())));
            curriculaPanel.addRowHeader(HEADER_COLUMN_GROUP1, dayStartPeriod, HEADER_COLUMN_GROUP1, dayEndPeriod,
                    createTableHeader(new JLabel(day.getLabel())));
            for (Period period : day.getPeriodList()) {
                roomsPanel.addRowHeader(HEADER_COLUMN, period,
                        createTableHeader(new JLabel(period.getTimeslot().getLabel())));
                teachersPanel.addRowHeader(HEADER_COLUMN, period,
                        createTableHeader(new JLabel(period.getTimeslot().getLabel())));
                curriculaPanel.addRowHeader(HEADER_COLUMN, period,
                        createTableHeader(new JLabel(period.getTimeslot().getLabel())));
            }
        }
        roomsPanel.addRowHeader(HEADER_COLUMN_GROUP1, null, HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
        teachersPanel.addRowHeader(HEADER_COLUMN_GROUP1, null, HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
        curriculaPanel.addRowHeader(HEADER_COLUMN_GROUP1, null, HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned")));
    }

    private void fillLectureCells(CourseSchedule courseSchedule) {
        preparePlanningEntityColors(courseSchedule.getLectureList());
        for (Lecture lecture : courseSchedule.getLectureList()) {
            Color color = determinePlanningEntityColor(lecture, lecture.getCourse());
            String toolTip = determinePlanningEntityTooltip(lecture);
            roomsPanel.addCell(lecture.getRoom(), lecture.getPeriod(),
                    createButton(lecture, color, toolTip));
            teachersPanel.addCell(lecture.getTeacher(), lecture.getPeriod(),
                    createButton(lecture, color, toolTip));
            for (Curriculum curriculum : lecture.getCurriculumList()) {
                curriculaPanel.addCell(curriculum, lecture.getPeriod(),
                        createButton(lecture, color, toolTip));
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

    private JButton createButton(Lecture lecture, Color color, String toolTip) {
        JButton button = SwingUtils.makeSmallButton(new JButton(new LectureAction(lecture)));
        button.setBackground(color);
        if (lecture.isLocked()) {
            button.setIcon(CommonIcons.LOCKED_ICON);
        }
        button.setToolTipText(toolTip);
        return button;
    }

    @Override
    public boolean isIndictmentHeatMapEnabled() {
        return true;
    }

    private class LectureAction extends AbstractAction {

        private Lecture lecture;

        public LectureAction(Lecture lecture) {
            super(lecture.getLabel());
            this.lecture = lecture;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(3, 2));
            listFieldsPanel.add(new JLabel("Period:"));
            CourseSchedule courseSchedule = getSolution();
            List<Period> periodList = courseSchedule.getPeriodList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox periodListField = new JComboBox(
                    periodList.toArray(new Object[periodList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(periodListField);
            periodListField.setSelectedItem(lecture.getPeriod());
            listFieldsPanel.add(periodListField);
            listFieldsPanel.add(new JLabel("Room:"));
            List<Room> roomList = courseSchedule.getRoomList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox roomListField = new JComboBox(
                    roomList.toArray(new Object[roomList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(roomListField);
            roomListField.setSelectedItem(lecture.getRoom());
            listFieldsPanel.add(roomListField);
            listFieldsPanel.add(new JLabel("Locked:"));
            JCheckBox lockedField = new JCheckBox("immovable during planning");
            lockedField.setSelected(lecture.isLocked());
            listFieldsPanel.add(lockedField);
            int result = JOptionPane.showConfirmDialog(CurriculumCoursePanel.this.getRootPane(), listFieldsPanel,
                    "Select period and room", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Period toPeriod = (Period) periodListField.getSelectedItem();
                if (lecture.getPeriod() != toPeriod) {
                    solutionBusiness.doChangeMove(lecture, "period", toPeriod);
                }
                Room toRoom = (Room) roomListField.getSelectedItem();
                if (lecture.getRoom() != toRoom) {
                    solutionBusiness.doChangeMove(lecture, "room", toRoom);
                }
                boolean toLocked = lockedField.isSelected();
                if (lecture.isLocked() != toLocked) {
                    if (solutionBusiness.isSolving()) {
                        logger.error("Not doing user change because the solver is solving.");
                        return;
                    }
                    lecture.setLocked(toLocked);
                }
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
