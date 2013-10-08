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

package org.optaplanner.examples.pas.swingui;

import java.awt.BorderLayout;
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
import javax.swing.SwingConstants;

import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.common.swingui.timetable.TimeTableLayout;
import org.optaplanner.examples.common.swingui.timetable.TimeTableLayoutConstraints;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.pas.domain.AdmissionPart;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Night;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.solver.move.BedChangeMove;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class PatientAdmissionSchedulePanel extends SolutionPanel {

    private TimeTablePanel<Night, Bed> timeTablePanel;


    public PatientAdmissionSchedulePanel() {
        setLayout(new BorderLayout());
        timeTablePanel = new TimeTablePanel<Night, Bed>();
        add(timeTablePanel, BorderLayout.CENTER);
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private PatientAdmissionSchedule getPatientAdmissionSchedule() {
        return (PatientAdmissionSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        timeTablePanel.reset();
        PatientAdmissionSchedule patientAdmissionSchedule = (PatientAdmissionSchedule) solution;
        defineGrid(patientAdmissionSchedule);
        fillCells(patientAdmissionSchedule);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(PatientAdmissionSchedule patientAdmissionSchedule) {
        JButton footprint = new JButton("Patient9999");
        footprint.setMargin(new Insets(0, 0, 0, 0));
        int footprintWidth = footprint.getPreferredSize().width;
        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP2); // Department Header
        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP1); // Room Header
        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN); // Bed Header
        for (Night night : patientAdmissionSchedule.getNightList()) {
            timeTablePanel.defineColumnHeader(night, footprintWidth);
        }
        timeTablePanel.defineRowHeaderByKey(HEADER_ROW); // Night header
        timeTablePanel.defineRowHeader(null); // Unassigned bed
        for (Bed bed : patientAdmissionSchedule.getBedList()) {
            timeTablePanel.defineRowHeader(bed);
        }
    }

    private void fillCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        timeTablePanel.addCornerHeader(HEADER_COLUMN_GROUP2, HEADER_ROW, createHeaderPanel(new JLabel("Department")));
        timeTablePanel.addCornerHeader(HEADER_COLUMN_GROUP1, HEADER_ROW, createHeaderPanel(new JLabel("Room")));
        timeTablePanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Bed")));
        fillNightCells(patientAdmissionSchedule);
        fillBedCells(patientAdmissionSchedule);
        fillBedDesignationCells(patientAdmissionSchedule);
    }

    private void fillNightCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        for (Night night : patientAdmissionSchedule.getNightList()) {
            timeTablePanel.addColumnHeader(night, HEADER_ROW,
                    createHeaderPanel(new JLabel(night.getLabel(), SwingConstants.CENTER)));
        }
    }

    private void fillBedCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        timeTablePanel.addRowHeader(HEADER_COLUMN_GROUP2, null, HEADER_COLUMN, null,
                createHeaderPanel(new JLabel("Unassigned")));
        for (Bed bed : patientAdmissionSchedule.getBedList()) {
            timeTablePanel.addRowHeader(HEADER_COLUMN, bed, createHeaderPanel(new JLabel(bed.getLabel())));
        }
    }

    private void fillBedDesignationCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (BedDesignation bedDesignation : patientAdmissionSchedule.getBedDesignationList()) {
            JButton button = new JButton(new BedDesignationAction(bedDesignation));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setBackground(tangoColorFactory.pickColor(bedDesignation));
            AdmissionPart admissionPart = bedDesignation.getAdmissionPart();
            timeTablePanel.addCell(admissionPart.getFirstNight(), bedDesignation.getBed(),
                    admissionPart.getLastNight(), bedDesignation.getBed(), button);
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

    private class BedDesignationAction extends AbstractAction {

        private BedDesignation bedDesignation;

        public BedDesignationAction(BedDesignation bedDesignation) {
            super(bedDesignation.getAdmissionPart().getPatient().getName());
            this.bedDesignation = bedDesignation;
        }

        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<Bed> bedList = getPatientAdmissionSchedule().getBedList();
            JComboBox bedListField = new JComboBox(bedList.toArray());
            bedListField.setSelectedItem(bedDesignation.getBed());
            listFieldsPanel.add(bedListField);
            int result = JOptionPane.showConfirmDialog(PatientAdmissionSchedulePanel.this.getRootPane(), listFieldsPanel,
                    "Select bed", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Bed toBed = (Bed) bedListField.getSelectedItem();
                solutionBusiness.doMove(new BedChangeMove(bedDesignation, toBed));
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
