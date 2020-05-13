/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.coachshuttlegathering.swingui;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class CoachShuttleGatheringPanel extends SolutionPanel<CoachShuttleGatheringSolution> {

    public static final String LOGO_PATH =
            "/org/optaplanner/examples/coachshuttlegathering/swingui/coachShuttleGatheringLogo.png";

    private CoachShuttleGatheringWorldPanel coachShuttleGatheringWorldPanel;

    public CoachShuttleGatheringPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        coachShuttleGatheringWorldPanel = new CoachShuttleGatheringWorldPanel(this);
        coachShuttleGatheringWorldPanel.setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
        tabbedPane.add("World", coachShuttleGatheringWorldPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(CoachShuttleGatheringSolution solution) {
        coachShuttleGatheringWorldPanel.resetPanel(solution);
    }

    @Override
    public void updatePanel(CoachShuttleGatheringSolution solution) {
        coachShuttleGatheringWorldPanel.updatePanel(solution);
    }

}
