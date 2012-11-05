/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;



import java.util.List;
import org.jbpm.task.TaskEvent;


/**
 * The Task Events Service is intended to 
 *  provide all the functionality required to handle
 *  the events that are being emitted by the module
 */
public interface TaskEventsService {
    
    List<TaskEvent> getTaskEventsById(long taskId);
    
    void removeTaskEventsById(long taskId);
    
  
}
