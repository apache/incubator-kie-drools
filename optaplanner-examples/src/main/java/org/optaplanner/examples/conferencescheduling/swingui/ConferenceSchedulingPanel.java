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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingCfpDevoxxImporter;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingPanel extends SolutionPanel<ConferenceSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/conferencescheduling/swingui/conferenceSchedulingLogo.png";

    public ConferenceSchedulingPanel() {
        JButton importConferenceButton = new JButton("Import conference");
        importConferenceButton.addActionListener(event -> {
            //TODO: Add a panel to get conferenceBaseUrl from the user
            ConferenceSchedulingCfpDevoxxImporter conferenceSchedulingImporter = new ConferenceSchedulingCfpDevoxxImporter("https://dvbe18.confinabox.com/api/conferences/DVBE18");
            solutionBusiness.setSolution(conferenceSchedulingImporter.importSolution());
        });

        JButton button = new JButton("Show in LibreOffice or Excel");
        button.addActionListener(event -> {
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
        add(importConferenceButton);
        add(button);
        add(new JLabel("Changes to that file are ignored unless you explicitly save it there and open it here."));
    }

    @Override
    public void resetPanel(ConferenceSolution solution) {
    }

}
