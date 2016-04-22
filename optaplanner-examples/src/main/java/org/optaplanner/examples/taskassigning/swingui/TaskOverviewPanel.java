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

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.swing.impl.TangoColorFactory;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class TaskOverviewPanel extends JPanel {

    public static final int HEADER_COLUMN_WIDTH = 150;
    public static final int ROW_HEIGHT = 40;

    public TaskOverviewPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
    }

    public void resetPanel(TaskAssigningSolution taskAssigningSolution) {
        removeAll();
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        List<Employee> employeeList = taskAssigningSolution.getEmployeeList();
        Map<Employee, Integer> employeeIndexMap = new HashMap<>(employeeList.size());
        int employeeIndex = 0;
        for (Employee employee : employeeList) {
            JLabel employeeLabel = new JLabel(employee.getLabel(), SwingConstants.LEFT);
            employeeLabel.setOpaque(true);
            employeeLabel.setToolTipText(employee.getToolText());
            employeeLabel.setLocation(0, employeeIndex * ROW_HEIGHT);
            employeeLabel.setSize(HEADER_COLUMN_WIDTH, ROW_HEIGHT);
            employeeLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            add(employeeLabel);
            employeeIndexMap.put(employee, employeeIndex);
            employeeIndex++;
        }
        int unassignedIndex = employeeList.size();
        for (Task task : taskAssigningSolution.getTaskList()) {
            TaskPanel taskPanel = new TaskPanel(task);
            taskPanel.setToolTipText(task.getToolText());
            taskPanel.setBackground(tangoColorFactory.pickColor(task.getTaskType()));
            int x;
            int y;
            if (task.getEmployee() != null) {
                x = HEADER_COLUMN_WIDTH + task.getStartTime();
                y = employeeIndexMap.get(task.getEmployee()) * ROW_HEIGHT;
            } else {
                x = HEADER_COLUMN_WIDTH;
                y = unassignedIndex * ROW_HEIGHT;
                unassignedIndex++;
            }
            taskPanel.setLocation(x, y);
            add(taskPanel);
        }
        repaint();
    }

    private class TaskPanel extends JPanel {

        private final Task task;

        public TaskPanel(Task task) {
            this.task = task;
            setLayout(null);
            setSize(task.getDuration(), ROW_HEIGHT);
            JLabel codeLabel = new JLabel(task.getCode(), SwingConstants.CENTER);
            codeLabel.setLocation(0, 0);
            codeLabel.setSize(task.getDuration(), ROW_HEIGHT / 2);
            add(codeLabel);
            JLabel titleLabel = new JLabel(task.getTitle(), SwingConstants.CENTER);
            titleLabel.setLocation(0, ROW_HEIGHT / 2);
            titleLabel.setSize(task.getDuration(), ROW_HEIGHT / 2);
            add(titleLabel);
        }

    }

}
