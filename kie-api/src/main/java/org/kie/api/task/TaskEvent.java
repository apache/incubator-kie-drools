package org.kie.api.task;

import java.util.Date;
import java.util.Map;

import org.kie.api.task.model.Task;

public interface TaskEvent {

    Map<String, Object> getMetadata();

    Task getTask();

    TaskContext getTaskContext();
    
    Date getEventDate();
}
