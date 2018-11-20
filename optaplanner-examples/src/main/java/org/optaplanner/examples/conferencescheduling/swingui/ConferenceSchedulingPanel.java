/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.conferencescheduling.swingui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingCfpDevoxxImporter;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingPanel extends SolutionPanel<ConferenceSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/conferencescheduling/swingui/conferenceSchedulingLogo.png";

    public ConferenceSchedulingPanel() {
        JButton importConferenceButton = new JButton("Import from CFP");
        importConferenceButton.addActionListener(event -> {
            String[] cfpArray = {"devoxx-cfp"};
            JComboBox cfpConferenceBox = new JComboBox(cfpArray);
            JTextField cfpRestUrlTextField = new JTextField("https://dvbe18.confinabox.com/api/conferences/DVBE18");
            Object[] dialogue = {
                    "Choose conference:", cfpConferenceBox,
                    "Enter CFP REST Url:", cfpRestUrlTextField
            };

            int option = JOptionPane.showConfirmDialog(this, dialogue, "Import", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String conferenceBaseUrl = cfpRestUrlTextField.getText();
                new ImporterWorker(conferenceBaseUrl).executeAndShowDialog();
            }
        });

        JButton publishButton = new JButton("Publish");
        publishButton.addActionListener(actionEvent -> {
            solutionBusiness.getSolution().getTalkList().forEach(talk -> {
                talk.setPublishedTimeslot(talk.getTimeslot());
                talk.setPublishedRoom(talk.getRoom());
            });
        });

        JButton showInLibreOfficeOrExcelButton = new JButton("Show in LibreOffice or Excel");
        showInLibreOfficeOrExcelButton.addActionListener(event -> {
            SolutionFileIO<ConferenceSolution> solutionFileIO = new ConferenceSchedulingXlsxFileIO();
            File tempFile;
            try {
                tempFile = File.createTempFile(solutionBusiness.getSolutionFileName(), "." + solutionFileIO.getOutputFileExtension());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create temp file.", e);
            }
            solutionFileIO.write(solutionBusiness.getSolution(), tempFile);
            try {
                Desktop.getDesktop().open(tempFile);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to show temp file (" + tempFile + ") in LibreOffice or Excel.", e);
            }
        });

        JPanel showPanel = new JPanel();
        JPanel importPanel = new JPanel();
        showPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        importPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        showPanel.add(showInLibreOfficeOrExcelButton);
        showPanel.add(new JLabel("Changes to that file are ignored unless you explicitly save it there and open it here."));
        importPanel.add(importConferenceButton);
        importPanel.add(publishButton);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1));
        buttonsPanel.add(showPanel);
        buttonsPanel.add(importPanel);

        setLayout(new BorderLayout());
        add(buttonsPanel, BorderLayout.NORTH);
    }

    @Override
    public void resetPanel(ConferenceSolution solution) {
    }

    private class ImporterWorker extends SwingWorker<ConferenceSolution, Void> {

        private final String conferenceBaseUrl;
        private final JDialog dialog;

        public ImporterWorker(String conferenceBaseUrl) {
            this.conferenceBaseUrl = conferenceBaseUrl;
            dialog = new JDialog(ConferenceSchedulingPanel.this.getSolverAndPersistenceFrame(), true);
            JPanel contentPane = new JPanel(new BorderLayout(10, 10));
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            contentPane.add(new JLabel("Importing CFP data in progress..."), BorderLayout.NORTH);
            JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
            progressBar.setIndeterminate(true);
            contentPane.add(progressBar, BorderLayout.CENTER);
            JButton button = new JButton("Cancel");
            button.addActionListener(e -> cancel(false));
            contentPane.add(button, BorderLayout.SOUTH);
            dialog.setContentPane(contentPane);
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    cancel(false);
                }
            });
            dialog.pack();
            dialog.setLocationRelativeTo(ConferenceSchedulingPanel.this.getSolverAndPersistenceFrame());
        }

        public void executeAndShowDialog() {
            execute();
            dialog.setVisible(true);
        }

        @Override
        protected ConferenceSolution doInBackground() {
            return new ConferenceSchedulingCfpDevoxxImporter(conferenceBaseUrl).importSolution();
        }

        @Override
        protected void done() {
            dialog.dispose();
            if (isCancelled()) {
                return;
            }
            try {
                ConferenceSolution cfpProblem = get();
                solutionBusiness.setSolution(cfpProblem);
                JOptionPane.showMessageDialog(ConferenceSchedulingPanel.this, "CFP data imported successfully.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Importing was interrupted.", e);
            } catch (ExecutionException e) {
                JOptionPane.showMessageDialog(ConferenceSchedulingPanel.this,
                        "CFP data imported failed.\nThe next dialog will explain the cause.");
                throw new IllegalStateException("Importing failed.", e.getCause());
            }
        }

    }
}
