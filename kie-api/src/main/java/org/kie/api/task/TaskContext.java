/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.api.task;

import org.kie.api.task.model.Task;

public interface TaskContext {

    /**
     * Returns currently configured UserGroupCallback.
     * @return returns user group callback
     */
    UserGroupCallback getUserGroupCallback();

    /**
     * Loads task (given as argument) variables - both input and output if exists.
     * In case variables are already set they are not reread from data store.
     * @param task task which should have variables (both input and output) set
     * @return returns task with variables set
     */
    Task loadTaskVariables(Task task);
    
    /**
     * Returns user id who performs the operation
     * @return user id of the caller
     */
    String getUserId();
}
