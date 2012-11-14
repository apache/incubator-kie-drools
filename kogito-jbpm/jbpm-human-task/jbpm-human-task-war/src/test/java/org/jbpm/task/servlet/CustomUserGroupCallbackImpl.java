package org.jbpm.task.servlet;

import java.util.List;
import org.jbpm.task.identity.UserGroupCallback;



public class CustomUserGroupCallbackImpl implements UserGroupCallback {

	public boolean existsUser(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean existsGroup(String groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<String> getGroupsForUser(String userId, List<String> groupIds,
			List<String> allExistingGroupIds) {
		// TODO Auto-generated method stub
		return null;
	}

}
