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
import org.optaplanner.examples.pas.domain.AdmissionPart;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Night;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.solver.move.BedChangeMove;

public class PatientAdmissionSchedulePanel extends SolutionPanel {

    private TimeTableLayout timeTableLayout;

    private Map<Night, Integer> nightXMap;
    private Map<Bed, Integer> bedYMap;

    public PatientAdmissionSchedulePanel() {
        timeTableLayout = new TimeTableLayout();
        setLayout(timeTableLayout);
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private PatientAdmissionSchedule getPatientAdmissionSchedule() {
        return (PatientAdmissionSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        removeAll();
        PatientAdmissionSchedule patientAdmissionSchedule = (PatientAdmissionSchedule) solution;
        defineGrid(patientAdmissionSchedule);
        fillCells(patientAdmissionSchedule);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(PatientAdmissionSchedule patientAdmissionSchedule) {
        timeTableLayout.reset();
        JButton footprint = new JButton("Patient9999");
        footprint.setMargin(new Insets(0, 0, 0, 0));
        int footprintWidth = footprint.getPreferredSize().width;
        int footprintHeight = footprint.getPreferredSize().height;
        timeTableLayout.addColumn(150); // Header
        nightXMap = new HashMap<Night, Integer>(patientAdmissionSchedule.getNightList().size());
        for (Night night : patientAdmissionSchedule.getNightList()) {
            int x = timeTableLayout.addColumn(footprintWidth);
            nightXMap.put(night, x);
        }
        timeTableLayout.addRow(footprintHeight); // Header
        bedYMap = new HashMap<Bed, Integer>(patientAdmissionSchedule.getBedList().size());
        timeTableLayout.addRow(footprintHeight); // Unassigned
        bedYMap.put(null, 1);
        for (Bed bed : patientAdmissionSchedule.getBedList()) {
            int y = timeTableLayout.addRow(footprintHeight);
            bedYMap.put(bed, y);
        }
    }

    private void fillCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        for (Night night : patientAdmissionSchedule.getNightList()) {
            JPanel nightLabel = createHeaderPanel(new JLabel(night.getLabel(), SwingConstants.CENTER));
            add(nightLabel, new TimeTableLayoutConstraints(nightXMap.get(night), 0, true));
        }
        JPanel unassignedLabel = createHeaderPanel(new JLabel("Unassigned"));
        add(unassignedLabel, new TimeTableLayoutConstraints(0, bedYMap.get(null), true));
        for (Bed bed : patientAdmissionSchedule.getBedList()) {
            JPanel bedLabel = createHeaderPanel(new JLabel(bed.getLabel()));
            add(bedLabel, new TimeTableLayoutConstraints(0, bedYMap.get(bed), true));
        }
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (BedDesignation bedDesignation : patientAdmissionSchedule.getBedDesignationList()) {
            JButton button = new JButton(new BedDesignationAction(bedDesignation));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setBackground(tangoColorFactory.pickColor(bedDesignation));
            AdmissionPart admissionPart = bedDesignation.getAdmissionPart();
            int x1 = nightXMap.get(admissionPart.getFirstNight());
            int x2 = nightXMap.get(admissionPart.getLastNight());
            int y = bedYMap.get(bedDesignation.getBed());
            add(button, new TimeTableLayoutConstraints(x1, y, x2 - x1 + 1, 1));
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
