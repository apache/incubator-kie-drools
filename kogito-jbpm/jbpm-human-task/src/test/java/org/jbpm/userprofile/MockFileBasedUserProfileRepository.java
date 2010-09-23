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

import java.util.ArrayList;
import java.util.List;

import org.jbpm.userprofile.DroolsTaskUserProfile;
import org.jbpm.userprofile.Group;
import org.jbpm.userprofile.OrganizationalEntity;
import org.jbpm.userprofile.User;
import org.jbpm.userprofile.UserProfile;
import org.jbpm.userprofile.UserProfileRepository;

public class MockFileBasedUserProfileRepository implements UserProfileRepository {
	public UserProfile getUserProfile(String userName) {
		//load the property file, get user info.
		DroolsTaskUserProfile ui = new DroolsTaskUserProfile();

		ui.setID(userName);
		return ui;
	}

	public void setUserProfile(UserProfile info) {
		if(!(info instanceof DroolsTaskUserProfile)) {
			return;
		}
		DroolsTaskUserProfile dtup = (DroolsTaskUserProfile)info;
		//update file properties
	}
	
	
    public List<User> getUsers() {
    	//may need to look into RMDB to get the list of all users
		return null;
	}
    
    public String[] getUserIds() {
    	//may need to look into RMDB to get the list of all users
		return null;
	}
	
	public List<Group> getGroups() {
		List<OrganizationalEntity> members = new ArrayList<OrganizationalEntity>();
		members.add(new User());
		
		Group group = new Group();
		group.setMembers(members);

		List<Group> result = new ArrayList<Group>();
		
		result.add(group);
		return null;
    }
	
	public String[] getGroupIds() {
		return null;
    }
}
