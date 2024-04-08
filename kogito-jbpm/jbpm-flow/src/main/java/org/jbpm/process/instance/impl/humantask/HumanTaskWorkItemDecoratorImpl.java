/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.process.instance.impl.humantask;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport.AssignmentType;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.Policy;

public class HumanTaskWorkItemDecoratorImpl implements InternalHumanTaskWorkItem {

    private NodeInstance nodeInstance;
    private InternalHumanTaskWorkItem delegate;

    public HumanTaskWorkItemDecoratorImpl(NodeInstance nodeInstance, InternalHumanTaskWorkItem delegate) {
        this.nodeInstance = nodeInstance;
        this.delegate = delegate;
    }

    private InternalKnowledgeRuntime getKieRuntime() {
        return ((WorkflowProcessInstanceImpl) nodeInstance.getProcessInstance()).getKnowledgeRuntime();
    }

    private Optional<KogitoProcessEventSupport> getEventSupport() {
        if (!isActive()) {
            return Optional.empty();
        }

        if (getKieRuntime() == null) {
            return Optional.empty();
        }

        if (getKieRuntime().getProcessRuntime() == null) {
            return Optional.empty();
        }

        return Optional.of(InternalProcessRuntime.asKogitoProcessRuntime(getKieRuntime().getProcessRuntime()).getProcessEventSupport());
    }

    private boolean isActive() {
        return nodeInstance.getProcessInstance() != null && getKieRuntime() != null;
    }

    @Override
    public String getTaskName() {
        return delegate.getTaskName();
    }

    @Override
    public String getTaskDescription() {
        return delegate.getTaskDescription();
    }

    @Override
    public String getTaskPriority() {
        return delegate.getTaskPriority();
    }

    @Override
    public String getReferenceName() {
        return delegate.getReferenceName();
    }

    @Override
    public String getActualOwner() {
        return delegate.getActualOwner();
    }

    @Override
    public Set<String> getPotentialUsers() {
        return delegate.getPotentialUsers();
    }

    @Override
    public Set<String> getPotentialGroups() {
        return delegate.getPotentialGroups();
    }

    @Override
    public Set<String> getAdminUsers() {
        return delegate.getAdminUsers();
    }

    @Override
    public Set<String> getAdminGroups() {
        return delegate.getAdminGroups();
    }

    @Override
    public Set<String> getExcludedUsers() {
        return delegate.getExcludedUsers();
    }

    @Override
    public void setId(String uuid) {
        delegate.setId(uuid);
    }

