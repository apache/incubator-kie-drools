package org.jbpm.task.servlet;

import java.util.Iterator;

import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.UserInfo;

public class CustomUserInfo implements UserInfo {

	public String getDisplayName(OrganizationalEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasEmail(Group group) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getEmailForEntity(OrganizationalEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLanguageForEntity(OrganizationalEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
