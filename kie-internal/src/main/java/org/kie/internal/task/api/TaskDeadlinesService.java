package org.kie.internal.task.api;

import java.util.Arrays;
import java.util.List;

import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.Deadline;

/**
 * The Task Deadlines Service is intended to handle
 *  all the Deadlines associated with a Task
 */
public interface TaskDeadlinesService {

    public enum DeadlineType {
        START(Status.Created, Status.Ready, Status.Reserved),
        END(Status.Created, Status.Ready, Status.Reserved, Status.InProgress, Status.Suspended);
        private List<Status> validStatuses;

        private DeadlineType(Status... statuses) {
            this.validStatuses = Arrays.asList(statuses);
        }

        public boolean isValidStatus(Status status) {
            return this.validStatuses.contains(status);
        }

    }

    public void schedule(long taskId, long deadlineId, long delay, DeadlineType type);

    public void unschedule(long taskId, DeadlineType type);
    
    void unschedule(long taskId, Deadline deadline, DeadlineType type);

}
