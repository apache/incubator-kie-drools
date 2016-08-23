/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.task.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.Deadline;

public interface TaskPersistenceContext {

    Task findTask(Long taskId);

    Task persistTask(Task task);

    Task updateTask(Task task);

    Task removeTask(Task task);

    Group findGroup(String groupId);

    Group persistGroup(Group group);

    Group updateGroup(Group group);

    Group removeGroup(Group group);

    User findUser(String userId);

    User persistUser(User user);

    User updateUser(User user);

    User removeUser(User user);

    OrganizationalEntity findOrgEntity(String orgEntityId);

    OrganizationalEntity persistOrgEntity(OrganizationalEntity orgEntity);

    OrganizationalEntity updateOrgEntity(OrganizationalEntity orgEntity);

    OrganizationalEntity removeOrgEntity(OrganizationalEntity orgEntity);

    Content findContent(Long contentId);

    Content persistContent(Content content);

    Content updateContent(Content content);

    Content removeContent(Content content);

    Long findTaskIdByContentId(Long contentId);

    Attachment findAttachment(Long attachmentId);

    Attachment persistAttachment(Attachment attachment);

    Attachment updateAttachment(Attachment attachment);

    Attachment removeAttachment(Attachment attachment);

    Comment findComment(Long commentId);

    Comment persistComment(Comment comment);

    Comment updateComment(Comment comment);

    Comment removeComment(Comment comment);

    Deadline findDeadline(Long deadlineId);

    Deadline persistDeadline(Deadline deadline);

    Deadline updateDeadline(Deadline deadline);

    Deadline removeDeadline(Deadline deadline);

    /*
     * Query related methods
     */

    <T> T queryWithParametersInTransaction(String queryName, Map<String, Object> params, Class<T> clazz);

    <T> T queryWithParametersInTransaction(String queryName, boolean singleResult, Map<String, Object> params, Class<T> clazz);

    <T> T queryAndLockWithParametersInTransaction(String queryName, Map<String, Object> params, boolean singleResult, Class<T> clazz);

    <T> T queryInTransaction(String queryName, Class<T> clazz);

    <T> T queryStringInTransaction(String queryString, Class<T> clazz );

    <T> T queryStringWithParametersInTransaction(String queryString, boolean singleResult, Map<String, Object> params, Class<T> clazz );

    <T> T queryStringWithParametersInTransaction(String queryString,  Map<String, Object> params, Class<T> clazz );

    <T> T queryAndLockStringWithParametersInTransaction(String queryName, Map<String, Object> params, boolean singleResult, Class<T> clazz);

    int executeUpdateString(String updateString);

    HashMap<String, Object> addParametersToMap(Object ... parameterValues);

    /*
     * Following are optional methods that are more like extension to
     * default data model to allow flexible add-ons
     */
    <T> T persist(T object);

    <T> T find(Class<T> entityClass, Object primaryKey);

    <T> T remove(T entity);

    <T> T merge(T entity);

    /*
     * life cycle methods
     */
    boolean isOpen();

    void joinTransaction();

    void close();

    /*
     * JPA Query methods
     */

    List<TaskSummary> doTaskSummaryCriteriaQuery(String userId, UserGroupCallback userGroupCallback, Object queryWhere);
}
