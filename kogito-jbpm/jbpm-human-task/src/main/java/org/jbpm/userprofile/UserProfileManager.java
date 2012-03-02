/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.userprofile;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * userProfileManager retrieves and update user profile using a pluggable UserProfileRepository. 
 * UserProfileRepository is normally implemented by application users based on the persistent mechanism 
 * (for example property file based, RMDB based, LDAP based etc) as well as the data schema that 
 * is used by users' application.
 * Following snippet shows how to configure UserProfileRepository in components.xml.
    <component name="userProfileManager">
	    <property name="userProfileRepository">org.drools.task.MockFileBasedUserProfileRepository</property>
    </component>
 *   
 */

@Scope(ScopeType.APPLICATION)
@Startup
@Name("userProfileManager")
public class UserProfileManager {
	UserProfileRepository userProfileRepository = null;

    /**
     * Return current user.
     * 
     * @return User 
     */
	public User getUser() {
		String userName = "";
		if (Contexts.isApplicationContextActive()) {
			userName = Identity.instance().getCredentials().getUsername();
		}
		return getUser(userName);
	}
	
    /**
     * Return the user according to the userId.
     * 
     * @return User 
     */	
	public User getUser(String userId) {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		User user = new User();
		UserProfile profile = userProfileRepository.getUserProfile(userId);
		user.setUserProfile(profile);
		user.setId(profile.getID());
		return user;
	}
	
    /**
     * Update user info
     * 
     */
	public void updateUser(User user) {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return;
		}
		userProfileRepository.setUserProfile(user.getUserProfile());			
	}
	
    /**
     * Return all registered users
     * 
     * @return List<User>, a list of all registered users
     */
	public List<User> getUsers() {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		return userProfileRepository.getUsers();
	}
	
    /**
     * Return a list of Ids of all registered users instead of fully populated User classes.
     * 
     * @return String[], a list of all registered users' id. 
     */
	public String[] getUserIds() {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		return userProfileRepository.getUserIds();
	}

    /**
     * Return all registered users
     * 
     * @return List<Group>, a list of all registered groups
     */
	public List<Group> getGroups() {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		return userProfileRepository.getGroups();
	}
	
    /**
     * Return a list of Ids of all registered groups instead of fully populated Group classes.
     * 
     * @return  String[], a list of all registered groups' id. 
     */
	public String[] getGroupIds() {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		return userProfileRepository.getUserIds();
	}
	
    /**
     * Return a list of all the groups that the user belongs to.
     * 
     * @param userId Id of the user.
     * @return List<Group>, a list of groups that the user belongs to. 
     */
	public List<Group> getGroupsForUser(String userId) {
		return null;
	}
	
    /**
     * Return a list of all the direct groups and all the sub groups that the user belongs to.
     * 
     * @param userId Id of the user.
     * @return List<Group>, a list of all the direct groups and all the sub 
     * groups that the user belongs to.
     */
	public List<Group> getFlattenedGroupsForUser(String userId) {
		return null;
	}
	
	public UserProfileRepository getUserProfileRepository() {
		return userProfileRepository;
	}

	public void setUserProfileRepository(UserProfileRepository userProfileRepository) {
		this.userProfileRepository = userProfileRepository;
	}

}
