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
