/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.services.task.identity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Alternative;

import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.utils.MVELUtils;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.internal.task.api.UserGroupCallback;

@Alternative
@Selectable
public class MvelUserGroupCallbackImpl implements UserGroupCallback {

    private Map<UserImpl, List<GroupImpl>> userGroupMapping;

    public MvelUserGroupCallbackImpl() {
        Reader reader = null;
        Map vars = new HashMap();
        try {
            reader = new InputStreamReader(getClass().getResourceAsStream("UserGroupsAssignmentsOne.mvel"));
            userGroupMapping = (Map<UserImpl, List<GroupImpl>>) MVELUtils.eval(reader, vars);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader = null;
        }
    }

    public boolean existsUser(String userId) {
        Iterator<UserImpl> iter = userGroupMapping.keySet().iterator();
        while (iter.hasNext()) {
            UserImpl u = iter.next();
            if (u.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean existsGroup(String groupId) {
        Iterator<UserImpl> iter = userGroupMapping.keySet().iterator();
        while (iter.hasNext()) {
            UserImpl u = iter.next();
            List<GroupImpl> groups = userGroupMapping.get(u);
            for (GroupImpl g : groups) {
                if (g.getId().equals(groupId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getGroupsForUser(String userId, List<String> groupIds) {
        return getGroupsForUser(userId);
    }

    public List<String> getGroupsForUser(String userId, List<String> groupIds,
            List<String> allExistingGroupIds) {
        return getGroupsForUser(userId);
    }

    public List<String> getGroupsForUser(String userId) {
        Iterator<UserImpl> iter = userGroupMapping.keySet().iterator();
        while (iter.hasNext()) {
            UserImpl u = iter.next();
            if (u.getId().equals(userId)) {
                List<String> groupList = new ArrayList<String>();
                List<GroupImpl> userGroupList = userGroupMapping.get(u);
                for (GroupImpl g : userGroupList) {
                    groupList.add(g.getId());
                }
                return groupList;
            }
        }
        return null;
    }
}
