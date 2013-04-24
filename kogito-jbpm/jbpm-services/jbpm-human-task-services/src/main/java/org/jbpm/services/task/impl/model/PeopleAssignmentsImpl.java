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

package org.jbpm.services.task.impl.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.jbpm.services.task.utils.CollectionUtils;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalPeopleAssignments;

@Embeddable
public class PeopleAssignmentsImpl implements InternalPeopleAssignments {
	
    @ManyToOne()
    private UserImpl                       taskInitiator;

    @ManyToMany(targetEntity=OrganizationalEntityImpl.class)
    @JoinTable(name = "PeopleAssignments_PotOwners", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "entity_id"))
    private List<OrganizationalEntity> potentialOwners        = Collections.emptyList();

    @ManyToMany(targetEntity=OrganizationalEntityImpl.class)
    @JoinTable(name = "PeopleAssignments_ExclOwners", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "entity_id"))
    private List<OrganizationalEntity> excludedOwners         = Collections.emptyList();

    @ManyToMany(targetEntity=OrganizationalEntityImpl.class)
    @JoinTable(name = "PeopleAssignments_Stakeholders", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "entity_id"))
    private List<OrganizationalEntity> taskStakeholders       = Collections.emptyList();

    @ManyToMany(targetEntity=OrganizationalEntityImpl.class)
    @JoinTable(name = "PeopleAssignments_BAs", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "entity_id"))
    private List<OrganizationalEntity> businessAdministrators = Collections.emptyList();

    @ManyToMany(targetEntity=OrganizationalEntityImpl.class)
    @JoinTable(name = "PeopleAssignments_Recipients", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "entity_id"))
    private List<OrganizationalEntity> recipients             = Collections.emptyList();

    public PeopleAssignmentsImpl() {

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( taskInitiator != null ) {
            out.writeBoolean( true );
            taskInitiator.writeExternal( out );
        } else {
            out.writeBoolean( false );
        }
        CollectionUtils.writeOrganizationalEntityList( potentialOwners,
                                                       out );
        CollectionUtils.writeOrganizationalEntityList( excludedOwners,
                                                       out );
        CollectionUtils.writeOrganizationalEntityList( taskStakeholders,
                                                       out );
        CollectionUtils.writeOrganizationalEntityList( businessAdministrators,
                                                       out );
        CollectionUtils.writeOrganizationalEntityList( recipients,
                                                       out );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        if ( in.readBoolean() ) {
            taskInitiator = new UserImpl();
            taskInitiator.readExternal( in );
        }
        potentialOwners = CollectionUtils.readOrganizationalEntityList( in );
        excludedOwners = CollectionUtils.readOrganizationalEntityList( in );
        taskStakeholders = CollectionUtils.readOrganizationalEntityList( in );
        businessAdministrators = CollectionUtils.readOrganizationalEntityList( in );
        recipients = CollectionUtils.readOrganizationalEntityList( in );
    }

    public User getTaskInitiator() {
        return taskInitiator;
    }

    public void setTaskInitiator(User taskInitiator) {
        this.taskInitiator = (UserImpl) taskInitiator;
    }

    public List<OrganizationalEntity> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    public List<OrganizationalEntity> getExcludedOwners() {
        return excludedOwners;
    }

    public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
        this.excludedOwners = excludedOwners;
    }

    public List<OrganizationalEntity> getTaskStakeholders() {
        return taskStakeholders;
    }

    public void setTaskStakeholders(List<OrganizationalEntity> taskStakeholders) {
        this.taskStakeholders = taskStakeholders;
    }

    public List<OrganizationalEntity> getBusinessAdministrators() {
        return businessAdministrators;
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = businessAdministrators;
    }

    public List<OrganizationalEntity> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<OrganizationalEntity> recipients) {
        this.recipients = recipients;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + CollectionUtils.hashCode( businessAdministrators );
        result = prime * result + CollectionUtils.hashCode( excludedOwners );
        result = prime * result + ((potentialOwners == null) ? 0 : CollectionUtils.hashCode( potentialOwners ));
        result = prime * result + CollectionUtils.hashCode( recipients );
        result = prime * result + ((taskInitiator == null) ? 0 : taskInitiator.hashCode());
        result = prime * result + CollectionUtils.hashCode( taskStakeholders );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof PeopleAssignmentsImpl) ) return false;
        PeopleAssignmentsImpl other = (PeopleAssignmentsImpl) obj;

        if ( taskInitiator == null ) {
            if ( other.taskInitiator != null ) return false;
        } else if ( !taskInitiator.equals( other.taskInitiator ) ) return false;

        return CollectionUtils.equals( businessAdministrators,
                                       other.businessAdministrators ) && CollectionUtils.equals( excludedOwners,
                                                                                                 other.excludedOwners ) && CollectionUtils.equals( potentialOwners,
                                                                                                                                                   other.potentialOwners ) && CollectionUtils.equals( recipients,
                                                                                                                                                                                                      other.recipients )
               && CollectionUtils.equals( taskStakeholders,
                                          other.taskStakeholders );
    }

}
