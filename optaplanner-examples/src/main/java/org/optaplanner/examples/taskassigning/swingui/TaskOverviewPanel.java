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

package org.optaplanner.examples.taskassigning.swingui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Skill;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskOrEmployee;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TaskOverviewPanel extends JPanel implements Scrollable {

    public static final int HEADER_ROW_HEIGHT = 50;
    public static final int HEADER_COLUMN_WIDTH = 150;
    public static final int ROW_HEIGHT = 50;
    public static final int TIME_COLUMN_WIDTH = 60;

    private final TaskAssigningPanel taskAssigningPanel;
    private final ImageIcon[] affinityIcons;
    private final ImageIcon[] priorityIcons;

    private TangoColorFactory skillColorFactory;

    private int consumedDuration = 0;

    public TaskOverviewPanel(TaskAssigningPanel taskAssigningPanel) {
        this.taskAssigningPanel = taskAssigningPanel;
        affinityIcons = new ImageIcon[] {
                new ImageIcon(getClass().getResource("affinityNone.png")),
                new ImageIcon(getClass().getResource("affinityLow.png")),
                new ImageIcon(getClass().getResource("affinityMedium.png")),
                new ImageIcon(getClass().getResource("affinityHigh.png"))
        };
        priorityIcons = new ImageIcon[] {
                new ImageIcon(getClass().getResource("priorityMinor.png")),
                new ImageIcon(getClass().getResource("priorityMajor.png")),
                new ImageIcon(getClass().getResource("priorityCritical.png"))
        };
        setLayout(null);
        setMinimumSize(new Dimension(HEADER_COLUMN_WIDTH * 2, ROW_HEIGHT * 8));
    }

    public void resetPanel(TaskAssigningSolution taskAssigningSolution) {
        removeAll();
        skillColorFactory = new TangoColorFactory();
        List<Employee> employeeList = taskAssigningSolution.getEmployeeList();
        Map<Employee, Integer> employeeIndexMap = new HashMap<>(employeeList.size());
        int employeeIndex = 0;
        for (Employee employee : employeeList) {
            JLabel employeeLabel = new JLabel(employee.getLabel(), new TaskOrEmployeeIcon(employee), SwingConstants.LEFT);
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
            JButton taskButton = createTaskButton(task);
            int x;
            int y;
            if (task.getEmployee() != null) {
                x = HEADER_COLUMN_WIDTH + task.getStartTime();
                y = HEADER_ROW_HEIGHT + employeeIndexMap.get(task.getEmployee()) * ROW_HEIGHT;
            } else {
                x = HEADER_COLUMN_WIDTH + task.getReadyTime();
                y = HEADER_ROW_HEIGHT + unassignedIndex * ROW_HEIGHT;
                unassignedIndex++;
            }
            if (x + taskButton.getWidth() > panelWidth) {
                panelWidth = x + taskButton.getWidth();
            }
            taskButton.setLocation(x, y);
            add(taskButton);
        }
        for (int x = HEADER_COLUMN_WIDTH; x < panelWidth; x += TIME_COLUMN_WIDTH) {
            // Use 10 hours per day
            int minutes = (x - HEADER_COLUMN_WIDTH) % (10 * 60);
            // Start at 8:00
            int hours = 8 + (minutes / 60);
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

    public void setConsumedDuration(int consumedDuration) {
        this.consumedDuration = consumedDuration;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(TangoColorFactory.ALUMINIUM_2);
        int lineX = HEADER_COLUMN_WIDTH + consumedDuration;
        g.fillRect(HEADER_COLUMN_WIDTH, 0, lineX, getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(lineX, 0, getWidth(), getHeight());
    }

    private JButton createTaskButton(Task task) {
        JButton taskButton = SwingUtils.makeSmallButton(new JButton(new TaskAction(task)));
        taskButton.setBackground(task.isPinned() ? TangoColorFactory.ALUMINIUM_3 : TangoColorFactory.ALUMINIUM_1);
        taskButton.setHorizontalTextPosition(SwingConstants.CENTER);
        taskButton.setVerticalTextPosition(SwingConstants.TOP);
        taskButton.setSize(task.getDuration(), ROW_HEIGHT);
        return taskButton;
    }

    private class TaskAction extends AbstractAction {

        private final Task task;

        public TaskAction(Task task) {
            super(task.getCode(), new TaskOrEmployeeIcon(task));
            this.task = task;
            // Tooltip
            putValue(SHORT_DESCRIPTION, task.getToolText());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<TaskOrEmployee> taskOrEmployeeList = new ArrayList<>();
            taskOrEmployeeList.addAll(taskAssigningPanel.getSolution().getEmployeeList());
            taskOrEmployeeList.addAll(taskAssigningPanel.getSolution().getTaskList());
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox TaskOrEmployeeListField = new JComboBox(
                    taskOrEmployeeList.toArray(new Object[taskOrEmployeeList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(TaskOrEmployeeListField);
            TaskOrEmployeeListField.setSelectedItem(task.getPreviousTaskOrEmployee());
            listFieldsPanel.add(TaskOrEmployeeListField);
            int result = JOptionPane.showConfirmDialog(TaskOverviewPanel.this.getRootPane(),
                    listFieldsPanel, "Select previous task or employee for " + task.getLabel(),
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                TaskOrEmployee toTaskOrEmployee = (TaskOrEmployee) TaskOrEmployeeListField.getSelectedItem();
                taskAssigningPanel.getSolutionBusiness().doChangeMove(task, "previousTaskOrEmployee", toTaskOrEmployee);
                taskAssigningPanel.getSolverAndPersistenceFrame().resetScreen();
            }
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

    private class TaskOrEmployeeIcon implements Icon {

        private static final int SKILL_ICON_WIDTH = 8;
        private static final int SKILL_ICON_HEIGHT = 16;

        private final ImageIcon priorityIcon;
        private final List<Color> skillColorList;
        private final ImageIcon affinityIcon;

        private TaskOrEmployeeIcon(Task task) {
            priorityIcon = priorityIcons[task.getPriority().ordinal()];
            List<Skill> skillList = task.getTaskType().getRequiredSkillList();
            skillColorList = new ArrayList<>(skillList.size());
            for (Skill skill : skillList) {
                skillColorList.add(skillColorFactory.pickColor(skill));
            }
            affinityIcon = affinityIcons[task.getAffinity().ordinal()];
        }

        private TaskOrEmployeeIcon(Employee employee) {
            priorityIcon = null;
            Set<Skill> skillSet = employee.getSkillSet();
            skillColorList = new ArrayList<>(skillSet.size());
            for (Skill skill : skillSet) {
                skillColorList.add(skillColorFactory.pickColor(skill));
            }
            affinityIcon = null;
        }

        @Override
        public int getIconWidth() {
            int width = 0;
            if (priorityIcon != null) {
                width += priorityIcon.getIconWidth();
            }
            width += skillColorList.size() * SKILL_ICON_WIDTH;
            if (affinityIcon != null) {
                width += affinityIcon.getIconWidth();
            }
            return width;
        }

        @Override
        public int getIconHeight() {
            int height = SKILL_ICON_HEIGHT;
            if (priorityIcon != null && priorityIcon.getIconHeight() > height) {
                height = priorityIcon.getIconHeight();
            }
            if (affinityIcon != null && affinityIcon.getIconHeight() > height) {
                height = affinityIcon.getIconHeight();
            }
            return height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int innerX = x;
            if (priorityIcon != null) {
                priorityIcon.paintIcon(c, g, innerX, y);
                innerX += priorityIcon.getIconWidth();
            }
            for (Color skillColor : skillColorList) {
                g.setColor(skillColor);
                g.fillRect(innerX + 1, y + 1, SKILL_ICON_WIDTH - 2, SKILL_ICON_HEIGHT - 2);
                g.setColor(TangoColorFactory.ALUMINIUM_5);
                g.drawRect(innerX + 1, y + 1, SKILL_ICON_WIDTH - 2, SKILL_ICON_HEIGHT - 2);
                innerX += SKILL_ICON_WIDTH;
            }
            if (affinityIcon != null) {
                affinityIcon.paintIcon(c, g, innerX, y);
                innerX += affinityIcon.getIconWidth();
            }
        }

    }

}
