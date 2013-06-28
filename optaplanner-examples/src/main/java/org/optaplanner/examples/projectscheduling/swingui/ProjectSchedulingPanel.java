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

package org.optaplanner.examples.projectscheduling.swingui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.projectscheduling.domain.ProjectsSchedule;

public class ProjectSchedulingPanel extends SolutionPanel {

    public ProjectSchedulingPanel() {
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private ProjectsSchedule getProjectsSchedule() {
        return (ProjectsSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        ProjectsSchedule projectsSchedule = (ProjectsSchedule) solution;
        updatePanel(solution);
    }

    @Override
    public void updatePanel(Solution solution) {
    }

}
