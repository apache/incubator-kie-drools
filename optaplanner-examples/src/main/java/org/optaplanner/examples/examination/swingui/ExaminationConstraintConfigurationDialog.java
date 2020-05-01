/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.ExaminationConstraintConfiguration;

public class ExaminationConstraintConfigurationDialog extends JDialog {

    protected final ExaminationPanel examinationPanel;

    private JSpinner twoInARowPenaltyField;
    private JSpinner twoInADayPenaltyField;
    private JSpinner periodSpreadLengthField;
    private JSpinner periodSpreadPenaltyField;
    private JSpinner mixedDurationPenaltyField;
    private JSpinner frontLoadLargeTopicSizeField;
    private JSpinner frontLoadLastPeriodSizeField;
    private JSpinner frontLoadPenaltyField;
    protected final AbstractAction saveAction;
    protected final AbstractAction cancelAction;

    public ExaminationConstraintConfigurationDialog(Frame owner, ExaminationPanel examinationPanel) {
        super(owner, "Edit examination constraint configuration", true);
        this.examinationPanel = examinationPanel;
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(createFormPanel(), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveAction = new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        };
        buttonPanel.add(new JButton(saveAction));
        cancelAction = new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        buttonPanel.add(new JButton(cancelAction));
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(contentPanel);
        pack();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("2 exams in a row penalty"));
        twoInARowPenaltyField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        formPanel.add(twoInARowPenaltyField);
        formPanel.add(new JLabel("2 exams in a day penalty"));
        twoInADayPenaltyField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        formPanel.add(twoInADayPenaltyField);
        formPanel.add(new JLabel("Period spread length"));
        periodSpreadLengthField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        formPanel.add(periodSpreadLengthField);
        formPanel.add(new JLabel("Period spread penalty"));
        periodSpreadPenaltyField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        formPanel.add(periodSpreadPenaltyField);
        formPanel.add(new JLabel("Exams of mixed duration penalty"));
        mixedDurationPenaltyField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        formPanel.add(mixedDurationPenaltyField);
        formPanel.add(new JLabel("Front load: large exam size"));
        frontLoadLargeTopicSizeField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        frontLoadLargeTopicSizeField.setEnabled(false); // Not yet implemented: requires Topic changes if changed
        formPanel.add(frontLoadLargeTopicSizeField);
        formPanel.add(new JLabel("Front load: last period size"));
        frontLoadLastPeriodSizeField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        frontLoadLastPeriodSizeField.setEnabled(false); // Not yet implemented: requires Period changes if changed
        formPanel.add(frontLoadLastPeriodSizeField);
        formPanel.add(new JLabel("Front load: penalty"));
        frontLoadPenaltyField = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        formPanel.add(frontLoadPenaltyField);
        return formPanel;
    }

    public void setExaminationConstraintConfiguration(ExaminationConstraintConfiguration constraintConfiguration) {
        twoInARowPenaltyField.setValue(constraintConfiguration.getTwoInARowPenalty());
        twoInADayPenaltyField.setValue(constraintConfiguration.getTwoInADayPenalty());
        periodSpreadLengthField.setValue(constraintConfiguration.getPeriodSpreadLength());
        periodSpreadPenaltyField.setValue(constraintConfiguration.getPeriodSpreadPenalty());
        mixedDurationPenaltyField.setValue(constraintConfiguration.getMixedDurationPenalty());
        frontLoadLargeTopicSizeField.setValue(constraintConfiguration.getFrontLoadLargeTopicSize());
        frontLoadLastPeriodSizeField.setValue(constraintConfiguration.getFrontLoadLastPeriodSize());
        frontLoadPenaltyField.setValue(constraintConfiguration.getFrontLoadPenalty());
        setLocationRelativeTo(examinationPanel.getTopLevelAncestor());
    }

    private void save() {
        final int twoInARowPenalty = (Integer) twoInARowPenaltyField.getValue();
        final int twoInADayPenalty = (Integer) twoInADayPenaltyField.getValue();
        final int periodSpreadLength = (Integer) periodSpreadLengthField.getValue();
        final int periodSpreadPenalty = (Integer) periodSpreadPenaltyField.getValue();
        final int mixedDurationPenalty = (Integer) mixedDurationPenaltyField.getValue();
        final int frontLoadLargeTopicSize = (Integer) frontLoadLargeTopicSizeField.getValue();
        final int frontLoadLastPeriodSize = (Integer) frontLoadLastPeriodSizeField.getValue();
        final int frontLoadPenalty = (Integer) frontLoadPenaltyField.getValue();
        setVisible(false);
        examinationPanel.doProblemFactChange(scoreDirector -> {
            Examination examination = scoreDirector.getWorkingSolution();
            ExaminationConstraintConfiguration constraintConfiguration = examination.getConstraintConfiguration();
            scoreDirector.beforeProblemPropertyChanged(constraintConfiguration);
            constraintConfiguration.setTwoInARowPenalty(twoInARowPenalty);
            constraintConfiguration.setTwoInADayPenalty(twoInADayPenalty);
            constraintConfiguration.setPeriodSpreadLength(periodSpreadLength);
            constraintConfiguration.setPeriodSpreadPenalty(periodSpreadPenalty);
            constraintConfiguration.setMixedDurationPenalty(mixedDurationPenalty);
            constraintConfiguration.setFrontLoadLargeTopicSize(frontLoadLargeTopicSize);
            constraintConfiguration.setFrontLoadLastPeriodSize(frontLoadLastPeriodSize);
            constraintConfiguration.setFrontLoadPenalty(frontLoadPenalty);
            scoreDirector.afterProblemPropertyChanged(constraintConfiguration);
        }, true);
    }

}
