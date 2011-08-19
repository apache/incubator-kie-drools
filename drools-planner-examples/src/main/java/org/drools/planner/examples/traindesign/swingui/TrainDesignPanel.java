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

package org.drools.planner.examples.traindesign.swingui;

import java.awt.Color;
import java.awt.GridLayout;
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

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.traindesign.domain.TrainDesign;

/**
 * TODO this code is highly unoptimized
 */
public class TrainDesignPanel extends SolutionPanel {

    private GridLayout gridLayout;

    public TrainDesignPanel() {
        gridLayout = new GridLayout(0, 1);
        setLayout(gridLayout);
    }

    public void resetPanel(Solution solution) {
        removeAll();
        TrainDesign trainDesign = (TrainDesign) solution;
    }

}
