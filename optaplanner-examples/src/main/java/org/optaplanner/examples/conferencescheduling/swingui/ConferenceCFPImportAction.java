/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingCfpDevoxxImporter;

public class ConferenceCFPImportAction implements CommonApp.ExtraAction<ConferenceSolution> {

    @Override
    public String getName() {
        return "Import from CFP";
    }

    @Override
    public BiConsumer<SolutionBusiness<ConferenceSolution, ?>, SolutionPanel<ConferenceSolution>> getConsumer() {
        return (solutionBusiness, solutionPanel) -> {
            String[] cfpArray = { "cfp-devoxx" };
            JComboBox<String> cfpConferenceBox = new JComboBox<>(cfpArray);
            JTextField cfpRestUrlTextField = new JTextField("https://dvbe18.confinabox.com/api/conferences/DVBE18");
            Object[] dialogue = {
                    "Choose conference:", cfpConferenceBox,
                    "Enter CFP REST Url:", cfpRestUrlTextField,
            };

            int option = JOptionPane.showConfirmDialog(solutionPanel, dialogue, "Import", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String conferenceBaseUrl = cfpRestUrlTextField.getText();
                new ConferenceCFPImportWorker(solutionBusiness, solutionPanel, conferenceBaseUrl)
                        .executeAndShowDialog();
            }
        };
    }

    private class ConferenceCFPImportWorker extends SwingWorker<ConferenceSolution, Void> {

        private final SolutionBusiness<ConferenceSolution, ?> solutionBusiness;
        private final SolutionPanel<ConferenceSolution> solutionPanel;
        private String conferenceBaseUrl;

        private final JDialog dialog;

        public ConferenceCFPImportWorker(SolutionBusiness<ConferenceSolution, ?> solutionBusiness,
                SolutionPanel<ConferenceSolution> solutionPanel,
                String conferenceBaseUrl) {
            this.solutionBusiness = solutionBusiness;
            this.solutionPanel = solutionPanel;
            this.conferenceBaseUrl = conferenceBaseUrl;
            dialog = new JDialog(solutionPanel.getSolverAndPersistenceFrame(), true);
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
            dialog.setLocationRelativeTo(solutionPanel.getSolverAndPersistenceFrame());
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
            ConferenceSolution cfpProblem;
            try {
                cfpProblem = get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Importing was interrupted.", e);
            } catch (ExecutionException e) {
                JOptionPane.showMessageDialog(solutionPanel,
                        "CFP import failed.\nThe next dialog will explain the cause.\n\n"
                                + "Fix it in ConferenceSchedulingCfpDevoxxImporter.java in the optaplanner repository.");
                throw new IllegalStateException("Importing failed.", e.getCause());
            }
            solutionBusiness.setSolution(cfpProblem);
            solutionBusiness.setSolutionFileName(solutionBusiness.getSolution().getConferenceName());
            JOptionPane.showMessageDialog(solutionPanel, "CFP data imported successfully.");
            solutionPanel.getSolverAndPersistenceFrame().setSolutionLoaded(null);
        }
    }
}
