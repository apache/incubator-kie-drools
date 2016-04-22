/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.taskassigning.swingui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.swing.impl.TangoColorFactory;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class TaskAssigningPanel extends SolutionPanel<TaskAssigningSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/taskassigning/swingui/taskAssigningLogo.png";

    private TaskOverviewPanel taskOverviewPanel;

    public TaskAssigningPanel() {
        setLayout(new BorderLayout());
        taskOverviewPanel = new TaskOverviewPanel();
        add(taskOverviewPanel, BorderLayout.CENTER);
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    @Override
    public void resetPanel(TaskAssigningSolution taskAssigningSolution) {
        taskOverviewPanel.resetPanel(taskAssigningSolution);
    }

}
