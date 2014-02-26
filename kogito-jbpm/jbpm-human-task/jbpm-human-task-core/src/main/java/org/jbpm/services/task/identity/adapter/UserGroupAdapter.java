package org.jbpm.services.task.identity.adapter;

import java.util.List;

public interface UserGroupAdapter {

	List<String> getGroupsForUser(String userId);
}
