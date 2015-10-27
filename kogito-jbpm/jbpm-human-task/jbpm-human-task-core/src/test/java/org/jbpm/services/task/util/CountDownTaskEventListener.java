package org.jbpm.services.task.util;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.kie.api.task.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CountDownTaskEventListener implements TaskLifeCycleEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CountDownTaskEventListener.class);
    
    
    private CountDownLatch latch;

    private boolean reassignmentAware;
    private boolean notificationAware;
    
    public CountDownTaskEventListener(int threads, boolean reassignmentAware, boolean notificationAware) {
        this.latch = new CountDownLatch(threads);
        this.reassignmentAware = reassignmentAware;
        this.notificationAware = notificationAware;
    }
    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers notification/reassignment");
        }
    }
    
    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers notification/reassignment");
        }
    }
    
    public void reset(int threads) {
        this.latch = new CountDownLatch(threads);
    }

    @Override
    public void beforeTaskActivatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskClaimedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskSkippedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskStartedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskStoppedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskCompletedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskFailedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskAddedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskExitedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskReleasedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskResumedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskSuspendedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskForwardedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskDelegatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskNominatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskActivatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskClaimedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskSkippedEvent(TaskEvent event) {
        
        latch.countDown();
    }

    @Override
    public void afterTaskStartedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskStoppedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskCompletedEvent(TaskEvent event) {
        
        latch.countDown();
    }

    @Override
    public void afterTaskFailedEvent(TaskEvent event) {
        
        latch.countDown();
    }

    @Override
    public void afterTaskAddedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskExitedEvent(TaskEvent event) {
        
        latch.countDown();
    }

    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskNominatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskUpdatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskUpdatedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void beforeTaskReassignedEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskReassignedEvent(TaskEvent event) {
        
        if (reassignmentAware) {
            latch.countDown();
        }
    }

    @Override
    public void beforeTaskNotificationEvent(TaskEvent event) {
        
        
    }

    @Override
    public void afterTaskNotificationEvent(TaskEvent event) {
        if (notificationAware) {
            latch.countDown();
        }
        
    }
    @Override
    public void afterTaskInputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        
    }
    @Override
    public void afterTaskOutputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        
    }
}
