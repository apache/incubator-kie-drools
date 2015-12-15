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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;

public class CoachShuttleGatheringPanel extends SolutionPanel {

    // TODO Create logo
    public static final String LOGO_PATH = null;
//    public static final String LOGO_PATH = "/org/optaplanner/examples/coachshuttlegathering/swingui/coachShuttleGatheringLogo.png";

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
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public CoachShuttleGatheringSolution getCoachShuttleGatheringSolution() {
        return (CoachShuttleGatheringSolution) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution s) {
        CoachShuttleGatheringSolution solution = (CoachShuttleGatheringSolution) s;
        coachShuttleGatheringWorldPanel.resetPanel(solution);
    }

    @Override
    public void updatePanel(Solution s) {
        CoachShuttleGatheringSolution solution = (CoachShuttleGatheringSolution) s;
        coachShuttleGatheringWorldPanel.updatePanel(solution);
    }

}
