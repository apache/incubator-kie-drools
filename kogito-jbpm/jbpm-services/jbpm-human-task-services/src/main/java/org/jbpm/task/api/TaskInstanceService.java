/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jbpm.task.ContentData;
import org.jbpm.task.FaultData;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;

/**
 * The Task Instance Service is in charge of
 *  handling all the actions required to interact with a 
 *  Task Instance. All the operations described in the WS-HT specification
 *  related with the Task Lifecycle are implemented here.
 */
public interface TaskInstanceService {

    /**
     * LIFECYCLE METHODS
     *
     */
    long newTask(String name, Map<String, Object> params);

    long newTask(TaskDef def, Map<String, Object> params);

    long newTask(TaskDef def, Map<String, Object> params, boolean deploy);

    long addTask(Task task, Map<String, Object> params);

    long addTask(Task task, ContentData data);

    void activate(long taskId, String userId);

    void claim(long taskId, String userId);

    void claim(long taskId, String userId, List<String> groupIds);

    void claimNextAvailable(String userId, String language);

    void claimNextAvailable(String userId, List<String> groupIds, String language);

    void complete(long taskId, String userId, Map<String, Object> data);

    void delegate(long taskId, String userId, String targetUserId);

    void exit(long taskId, String userId);

    void fail(long taskId, String userId, Map<String, Object> faultData);

    void forward(long taskId, String userId, String targetEntityId);

    void release(long taskId, String userId);

    void remove(long taskId, String userId);

    void resume(long taskId, String userId);

    void skip(long taskId, String userId);

    void start(long taskId, String userId);

    void stop(long taskId, String userId);

    void suspend(long taskId, String userId);

    void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners);

    void setFault(long taskId, String userId, FaultData fault);

    void setOutput(long taskId, String userId, Object outputContentData);

    void deleteFault(long taskId, String userId);

    void deleteOutput(long taskId, String userId);

    void setPriority(long taskId, int priority);
    
    void setTaskNames(long taskId, List<I18NText> taskNames);
    
    void setExpirationDate(long taskId, Date date);
    
    public void setDescriptions(long taskId, List<I18NText> descriptions);
    
    public void setSkipable(long taskId, boolean skipable);
    
    void setSubTaskStrategy(long taskId, SubTasksStrategy strategy);
    
    int getPriority(long taskId);
    
    Date getExpirationDate(long taskId);
    
    List<I18NText> getDescriptions(long taskId);
    
    boolean isSkipable(long taskId);
    
    SubTasksStrategy getSubTaskStrategy(long taskId);
    
    
}
