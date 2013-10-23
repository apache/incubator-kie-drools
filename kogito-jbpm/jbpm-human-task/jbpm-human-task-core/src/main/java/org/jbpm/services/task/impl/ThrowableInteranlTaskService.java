/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.impl;

import java.util.List;
import java.util.Map;

import org.jbpm.services.task.exception.TaskException;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;

/**
 * Extension to the regular task service interface to declare exceptions that might be thrown 
 * from the logic of operations. Exceptions are generically RuntimeExceptions so there is no need to
 * be declared although it makes it much easier to use with Proxy classes.
 *
 */
public interface ThrowableInteranlTaskService extends InternalTaskService {

    void activate(long taskId, String userId) throws TaskException;

    void claim(long taskId, String userId) throws TaskException;

    void claimNextAvailable(String userId, String language) throws TaskException;

    void complete(long taskId, String userId, Map<String, Object> data) throws TaskException;

    void delegate(long taskId, String userId, String targetUserId) throws TaskException;

    void exit(long taskId, String userId) throws TaskException;

    void fail(long taskId, String userId, Map<String, Object> faultData) throws TaskException;

    void forward(long taskId, String userId, String targetEntityId) throws TaskException;
    
    long addTask(Task task, Map<String, Object> params) throws TaskException;

    void release(long taskId, String userId) throws TaskException;

    void resume(long taskId, String userId) throws TaskException;

    void skip(long taskId, String userId) throws TaskException;

    void start(long taskId, String userId) throws TaskException;

    void stop(long taskId, String userId) throws TaskException;

    void suspend(long taskId, String userId) throws TaskException;

    void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) throws TaskException;
}
