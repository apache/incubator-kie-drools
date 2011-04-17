/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.tsp.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.drools.planner.core.move.Move;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.common.swingui.WorkflowFrame;
import org.drools.planner.examples.tsp.domain.CityAssignment;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;
import org.drools.planner.examples.tsp.solver.move.SubTourChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class TspPanel extends SolutionPanel {

    private TspWorldPanel tspWorldPanel;
    private TspListPanel tspListPanel;

    public TspPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tspWorldPanel = new TspWorldPanel(this);
        tspWorldPanel.setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
        tabbedPane.add("World", tspWorldPanel);
        tspListPanel = new TspListPanel(this);
        JScrollPane tspListScrollPane = new JScrollPane(tspListPanel);
        tabbedPane.add("List", tspListScrollPane);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    public TravelingSalesmanTour getTravelingSalesmanTour() {
        return (TravelingSalesmanTour) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        tspWorldPanel.resetPanel();
        tspListPanel.resetPanel();
    }

    public void doMove(Move move) {
        solutionBusiness.doMove(move);
    }

    public WorkflowFrame getWorkflowFrame() {
        return workflowFrame;
    }

}
