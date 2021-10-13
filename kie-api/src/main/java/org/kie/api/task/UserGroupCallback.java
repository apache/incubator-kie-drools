/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

public interface UserGroupCallback {
    /**
     * Resolves existence of user id.
     * @param userId    the user id assigned to the task
     * @return true if userId exists, false otherwise.
     */
    boolean existsUser(String userId);

    /**
     * Resolves existence of group id.
     * @param groupId   the group id assigned to the task
     * @return true if groupId exists, false otherwise.
     */
    boolean existsGroup(String groupId);

    /**
     * Returns list of group ids for specified user id.
     * @param userId    the user id assigned to the task
     * @return List of group ids.
     */
    List<String> getGroupsForUser(String userId);

}
