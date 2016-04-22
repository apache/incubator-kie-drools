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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TaskOverviewPanel extends JPanel implements Scrollable {

    public static final int HEADER_ROW_HEIGHT = 40;
    public static final int HEADER_COLUMN_WIDTH = 150;
    public static final int ROW_HEIGHT = 40;
    public static final int TIME_COLUMN_WIDTH = 60;

    public TaskOverviewPanel() {
        setLayout(null);
        setMinimumSize(new Dimension(HEADER_COLUMN_WIDTH * 2, ROW_HEIGHT * 8));
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
            employeeLabel.setLocation(0, HEADER_ROW_HEIGHT + employeeIndex * ROW_HEIGHT);
            employeeLabel.setSize(HEADER_COLUMN_WIDTH, ROW_HEIGHT);
            employeeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            add(employeeLabel);
            employeeIndexMap.put(employee, employeeIndex);
            employeeIndex++;
        }
        int panelWidth = HEADER_COLUMN_WIDTH;
        int unassignedIndex = employeeList.size();
        for (Task task : taskAssigningSolution.getTaskList()) {
            TaskPanel taskPanel = new TaskPanel(task);
            taskPanel.setToolTipText(task.getToolText());
            taskPanel.setBackground(tangoColorFactory.pickColor(task.getTaskType()));
            int x;
            int y;
            if (task.getEmployee() != null) {
                x = HEADER_COLUMN_WIDTH + task.getStartTime();
                y = HEADER_ROW_HEIGHT + employeeIndexMap.get(task.getEmployee()) * ROW_HEIGHT;
            } else {
                x = HEADER_COLUMN_WIDTH;
                y = unassignedIndex * ROW_HEIGHT;
                unassignedIndex++;
            }
            if (x + taskPanel.getWidth() > panelWidth) {
                panelWidth = x + taskPanel.getWidth();
            }
            taskPanel.setLocation(x, y);
            add(taskPanel);
        }
        for (int x = HEADER_COLUMN_WIDTH; x < panelWidth; x += TIME_COLUMN_WIDTH) {
            // Start at 8:00
            int minutes = (8 * 60 + (x - HEADER_COLUMN_WIDTH)) % (24 * 60);
            int hours = minutes / 60;
            minutes %= 60;
            JLabel timeLabel = new JLabel((hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes);
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            timeLabel.setLocation(x, 0);
            timeLabel.setSize(TIME_COLUMN_WIDTH, ROW_HEIGHT);
            add(timeLabel);
        }
        if ((panelWidth - HEADER_COLUMN_WIDTH) % TIME_COLUMN_WIDTH != 0) {
            panelWidth = panelWidth - ((panelWidth - HEADER_COLUMN_WIDTH) % TIME_COLUMN_WIDTH) + TIME_COLUMN_WIDTH;
        }

        Dimension size = new Dimension(panelWidth, HEADER_ROW_HEIGHT + unassignedIndex * ROW_HEIGHT);
        setSize(size);
        setPreferredSize(size);
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

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return SolutionPanel.PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return false;
    }

}