    @Override
    public void setAttachment(String id, Attachment attachment) {
        Attachment oldValue = delegate.getAttachments().get(id);
        delegate.setAttachment(id, attachment);
        if (oldValue != null) {
            getEventSupport().ifPresent(e -> e.fireOnUserTaskAttachmentChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), oldValue, attachment));
        } else {
            getEventSupport().ifPresent(e -> e.fireOnUserTaskAttachmentAdded(getProcessInstance(), getNodeInstance(), getKieRuntime(), attachment));
        }
    }

    @Override
    public Attachment removeAttachment(String id) {
        Attachment oldValue = delegate.getAttachments().remove(id);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskAttachmentDeleted(getProcessInstance(), getNodeInstance(), getKieRuntime(), oldValue));
        return oldValue;
    }

    @Override
    public void setComment(String id, Comment comment) {
        Comment oldValue = delegate.getComments().remove(id);
        delegate.setComment(id, comment);
        if (oldValue != null) {
            getEventSupport().ifPresent(e -> e.fireOnUserTaskCommentChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), oldValue, comment));
        } else {
            getEventSupport().ifPresent(e -> e.fireOnUserTaskCommentAdded(getProcessInstance(), getNodeInstance(), getKieRuntime(), comment));
        }

    }

    @Override
    public Comment removeComment(String id) {
        Comment oldValue = delegate.getComments().remove(id);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskCommentDeleted(getProcessInstance(), getNodeInstance(), getKieRuntime(), oldValue));
        return oldValue;
    }

    @Override
    public Map<Object, Attachment> getAttachments() {
        return delegate.getAttachments();
    }

    @Override
    public Map<Object, Comment> getComments() {
        return delegate.getComments();
    }

    @Override
    public long getId() {
        return delegate.getId();
    }

    @Override
    public String getStringId() {
        return delegate.getStringId();
    }

    @Override
    public String getProcessInstanceStringId() {
        return delegate.getProcessInstanceId();
    }

    @Override
    public String getPhaseId() {
        return delegate.getPhaseId();
    }

    @Override
    public String getPhaseStatus() {
        return delegate.getPhaseStatus();
    }

    @Override
    public Date getStartDate() {
        return delegate.getStartDate();
    }

    @Override
    public Date getCompleteDate() {
        return delegate.getCompleteDate();
    }

    @Override
    public KogitoNodeInstance getNodeInstance() {
        return delegate.getNodeInstance();
    }

    @Override
    public KogitoProcessInstance getProcessInstance() {
        return delegate.getProcessInstance();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public int getState() {
        return delegate.getState();
    }

    @Override
    public Object getParameter(String name) {
        return delegate.getParameter(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return delegate.getParameters();
    }

    @Override
    public Object getResult(String name) {
        return delegate.getResult(name);
    }

    @Override
    public Map<String, Object> getResults() {
        return delegate.getResults();
    }

    @Override
    public String getProcessInstanceId() {
        return delegate.getProcessInstanceId();
    }

    @Override
    public String getNodeInstanceStringId() {
        return delegate.getNodeInstanceStringId();
    }

    @Override
    public String getDeploymentId() {
        return delegate.getDeploymentId();
    }

    @Override
    public long getNodeInstanceId() {
        return delegate.getNodeInstanceId();
    }

    @Override
    public WorkflowElementIdentifier getNodeId() {
        return delegate.getNodeId();
    }

    // set data
    @Override
    public void setPhaseId(String phaseId) {
        delegate.setPhaseId(phaseId);
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        delegate.setProcessInstanceId(processInstanceId);
    }

    @Override
    public void setNodeInstanceId(String deploymentId) {
        delegate.setNodeInstanceId(deploymentId);
    }

    @Override
    public void setPhaseStatus(String phaseStatus) {
        String oldPhaseStatus = delegate.getPhaseStatus();
        delegate.setPhaseStatus(phaseStatus);
        getEventSupport().ifPresent(e -> e.fireOneUserTaskStateChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), oldPhaseStatus, phaseStatus));

    }

    @Override
    public void setStartDate(Date date) {
        delegate.setStartDate(date);
    }

    @Override
    public void setCompleteDate(Date date) {
        delegate.setCompleteDate(date);
    }

    @Override
    public void setNodeInstance(KogitoNodeInstance nodeInstance) {
        delegate.setNodeInstance(nodeInstance);
    }

    @Override
    public void setProcessInstance(KogitoProcessInstance processInstance) {
        delegate.setProcessInstance(processInstance);
    }

    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    @Override
    public void setParameter(String name, Object value) {
        Object oldValue = delegate.getParameter(name);
        delegate.setParameter(name, value);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskInputVariableChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), name, value, oldValue));
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        parameters.forEach(this::setParameter);
    }

    @Override
    public void setResults(Map<String, Object> results) {
        if (results != null) {
            results.forEach(this::setResult);
        } else {
            Map<String, Object> outcome = new HashMap<>(delegate.getResults());
            delegate.setResults(null);
            if (isActive()) {
                for (String key : outcome.keySet()) {
                    getEventSupport().ifPresent(e -> e.fireOnUserTaskOutputVariableChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), key, null, outcome.get(key)));
                }
            }
        }
    }

    @Override
    public void setResult(String name, Object value) {
        Object oldValue = delegate.getParameter(name);
        delegate.setResult(name, value);
        if (isActive()) {
            getEventSupport().ifPresent(e -> e.fireOnUserTaskOutputVariableChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), name, value, oldValue));
        }
    }

    @Override
    public void setState(int state) {
        delegate.setState(state);
    }

    @Override
    public void setDeploymentId(String deploymentId) {
        delegate.setDeploymentId(deploymentId);
    }

    @Override
    public void setNodeInstanceId(long deploymentId) {
        delegate.setNodeInstanceId(deploymentId);
    }

    @Override
    public void setNodeId(WorkflowElementIdentifier nodeIdentifier) {
        delegate.setNodeId(nodeIdentifier);
    }

    @Override
    public void setTaskName(String taskName) {
        delegate.setTaskName(taskName);

    }

    @Override
    public void setTaskDescription(String taskDescription) {
        delegate.setTaskDescription(taskDescription);
        if (delegate.getPhaseStatus() == null) {
            return;
        }
        getEventSupport().ifPresent(e -> e.fireOneUserTaskStateChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), delegate.getPhaseStatus(), delegate.getPhaseStatus()));
    }

    @Override
    public void setTaskPriority(String taskPriority) {
        delegate.setTaskPriority(taskPriority);
        if (delegate.getPhaseStatus() == null) {
            return;
        }
        getEventSupport().ifPresent(e -> e.fireOneUserTaskStateChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), delegate.getPhaseStatus(), delegate.getPhaseStatus()));
    }

    @Override
    public void setReferenceName(String referenceName) {
        delegate.setReferenceName(referenceName);
    }

    @Override
    public void setActualOwner(String actualOwner) {
        String currentPhaseStatus = delegate.getPhaseStatus();
        delegate.setActualOwner(actualOwner);
        if (currentPhaseStatus == null) {
            return;
        }
        getEventSupport().ifPresent(e -> e.fireOneUserTaskStateChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), currentPhaseStatus, currentPhaseStatus));
    }

    @Override
    public void setPotentialUsers(Set<String> potentialUsers) {
        Set<String> oldValue = new HashSet<>(delegate.getPotentialUsers());
        delegate.setPotentialUsers(potentialUsers);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskAssignmentChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), AssignmentType.USER_OWNERS, oldValue, potentialUsers));
    }

    @Override
    public void setPotentialGroups(Set<String> potentialGroups) {
        Set<String> oldValue = new HashSet<>(delegate.getPotentialGroups());
        delegate.setPotentialGroups(potentialGroups);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskAssignmentChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), AssignmentType.USER_GROUPS, oldValue, potentialGroups));
    }

    @Override
    public void setAdminGroups(Set<String> potentialAdmins) {
        Set<String> oldValue = new HashSet<>(delegate.getAdminGroups());
        delegate.setAdminGroups(potentialAdmins);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskAssignmentChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), AssignmentType.ADMIN_GROUPS, oldValue, potentialAdmins));
    }

    @Override
    public void setAdminUsers(Set<String> adminUsers) {
        Set<String> oldValue = new HashSet<>(delegate.getAdminUsers());
        delegate.setAdminUsers(adminUsers);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskAssignmentChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), AssignmentType.ADMIN_USERS, oldValue, adminUsers));

    }

    @Override
    public void setExcludedUsers(Set<String> excludedUsers) {
        Set<String> oldValue = new HashSet<>(delegate.getExcludedUsers());
        delegate.setExcludedUsers(excludedUsers);
        getEventSupport().ifPresent(e -> e.fireOnUserTaskAssignmentChange(getProcessInstance(), getNodeInstance(), getKieRuntime(), AssignmentType.USERS_EXCLUDED, oldValue, excludedUsers));
    }

    @Override
    public boolean enforce(Policy<?>... policies) {
        return delegate.enforce(policies);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
