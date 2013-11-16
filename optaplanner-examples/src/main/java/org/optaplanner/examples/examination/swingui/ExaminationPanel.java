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

package org.optaplanner.examples.examination.swingui;

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
import javax.swing.ImageIcon;
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
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.Period;
import org.optaplanner.examples.examination.domain.Room;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.HEADER_ROW;

public class ExaminationPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/examination/swingui/examinationLogo.png";

    private final TimeTablePanel<Room, Period> roomsPanel;

    public ExaminationPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        roomsPanel = new TimeTablePanel<Room, Period>();
        tabbedPane.add("Rooms", new JScrollPane(roomsPanel));
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

    private Examination getExamination() {
        return (Examination) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        roomsPanel.reset();
        Examination examination = (Examination) solution;
        defineGrid(examination);
        fillCells(examination);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(Examination examination) {
        JButton footprint = new JButton("999999");
        footprint.setMargin(new Insets(0, 0, 0, 0));
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
        roomsPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Time")));
        fillRoomCells(examination);
        fillDayCells(examination);
        fillLectureCells(examination);
    }

    private void fillRoomCells(Examination examination) {
        for (Room room : examination.getRoomList()) {
            roomsPanel.addColumnHeader(room, HEADER_ROW,
                    createHeaderPanel(new JLabel(room.getLabel(), SwingConstants.CENTER)));
        }
        roomsPanel.addColumnHeader(null, HEADER_ROW,
                createHeaderPanel(new JLabel("Unassigned", SwingConstants.CENTER)));
    }

    private void fillDayCells(Examination examination) {
        for (Period period : examination.getPeriodList()) {
            roomsPanel.addRowHeader(HEADER_COLUMN, period,
                    createHeaderPanel(new JLabel(period.getLabel())));
        }
        roomsPanel.addRowHeader(HEADER_COLUMN, null,
                createHeaderPanel(new JLabel("Unassigned")));
    }

    private void fillLectureCells(Examination examination) {
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (Exam exam : examination.getExamList()) {
            Color examColor = tangoColorFactory.pickColor(exam);
            roomsPanel.addCell(exam.getRoom(), exam.getPeriod(),
                    createButton(exam, examColor));
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

    private JButton createButton(Exam exam, Color color) {
        JButton button = new JButton(new ExamAction(exam));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBackground(color);
        return button;
    }

    private class ExamAction extends AbstractAction {

        private Exam exam;

        public ExamAction(Exam exam) {
            super(exam.getLabel());
            this.exam = exam;
        }

        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 2));
            listFieldsPanel.add(new JLabel("Period:"));
            List<Period> periodList = getExamination().getPeriodList();
            JComboBox periodListField = new JComboBox(periodList.toArray());
            periodListField.setSelectedItem(exam.getPeriod());
            listFieldsPanel.add(periodListField);
            listFieldsPanel.add(new JLabel("Room:"));
            List<Room> roomList = getExamination().getRoomList();
            JComboBox roomListField = new JComboBox(roomList.toArray());
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

}
