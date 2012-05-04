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

package org.jbpm.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.task.UserInfo;

public class MockUserInfo implements UserInfo {

    private Map<Group, List<OrganizationalEntity>> groups = new HashMap<Group, List<OrganizationalEntity>>();

    private Map<OrganizationalEntity, String> emails = new HashMap<OrganizationalEntity, String>();

    private Map<OrganizationalEntity, String> languages = new HashMap<OrganizationalEntity, String>();

    private Map<OrganizationalEntity, String> displayNames = new HashMap<OrganizationalEntity, String>();

    public Map<Group, List<OrganizationalEntity>> getGroups() {
        return groups;
    }

    public void setGroups(Map<Group, List<OrganizationalEntity>> groups) {
        this.groups = groups;
    }

    public Map<OrganizationalEntity, String> getEmails() {
        return emails;
    }

    public void setEmails(Map<OrganizationalEntity, String> emails) {
        this.emails = emails;
    }

    public String getEmailForEntity(OrganizationalEntity entity) {
        return emails.get( entity );
    }



    public Map<OrganizationalEntity, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(Map<OrganizationalEntity, String> displayNames) {
        this.displayNames = displayNames;
    }

    public Map<OrganizationalEntity, String> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<OrganizationalEntity, String> languages) {
        this.languages = languages;
    }

    public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
        return groups.get( group ).iterator();
    }

    public boolean hasEmail(Group group) {
        return emails.containsKey( group );
    }

    public String getDisplayName(OrganizationalEntity entity) {
        String displayName = displayNames.get( entity );
        return ( displayName != null ) ? displayName : entity.getId();
    }

    public String getLanguageForEntity(OrganizationalEntity entity) {
        return languages.get( entity );
    }


}
