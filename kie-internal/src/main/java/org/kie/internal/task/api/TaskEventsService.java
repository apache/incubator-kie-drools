package org.kie.internal.task.api;

import java.util.List;

import org.kie.internal.task.api.model.TaskEvent;


/**
 * The Task Events Service is intended to
 *  provide all the functionality required to handle
 *  the events that are being emitted by the module
 */
public interface TaskEventsService {

    List<TaskEvent> getTaskEventsById(long taskId);

    void removeTaskEventsById(long taskId);


}
