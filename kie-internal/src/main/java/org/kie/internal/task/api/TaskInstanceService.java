package org.kie.internal.task.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.api.model.SubTasksStrategy;

/**
 * The Task Instance Service is in charge of
 *  handling all the actions required to interact with a
 *  Task Instance. All the operations described in the WS-HT specification
 *  related with the Task Lifecycle are implemented here.
 */
public interface TaskInstanceService extends CommandExecutor {

    /*
     * LIFECYCLE METHODS
     */

    default void fireEvent(Operation operation, long taskId) {}

    default void fireEvent(Operation operation, Task task) {}

    long addTask(Task task, Map<String, Object> params);

    long addTask(Task task, ContentData data);

    void activate(long taskId, String userId);

    void claim(long taskId, String userId);

    void claim(long taskId, String userId, List<String> groupIds);

    void claimNextAvailable(String userId);

    void claimNextAvailable(String userId, List<String> groupIds);

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

    void setName(long taskId, String name);

    void setDescription(long taskId, String description);

    void setSubject(long taskId, String subject);

    long addOutputContentFromUser(long taskId, String userId, Map<String, Object> params);

    Content getContentByIdForUser( long contentId, String userId );

    Map<String, Object> getContentMapForUser( Long taskId, String userId );



}
