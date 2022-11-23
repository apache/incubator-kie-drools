package org.optaplanner.examples.taskassigning.swingui;

import static org.optaplanner.examples.taskassigning.persistence.TaskAssigningGenerator.BASE_DURATION_AVERAGE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.taskassigning.domain.Customer;
import org.optaplanner.examples.taskassigning.domain.Priority;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskType;

public class TaskAssigningPanel extends SolutionPanel<TaskAssigningSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/taskassigning/swingui/taskAssigningLogo.png";

    private final TaskOverviewPanel taskOverviewPanel;

    private JSpinner consumeRateField;
    private AbstractAction consumeAction;
    private Timer consumeTimer;
    private JSpinner produceRateField;
    private AbstractAction produceAction;
    private Timer produceTimer;

    private int consumedTimeInSeconds = 0;
    private int previousConsumedTime = 0; // In minutes
    private int producedTimeInSeconds = 0;
    private int previousProducedTime = 0; // In minutes
    private volatile Random producingRandom;

    public TaskAssigningPanel() {
        setLayout(new BorderLayout());
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        taskOverviewPanel = new TaskOverviewPanel(this);
        add(new JScrollPane(taskOverviewPanel), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 0));
        JPanel consumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consumePanel.add(new JLabel("Consume rate:"));
        consumeRateField = new JSpinner(new SpinnerNumberModel(600, 10, 3600, 10));
        consumePanel.add(consumeRateField);
        consumeTimer = new Timer(1000, e -> {
            consumedTimeInSeconds += (Integer) consumeRateField.getValue();
            consumeUpTo(consumedTimeInSeconds / 60);
            repaint();
        });
        consumeAction = new AbstractAction("Consume") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!consumeTimer.isRunning()) {
                    consumeRateField.setEnabled(false);
                    consumeTimer.start();
                } else {
                    consumeRateField.setEnabled(true);
                    consumeTimer.stop();
                }
            }
        };
        consumePanel.add(new JToggleButton(consumeAction));
        // FIXME remove this when https://issues.redhat.com/browse/PLANNER-2633 is done.
        Arrays.stream(consumePanel.getComponents()).forEach(component -> {
            component.setEnabled(false);
            if (component instanceof JComponent) {
                ((JComponent) component).setToolTipText("This feature is currently disabled.");
            }
        });
        headerPanel.add(consumePanel);
        JPanel producePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        producePanel.add(new JLabel("Produce rate:"));
        produceRateField = new JSpinner(new SpinnerNumberModel(600, 10, 3600, 10));
        producePanel.add(produceRateField);
        produceTimer = new Timer(1000, e -> {
            producedTimeInSeconds += (Integer) produceRateField.getValue();
            produceUpTo(producedTimeInSeconds / 60);
            repaint();
        });
        produceAction = new AbstractAction("Produce") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!produceTimer.isRunning()) {
                    produceRateField.setEnabled(false);
                    produceTimer.start();
                } else {
                    produceRateField.setEnabled(true);
                    produceTimer.stop();
                }
            }
        };
        producePanel.add(new JToggleButton(produceAction));
        headerPanel.add(producePanel);
        return headerPanel;
    }

    /**
     * @param consumedTime in minutes, just like {@link Task#getStartTime()}
     */
    public void consumeUpTo(final int consumedTime) {
        taskOverviewPanel.setConsumedDuration(consumedTime);
        if (consumedTime <= previousConsumedTime) {
            // Occurs due to rounding down of consumedTimeInSeconds
            return;
        }
        logger.debug("Scheduling consumption of all tasks up to {} minutes.", consumedTime);
        previousConsumedTime = consumedTime;
        doProblemChange((taskAssigningSolution, problemChangeDirector) -> {
            taskAssigningSolution.setFrozenCutoff(consumedTime);
            // TODO update list variable pins: https://issues.redhat.com/browse/PLANNER-2633.
        });
    }

    /**
     * @param producedTime in minutes, just like {@link Task#getStartTime()}
     */
    public void produceUpTo(final int producedTime) {
        if (producedTime <= previousProducedTime) {
            // Occurs due to rounding down of producedDurationInSeconds
            return;
        }
        final int baseDurationBudgetPerEmployee = (producedTime - previousProducedTime);
        final int newTaskCount = getSolution().getEmployeeList().size() * baseDurationBudgetPerEmployee / BASE_DURATION_AVERAGE;
        if (newTaskCount <= 0) {
            // Do not change previousProducedDuration
            return;
        }
        logger.debug("Scheduling production of {} new tasks.", newTaskCount);
        previousProducedTime = producedTime;
        final int readyTime = previousConsumedTime;
        doProblemChange((taskAssigningSolution, problemChangeDirector) -> {
            List<TaskType> taskTypeList = taskAssigningSolution.getTaskTypeList();
            List<Customer> customerList = taskAssigningSolution.getCustomerList();
            Priority[] priorities = Priority.values();
            List<Task> taskList = taskAssigningSolution.getTaskList();
            for (int i = 0; i < newTaskCount; i++) {
                TaskType taskType = taskTypeList.get(producingRandom.nextInt(taskTypeList.size()));
                long nextTaskId = 0L;
                int nextIndexInTaskType = 0;
                for (Task other : taskList) {
                    if (nextTaskId <= other.getId()) {
                        nextTaskId = other.getId() + 1L;
                    }
                    if (taskType == other.getTaskType()) {
                        if (nextIndexInTaskType <= other.getIndexInTaskType()) {
                            nextIndexInTaskType = other.getIndexInTaskType() + 1;
                        }
                    }
                }
                // Prevent the new task from being assigned retroactively
                Task task = new Task(nextTaskId, taskType, nextIndexInTaskType,
                        customerList.get(producingRandom.nextInt(customerList.size())), readyTime,
                        priorities[producingRandom.nextInt(priorities.length)]);
                problemChangeDirector.addEntity(task, taskList::add);
            }
        });
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(TaskAssigningSolution solution) {
        consumedTimeInSeconds = solution.getFrozenCutoff() * 60;
        previousConsumedTime = solution.getFrozenCutoff();
        producedTimeInSeconds = 0;
        previousProducedTime = 0;
        producingRandom = new Random(0); // Random is thread safe
        taskOverviewPanel.resetPanel(solution);
        taskOverviewPanel.setConsumedDuration(consumedTimeInSeconds / 60);
    }

    @Override
    public void updatePanel(TaskAssigningSolution taskAssigningSolution) {
        taskOverviewPanel.resetPanel(taskAssigningSolution);
    }

}
