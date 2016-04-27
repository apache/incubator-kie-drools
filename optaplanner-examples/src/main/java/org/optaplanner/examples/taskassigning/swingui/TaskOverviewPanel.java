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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.pas.domain.RequiredPatientEquipment;
import org.optaplanner.examples.pas.domain.RoomEquipment;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Skill;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TaskOverviewPanel extends JPanel implements Scrollable {

    public static final int HEADER_ROW_HEIGHT = 40;
    public static final int HEADER_COLUMN_WIDTH = 150;
    public static final int ROW_HEIGHT = 40;
    public static final int TIME_COLUMN_WIDTH = 60;

    private TangoColorFactory customerColorFactory;
    private TangoColorFactory skillColorFactory;

    private int consumedDuration = 0;

    public TaskOverviewPanel() {
        setLayout(null);
        setMinimumSize(new Dimension(HEADER_COLUMN_WIDTH * 2, ROW_HEIGHT * 8));
    }

    public void resetPanel(TaskAssigningSolution taskAssigningSolution) {
        removeAll();
        customerColorFactory = new TangoColorFactory();
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
            TaskPanel taskPanel = new TaskPanel(task);
            taskPanel.setToolTipText(task.getToolText());
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

    public void setConsumedDuration(int consumedDuration) {
        this.consumedDuration = consumedDuration;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(TangoColorFactory.ALUMINIUM_3);
        int lineX = HEADER_COLUMN_WIDTH + consumedDuration;
        g.fillRect(HEADER_COLUMN_WIDTH, 0, lineX, getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(lineX, 0, getWidth(), getHeight());
    }

    private class TaskPanel extends JPanel {

        private final Task task;

        public TaskPanel(Task task) {
            this.task = task;
            setLayout(null);
            setBackground(task.isLocked() ? TangoColorFactory.ALUMINIUM_3 : TangoColorFactory.ALUMINIUM_1);
            setSize(task.getDuration(), ROW_HEIGHT);
            JLabel codeLabel = new JLabel(task.getCode(), new TaskOrEmployeeIcon(task), SwingConstants.CENTER);
            codeLabel.setLocation(0, 0);
            codeLabel.setSize(task.getDuration(), ROW_HEIGHT / 2);
            add(codeLabel);
            JLabel titleLabel = new JLabel(task.getTitle(), SwingConstants.CENTER);
            titleLabel.setLocation(0, ROW_HEIGHT / 2);
            titleLabel.setSize(task.getDuration(), ROW_HEIGHT / 2);
            add(titleLabel);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
        private static final int CUSTOMER_SKILL_GAP = 4;
        private static final int CUSTOMER_ICON_WIDTH = 8;
        private static final int CUSTOMER_ICON_HEIGHT = 16;

        private final Color customerColor;
        private final List<Color> skillColorList;

        private TaskOrEmployeeIcon(Task task) {
            customerColor = customerColorFactory.pickColor(task.getCustomer());
            List<Skill> skillList = task.getTaskType().getRequiredSkillList();
            skillColorList = new ArrayList<>(skillList.size());
            for (Skill skill : skillList) {
                skillColorList.add(skillColorFactory.pickColor(skill));
            }
        }

        private TaskOrEmployeeIcon(Employee employee) {
            customerColor = null;
            Set<Skill> skillSet = employee.getSkillSet();
            skillColorList = new ArrayList<>(skillSet.size());
            for (Skill skill : skillSet) {
                skillColorList.add(skillColorFactory.pickColor(skill));
            }
        }

        @Override
        public int getIconWidth() {
            return skillColorList.size() * SKILL_ICON_WIDTH + (customerColor == null ? 0 : CUSTOMER_ICON_WIDTH + CUSTOMER_SKILL_GAP);
        }

        @Override
        public int getIconHeight() {
            return Math.max(SKILL_ICON_HEIGHT, CUSTOMER_ICON_HEIGHT);
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int innerX = x;
            for (Color skillColor : skillColorList) {
                g.setColor(skillColor);
                g.fillRect(innerX + 1, y + 1, SKILL_ICON_WIDTH - 2, SKILL_ICON_HEIGHT - 2);
                g.setColor(TangoColorFactory.ALUMINIUM_5);
                g.drawRect(innerX + 1, y + 1, SKILL_ICON_WIDTH - 2, SKILL_ICON_HEIGHT - 2);
                innerX += SKILL_ICON_WIDTH;
            }
            innerX += CUSTOMER_SKILL_GAP;
            if (customerColor != null) {
                g.setColor(customerColor);
                g.fillOval(innerX + 1, y + 4, CUSTOMER_ICON_WIDTH - 2, CUSTOMER_ICON_HEIGHT - 8);
                g.setColor(TangoColorFactory.ALUMINIUM_5);
                g.drawOval(innerX + 1, y + 4, CUSTOMER_ICON_WIDTH - 2, CUSTOMER_ICON_HEIGHT - 8);
                innerX += CUSTOMER_ICON_WIDTH;
            }
        }

    }

}
