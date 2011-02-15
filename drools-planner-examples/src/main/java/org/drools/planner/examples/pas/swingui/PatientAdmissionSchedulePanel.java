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

package org.drools.planner.examples.pas.swingui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.pas.domain.PatientAdmissionSchedule;
import org.drools.planner.examples.pas.domain.Night;
import org.drools.planner.examples.pas.domain.Bed;
import org.drools.planner.examples.pas.domain.BedDesignation;
import org.drools.planner.examples.pas.solver.move.BedChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class PatientAdmissionSchedulePanel extends SolutionPanel {

    private static final Color HEADER_COLOR = Color.YELLOW;

    private GridLayout gridLayout;

    public PatientAdmissionSchedulePanel() {
        gridLayout = new GridLayout(0, 1);
        setLayout(gridLayout);
    }

    private PatientAdmissionSchedule getPatientAdmissionSchedule() {
        return (PatientAdmissionSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        removeAll();
        PatientAdmissionSchedule patientAdmissionSchedule = getPatientAdmissionSchedule();
        gridLayout.setColumns(patientAdmissionSchedule.getNightList().size() + 1);
        JLabel headerCornerLabel = new JLabel("Department_Room_Bed  \\  Night");
        headerCornerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        headerCornerLabel.setBackground(HEADER_COLOR);
        headerCornerLabel.setOpaque(true);
        add(headerCornerLabel);
        for (Night night : patientAdmissionSchedule.getNightList()) {
            JLabel nightLabel = new JLabel(night.toString());
            nightLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            nightLabel.setBackground(HEADER_COLOR);
            nightLabel.setOpaque(true);
            add(nightLabel);
        }
        Map<Bed, Map<Night, BedNightPanel>> bedNightPanelMap = new HashMap<Bed, Map<Night, BedNightPanel>>();
        for (Bed bed : patientAdmissionSchedule.getBedList()) {
            JLabel bedLabel = new JLabel(bed.toString());
            bedLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            bedLabel.setBackground(HEADER_COLOR);
            bedLabel.setOpaque(true);
            add(bedLabel);
            Map<Night, BedNightPanel> nightPanelMap = new HashMap<Night, BedNightPanel>();
            bedNightPanelMap.put(bed, nightPanelMap);
            for (Night night : patientAdmissionSchedule.getNightList()) {
                BedNightPanel bedNightPanel = new BedNightPanel();
                add(bedNightPanel);
                nightPanelMap.put(night, bedNightPanel);
            }
        }
        if (patientAdmissionSchedule.isInitialized()) {
            for (BedDesignation bedDesignation : patientAdmissionSchedule.getBedDesignationList()) {
                for (Night night : patientAdmissionSchedule.getNightList()) {
                    if (bedDesignation.getAdmissionPart().getFirstNight().getIndex() <= night.getIndex()
                            && night.getIndex() <= bedDesignation.getAdmissionPart().getLastNight().getIndex()) {
                        BedNightPanel bedNightPanel = bedNightPanelMap.get(bedDesignation.getBed()).get(night);
                        bedNightPanel.addBedDesignation(bedDesignation);
                    }
                }
            }
        }
    }

    private class BedNightPanel extends JPanel {

        public BedNightPanel() {
            super(new GridLayout(0, 1));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        }

        public void addBedDesignation(BedDesignation bedDesignation) {
            JButton button = new JButton(new BedDesignationAction(bedDesignation));
            add(button);
        }

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
                workflowFrame.updateScreen();
            }
        }

    }

}
