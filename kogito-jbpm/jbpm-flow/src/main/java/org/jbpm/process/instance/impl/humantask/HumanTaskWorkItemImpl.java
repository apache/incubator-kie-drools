/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.impl.humantask;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.NotAuthorizedException;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitems.impl.KogitoWorkItemImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumanTaskWorkItemImpl extends KogitoWorkItemImpl implements HumanTaskWorkItem {

    private static final long serialVersionUID = 6168927742199190604L;
    private static final Logger logger = LoggerFactory.getLogger(HumanTaskWorkItemImpl.class);

    private String taskName;
    private String taskDescription;
    private String taskPriority;
    private String referenceName;

    private String actualOwner;
    private Set<String> potentialUsers = new HashSet<>();
    private Set<String> potentialGroups = new HashSet<>();
    private Set<String> excludedUsers = new HashSet<>();
    private Set<String> adminUsers = new HashSet<>();
    private Set<String> adminGroups = new HashSet<>();
    private Map<Object, Comment> comments = new ConcurrentHashMap<>();
    private Map<Object, Attachment> attachments = new ConcurrentHashMap<>();

    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @Override
    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    @Override
    public Set<String> getPotentialUsers() {
        return potentialUsers;
    }

    public void setPotentialUsers(Set<String> potentialUsers) {
        this.potentialUsers = potentialUsers;
    }

    @Override
    public Set<String> getPotentialGroups() {
        return potentialGroups;
    }

    public void setPotentialGroups(Set<String> potentialGroups) {
        this.potentialGroups = potentialGroups;
    }

    @Override
    public Set<String> getExcludedUsers() {
        return excludedUsers;
    }

    public void setExcludedUsers(Set<String> excludedUsers) {
        this.excludedUsers = excludedUsers;
    }

    @Override
    public Set<String> getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(Set<String> adminUsers) {
        this.adminUsers = adminUsers;
    }

    @Override
    public Set<String> getAdminGroups() {
        return adminGroups;
    }

    public void setAdminGroups(Set<String> adminGroups) {
        this.adminGroups = adminGroups;
    }

    @Override
    public boolean enforce(Policy<?>... policies) {
        for (Policy<?> policy : policies) {
            if (policy instanceof SecurityPolicy) {
                try {
                    enforceAuthorization(((SecurityPolicy) policy).value());

                    return true;
                } catch (NotAuthorizedException e) {
                    return false;
                }
            }
        }
        boolean authorized = true;
        // there might have not been any policies given so let's ensure task is protected if any assignments is set
        String currentOwner = getActualOwner();
        if ((currentOwner != null && !currentOwner.trim().isEmpty()) || !getPotentialUsers().isEmpty()) {
            authorized = false;
        }

        return authorized;
    }

    protected void enforceAuthorization(IdentityProvider identity) {

        if (identity != null) {
            logger.debug("Identity information provided, enforcing security restrictions, user '{}' with roles '{}'", identity.getName(), identity.getRoles());
            // in case identity/auth info is given enforce security restrictions
            String user = identity.getName();
            String currentOwner = getActualOwner();
            // if actual owner is already set always enforce same user
            if (currentOwner != null && !currentOwner.trim().isEmpty() && !user.equals(currentOwner) &&
                    (getAdminUsers() == null || !getAdminUsers().contains(user))) {
                logger.debug("Work item {} has already owner assigned so requesting user must match - owner '{}' == requestor '{}'", getStringId(), currentOwner, user);
                throw new NotAuthorizedException("User " + user + " is not authorized to access task instance with id " + getStringId());
            }

            checkAssignedOwners(user, identity.getRoles());
        }
    }

    protected void checkAssignedOwners(String user, Collection<String> roles) {
        // is not in the excluded users
        if (getExcludedUsers().contains(user)) {
            logger.debug("Requesting user '{}' is excluded from the potential workers on work item {}", user, getStringId());
            throw new NotAuthorizedException("User " + user + " is not authorized to access task instance with id " + getStringId());
        }

        // if there are no assignments means open to everyone
        if (getPotentialUsers().isEmpty() && getPotentialGroups().isEmpty()) {
            return;
        }
        // check if user is in potential users or groups 
        if (!getPotentialUsers().contains(user) &&
                getPotentialGroups().stream().noneMatch(roles::contains)) {
            throw new NotAuthorizedException("User " + user + " is not authorized to access task instance with id " + getStringId());
        }
    }

    @Override
    public Map<Object, Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public Map<Object, Comment> getComments() {
        return comments;
    }
}
