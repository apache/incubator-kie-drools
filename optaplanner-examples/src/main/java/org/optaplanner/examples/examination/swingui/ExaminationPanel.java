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

package org.optaplanner.examples.examination.swingui;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.HEADER_ROW;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.FollowingExam;
import org.optaplanner.examples.examination.domain.Period;
import org.optaplanner.examples.examination.domain.Room;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class ExaminationPanel extends SolutionPanel<Examination> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/examination/swingui/examinationLogo.png";

    private final TimeTablePanel<Room, Period> roomsPanel;

    private ExaminationConstraintConfigurationDialog examinationConstraintConfigurationDialog;
    private AbstractAction institutionParametrizationEditAction;

    private int maximumPeriodDuration;
    private int maximumRoomCapacity;

    public ExaminationPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        roomsPanel = new TimeTablePanel<>();
        tabbedPane.add("Rooms", new JScrollPane(roomsPanel));
        add(tabbedPane, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        institutionParametrizationEditAction = new AbstractAction("Edit constraint weights") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (solutionBusiness.isSolving()) {
                    JOptionPane.showMessageDialog(ExaminationPanel.this.getTopLevelAncestor(),
                            "The GUI does not support this action yet during solving.\n"
                                    + "OptaPlanner itself does support it.\n"
                                    + "\nTerminate solving first and try again.",
                            "Unsupported in GUI", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                examinationConstraintConfigurationDialog.setExaminationConstraintConfiguration(
                        getSolution().getConstraintConfiguration());
                examinationConstraintConfigurationDialog.setVisible(true);
            }
        };
        institutionParametrizationEditAction.setEnabled(false);
        footerPanel.add(new JButton(institutionParametrizationEditAction));
        return footerPanel;
    }

    @Override
    public void setSolverAndPersistenceFrame(SolverAndPersistenceFrame solverAndPersistenceFrame) {
        super.setSolverAndPersistenceFrame(solverAndPersistenceFrame);
        examinationConstraintConfigurationDialog = new ExaminationConstraintConfigurationDialog(solverAndPersistenceFrame,
                this);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(Examination examination) {
        roomsPanel.reset();
        refreshMaximums(examination);
        defineGrid(examination);
        fillCells(examination);
        institutionParametrizationEditAction.setEnabled(true);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void refreshMaximums(Examination examination) {
        maximumPeriodDuration = 0;
        for (Period period : examination.getPeriodList()) {
            int periodDuration = period.getDuration();
            if (periodDuration > maximumPeriodDuration) {
                maximumPeriodDuration = periodDuration;
            }
        }
        maximumRoomCapacity = 0;
        for (Room room : examination.getRoomList()) {
            int roomCapacity = room.getCapacity();
            if (roomCapacity > maximumRoomCapacity) {
                maximumRoomCapacity = roomCapacity;
            }
        }
    }

    private void defineGrid(Examination examination) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("99999999"));
        int footprintWidth = footprint.getPreferredSize().width;

        roomsPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Period header
        for (Room room : examination.getRoomList()) {
            roomsPanel.defineColumnHeader(room, footprintWidth);
        }
        roomsPanel.defineColumnHeader(null, footprintWidth); // Unassigned

        roomsPanel.defineRowHeaderByKey(HEADER_ROW); // Room header
        for (Period period : examination.getPeriodList()) {
            roomsPanel.defineRowHeader(period);
        }
        roomsPanel.defineRowHeader(null); // Unassigned period
    }

    private void fillCells(Examination examination) {
        roomsPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createTableHeader(new JLabel("Time"), null));
        fillRoomCells(examination);
        fillPeriodCells(examination);
        fillExamCells(examination);
    }

    private void fillRoomCells(Examination examination) {
        for (Room room : examination.getRoomList()) {
            roomsPanel.addColumnHeader(room, HEADER_ROW,
                    createTableHeader(new JLabel(room.getLabel(), new RoomIcon(room), SwingConstants.CENTER),
                            "Capacity: " + room.getCapacity() + " (shown as rectangle)"));
        }
        roomsPanel.addColumnHeader(null, HEADER_ROW,
                createTableHeader(new JLabel("Unassigned", SwingConstants.CENTER), null));
    }

    private void fillPeriodCells(Examination examination) {
        for (Period period : examination.getPeriodList()) {
            roomsPanel.addRowHeader(HEADER_COLUMN, period, createTableHeader(
                    new JLabel(period.getLabel(), new PeriodIcon(period), SwingConstants.LEFT),
                    "Duration: " + period.getDuration()));
        }
        roomsPanel.addRowHeader(HEADER_COLUMN, null,
                createTableHeader(new JLabel("Unassigned"), null));
    }

    private void fillExamCells(Examination examination) {
        preparePlanningEntityColors(examination.getExamList());
        for (Exam exam : examination.getExamList()) {
            Color color = determinePlanningEntityColor(exam, exam);
            String toolTip = determinePlanningEntityTooltip(exam);
            roomsPanel.addCell(exam.getRoom(), exam.getPeriod(),
                    createButton(exam, color, toolTip));
        }
    }

    private JPanel createTableHeader(JLabel label, String toolTip) {
        if (toolTip != null) {
            label.setToolTipText(toolTip);
        }
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private JButton createButton(Exam exam, Color color, String toolTip) {
        JButton button = SwingUtils.makeSmallButton(new JButton(new ExamAction(exam)));
        button.setBackground(color);
        button.setToolTipText(toolTip);
        if (exam instanceof FollowingExam) {
            button.setForeground(TangoColorFactory.ALUMINIUM_5);
        }
        return button;
    }

    @Override
    public boolean isIndictmentHeatMapEnabled() {
        return true;
    }

    private class ExamAction extends AbstractAction {

        private Exam exam;

        public ExamAction(Exam exam) {
            super(exam.getLabel(), new ExamIcon(exam));
            this.exam = exam;
            // Tooltip
            putValue(SHORT_DESCRIPTION, "<html>Exam: " + exam.getLabel() + "<br/>"
                    + "Duration: " + exam.getTopicDuration() + " (shown in circle)<br/>"
                    + "Has " + exam.getTopicStudentSize() + " students (shown in rectangle)"
                    + "</html>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 2));
            listFieldsPanel.add(new JLabel("Period:"));
            Examination examination = getSolution();
            List<Period> periodList = examination.getPeriodList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox periodListField = new JComboBox(
                    periodList.toArray(new Object[periodList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(periodListField);
            periodListField.setSelectedItem(exam.getPeriod());
            listFieldsPanel.add(periodListField);
            listFieldsPanel.add(new JLabel("Room:"));
            List<Room> roomList = examination.getRoomList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox roomListField = new JComboBox(
                    roomList.toArray(new Object[roomList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(roomListField);
            roomListField.setSelectedItem(exam.getRoom());
            listFieldsPanel.add(roomListField);
            int result = JOptionPane.showConfirmDialog(ExaminationPanel.this.getRootPane(), listFieldsPanel,
                    "Select period and room", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Period toPeriod = (Period) periodListField.getSelectedItem();
                solutionBusiness.doChangeMove(exam, "period", toPeriod);
                Room toRoom = (Room) roomListField.getSelectedItem();
                solutionBusiness.doChangeMove(exam, "room", toRoom);
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

    private class PeriodIcon implements Icon {

        private static final int DIAMETER = 14;

        private final Period period;

        private PeriodIcon(Period period) {
            this.period = period;
        }

        @Override
        public int getIconWidth() {
            return DIAMETER;
        }

        @Override
        public int getIconHeight() {
            return DIAMETER;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(TangoColorFactory.ALUMINIUM_6);
            g.fillOval(x, y, DIAMETER, DIAMETER);
            g.setColor(Color.WHITE);
            g.fillArc(x, y, DIAMETER, DIAMETER, 90, -(360 * period.getDuration() / maximumPeriodDuration));
            g.setColor(TangoColorFactory.ALUMINIUM_6);
            g.drawOval(x, y, DIAMETER, DIAMETER);
        }

    }

    private class RoomIcon implements Icon {

        private static final int ICON_WIDTH = 10;
        private static final int ICON_HEIGHT = PeriodIcon.DIAMETER;

        private final Room room;

        private RoomIcon(Room room) {
            this.room = room;
        }

        @Override
        public int getIconWidth() {
            return ICON_WIDTH;
        }

        @Override
        public int getIconHeight() {
            return ICON_HEIGHT;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(TangoColorFactory.ALUMINIUM_6);
            g.fillRect(x + 1, y, ICON_WIDTH - 2, ICON_HEIGHT);
            g.setColor(Color.WHITE);
            int capacityHeight = ICON_HEIGHT * room.getCapacity() / maximumRoomCapacity;
            g.fillRect(x + 1, y + (ICON_HEIGHT - capacityHeight), ICON_WIDTH - 2, capacityHeight);
            g.setColor(TangoColorFactory.ALUMINIUM_6);
            g.drawRect(x + 1, y, ICON_WIDTH - 2, ICON_HEIGHT);
        }

    }

    private class ExamIcon implements Icon {

        private static final int DIAMETER = PeriodIcon.DIAMETER;
        private static final int ICON_WIDTH = RoomIcon.ICON_WIDTH;
        private static final int ICON_HEIGHT = RoomIcon.ICON_HEIGHT;

        private final Exam exam;

        private ExamIcon(Exam exam) {
            this.exam = exam;
        }

        @Override
        public int getIconWidth() {
            return DIAMETER + ICON_WIDTH;
        }

        @Override
        public int getIconHeight() {
            return DIAMETER;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.WHITE);
            g.fillOval(x, y, DIAMETER, DIAMETER);
            g.setColor(TangoColorFactory.ALUMINIUM_4);
            g.fillArc(x, y, DIAMETER, DIAMETER, 90, -(360 * exam.getTopicDuration() / maximumPeriodDuration));
            g.setColor(TangoColorFactory.ALUMINIUM_6);
            g.drawOval(x, y, DIAMETER, DIAMETER);

            x += DIAMETER + 1;
            g.setColor(Color.WHITE);
            g.fillRect(x + 1, y, ICON_WIDTH - 2, ICON_HEIGHT);
            g.setColor(TangoColorFactory.ALUMINIUM_4);
            int capacityHeight = ICON_HEIGHT * exam.getTopicStudentSize() / maximumRoomCapacity;
            g.fillRect(x + 1, y + (ICON_HEIGHT - capacityHeight), ICON_WIDTH - 2, capacityHeight);
            g.setColor(TangoColorFactory.ALUMINIUM_6);
            g.drawRect(x + 1, y, ICON_WIDTH - 2, ICON_HEIGHT);
        }

    }

}
