/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;



import java.util.List;
import org.jbpm.task.TaskEvent;


/**
 *
 */
public interface TaskEventsService {
    
    List<TaskEvent> getTaskEventsById(long taskId);
    
    void removeTaskEventsById(long taskId);
    
  
}
