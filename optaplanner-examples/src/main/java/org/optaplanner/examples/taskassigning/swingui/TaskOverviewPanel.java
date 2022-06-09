package org.optaplanner.examples.taskassigning.swingui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Skill;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
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
        List<Task> unassignedTaskList = new ArrayList<>(taskAssigningSolution.getTaskList());

        int rowIndex = 0;
        for (Employee employee : employeeList) {
            add(createEmployeeLabel(employee, rowIndex));
            rowIndex++;
        }

        rowIndex = 0;
        for (Employee employee : employeeList) {
            for (Task task : employee.getTasks()) {
                add(createTaskButton(task, rowIndex));
                unassignedTaskList.remove(task);
            }
            rowIndex++;
        }

        for (Task task : unassignedTaskList) {
            add(createTaskButton(task, rowIndex));
            rowIndex++;
        }

        int maxUnassignedTaskDuration = unassignedTaskList.stream().mapToInt(Task::getDuration).max().orElse(0);
        int maxEmployeeEndTime = employeeList.stream().mapToInt(Employee::getEndTime).max().orElse(0);
        int taskTableWidth = Math.max(maxEmployeeEndTime, maxUnassignedTaskDuration + consumedDuration);

        for (int timeGrain = 0; timeGrain < taskTableWidth; timeGrain += TIME_COLUMN_WIDTH) {
            add(createTimeLabel(timeGrain));
        }
        if (taskTableWidth % TIME_COLUMN_WIDTH != 0) {
            taskTableWidth += TIME_COLUMN_WIDTH - (taskTableWidth % TIME_COLUMN_WIDTH);
        }

        Dimension size = new Dimension(taskTableWidth + HEADER_COLUMN_WIDTH, HEADER_ROW_HEIGHT + rowIndex * ROW_HEIGHT);
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

    private JLabel createEmployeeLabel(Employee employee, int rowIndex) {
        JLabel employeeLabel = new JLabel(employee.getLabel(), new TaskOrEmployeeIcon(employee), SwingConstants.LEFT);
        employeeLabel.setOpaque(true);
        employeeLabel.setToolTipText(employee.getToolText());
        employeeLabel.setLocation(0, HEADER_ROW_HEIGHT + rowIndex * ROW_HEIGHT);
        employeeLabel.setSize(HEADER_COLUMN_WIDTH, ROW_HEIGHT);
        employeeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return employeeLabel;
    }

    private JButton createTaskButton(Task task, int rowIndex) {
        JButton taskButton = SwingUtils.makeSmallButton(new JButton(new TaskAction(task)));
        taskButton.setBackground(TangoColorFactory.ALUMINIUM_1);
        taskButton.setHorizontalTextPosition(SwingConstants.CENTER);
        taskButton.setVerticalTextPosition(SwingConstants.TOP);
        taskButton.setSize(task.getDuration(), ROW_HEIGHT);
        int x = HEADER_COLUMN_WIDTH + (task.getEmployee() == null ? task.getReadyTime() : task.getStartTime());
        int y = HEADER_ROW_HEIGHT + rowIndex * ROW_HEIGHT;
        taskButton.setLocation(x, y);
        return taskButton;
    }

    private JLabel createTimeLabel(int timeGrain) {
        // Use 10 hours per day
        int minutesInDay = timeGrain % (10 * 60);
        // Start at 8:00
        int hours = 8 + (minutesInDay / 60);
        int minutesInHour = minutesInDay % 60;
        JLabel timeLabel = new JLabel(String.format("%02d:%02d", hours, minutesInHour));
        timeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        timeLabel.setLocation(timeGrain + HEADER_COLUMN_WIDTH, 0);
        timeLabel.setSize(TIME_COLUMN_WIDTH, ROW_HEIGHT);
        return timeLabel;
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
            JComboBox<Integer> indexListField = new JComboBox<>();
            JComboBox<Employee> employeeListField = new JComboBox<>(
                    new Vector<>(taskAssigningPanel.getSolution().getEmployeeList()));
            employeeListField.addItemListener(itemEvent -> {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    // When en employee is selected, populate the index combo with indexes in the selected employee's task list.
                    indexListField.setModel(new DefaultComboBoxModel<>(availableIndexes((Employee) itemEvent.getItem())));
                }
            });
            LabeledComboBoxRenderer.applyToComboBox(employeeListField);
            selectCurrentEmployee(employeeListField);

            JCheckBox unassignCheckBox = new JCheckBox("Or unassign.");
            unassignCheckBox.addActionListener(checkBoxEvent -> {
                employeeListField.setEnabled(!unassignCheckBox.isSelected());
                indexListField.setEnabled(!unassignCheckBox.isSelected());
            });
            unassignCheckBox.setVisible(task.getEmployee() != null);

            JPanel listFieldsPanel = new JPanel(new GridLayout(4, 1));
            listFieldsPanel.add(new JLabel("Select employee and index:"));
            listFieldsPanel.add(employeeListField);
            listFieldsPanel.add(indexListField);
            listFieldsPanel.add(unassignCheckBox);
            int result = JOptionPane.showConfirmDialog(TaskOverviewPanel.this.getRootPane(),
                    listFieldsPanel, "Move " + task.getCode(),
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Employee selectedEmployee = (Employee) employeeListField.getSelectedItem();
                Integer selectedIndex = (Integer) indexListField.getSelectedItem();
                doProblemChange(selectedEmployee, selectedIndex, unassignCheckBox.isSelected());
                taskAssigningPanel.getSolverAndPersistenceFrame().resetScreen();
            }
        }

        private Vector<Integer> availableIndexes(Employee selectedEmployee) {
            int availableIndexes = selectedEmployee.getTasks().size();
            if (selectedEmployee == task.getEmployee()) {
                availableIndexes--;
            }
            return IntStream.rangeClosed(0, availableIndexes)
                    .boxed()
                    .collect(Collectors.toCollection(Vector::new));
        }

        private void selectCurrentEmployee(JComboBox<Employee> employeeListField) {
            // Without selecting null first, the next select wouldn't call the item listener if the selected employee
            // is the first on the list (and the index combo wouldn't be populated).
            employeeListField.setSelectedItem(null);
            if (task.getEmployee() == null) {
                employeeListField.setSelectedIndex(0);
            } else {
                employeeListField.setSelectedItem(task.getEmployee());
            }
        }

        private void doProblemChange(Employee selectedEmployee, Integer selectedIndex, boolean unassignTask) {
            SolutionBusiness<TaskAssigningSolution, ?> solutionBusiness = taskAssigningPanel.getSolutionBusiness();
            if (unassignTask) {
                solutionBusiness.doProblemChange(
                        (workingSolution, problemChangeDirector) -> problemChangeDirector.changeVariable(selectedEmployee,
                                "tasks",
                                e -> e.getTasks().remove((int) selectedIndex)));
            } else {
                if (task.getEmployee() == null) {
                    solutionBusiness.doProblemChange(
                            (workingSolution, problemChangeDirector) -> problemChangeDirector.changeVariable(selectedEmployee,
                                    "tasks",
                                    e -> e.getTasks().add(selectedIndex, task)));
                } else {
                    solutionBusiness.doProblemChange((workingSolution, problemChangeDirector) -> {
                        Task workingTask = problemChangeDirector.lookUpWorkingObjectOrFail(task);
                        problemChangeDirector.changeVariable(task.getEmployee(), "tasks",
                                e -> e.getTasks().remove(workingTask));
                        problemChangeDirector.changeVariable(selectedEmployee, "tasks",
                                e -> e.getTasks().add(selectedIndex, workingTask));
                    });
                }
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
            return (getParent().getWidth() > getPreferredSize().width);
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (getParent().getHeight() > getPreferredSize().height);
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
            skillColorList = task.getTaskType().getRequiredSkillList().stream()
                    .map(skillColorFactory::pickColor)
                    .collect(Collectors.toList());
            affinityIcon = affinityIcons[task.getAffinity().ordinal()];
        }

        private TaskOrEmployeeIcon(Employee employee) {
            priorityIcon = null;
            skillColorList = employee.getSkillSet().stream()
                    .sorted(Comparator.comparing(Skill::getName))
                    .map(skillColorFactory::pickColor)
                    .collect(Collectors.toList());
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
